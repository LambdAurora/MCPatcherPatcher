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

package me.lambdaurora.mcpatcherpatcher.fs;

import me.lambdaurora.mcpatcherpatcher.ResourceType;
import org.aperlambda.lambdacommon.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Represents the ZIP resource input accessor.
 * <p>
 * This is read-only.
 *
 * @author LambdAurora
 * @version 1.0.0
 * @since 1.0.0
 */
public class ZipAccessor implements ResourceAccessor
{
    private final ZipFile zip;

    public ZipAccessor(@NotNull File file) throws IOException
    {
        this.zip = new ZipFile(file);
    }

    @Override
    public boolean has(@NotNull ResourceType type, @NotNull Identifier identifier)
    {
        return this.zip.getEntry(ResourceAccessor.asPath(type, identifier)) != null;
    }

    @Override
    public @Nullable InputStream getInputStream(@NotNull String path)
    {
        ZipEntry entry = this.zip.getEntry(path);
        if (entry == null)
            return null;
        try {
            return this.zip.getInputStream(entry);
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public @NotNull Stream<Identifier> searchIn(@NotNull ResourceType type, @NotNull Identifier parent)
    {
        return this.zip.stream().filter(entry -> entry.getName().startsWith(type.getName() + "/") && !entry.isDirectory())
                .map(entry -> {
                    String name = entry.getName().replace(type.getName() + "/", "");
                    int first = name.indexOf("/");
                    if (first == name.length() - 1)
                        return Identifier.IDENTIFIER_INVALID;
                    return new Identifier(name.substring(0, first), name.substring(first + 1));
                })
                .filter(id -> !id.equals(Identifier.IDENTIFIER_INVALID))
                .filter(id -> id.getNamespace().equals(parent.getNamespace()))
                .filter(id -> id.getName().startsWith(parent.getName()));
    }

    @Override
    public @NotNull List<String> getNamespaces(@NotNull ResourceType type)
    {
        return this.zip.stream().filter(entry -> entry.getName().startsWith(type.getName() + "/") && entry.isDirectory())
                .map(entry -> entry.getName().split("/"))
                .filter(entry -> entry.length == 2)
                .map(entry -> entry[1])
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public boolean put(@NotNull String path, @NotNull byte[] out)
    {
        // Not supported.
        return false;
    }
}
