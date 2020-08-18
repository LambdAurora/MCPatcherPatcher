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
import me.lambdaurora.mcpatcherpatcher.fabric.fs.ResourceManagerAccessor;
import me.lambdaurora.mcpatcherpatcher.fabric.resource.MCPPResourcePack;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceReloadMonitor;
import net.minecraft.util.Unit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(ReloadableResourceManagerImpl.class)
public abstract class ReloadableResourceManagerImplMixin implements ReloadableResourceManager
{
    @Inject(method = "beginMonitoredReload", at = @At("HEAD"))
    private void onReload(Executor prepareExecutor, Executor applyExecutor, CompletableFuture<Unit> initialStage, List<ResourcePack> packs, CallbackInfoReturnable<ResourceReloadMonitor> cir)
    {
        MCPatcherPatcherFabric mod = MCPatcherPatcherFabric.get();
        mod.log("Inject generated resource packs.");
        packs.add(mod.resourcePack = new MCPPResourcePack());

        // @TODO MOVE THE CONVERTER !! URGENT !!
        mod.main.convert(new ResourceManagerAccessor(this), mod.resourcePack);
    }
}
