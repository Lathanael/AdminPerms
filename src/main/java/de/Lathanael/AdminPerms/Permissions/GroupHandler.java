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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.entity.Player;

import de.Lathanael.AdminPerms.bukkit.ConfigEnum;

/**
 * @author Lathanael (aka Philippe Leipold)
 *
 */
public class GroupHandler {

	private static final GroupHandler instance = new GroupHandler();
	private final Map<String, Group> groups = Collections.synchronizedMap(new HashMap<String, Group>());
	private Group defaultGroup;
	
	public static GroupHandler getInstance() {
		return instance;
	}
	
	public void resetHandler() {
		groups.clear();
	}
	
	public void checkDefaultGroup() {
		if (defaultGroup != null) {
			return;
		}
		for (Group g : groups.values()) {
			if (defaultGroup == null || (g.getRank() >= 0 && defaultGroup.getRank() > g.getRank())) {
				defaultGroup = g;
			}
		}
	}
	
	public void addGroup(final Group group) {
		final String dGroup = ConfigEnum.DEFAULT_GROUP.getString();
		if (dGroup.equalsIgnoreCase("%rank%")) {
			if (defaultGroup == null || (group.getRank() >= 0 && defaultGroup.getRank() > group.getRank())) {
				defaultGroup = group;
			}
		} else if (group.getName().equalsIgnoreCase(dGroup)) {
			defaultGroup = group;
		}
		groups.put(group.getName(), group);
	}

	public Group getDefaultGroup() {
		return defaultGroup;
	}
	public Set<Group> getGroups() {
		final Set<Group> groups = new HashSet<Group>();
		for (Map.Entry<String, Group> entry : this.groups.entrySet()) {
			groups.add(entry.getValue());
		}
		return groups;
	}
	
	public Set<String> getGroupNames() {
		return this.groups.keySet();
	}
	
	public Map<String, Boolean> getGroupPermissions(final String groupName) {
		
		return groups.get(groupName.toLowerCase()).getPermissions();
	}
	
	public Map<String, Boolean> getGroupsPermissions(final List<String> groups) {
		Map<String, Boolean> newGroupPermissions = new HashMap<String, Boolean>();
		Group group;
		int oldRank = -1;
		for (String groupName : groups) {
			group = this.groups.get(groupName);
			if (oldRank == -1) {
				newGroupPermissions.putAll(group.getPermissions());
			} else if (oldRank < group.getRank()) {
				newGroupPermissions.putAll(group.getPermissions());
			} else if (oldRank >= group.getRank()) {
				for (Map.Entry<String, Boolean> entry : group.getPermissions().entrySet()) {
					if (!newGroupPermissions.containsKey(entry.getKey())) {
						newGroupPermissions.put(entry.getKey(), entry.getValue());
					}
				}
			}
			oldRank = group.getRank();
		}
		return newGroupPermissions;
	}
	
	public Set<String> getPlayers(final String groupName) {
		return groups.get(groupName.toLowerCase()).getPlayers();
	}
	
	public List<Player> getOnlinePlayers(final String groupName) {
		return groups.get(groupName.toLowerCase()).getOnlinePlayers();
	}
	
	public int getRank(final String groupName) {
		return groups.get(groupName.toLowerCase()).getRank();
	}
	
	public Group getGroup(final String groupName) {
		return groups.get(groupName.toLowerCase());
	}

	public Map<String, Boolean> getWorldGroupsPermissions(final List<String> groups,
			final String worldName) {
		Map<String, Boolean> newWorldGroupPermissions = new HashMap<String, Boolean>();
		Group group;
		int oldRank = -1;
		for (String groupName : groups) {
			group = this.groups.get(groupName.toLowerCase());
			if (oldRank == -1) {
				newWorldGroupPermissions.putAll(group.getWorldPermissions(worldName.toLowerCase()));
			} else if (oldRank < group.getRank()) {
				newWorldGroupPermissions.putAll(group.getWorldPermissions(worldName.toLowerCase()));
			} else if (oldRank >= group.getRank()) {
				for (Map.Entry<String, Boolean> entry : group.getWorldPermissions(worldName.toLowerCase()).entrySet()) {
					if (!newWorldGroupPermissions.containsKey(entry.getKey())) {
						newWorldGroupPermissions.put(entry.getKey(), entry.getValue());
					}
				}
			}
			oldRank = group.getRank();
		}
		return newWorldGroupPermissions;
	}

	public Map<String, Boolean> getWorldGroupPermissions(final String groupName,
			final String worldName) {
		return groups.get(groupName.toLowerCase()).getWorldPermissions(worldName.toLowerCase());
	}
	
	public void copyInheritatedPerms() {
		List<String> inheritance;
		Group group;
		for (Group g : groups.values()) {
			inheritance = g.getInheritance();
			for (String s : inheritance) {
				group = getGroup(s.toLowerCase());
				if (g.getRank() >= group.getRank()) {
					g.copyNotExistingPermissions(group.getPermissions());
					g.copyNotExistingWorldPermissions(group.getAllWorldPermissions());
				} else {
					g.setWorldPermissions(group.getAllWorldPermissions());
					g.setPermissions(group.getPermissions());
				}
			}
		}
	}
}
