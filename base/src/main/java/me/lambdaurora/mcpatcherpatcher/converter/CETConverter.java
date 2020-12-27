/*
 * Copyright (c) 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.lambdaurora.mcpatcherpatcher.converter;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.lambdaurora.mcpatcherpatcher.ErrorType;
import me.lambdaurora.mcpatcherpatcher.ResourceType;
import me.lambdaurora.mcpatcherpatcher.fs.ResourceAccessor;
import me.lambdaurora.mcpatcherpatcher.image.ImageProvider;
import me.lambdaurora.mcpatcherpatcher.mcpatcher.RandomEntityRule;
import org.aperlambda.lambdacommon.Identifier;
import org.aperlambda.lambdacommon.LambdaConstants;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents the converter for Custom Entity Textures. (Like RET but with properties files)
 * <p>
 * Converts to the Varied Mob Textures (VMT) format.
 *
 * @author LambdAurora
 * @version 1.0.0
 * @since 1.0.0
 */
public class CETConverter extends Converter
{
    public static final String CET_PARENT = "optifine/random";
    public static final Pattern CET_PATTERN = Pattern.compile("optifine/random/(?<type>\\w+)/(?<sub>(?:\\w+/)+)?(?<name>\\w+)\\.properties$");

    public CETConverter(@NotNull ResourceAccessor input, @NotNull ResourceAccessor output)
    {
        super(input, output);
    }

    @Override
    public @NotNull Map<Identifier, ErrorType> convert(@NotNull ImageProvider imageProvider)
    {
        Map<Identifier, ErrorType> failed = new HashMap<>();

        this.input.getNamespaces(ResourceType.ASSETS).stream()
                .map(namespace -> new Identifier(namespace, CET_PARENT))
                .forEach(parent -> this.input.searchIn(ResourceType.ASSETS, parent)
                        .filter(id -> id.getName().endsWith(".properties"))
                        .forEach(id -> {
                            Matcher matcher = CET_PATTERN.matcher(id.getName());
                            if (matcher.find()) {
                                String type = matcher.group("type");
                                String sub = matcher.group("sub");
                                String name = matcher.group("name");

                                if (type == null || name == null)
                                    return;
                                if (sub == null)
                                    sub = "";

                                Identifier vmtId = new Identifier(id.getNamespace(),
                                        "varied/textures/" + type + "/" + sub + name + ".json");

                                InputStream inputStream = this.input.getInputStream(ResourceType.ASSETS, id);
                                if (inputStream == null) {
                                    failed.put(id, ErrorType.INPUTSTREAM_IO);
                                    return;
                                }

                                Properties properties = new Properties();
                                try {
                                    properties.load(inputStream);
                                    inputStream.close();
                                } catch (IOException e) {
                                    failed.put(id, ErrorType.PROPERTIES_READ);
                                    return;
                                } finally {
                                    try {
                                        inputStream.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }

                                List<RandomEntityRule> rules = new ArrayList<>();

                                int count = properties.size();
                                for (int i = 0; i < count; i++) {
                                    int index = i + 1;

                                    String valTextures = properties.getProperty("textures." + index);
                                    if (valTextures == null)
                                        valTextures = properties.getProperty("skins." + index);

                                    if (valTextures != null) {
                                        rules.add(new RandomEntityRule(index,
                                                new Identifier(id.getNamespace(), id.getName().replace(".properties", "")),
                                                valTextures,
                                                properties));
                                    }
                                }

                                this.convert(vmtId, rules);
                            }
                        }));

        return failed;
    }

    /**
     * Converts one MCPatcher file to VMT format. The MCPatcher file is parsed as a set of rules.
     *
     * @param vmtId The VMT metadata file identifier.
     * @param rules The rules of the MCPatcher properties file.
     */
    public void convert(@NotNull Identifier vmtId, @NotNull List<RandomEntityRule> rules)
    {
        if (rules.size() == 0)
            return;

        JsonObject json;
        if (rules.size() > 1) {
            json = new JsonObject();
            json.addProperty("type", "varied-mobs:seq");
            JsonArray choices = new JsonArray();
            rules.forEach(rule -> choices.add(rule.toJson()));
            json.add("choices", choices);
        } else {
            json = rules.get(0).toJson();
        }

        String res = LambdaConstants.GSON_PRETTY.toJson(json);
        this.output.put(ResourceType.ASSETS, vmtId, res.getBytes());
    }

    @Override
    public @NotNull String getName()
    {
        return "CET";
    }
}
