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

package me.lambdaurora.mcpatcherpatcher;

import me.lambdaurora.mcpatcherpatcher.converter.CETConverter;
import me.lambdaurora.mcpatcherpatcher.converter.Converter;
import me.lambdaurora.mcpatcherpatcher.converter.RETConverter;
import me.lambdaurora.mcpatcherpatcher.converter.SkyConverter;
import me.lambdaurora.mcpatcherpatcher.fs.ResourceAccessor;
import me.lambdaurora.mcpatcherpatcher.fs.ZipAccessor;
import me.lambdaurora.mcpatcherpatcher.fs.ZipOutputAccessor;
import me.lambdaurora.mcpatcherpatcher.image.ImageProvider;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.zip.ZipOutputStream;

public class MCPatcherPatcher
{
    private final List<BiFunction<ResourceAccessor, ResourceAccessor, Converter>> converters = new ArrayList<>();
    private final ImageProvider imageProvider;

    public MCPatcherPatcher(@NotNull ImageProvider imageProvider)
    {
        this.imageProvider = imageProvider;
        this.init();
    }

    private void init()
    {
        this.converters.add(CETConverter::new);
        this.converters.add(RETConverter::new);
        this.converters.add(SkyConverter::new);
    }

    public void convert(@NotNull ResourceAccessor input, @NotNull ResourceAccessor output)
    {
        input.copy(output, "pack.mcmeta");
        input.copy(output, "pack.png");
        this.converters.forEach(f -> {
            Converter converter = f.apply(input, output);
            System.out.println("Applying " + converter.getName() + " conversion.");
            converter.convert(imageProvider);
            if (converter instanceof Closeable)
                ((Closeable) converter).close();
        });
    }

    public void convert(@NotNull File inputFile, @NotNull File outputFile) throws IOException
    {
        if (!inputFile.exists())
            System.out.println("Input File does not exist!");
        ResourceAccessor input;
        try {
            input = new ZipAccessor(inputFile);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        ResourceAccessor out;
        ZipOutputStream zipOutputStream;
        try {
            zipOutputStream = new ZipOutputStream(new FileOutputStream(outputFile));
            out = new ZipOutputAccessor(zipOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        this.convert(input, out);

        zipOutputStream.close();
    }

}
