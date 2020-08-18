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

package me.lambdaurora.mcpatcherpatcher.mcpatcher;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.aperlambda.lambdacommon.Identifier;
import org.aperlambda.lambdacommon.utils.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Represents a CET rule.
 *
 * @author LambdAurora
 * @version 1.0.0
 * @since 1.0.0
 */
public class RandomEntityRule
{
    private final int                             index;
    private final Identifier                      baseId;
    private final List<Identifier>                textures;
    private final List<Integer>                   weights;
    private final String                          nameRegex;
    private final Pair<Boolean, List<Identifier>> biomes;
    private final List<Pair<Integer, Integer>>    heights;
    private final Optional<Boolean>               baby;

    public RandomEntityRule(int index, @NotNull Identifier baseId, @NotNull String valTextures, @NotNull Properties properties)
    {
        this.index = index;
        this.baseId = baseId;
        this.textures = MCPatcherParser.parseIntList(valTextures).stream().map(i -> new Identifier(baseId.getNamespace(), baseId.getName() + i + ".png"))
                .collect(Collectors.toList());
        this.weights = MCPatcherParser.parseIntList(properties.getProperty("weights." + index));
        String nameRegex = properties.getProperty("name." + index);
        if (nameRegex != null) {
            if (nameRegex.matches("$i?pattern:")) {
                // Just yeet pattern and make it a real regex
                nameRegex = nameRegex.substring(nameRegex.indexOf(":") + 1);
                nameRegex = nameRegex.replaceAll("\\*", ".*")
                        .replaceAll("\\?", ".?");
            } else if (nameRegex.startsWith("iregex:")) {
                nameRegex = nameRegex.substring(nameRegex.indexOf(":") + 1);
                nameRegex = "(?i)" + nameRegex;
            } else if (nameRegex.startsWith("regex:"))
                nameRegex = nameRegex.substring(nameRegex.indexOf(":") + 1);
        }
        this.nameRegex = nameRegex;
        this.biomes = MCPatcherParser.parseBiomeList(properties.getProperty("biomes." + index));
        this.heights = MCPatcherParser.parseIntRangeList(properties.getProperty("heights." + index));
        this.baby = MCPatcherParser.parseBoolean(properties.getProperty("baby." + index));
    }

    /**
     * Returns the converted rule to the VMT format.
     *
     * @return The JSON.
     */
    public @NotNull JsonObject toJson()
    {
        return this.getNameJson();
    }

    public @NotNull JsonObject getNameJson()
    {
        if (this.nameRegex == null)
            return this.getBiomesJson();

        JsonObject json = new JsonObject();
        json.addProperty("type", "varied-mobs:name");
        json.addProperty("regex", this.nameRegex);
        json.add("value", this.getBiomesJson());

        return json;
    }

    public @NotNull JsonObject getBiomesJson()
    {
        if (this.biomes.value.size() == 0)
            return this.getPickJson();

        JsonObject json = new JsonObject();
        json.addProperty("type", "varied-mobs:biome");

        JsonArray biomes = new JsonArray();
        this.biomes.value.forEach(id -> biomes.add(id.toString()));
        json.add("biomes", biomes);

        if (this.biomes.key)
            json.addProperty("negate", true);

        json.add("value", this.getBabyJson());

        return json;
    }

    // Borked
    public @NotNull JsonObject getHeightsJson()
    {
        JsonObject babyJson = this.getBabyJson();
        if (this.heights.size() != 0) {
            JsonObject json = new JsonObject();
            json.addProperty("type", "varied-mobs:y-prop");
            JsonArray positions = new JsonArray();
            int rangeCount = 0;
            for (Pair<Integer, Integer> range : this.heights) {
                positions.add(range.key);
            }
            json.add("positions", positions);
            JsonArray choices = new JsonArray();

            return json;
        }
        return babyJson;
    }

    public @NotNull JsonObject getBabyJson()
    {
        return this.baby.map(baby -> {
            JsonObject json = new JsonObject();

            json.addProperty("type", "varied-mobs:baby");
            if (!baby)
                json.addProperty("negate", true);
            json.add("value", this.getPickJson());

            return json;
        }).orElse(this.getPickJson());
    }

    public @NotNull JsonObject getPickJson()
    {
        JsonObject json = new JsonObject();

        json.addProperty("type", "varied-mobs:pick");

        if (this.weights.size() > 0) {
            JsonArray weights = new JsonArray();
            this.weights.forEach(weights::add);
            json.add("weights", weights);
        }

        JsonArray choices = new JsonArray();
        // @TODO textures id move
        this.textures.forEach(id -> choices.add(id.toString()));
        json.add("choices", choices);

        return json;
    }
}
