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

package me.lambdaurora.mcpatcherpatcher.image;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;

/**
 * Represents an image provider.
 *
 * @author LambdAurora
 * @version 1.0.0
 * @since 1.0.0
 */
public interface ImageProvider
{
    /**
     * Reads an image from an input stream.
     *
     * @param inputStream The input stream.
     * @return A non-null image if read successfully.
     * @throws IOException If the image read failed.
     */
    @NotNull BasicImage readImage(@NotNull InputStream inputStream) throws IOException;
}
