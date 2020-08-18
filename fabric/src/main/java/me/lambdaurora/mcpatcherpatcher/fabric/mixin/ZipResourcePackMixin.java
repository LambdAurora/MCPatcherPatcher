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

package me.lambdaurora.mcpatcherpatcher.fabric.mixin;

import me.lambdaurora.mcpatcherpatcher.fabric.MCPatcherPatcherFabric;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.ZipResourcePack;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Mixin(ZipResourcePack.class)
public class ZipResourcePackMixin
{
    @Redirect(method = "findResources", at = @At(value = "NEW", target = "net/minecraft/util/Identifier"))
    private Identifier newIdentifier(String namespace, String path)
    {
        try {
            return new Identifier(namespace, path);
        } catch (InvalidIdentifierException e) {
            return MCPatcherPatcherFabric.DUMMY_ID;
        }
    }

    @Inject(method = "findResources", at = @At("RETURN"), cancellable = true)
    private void onFindResources(ResourceType type, String namespace, String prefix, int maxDepth, Predicate<String> pathFilter, CallbackInfoReturnable<Collection<Identifier>> cir)
    {
        Collection<Identifier> identifiers = cir.getReturnValue();
        cir.setReturnValue(identifiers.stream().filter(id -> !id.equals(MCPatcherPatcherFabric.DUMMY_ID)).collect(Collectors.toList()));
    }
}
