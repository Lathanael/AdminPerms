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

package de.Lathanael.AdminPerms.Interface;

import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

/**
 * @author Lathanael (aka Philippe Leipold)
 * @author platymuus (original author)
 * 
 */
public class PermissionInfo {

	private final ConfigurationSection node;
	private final String groupType;

	protected PermissionInfo(ConfigurationSection node, String groupType) {
		this.node = node;
		this.groupType = groupType;
	}

	/**
	 * Gets the list of groups this group/player inherits permissions from.
	 * @return The list of groups.
	 */
	public List<GroupCommand> getGroups() {
		ArrayList<GroupCommand> result = new ArrayList<GroupCommand>();

		for (String key : node.getStringList(groupType)) {
			GroupCommand group = plugin.getGroup(key);
			if (group != null) {
				result.add(group);
			}
		}

		return result;
	}

	/**
	 * Gets a map of non-world-specific permission nodes to boolean values that this group/player defines.
	 * @return The map of permissions.
	 */
	public Map<String, Boolean> getPermissions() {
		return plugin.getAllPerms(node.getName(), node.getName());
	}

	/**
	 * Gets a list of worlds this group/player defines world-specific permissions for.
	 * @return The list of worlds.
	 */
	public Set<String> getWorlds() {
		if (node.getConfigurationSection("worlds") == null) {
			return new HashSet<String>();
		}
		return node.getConfigurationSection("worlds").getKeys(false);
	}

	/**
	 * Gets a map of world-specific permission nodes to boolean values that this group/player defines.
	 * @param world The name of the world.
	 * @return The map of permissions.
	 */
	public Map<String, Boolean> getWorldPermissions(String world) {
		return plugin.getAllPerms(node.getName() + ":" + world, node.getName() + "/world/" + world);
	}

}
