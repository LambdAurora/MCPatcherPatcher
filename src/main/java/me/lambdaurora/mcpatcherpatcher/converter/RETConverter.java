/*
 *  Copyright (c) 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.lambdaurora.mcpatcherpatcher.converter;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.lambdaurora.mcpatcherpatcher.ErrorType;
import me.lambdaurora.mcpatcherpatcher.ResourceType;
import me.lambdaurora.mcpatcherpatcher.fs.ResourceAccessor;
import org.aperlambda.lambdacommon.Identifier;
import org.aperlambda.lambdacommon.LambdaConstants;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents the Random Entity Textures converter
 * <p>
 * Converts to the Varied Mob Textures (VMT) format.
 *
 * @author LambdAurora
 * @version 1.0.0
 * @since 1.0.0
 */
public class RETConverter extends Converter
{
    private static final Pattern RET_PATTERN = Pattern.compile("optifine/random/(?<type>\\w+)/(?<sub>(?:\\w+/)+)?(?<name>[A-z_-]+)(?<n>\\d+)?\\.png$");

    public RETConverter(@NotNull ResourceAccessor input, @NotNull ResourceAccessor output)
    {
        super(input, output);
    }

    @Override
    public @NotNull Map<Identifier, ErrorType> convert()
    {
        Map<Identifier, ErrorType> failed = new HashMap<>();
        Map<Identifier, List<TextureEntry>> retIds = new HashMap<>();
        this.input.getNamespaces(ResourceType.ASSETS).stream()
                .map(namespace -> new Identifier(namespace, CETConverter.CET_PARENT))
                .forEach(parent -> this.input.searchIn(ResourceType.ASSETS, parent)
                        .filter(id -> id.getName().endsWith(".png"))
                        .forEach(id -> {
                            Matcher matcher = RET_PATTERN.matcher(id.getName());
                            if (matcher.find()) {
                                String type = matcher.group("type");
                                String sub = matcher.group("sub");
                                String name = matcher.group("name");
                                String n = matcher.group("n");

                                if (type == null || name == null)
                                    return;
                                if (n == null)
                                    n = "";
                                if (sub == null)
                                    sub = "";

                                String path = type + "/" + sub + name;

                                Identifier propertiesId = new Identifier(id.getNamespace(),
                                        CETConverter.CET_PARENT).sub(path + ".properties");

                                if (this.input.has(ResourceType.ASSETS, propertiesId)) {
                                    return;
                                }

                                Identifier vmtId = new Identifier(id.getNamespace(),
                                        "varied/textures/" + path + ".json");

                                retIds.computeIfAbsent(vmtId, k -> new ArrayList<>()).add(new TextureEntry(type, sub, name, n, id));
                            }
                        }));

        retIds.forEach(this::convert);

        return failed;
    }

    /**
     * Converts the set of RET textures to a valid VMT format.
     *
     * @param vmtId    The VMT metadata file identifier.
     * @param textures The textures.
     */
    private void convert(@NotNull Identifier vmtId, @NotNull List<TextureEntry> textures)
    {
        JsonObject json = new JsonObject();
        if (textures.size() > 1) {
            json.addProperty("type", "varied-mobs:pick");
        } else {
            json.addProperty("type", "varied-mobs:result");
        }

        JsonArray jsonTextures = new JsonArray();

        textures.forEach(entry -> {
            Identifier vmtTexId = entry.toVMT();

            jsonTextures.add(vmtTexId.toString());

            InputStream in = this.input.getInputStream(ResourceType.ASSETS, entry.ofId);
            if (in == null) {
                return;
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            try {
                in.transferTo(out);
                this.output.put(ResourceType.ASSETS, vmtTexId, out.toByteArray());
                out.close();
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        if (textures.size() == 1) {
            json.addProperty("result", jsonTextures.get(0).getAsString());
        } else
            json.add("choices", jsonTextures);

        String res = LambdaConstants.GSON_PRETTY.toJson(json);
        this.output.put(ResourceType.ASSETS, vmtId, res.getBytes());
    }

    @Override
    public @NotNull String getName()
    {
        return "RET";
    }

    /**
     * Represents a texture entry.
     *
     * @version 1.0.0
     * @since 1.0.0
     */
    private static class TextureEntry
    {
        public final String     type;
        public final String     sub;
        public final String     name;
        public final String     n;
        public final Identifier ofId;

        private TextureEntry(@NotNull String type, @NotNull String sub, @NotNull String name, @NotNull String n, @NotNull Identifier ofId)
        {
            this.type = type;
            this.sub = sub;
            this.name = name;
            this.n = n;
            this.ofId = ofId;
        }

        public @NotNull Identifier toVMT()
        {
            return new Identifier(this.ofId.getNamespace(),
                    "textures/" + type + "/" + sub + "varied/" + name + n + ".png");
        }

        @Override
        public String toString()
        {
            return "TextureEntry{" +
                    "type='" + type + '\'' +
                    ", sub='" + sub + '\'' +
                    ", name='" + name + '\'' +
                    ", n='" + n + '\'' +
                    ", ofId=" + ofId +
                    '}';
        }
    }
}
