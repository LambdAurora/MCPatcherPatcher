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

import me.lambdaurora.mcpatcherpatcher.utils.BiomeUtils;
import org.aperlambda.lambdacommon.Identifier;
import org.aperlambda.lambdacommon.utils.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * Represents a set of utilities to parse the MCPatcher format.
 *
 * @author LambdAurora
 * @version 1.0.0
 * @since 1.0.0
 */
public final class MCPatcherParser
{
    private MCPatcherParser()
    {
        throw new UnsupportedOperationException("MCPatcherParser only contains static definitions.");
    }

    public static @NotNull List<Integer> parseIntList(@Nullable String string)
    {
        if (string == null)
            return Collections.emptyList();

        List<Integer> result = new ArrayList<>();

        List<String> tokens = tokenize(string, " ,");
        for (String token : tokens) {
            if (token.contains("-")) {
                String[] rangeStr = token.split("-");
                if (rangeStr.length == 2) {
                    int min = parseInt(rangeStr[0], -1);
                    int max = parseInt(rangeStr[0], -1);
                    if (min != -1 && max != -1 && min <= max) {
                        for (int i = min; i <= max; i++) {
                            result.add(i);
                        }
                    }
                }
            } else {
                int i = parseInt(token, -1);
                if (i != -1)
                    result.add(i);
            }
        }

        return result;
    }

    public static @NotNull List<Pair<Integer, Integer>> parseIntRangeList(@Nullable String input)
    {
        if (input == null)
            return Collections.emptyList();

        List<Pair<Integer, Integer>> result = new ArrayList<>();

        List<String> tokens = tokenize(input, " ,");
        for (String token : tokens) {
            Pair<Integer, Integer> range = parseIntRange(token);
            if (range != null)
                result.add(range);
        }

        return result;
    }

    public static @Nullable Pair<Integer, Integer> parseIntRange(@Nullable String input)
    {
        if (input == null)
            return null;

        String[] parts = input.split("-");
        if (parts.length != 2)
            return null;

        int min = parseInt(parts[0], -1);
        int max = parseInt(parts[1], -1);

        if (min < 0 || max < 0)
            return null;

        return Pair.of(min, max);
    }

    public static @NotNull Pair<Boolean, List<Identifier>> parseBiomeList(@Nullable String input)
    {
        List<Identifier> result = new ArrayList<>();
        if (input == null)
            return Pair.of(false, result);

        boolean negative = false;
        if (input.startsWith("!")) {
            negative = true;
            input = input.substring(1);
        }

        List<String> tokens = tokenize(input, " ,");
        for (String token : tokens) {
            result.add(BiomeUtils.parseBiome(token));
        }

        return Pair.of(negative, result);
    }

    public static Optional<Boolean> parseBoolean(@Nullable String input)
    {
        if (input == null)
            return Optional.empty();

        if (input.equalsIgnoreCase("true"))
            return Optional.of(true);
        else if (input.equalsIgnoreCase("false"))
            return Optional.of(false);

        return Optional.empty();
    }

    private static int parseInt(@NotNull String input, int defaultValue)
    {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private static @NotNull List<String> tokenize(@NotNull String input, @NotNull String delim)
    {
        StringTokenizer tokenizer = new StringTokenizer(input, delim);
        List<String> result = new ArrayList<>();

        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            result.add(token);
        }

        return result;
    }

    public static Number toTickTime(@NotNull String time)
    {
        String[] parts = time.split(":");
        if (parts.length != 2)
            return null;
        int h = Integer.parseInt(parts[0]);
        int m = Integer.parseInt(parts[1]);
        int minutes = (h + (h >= 6 ? -6 : 18)) * 60 + m;
        return minutes * 1000 / 60;
    }

    public static Number normalizeTickTime(int tickTime)
    {
        while (tickTime < 0) {
            tickTime += 24000;
        }
        while (tickTime > 24000) {
            tickTime -= 24000;
        }
        return tickTime;
    }
}
