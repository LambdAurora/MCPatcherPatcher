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

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Represents the ZIP resource output accessor.
 * <p>
 * This is write-only and read-only to an extent.
 *
 * @author LambdAurora
 * @version 1.0.0
 * @since 1.0.0
 */
public class ZipOutputAccessor implements ResourceAccessor
{
    private final Map<ResourceType, List<Identifier>> resources = new HashMap<>();
    private final ZipOutputStream                     zipOut;

    public ZipOutputAccessor(@NotNull ZipOutputStream zipOut)
    {
        this.zipOut = zipOut;
    }

    @Override
    public boolean has(@NotNull ResourceType type, @NotNull Identifier identifier)
    {
        return false;
    }

    @Override
    public @NotNull Stream<Identifier> searchIn(@NotNull ResourceType type, @NotNull Identifier parent)
    {
        return Stream.<Identifier>builder().build();
    }

    @Override
    public @Nullable InputStream getInputStream(@NotNull String path)
    {
        return null;
    }

    @Override
    public @NotNull Collection<String> getNamespaces(@NotNull ResourceType type)
    {
        return this.resources.getOrDefault(type, new ArrayList<>())
                .stream().map(Identifier::getNamespace)
                .distinct().collect(Collectors.toList());
    }

    @Override
    public boolean put(@NotNull ResourceType type, @NotNull Identifier identifier, @NotNull byte[] out)
    {
        boolean result = this.put(ResourceAccessor.asPath(type, identifier), out);

        if (result) {
            List<Identifier> identifiers = this.resources.computeIfAbsent(type, k -> new ArrayList<>());
            identifiers.add(identifier);
        }

        return result;
    }

    @Override
    public boolean put(@NotNull String path, @NotNull byte[] out)
    {
        ZipEntry entry = new ZipEntry(path);
        try {
            this.zipOut.putNextEntry(entry);

            this.zipOut.write(out, 0, out.length);
            this.zipOut.closeEntry();

            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
