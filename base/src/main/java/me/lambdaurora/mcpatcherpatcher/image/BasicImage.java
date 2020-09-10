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

package me.lambdaurora.mcpatcherpatcher.image;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a basic image.
 *
 * @author LambdAurora
 * @version 1.0.0
 * @since 1.0.0
 */
public interface BasicImage
{
    /**
     * Gets the width of the image.
     *
     * @return The width.
     */
    int getWidth();

    /**
     * Gets the height of the image.
     *
     * @return The height.
     */
    int getHeight();

    /**
     * Gets the pixel color at the specified coordinates.
     *
     * @param x The X-coordinate of the pixel.
     * @param y The Y-coordinate of the pixel.
     * @return The pixel color.
     */
    int getPixelColor(int x, int y);

    /**
     * Sets the pixel color at the specified coordinates.
     *
     * @param x     The X-coordinate of the pixel.
     * @param y     The Y-coordinate of the pixel.
     * @param color The pixel color.
     */
    void setPixelColor(int x, int y, int color);

    /**
     * Returns the bytes of the image.
     *
     * @return The bytes.
     */
    byte[] getBytes();

    /**
     * Returns a sub-image from this image.
     *
     * @param x      The X-coordinate of the start of the sub-image.
     * @param y      The Y-coordinate of the start of the sub-image.
     * @param width  The width of the sub-image.
     * @param height The height of the sub-image.
     * @return The sub-image.
     */
    @NotNull BasicImage getSubImage(int x, int y, int width, int height);

    /**
     * Free the image from memory.
     * <p>
     * Useful for implementations using native libraries/code.
     */
    void close();
}
