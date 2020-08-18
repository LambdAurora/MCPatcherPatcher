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

package me.lambdaurora.mcpatcherpatcher.fabric.util;

import org.aperlambda.lambdacommon.Identifier;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an utility class for identifiers.
 *
 * @author LambdAurora
 * @version 1.0.0
 * @since 1.0.0
 */
public final class IdentifierUtils
{
    private IdentifierUtils()
    {
        throw new UnsupportedOperationException("IdentifierUtils only contains static definitions.");
    }

    public static @NotNull Identifier fromMc(@NotNull net.minecraft.util.Identifier mc)
    {
        return new Identifier(mc.getNamespace(), mc.getPath());
    }

    public static @NotNull net.minecraft.util.Identifier toMc(@NotNull Identifier id)
    {
        return new net.minecraft.util.Identifier(id.getNamespace(), id.getName());
    }
}
