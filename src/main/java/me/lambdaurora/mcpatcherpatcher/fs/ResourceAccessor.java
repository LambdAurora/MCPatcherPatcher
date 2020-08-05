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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Stream;

/**
 * Represents an abstraction of resource access.
 * <p>
 * This allows to re-implement easily the resource pack patcher to ZIP, folder or runtime resource packs.
 *
 * @author LambdAurora
 * @version 1.0.0
 * @since 1.0.0
 */
public interface ResourceAccessor
{
    /**
     * Returns whether the specified resource exists or not.
     *
     * @param type       The resource type.
     * @param identifier The resource identifier.
     * @return True if the specified resource exists, else false.
     */
    boolean has(@NotNull ResourceType type, @NotNull Identifier identifier);

    /**
     * Returns a stream of resource identifiers present in the specified parent folder.
     *
     * @param type   The resource type.
     * @param parent The parent resource identifier.
     * @return The stream of identifiers.
     */
    @NotNull Stream<Identifier> searchIn(@NotNull ResourceType type, @NotNull Identifier parent);

    /**
     * Returns an input stream from the specified resource.
     *
     * @param type       The resource type.
     * @param identifier The resource identifier.
     * @return An input stream if the resource exists and can be opened, else null.
     * @see #getInputStream(String)
     */
    default @Nullable InputStream getInputStream(@NotNull ResourceType type, @NotNull Identifier identifier)
    {
        return this.getInputStream(asPath(type, identifier));
    }

    /**
     * Returns an input stream from the specified path.
     *
     * @param path The path to the resource.
     * @return An input stream if the resource exists and can be opened, else null.
     * @see #getInputStream(ResourceType, Identifier)
     */
    @Nullable InputStream getInputStream(@NotNull String path);

    /**
     * Returns the list of namespaces existing in the resource pack.
     *
     * @param type The resource type.
     * @return A list of namespaces.
     */
    @NotNull List<String> getNamespaces(@NotNull ResourceType type);

    /**
     * Puts the data in the specified resource.
     *
     * @param type       The resource type.
     * @param identifier The resource identifier.
     * @param out        The data.
     * @return True if the data has been successfully put, else false.
     * @see #put(String, byte[])
     */
    default boolean put(@NotNull ResourceType type, @NotNull Identifier identifier, @NotNull byte[] out)
    {
        return this.put(asPath(type, identifier), out);
    }

    /**
     * Puts the data in the specified resource.
     *
     * @param path The resource path.
     * @param out  The data.
     * @return True if the data has been successfully put, else false.
     * @see #put(ResourceType, Identifier, byte[])
     */
    boolean put(@NotNull String path, @NotNull byte[] out);

    /**
     * Copies the resource from this accessor to the specified accessor.
     *
     * @param outAccessor The output resource accessor.
     * @param type        The resource type.
     * @param identifier  The resource identifier.
     * @see #copy(ResourceAccessor, String)
     */
    default void copy(@NotNull ResourceAccessor outAccessor, @NotNull ResourceType type, @NotNull Identifier identifier)
    {
        InputStream in = this.getInputStream(type, identifier);
        if (in == null) {
            return;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            in.transferTo(out);
            outAccessor.put(type, identifier, out.toByteArray());
            out.close();
            in.close();
        } catch (IOException e) {
        }
    }

    /**
     * Copies the resource from this accessor to the specified accessor.
     *
     * @param outAccessor The output resource accessor.
     * @param path        The resource path.
     * @see #copy(ResourceAccessor, ResourceType, Identifier)
     */
    default void copy(@NotNull ResourceAccessor outAccessor, @NotNull String path)
    {
        InputStream in = this.getInputStream(path);
        if (in == null) {
            return;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            in.transferTo(out);
            outAccessor.put(path, out.toByteArray());
            out.close();
            in.close();
        } catch (IOException e) {
        }
    }

    static @NotNull String asPath(@NotNull ResourceType type, @NotNull Identifier identifier)
    {
        return type.getName() + "/" + identifier.getNamespace() + "/" + identifier.getName();
    }
}
