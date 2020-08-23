package me.lambdaurora.mcpatcherpatcher.converter;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.lambdaurora.mcpatcherpatcher.ErrorType;
import me.lambdaurora.mcpatcherpatcher.ResourceType;
import me.lambdaurora.mcpatcherpatcher.fs.ResourceAccessor;
import me.lambdaurora.mcpatcherpatcher.mcpatcher.MCPatcherParser;
import org.aperlambda.lambdacommon.Identifier;
import org.aperlambda.lambdacommon.LambdaConstants;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents the converter for Custom Sky Boxes.
 * <p>
 * Converts to the fabricskyboxes (FSB) format.
 *
 * @author FlashyReese
 * @version 1.0.0
 * @since 1.0.0
 */
public class SkyConverter extends Converter
{
    public static final String  SKY_PARENT  = "optifine/sky";

    public static final Pattern SKY_PATTERN = Pattern.compile("optifine/sky/(?<world>\\w+)/(?<name>\\w+).properties$");

    public SkyConverter(@NotNull ResourceAccessor input, @NotNull ResourceAccessor output)
    {
        super(input, output);
    }

    @Override
    public @NotNull Map<Identifier, ErrorType> convert()
    {
        Map<Identifier, ErrorType> failed = new HashMap<>();

        this.input.getNamespaces(ResourceType.ASSETS).stream()
                .map(namespace -> new Identifier(namespace, SKY_PARENT))
                .forEach(parent -> this.input.searchIn(ResourceType.ASSETS, parent)
                        .filter(id -> id.getName().endsWith(".properties"))
                        .forEach(id -> {
                            Matcher matcher = SKY_PATTERN.matcher(id.getName());
                            if (matcher.find()) {
                                String world = matcher.group("world");
                                String name = matcher.group("name");

                                if (world == null || name == null)
                                    return;

                                Identifier fsbId = new Identifier(id.getNamespace(),
                                        "fabricskyboxes/sky/" + world + "/" + name + ".json");

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
                                }

                                // Fixme: bad idea I'm just yeeting code
                                String texture = new Identifier(id.getNamespace(), id.getName().substring(0,
                                        id.getName().lastIndexOf("/") + 1) + properties.getProperty("source")
                                        .replaceFirst("\\./", "")).toString();

                                this.convert(fsbId, texture, properties);
                            }
                        }));

        return failed;
    }

    /**
     * Converts one MCPatcher file to FSB format.
     *  @param fsbId The FSB metadata file identifier.
     * @param texture The Texture Location.
     * @param properties The MCPatcher properties file.
     */
    private void convert(@NotNull Identifier fsbId, @NotNull String texture, @NotNull Properties properties)
    {
        if (properties.size() == 0)
            return;
        JsonObject json = null;
        if (properties.size() > 1) {
            json = new JsonObject();
            json.addProperty("texture", texture);
            convertPropertiesToJson(json, properties);
        }

        if (json == null)
            return;

        String res = LambdaConstants.GSON_PRETTY.toJson(json);
        this.output.put(ResourceType.ASSETS, fsbId, res.getBytes());
    }

    private void convertPropertiesToJson(@NotNull JsonObject json, @NotNull Properties properties)
    {
        // Very cursed
        json.addProperty("startFadeIn", MCPatcherParser.getTimeTick(properties.getProperty("startFadeIn")));
        json.addProperty("endFadeIn", MCPatcherParser.getTimeTick(properties.getProperty("endFadeIn")));
        json.addProperty("endFadeOut", MCPatcherParser.getTimeTick(properties.getProperty("endFadeOut")));
        if(properties.getProperty("blend") != null)
            json.addProperty("blend", properties.getProperty("blend"));
        if(properties.getProperty("rotate") != null)
            json.addProperty("rotate", Boolean.parseBoolean(properties.getProperty("rotate")));
        if(properties.getProperty("speed") != null)
            json.addProperty("speed", Float.parseFloat(properties.getProperty("speed")));
        if(properties.getProperty("axis") != null){
            JsonArray axis = new JsonArray();
            for (String a: properties.getProperty("axis").split(" ")){
                axis.add(Float.parseFloat(a));
            }
            json.add("axis", axis);
        }
        if(properties.getProperty("weather") != null)
            json.addProperty("weather", properties.getProperty("weather"));
        if(properties.getProperty("biomes") != null){
            JsonArray biomes = new JsonArray();
            for (String biome: Objects.requireNonNull(properties.getProperty("biomes").split(" "))){
                biomes.add(biome);
            }
            json.add("biomes", biomes);
        }
        if(properties.getProperty("heights") != null){
            JsonArray heights = new JsonArray();
            for (String height: properties.getProperty("heights").split(" ")){
                heights.add(height);
            }
            json.add("heights", heights);
        }
        if(properties.getProperty("transition") != null)
            json.addProperty("transition", properties.getProperty("transition"));
    }

    @Override
    public @NotNull String getName()
    {
        return "Sky";
    }
}
