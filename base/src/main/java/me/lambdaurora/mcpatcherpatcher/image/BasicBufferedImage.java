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

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Represents a basic image using BufferedImage as implementation.
 *
 * @author LambdAurora
 * @version 1.0.0
 * @since 1.0.0
 */
public class BasicBufferedImage implements BasicImage
{
    private final BufferedImage image;

    public BasicBufferedImage(@NotNull BufferedImage image)
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
        return this.image.getRGB(x, y);
    }

    @Override
    public void setPixelColor(int x, int y, int color)
    {
        this.image.setRGB(x, y, color);
    }

    @Override
    public byte[] getBytes()
    {
        try {
            return toByteArrayAutoClosable(this.image, "png");
        } catch (IOException e) {
            return new byte[0];
        }
    }

    @Override
    public @NotNull BasicImage getSubImage(int x, int y, int width, int height)
    {
        return new BasicBufferedImage(this.image.getSubimage(x, y, width, height));
    }

    @Override
    public void close()
    {
    }

    /**
     * @author FlashyReese
     */
    private static byte[] toByteArrayAutoClosable(@NotNull BufferedImage image, @NotNull String imageType) throws IOException
    {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            ImageIO.write(image, imageType, out); // Technically we don't need a imageType param, I'm pretty sure all mc resource packs are png only.
            return out.toByteArray();
        }
    }
}
