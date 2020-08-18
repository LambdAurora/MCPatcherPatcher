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

package me.lambdaurora.mcpatcherpatcher.fabric.resource;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import me.lambdaurora.mcpatcherpatcher.fabric.util.IdentifierUtils;
import me.lambdaurora.mcpatcherpatcher.fs.ResourceAccessor;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents the virtual resource pack of MCPatcherPatcher.
 *
 * @author LambdAurora
 * @version 1.0.0
 * @since 1.0.0
 */
public class MCPPResourcePack implements ResourcePack, ResourceAccessor
{
    private final List<String>                     namespaces = new ArrayList<>();
    private final Object2ObjectMap<String, byte[]> resources  = new Object2ObjectOpenHashMap<>();

    @Override
    public boolean put(@NotNull String resource, byte[] data)
    {
        Identifier id = fromPath(resource);
        if (!this.namespaces.contains(id.getNamespace())) {
            this.namespaces.add(id.getNamespace());
        }
        this.resources.put(resource, data);
        return true;
    }

    public void putText(@NotNull String resource, @NotNull String text)
    {
        this.put(resource, text.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public InputStream openRoot(String fileName) throws IOException
    {
        byte[] data;
        if ((data = this.resources.get(fileName)) != null) {
            return new ByteArrayInputStream(data);
        }
        throw new IOException("Generated resources pack has no data or alias for " + fileName);
    }

    @Override
    public @Nullable InputStream getInputStream(@NotNull String path)
    {
        if (!this.resources.containsKey(path))
            return null;
        return new ByteArrayInputStream(this.resources.get(path));
    }

    @Override
    public InputStream open(ResourceType type, Identifier id) throws IOException
    {
        if (type == ResourceType.SERVER_DATA) throw new IOException("Reading server data from MCPatcherPatcher client resource pack");
        return this.openRoot(type.getDirectory() + "/" + id.getNamespace() + "/" + id.getPath());
    }

    @Override
    public Collection<Identifier> findResources(ResourceType type, String namespace, String prefix, int maxDepth, Predicate<String> pathFilter)
    {
        if (type == ResourceType.SERVER_DATA) return Collections.emptyList();
        String start = "assets/" + namespace + "/" + prefix;
        return this.resources.keySet().stream()
                .filter(s -> s.startsWith(start) && pathFilter.test(s))
                .map(MCPPResourcePack::fromPath)
                .collect(Collectors.toList());
    }

    @Override
    public @NotNull Stream<org.aperlambda.lambdacommon.Identifier> searchIn(@NotNull me.lambdaurora.mcpatcherpatcher.ResourceType type, @NotNull org.aperlambda.lambdacommon.Identifier parent)
    {
        String parentPath = ResourceAccessor.asPath(type, parent);

        return this.resources.keySet().stream().filter(path -> path.startsWith(parentPath))
                .map(path -> {
                    int first = path.indexOf('/');
                    if (first == path.length() - 1 || first == -1)
                        return null;
                    return new org.aperlambda.lambdacommon.Identifier(path.substring(0, first), path.substring(first + 1));
                })
                .filter(Objects::nonNull);
    }

    @Override
    public boolean has(@NotNull me.lambdaurora.mcpatcherpatcher.ResourceType type, @NotNull org.aperlambda.lambdacommon.Identifier identifier)
    {
        return this.contains(toMcResourceType(type), IdentifierUtils.toMc(identifier));
    }

    @Override
    public boolean contains(ResourceType type, Identifier id)
    {
        String path = type.getDirectory() + "/" + id.getNamespace() + "/" + id.getPath();
        return this.resources.containsKey(path);
    }

    @Override
    public Set<String> getNamespaces(ResourceType type)
    {
        return new HashSet<>(this.namespaces);
    }

    @Override
    public @NotNull Collection<String> getNamespaces(@NotNull me.lambdaurora.mcpatcherpatcher.ResourceType type)
    {
        return this.getNamespaces(toMcResourceType(type));
    }

    @Override
    public <T> T parseMetadata(ResourceMetadataReader<T> metaReader) throws IOException
    {
        return null;
    }

    @Override
    public String getName()
    {
        return "MCPatcherPatcher generated resource pack";
    }

    @Override
    public void close()
    {
    }

    private static @NotNull ResourceType toMcResourceType(@NotNull me.lambdaurora.mcpatcherpatcher.ResourceType resourceType)
    {
        switch (resourceType) {
            case DATA:
                return ResourceType.SERVER_DATA;
            case ASSETS:
                return ResourceType.CLIENT_RESOURCES;
        }
        return ResourceType.CLIENT_RESOURCES;
    }

    private static Identifier fromPath(String path)
    {
        String[] split = path.split("/", 2);
        return new Identifier(split[0], split[1]);
    }
}
