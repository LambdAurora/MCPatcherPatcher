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

import org.aperlambda.lambdacommon.Identifier;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an utility class for Identifiers.
 *
 * @author LambdAurora
 * @version 1.0.0
 * @since 1.0.0
 */
public final class Identifiers
{
    private Identifiers()
    {
        throw new UnsupportedOperationException("Identifiers only contains static definitions.");
    }

    /**
     * Returns an identifier in the Minecraft namespace.
     *
     * @param name The name.
     * @return The identifier.
     */
    public static @NotNull Identifier mc(@NotNull String name)
    {
        return new Identifier("minecraft", name);
    }

    public static @NotNull Identifier parseIdentifier(@NotNull String id)
    {
        try {
            return new Identifier(id);
        } catch (IllegalArgumentException e) {
            return mc(id);
        }
    }
}
