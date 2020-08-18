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

import me.lambdaurora.mcpatcherpatcher.converter.CETConverter;
import me.lambdaurora.mcpatcherpatcher.converter.Converter;
import me.lambdaurora.mcpatcherpatcher.converter.RETConverter;
import me.lambdaurora.mcpatcherpatcher.fs.ResourceAccessor;
import me.lambdaurora.mcpatcherpatcher.fs.ZipAccessor;
import me.lambdaurora.mcpatcherpatcher.fs.ZipOutputAccessor;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.zip.ZipOutputStream;

public class MCPatcherPatcher
{
    private final List<BiFunction<ResourceAccessor, ResourceAccessor, Converter>> converters = new ArrayList<>();

    public void init()
    {
        this.converters.add(CETConverter::new);
        this.converters.add(RETConverter::new);
    }

    public void convert(@NotNull ResourceAccessor input, @NotNull ResourceAccessor output)
    {
        input.copy(output, "pack.mcmeta");
        input.copy(output, "pack.png");
        this.converters.forEach(f -> {
            Converter converter = f.apply(input, output);
            System.out.println("Applying " + converter.getName() + " conversion.");
            converter.convert();
        });
    }

    public static void main(String[] args)
    {
        MCPatcherPatcher patcher = new MCPatcherPatcher();
        patcher.init();

        File resPack = new File("Creature Variety 1.8.6.zip");
        if (!resPack.exists())
            System.out.println("no");
        ResourceAccessor input;
        try {
            input = new ZipAccessor(resPack);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        File outPack = new File("creature_variety_noof.zip");
        /*if (!outPack.exists() && !outPack.mkdirs()) {
            System.out.println("Cannot mkdirs outPack");
            return;
        }*/
        ResourceAccessor out;
        try {
            out = new ZipOutputAccessor(new ZipOutputStream(new FileOutputStream(outPack)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

        patcher.convert(input, out);
    }
}
