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

package de.Lathanael.AdminPerms.Permissions;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;

import de.Lathanael.AdminPerms.Backend.IBackend;
import de.Lathanael.AdminPerms.Logging.DebugLog;
import de.Lathanael.AdminPerms.bukkit.Main;

/**
 * @author Lathanael (aka Philippe Leipold)
 * @author SpaceManiac (original author)
 *
 */
public class PermissionsHandler {
	
	private static final PermissionsHandler instance = new PermissionsHandler();
	private HashMap<String, PermissionAttachment> permissions = new HashMap<String,
			PermissionAttachment>();
	private IBackend perms;
	private BukkitPermsReflection replacePermissible = new BukkitPermsReflection();
	
	public static PermissionsHandler getInstance() {
		return instance;
	}
	
	public void setBackend(final IBackend backend) {
		this.perms = backend;
		perms.load();
	}
	
	public IBackend getBackend() {
		return perms;
	}

	public void registerPlayer(final Player[] players) {
		for (Player player : players) {
			registerPlayer(player);
		}
	}

	public void registerPlayer(final Player player) {
		if (permissions.containsKey(player.getName().toLowerCase())) {
			DebugLog.INSTANCE.warning("Registering " + player.getName() + ": was already " 
					+ "registered");
			unregisterPlayer(player);
		}
		if (PlayerHandler.getInstance().getPlayer(player) == null) {
			perms.createDefaultPlayerEntry(player);
			perms.load(player.getName());
		}
		PermissionAttachment attachment = player.addAttachment(Main.getInstance());
		permissions.put(player.getName().toLowerCase(), attachment);
		calculateAttachment(player);
		try {
			replacePermissible.replacePermissible(player, new AdminPermsPermissible(player));
		} catch (Exception e) {
			Main.getInstance().getLogger().warning("Could not inject AdminPerms permissible"
					+ " into bukkit, cause: " + e.getCause());
			Main.getInstance().getLogger().warning("For more information refer to the"
					+ " debug.log!");
			DebugLog.INSTANCE.log(Level.SEVERE, "Could not inject AdminPerms permissible!", e);
		}
	}

	public void unregisterPlayer(final Player[] players) {
		for (Player player : players) {
			unregisterPlayer(player);
		}
	}

	public void unregisterPlayer(final Player player) {
		if (permissions.containsKey(player.getName().toLowerCase())) {
			try {
				player.removeAttachment(permissions.get(player.getName().toLowerCase()));
			}
			catch (IllegalArgumentException ex) {
				DebugLog.INSTANCE.warning("Unregistering " + player.getName() 
						+ ": player did not have attachment");
			}
			permissions.remove(player.getName().toLowerCase());
		} else {
			DebugLog.INSTANCE.warning("Unregistering " + player.getName() 
					+ ": was not registered");
		}
	}

	public void refreshPermissions() {
		perms.reload();
		for (String player : permissions.keySet()) {
			PermissionAttachment attachment = permissions.get(player);
			for (String key : attachment.getPermissions().keySet()) {
				attachment.unsetPermission(key);
			}
			calculateAttachment(Main.getInstance().getServer().getPlayer(player));
		}
	}

	public void refreshPermissions(final String player) {
		perms.reload(player);
		PermissionAttachment attachment = permissions.get(player);
		for (String key : attachment.getPermissions().keySet()) {
			attachment.unsetPermission(key);
		}
		calculateAttachment(Main.getInstance().getServer().getPlayer(player));
	}

	public void calculateAttachment(final Player player, final String worldName) {
		if (player == null) {
			return;
		}
		PermissionAttachment attachment = permissions.get(player.getName().toLowerCase());
		if (attachment == null) {
			DebugLog.INSTANCE.warning("Calculating permissions on " + player.getName() 
					+ ": attachment was null");
			return;
		}
		for (String key : attachment.getPermissions().keySet()) {
			attachment.unsetPermission(key);
		}
		Map<String, Boolean> perms = calculatePlayerPermissions(player.getName().toLowerCase(),
				worldName.toLowerCase());
		for (Map.Entry<String, Boolean> entry : perms.entrySet()) {
			attachment.setPermission(entry.getKey(), entry.getValue());
		}
		PlayerHandler.getInstance().getPlayer(player).setCalculatedPerms(perms);
		player.recalculatePermissions();
	}
	
	public void calculateAttachment(final Player player) {
		calculateAttachment(player, player.getWorld().getName().toLowerCase());
	}
	
	public boolean hasFalsePerm(final Player player, final String permission) {
		return PlayerHandler.getInstance().getPlayer(player).getCalculatedPerms()
				.contains(permission.toLowerCase());
	}
	
	public boolean hasFalsePerm(final Player player, final Permission permission) {
		return hasFalsePerm(player, permission.getName().toLowerCase());
	}
	
	private Map<String, Boolean> calculatePlayerPermissions(final String player,
			final String world) {
		final PermPlayer pPlayer = PlayerHandler.getInstance().getPlayer(player.toLowerCase());
		if (pPlayer == null) {
			PlayerHandler.getInstance().addPlayer(player.toLowerCase());
			return calculateGroupPermissions(GroupHandler.getInstance().getDefaultGroup()
					.getName(), world);
		}
		Map<String, Boolean> perms = pPlayer.getPermissions();
		// World permissions override non world ones
		perms.putAll(pPlayer.getWorldPermissions(world));
		Map<String, Boolean> groupPerms = GroupHandler.getInstance()
				.getGroupsPermissions(pPlayer.getGroups());
		Map<String, Boolean> worldGroupPerms = GroupHandler.getInstance()
				.getWorldGroupsPermissions(pPlayer.getGroups(), world);
		// User permissions have a higher "value" than group permissions
		for (Map.Entry<String, Boolean> entry : worldGroupPerms.entrySet()) {
			if (!perms.containsKey(entry.getKey())) {
				perms.put(entry.getKey(), entry.getValue());
			}
		}
		// User permissions and world group permissions have a higher "value" 
		// than group permissions
		for (Map.Entry<String, Boolean> entry : groupPerms.entrySet()) {
			if (!perms.containsKey(entry.getKey())) {
				perms.put(entry.getKey(), entry.getValue());
			}
		}
		return perms;
	}

	private Map<String, Boolean> calculateGroupPermissions(final String group,
			final String world) {
		Map<String, Boolean> perms = GroupHandler.getInstance()
				.getGroupPermissions(group.toLowerCase());
		// World permissions override non world ones
		perms.putAll(GroupHandler.getInstance().getWorldGroupPermissions(group.toLowerCase(),
				world));
		return perms;
	}
}
