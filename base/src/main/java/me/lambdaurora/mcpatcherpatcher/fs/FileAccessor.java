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

package me.lambdaurora.mcpatcherpatcher.fs;

import me.lambdaurora.mcpatcherpatcher.ResourceType;
import org.aperlambda.lambdacommon.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents a file resource accessor.
 *
 * @author LambdAurora
 * @version 1.0.0
 * @since 1.0.0
 */
public class FileAccessor implements ResourceAccessor
{
    private final File directory;

    public FileAccessor(@NotNull File directory)
    {
        this.directory = directory.getAbsoluteFile();
    }

    @Override
    public boolean has(@NotNull ResourceType type, @NotNull Identifier identifier)
    {
        return new File(directory, ResourceAccessor.asPath(type, identifier)).exists();
    }

    @Override
    public @NotNull Stream<Identifier> searchIn(@NotNull ResourceType type, @NotNull Identifier parent)
    {
        File parentFile = new File(directory, ResourceAccessor.asPath(type, parent));

        if (!parentFile.exists() || !parentFile.isDirectory())
            return Stream.<Identifier>builder().build();

        List<Identifier> id = new ArrayList<>();

        this.searchIn(type, parentFile.getAbsoluteFile(), id);

        return id.stream();
    }

    private void searchIn(@NotNull ResourceType type, @NotNull File parent, @NotNull List<Identifier> results)
    {
        if (!parent.isDirectory()) {
            String path = parent.getPath().replace(this.directory.getPath(), "");
            if (path.length() == 0)
                return;

            if (path.startsWith(File.separator)) {
                path = path.substring(File.separator.length());
                if (path.length() == 0)
                    return;
            }

            if (!path.startsWith(type.getName() + File.separator))
                return;
            path = path.substring((type.getName() + File.separator).length());

            int first = path.indexOf(File.separator);
            if (first == path.length() - 1 || first == -1)
                return;

            results.add(new Identifier(path.substring(0, first), path.substring(first + 1).replaceAll(File.pathSeparator, "/")));
        } else {
            for (File file : parent.listFiles()) {
                this.searchIn(type, file.getAbsoluteFile(), results);
            }
        }
    }

    @Override
    public @Nullable InputStream getInputStream(@NotNull String path)
    {
        File file = new File(directory, path);
        if (!file.exists())
            return null;
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    @Override
    public @NotNull Collection<String> getNamespaces(@NotNull ResourceType type)
    {
        return Arrays.stream(new File(directory, type.getName()).listFiles()).filter(File::isDirectory).map(File::getName).collect(Collectors.toList());
    }

    @Override
    public boolean put(@NotNull String path, @NotNull byte[] out)
    {
        File file = new File(directory, path);
        if (!file.getParentFile().exists() && !file.getParentFile().mkdirs())
            return false;
        try {
            FileOutputStream outF = new FileOutputStream(file);

            outF.write(out);

            outF.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
