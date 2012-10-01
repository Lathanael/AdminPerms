/*************************************************************************
 * Copyright (C) 2012 Philippe Leipold
 *
 * This file is part of AdminPerms.
 *
 * AdminPerms is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * AdminPerms is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with AdminPerms. If not, see <http://www.gnu.org/licenses/>.
 *
 **************************************************************************/

package de.Lathanael.AdminPerms.Listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.player.*;

import de.Lathanael.AdminPerms.Logging.DebugLog;
import de.Lathanael.AdminPerms.Permissions.PermissionsHandler;
import de.Lathanael.AdminPerms.bukkit.ConfigEnum;

/**
 * @author Lathanael (aka Philippe Leipold)
 * @author platymuus (original author)
 *
 */
public class AdminPermsPlayerListener implements Listener {

	// Keep track of player's world

	@EventHandler(priority = EventPriority.LOWEST)
	public void onWorldChange(PlayerChangedWorldEvent event) {
		PermissionsHandler.getInstance().calculateAttachment(event.getPlayer());
	}

	// Register players when needed

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerLogin(PlayerJoinEvent event) {
		DebugLog.INSTANCE.info("Player " + event.getPlayer().getName() + " joined, registering...");
		PermissionsHandler.getInstance().registerPlayer(event.getPlayer());
	}

	// Unregister players when needed

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerKick(PlayerKickEvent event) {
		if (event.isCancelled()) return;
		DebugLog.INSTANCE.info("Player " + event.getPlayer().getName() + " was kicked, unregistering...");
		PermissionsHandler.getInstance().unregisterPlayer(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerQuit(PlayerQuitEvent event) {
		DebugLog.INSTANCE.info("Player " + event.getPlayer().getName() + " quit, unregistering...");
		PermissionsHandler.getInstance().unregisterPlayer(event.getPlayer());
	}

	// Prevent doing things in the event of permissions.build: false

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.isCancelled()) return;
		if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_AIR) {
			return;
		}
		if (!event.getPlayer().hasPermission("adminperms.build")) {
			bother(event.getPlayer());
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if (event.isCancelled()) return;
		if (!event.getPlayer().hasPermission("permissions.build")) {
			bother(event.getPlayer());
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (event.isCancelled()) return;
		if (!event.getPlayer().hasPermission("permissions.build")) {
			bother(event.getPlayer());
			event.setCancelled(true);
		}
	}

	private void bother(Player player) {
		final String msg = ConfigEnum.BUILD.getString();
		if (msg.length() > 0) {
			String message = msg.replace('&', '\u00A7');
			player.sendMessage(message);
		}
	}

}
