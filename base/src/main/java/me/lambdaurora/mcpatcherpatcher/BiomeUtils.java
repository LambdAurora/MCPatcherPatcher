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

package me.lambdaurora.mcpatcherpatcher;

import me.lambdaurora.mcpatcherpatcher.Identifiers;
import org.aperlambda.lambdacommon.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents biome-related utilities.
 * <p>
 * This only exists to fix legacy formatting from MCPatcher/OptiFine.
 *
 * @author LambdAurora
 * @version 1.0.0
 * @since 1.0.0
 */
public final class BiomeUtils
{
    private static final Map<String, Identifier> BIOMES = new HashMap<>();

    private BiomeUtils()
    {
        throw new UnsupportedOperationException("BiomeUtils only contains static definitions");
    }

    public static @NotNull Identifier parseBiome(@NotNull String biome)
    {
        try {
            return new Identifier(biome); // Congrats, it's valid!
        } catch (IllegalArgumentException e) {
            // Oh shit, it's the obsolete format, STOP HOLDING TO THE PAST
            Identifier biomeId = BIOMES.get(biome);
            if (biomeId == null) {
                String name = biome.replace("+", "");
                if (biome.endsWith("+"))
                    name = "modified_" + name;

                int i = 0;
                while (i < name.length()) {
                    char c = name.charAt(i);
                    if (Character.isUpperCase(c)) {
                        String prefix = "_";
                        if (i == 0)
                            prefix = "";
                        String begin = name.substring(0, i);
                        name = begin + prefix + Character.toLowerCase(c) + name.substring(i + 1);
                    }
                    i++;
                }

                biomeId = Identifiers.mc(name);
            }
            return biomeId;
        }
    }

    static {
        BIOMES.put("Jungle", Identifiers.mc("jungle"));
        BIOMES.put("JungleHills", Identifiers.mc("jungle_hills"));
    }
}
