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

package me.lambdaurora.mcpatcherpatcher.fabric.fs;

import me.lambdaurora.mcpatcherpatcher.ResourceType;
import me.lambdaurora.mcpatcherpatcher.fabric.util.IdentifierUtils;
import me.lambdaurora.mcpatcherpatcher.fs.ResourceAccessor;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import org.aperlambda.lambdacommon.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.stream.Stream;

/**
 * Represents a resource accessor using Minecraft's resource manager.
 */
public class ResourceManagerAccessor implements ResourceAccessor
{
    private final ResourceManager resourceManager;

    public ResourceManagerAccessor(@NotNull ResourceManager resourceManager)
    {
        this.resourceManager = resourceManager;
    }

    @Override
    public boolean has(@NotNull ResourceType type, @NotNull Identifier identifier)
    {
        return this.resourceManager.containsResource(IdentifierUtils.toMc(identifier));
    }

    @Override
    public @NotNull Stream<Identifier> searchIn(@NotNull ResourceType type, @NotNull Identifier parent)
    {
        return this.resourceManager.findResources(parent.getName(), path -> {
            for (int i = 0; i < path.length(); ++i) {
                if (!net.minecraft.util.Identifier.isPathCharacterValid(path.charAt(i))) {
                    return false;
                }
            }

            return true;
        }).stream()
                .map(IdentifierUtils::fromMc)
                .filter(id -> id.getNamespace().equals(parent.getNamespace()));
    }

    @Override
    public @Nullable InputStream getInputStream(@NotNull ResourceType type, @NotNull Identifier identifier)
    {
        try {
            Resource resource = this.resourceManager.getResource(IdentifierUtils.toMc(identifier));
            if (resource == null)
                return null;
            return resource.getInputStream();
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public @Nullable InputStream getInputStream(@NotNull String path)
    {
        return null;
    }

    @Override
    public @NotNull Collection<String> getNamespaces(@NotNull ResourceType type)
    {
        return this.resourceManager.getAllNamespaces();
    }

    @Override
    public boolean put(@NotNull String path, @NotNull byte[] out)
    {
        return false;
    }
}
