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

import me.lambdaurora.mcpatcherpatcher.ErrorType;
import me.lambdaurora.mcpatcherpatcher.fs.ResourceAccessor;
import org.aperlambda.lambdacommon.Identifier;
import org.aperlambda.lambdacommon.utils.Nameable;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Represents a converter.
 *
 * @author LambdAurora
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class Converter implements Nameable
{
    protected final ResourceAccessor input;
    protected final ResourceAccessor output;

    public Converter(@NotNull ResourceAccessor input, @NotNull ResourceAccessor output)
    {
        this.input = input;
        this.output = output;
    }

    /**
     * Converts the resource pack using the format associated to this converter to a new format.
     *
     * @return A map of failed conversions and their error type.
     */
    public abstract @NotNull Map<Identifier, ErrorType> convert();
}
