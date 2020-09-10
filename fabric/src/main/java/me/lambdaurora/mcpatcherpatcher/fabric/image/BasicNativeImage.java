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

package me.lambdaurora.mcpatcherpatcher.fabric.image;

import me.lambdaurora.mcpatcherpatcher.image.BasicImage;
import net.minecraft.client.texture.NativeImage;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * Represents a basic image using NativeImage as implementation.
 *
 * @author LambdAurora
 * @version 1.0.0
 * @since 1.0.0
 */
public class BasicNativeImage implements BasicImage
{
    private final NativeImage image;

    public BasicNativeImage(@NotNull NativeImage image)
    {
        this.image = image;
    }

    @Override
    public int getWidth()
    {
        return this.image.getWidth();
    }

    @Override
    public int getHeight()
    {
        return this.image.getHeight();
    }

    @Override
    public int getPixelColor(int x, int y)
    {
        return this.image.getPixelColor(x, y);
    }

    @Override
    public void setPixelColor(int x, int y, int color)
    {
        this.image.setPixelColor(x, y, color);
    }

    @Override
    public byte[] getBytes()
    {
        try {
            return this.image.getBytes();
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public @NotNull BasicImage getSubImage(int x, int y, int width, int height)
    {
        BasicNativeImage subImage = new BasicNativeImage(new NativeImage(width, height, false));

        for (int iY = 0; iY < height && (y + iY) < this.getHeight(); iY++) {
            for (int iX = 0; iX < width && (x + iX) < this.getWidth(); iX++) {
                subImage.setPixelColor(iX, iY, this.getPixelColor(x + iX, y + iY));
            }
        }

        return subImage;
    }

    @Override
    public void close()
    {
        this.image.close();
    }
}
