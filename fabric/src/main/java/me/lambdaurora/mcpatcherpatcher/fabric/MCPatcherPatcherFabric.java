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

package me.lambdaurora.mcpatcherpatcher.fabric;

import me.lambdaurora.mcpatcherpatcher.MCPatcherPatcher;
import me.lambdaurora.mcpatcherpatcher.fabric.resource.MCPPResourcePack;
import net.fabricmc.api.ClientModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Represents the MCPatcherPatcher Fabric mod.
 *
 * @author LambdAurora
 * @version 1.0.0
 * @since 1.0.0
 */
public class MCPatcherPatcherFabric implements ClientModInitializer
{
    public static final String                        MODID    = "mcpatcherpatcher";
    public static final net.minecraft.util.Identifier DUMMY_ID = new net.minecraft.util.Identifier(MODID, "dummy");
    private static      MCPatcherPatcherFabric        INSTANCE;
    public final        Logger                        logger   = LogManager.getLogger(MODID);
    public final        MCPatcherPatcher              main     = new MCPatcherPatcher();
    public              MCPPResourcePack              resourcePack;

    @Override
    public void onInitializeClient()
    {
        INSTANCE = this;

        this.main.init();
    }

    /**
     * Prints a message to the terminal.
     *
     * @param info The message to print.
     */
    public void log(String info)
    {
        this.logger.info("[MCPatcherPatcher] " + info);
    }

    /**
     * Returns the MCPatcherPatcher mod instance.
     *
     * @return The mod instance.
     */
    public static MCPatcherPatcherFabric get()
    {
        return INSTANCE;
    }
}
