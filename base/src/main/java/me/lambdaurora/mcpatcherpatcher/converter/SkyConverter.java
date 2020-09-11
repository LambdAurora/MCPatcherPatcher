package me.lambdaurora.mcpatcherpatcher.converter;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.lambdaurora.mcpatcherpatcher.ErrorType;
import me.lambdaurora.mcpatcherpatcher.ResourceType;
import me.lambdaurora.mcpatcherpatcher.fs.ResourceAccessor;
import me.lambdaurora.mcpatcherpatcher.image.BasicImage;
import me.lambdaurora.mcpatcherpatcher.image.ImageProvider;
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
    public static final String  FABRICSKYBOXES_PARENT = "fabricskyboxes/sky";
    public static final String  SKY_PARENT            = "optifine/sky";
    public static final Pattern SKY_PATTERN           = Pattern.compile("optifine/sky/(?<world>\\w+)/(?<name>\\w+).properties$");

    public SkyConverter(@NotNull ResourceAccessor input, @NotNull ResourceAccessor output)
    {
        super(input, output);
    }

    @Override
    public @NotNull Map<Identifier, ErrorType> convert(@NotNull ImageProvider imageProvider)
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

                                Identifier fsbId = new Identifier(id.getNamespace(), String.format("%s/%s.json", FABRICSKYBOXES_PARENT, name));

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
                                Identifier textureId = new Identifier(id.getNamespace(), parent.getName() + String.format("/%s", world) + properties.getProperty("source")
                                        .replaceFirst("\\.", ""));

                                InputStream textureInputStream = this.input.getInputStream(ResourceType.ASSETS, textureId);
                                if (textureInputStream == null) {
                                    failed.put(textureId, ErrorType.INPUTSTREAM_IO);
                                    return;
                                }

                                BasicImage textureImage;
                                try {
                                    textureImage = imageProvider.readImage(textureInputStream);
                                } catch (IOException e) {
                                    failed.put(textureId, ErrorType.INPUTSTREAM_IO);
                                    return;
                                }

                                this.convert(fsbId, textureId, textureImage, properties);
                            }
                        }));

        return failed;
    }

    /**
     * Converts one MCPatcher file to FSB format.
     *
     * @param fsbId        The FSB metadata file identifier.
     * @param textureId    The texture file identifier.
     * @param textureImage The texture BasicImage
     * @param properties   The MCPatcher properties file.
     */
    private void convert(@NotNull Identifier fsbId, @NotNull Identifier textureId, @NotNull BasicImage textureImage, @NotNull Properties properties)
    {
        if (properties.size() == 0)
            return;
        JsonObject json = null;
        if (properties.size() > 1) {
            json = new JsonObject();
            processSkyboxTexture(json, textureId, textureImage);

            int startFadeIn = MCPatcherParser.toTickTime(properties.getProperty("startFadeIn")).intValue();
            int endFadeIn = MCPatcherParser.toTickTime(properties.getProperty("endFadeIn")).intValue();
            int endFadeOut = MCPatcherParser.toTickTime(properties.getProperty("endFadeOut")).intValue();
            int startFadeOut;
            if (properties.containsKey("startFadeOut")) {
                startFadeOut = MCPatcherParser.toTickTime(properties.getProperty("startFadeOut")).intValue();
            } else {
                startFadeOut = endFadeOut - (endFadeIn - startFadeIn);
                if (startFadeIn <= startFadeOut && endFadeIn >= startFadeOut) {
                    startFadeOut = endFadeOut;
                }
            }
            json.addProperty("startFadeIn", MCPatcherParser.normalizeTickTime(startFadeIn));
            json.addProperty("endFadeIn", MCPatcherParser.normalizeTickTime(endFadeIn));
            json.addProperty("startFadeOut", MCPatcherParser.normalizeTickTime(startFadeOut));
            json.addProperty("endFadeOut", MCPatcherParser.normalizeTickTime(endFadeOut));

            if (properties.containsKey("speed")) {
                json.addProperty("transitionSpeed", Float.parseFloat(properties.getProperty("speed")));
            }

            if (properties.containsKey("biomes")) {
                String[] biomes = properties.getProperty("biomes").split(" ");
                if (biomes.length == 1) {
                    json.addProperty("biomes", biomes[0]);
                } else {
                    JsonArray jsonBiomes = new JsonArray();
                    for (String biome : biomes) {
                        jsonBiomes.add(biome);
                    }
                    json.add("biomes", jsonBiomes);
                }
            }
            //FSB dimensions default is overworld, how should I check for existing json?
        }

        if (json == null)
            return;

        String res = LambdaConstants.GSON_PRETTY.toJson(json);
        this.output.put(ResourceType.ASSETS, fsbId, res.getBytes());
    }

    private void processSkyboxTexture(@NotNull JsonObject json, @NotNull Identifier textureId, @NotNull BasicImage textureImage)
    {
        String textureName = textureId.getName().substring(textureId.getName().lastIndexOf("/") + 1, textureId.getName().lastIndexOf("."));

        int scale = textureImage.getHeight() / 2;

        BasicImage bottom = textureImage.getSubImage(0, 0, scale, scale);
        BasicImage top = textureImage.getSubImage(scale, 0, scale, scale);
        BasicImage west = textureImage.getSubImage(scale * 2, 0, scale, scale);
        BasicImage north = textureImage.getSubImage(0, scale, scale, scale);
        BasicImage east = textureImage.getSubImage(scale, scale, scale, scale);
        BasicImage south = textureImage.getSubImage(scale * 2, scale, scale, scale);

        processFaceTexture(json, textureId, textureName, "top", top);
        processFaceTexture(json, textureId, textureName, "bottom", bottom);
        processFaceTexture(json, textureId, textureName, "north", north);
        processFaceTexture(json, textureId, textureName, "south", south);
        processFaceTexture(json, textureId, textureName, "east", east);
        processFaceTexture(json, textureId, textureName, "west", west);

        System.out.println("Processed: " + textureName);
    }

    private void processFaceTexture(@NotNull JsonObject json, @NotNull Identifier textureId, @NotNull String textureName, @NotNull String face, @NotNull BasicImage texture)
    {
        Identifier faceId = new Identifier(textureId.getNamespace(), String.format("%s/%s.png", FABRICSKYBOXES_PARENT, String.format("%s_%s", textureName, face)));
        this.output.put(ResourceType.ASSETS, faceId, texture.getBytes());
        json.addProperty(String.format("texture_%s", face), faceId.toString());
    }

    @Override
    public @NotNull String getName()
    {
        return "Sky";
    }
}
