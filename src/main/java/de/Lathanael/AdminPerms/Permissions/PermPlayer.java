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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.entity.Player;

import de.Lathanael.AdminPerms.Logging.DebugLog;

/**
 * @author Lathanael (aka Philippe Leipold)
 *
 */
public class PermPlayer {
	
	private final String name;
	private final Map<String, Boolean> permissions = Collections.synchronizedMap(new HashMap<String, Boolean>());
	private final Map<String, Map<String, Boolean>> worldPermissions = Collections.synchronizedMap(new HashMap<String, Map<String, Boolean>>());
	private final List<String> groups = Collections.synchronizedList(new ArrayList<String>());
	private final Map<String, String> info = Collections.synchronizedMap(new HashMap<String, String>());
	private final Set<String> calculatedPerms = Collections.synchronizedSet(new HashSet<String>());
	private Group highestGroup = null;
	
	public PermPlayer(final String name, final Map<String, Boolean> permissions, 
			final List<String> groups, final Map<String, String> info, final Map<String, Map<String, Boolean>> worldPrmissions) {
		this.name = name.toLowerCase();
		this.permissions.putAll(permissions);
		this.groups.addAll(groups);
		this.info.putAll(info);
		this.worldPermissions.putAll(worldPrmissions);
		setHighestGroup();
	}
	
	public PermPlayer(final Player player) {
		name = player.getName().toLowerCase();
		groups.add(GroupHandler.getInstance().getDefaultGroup().getName());
		setHighestGroup();
	}
	
	public PermPlayer(final String playerName) {
		name = playerName.toLowerCase();
		groups.add(GroupHandler.getInstance().getDefaultGroup().getName());
		setHighestGroup();
	}

	private void setHighestGroup() {
		Group group = null;
		for (String g : groups) {
			group = GroupHandler.getInstance().getGroup(g.toLowerCase());
			if (group == null) {
				DebugLog.INSTANCE.info("A group with the name " + g 
						+ " does not exist. Check your group setup!");
				continue;
			}
			if (highestGroup != null) {
				if (highestGroup.getRank() < group.getRank()) {
					highestGroup = group;
				}
			} else if (group.getRank() >= 0) {
				highestGroup = group;
			}
		}
	}
	
	public Group getHighestGroup() {
		return highestGroup;
	}
	
	public void promote() {
		final String pGroup = highestGroup.getPromoteTo();
		removeGroup(highestGroup.getName());
		addGroup(pGroup);
	}
	
	public void demote() {
		final String dGroup = highestGroup.getDemoteTo();
		removeGroup(highestGroup.getName());
		addGroup(dGroup);
	}
	
	public Map<String, String> getInfos() {
		return info;
	}
	
	public String getInfo(final String infoEntry) {
		return info.get(infoEntry.toLowerCase());
	}
	
	public void setInfo(final String infoName, final String infoEntry) {
		info.put(infoName.toLowerCase(), infoEntry);
	}
	
	public String getName() {
		return name;
	}
	
	public Map<String, Boolean> getPermissions() {
		return permissions;
	}

	public Map<String, Boolean> getWorldPermissions(final String worldName) {
		if (worldPermissions.get(worldName.toLowerCase()) == null) {
			return new HashMap<String, Boolean>();
		}
		return worldPermissions.get(worldName.toLowerCase());
	}
	
	public Map<String, Map<String, Boolean>> getAllWorldPermissions() {
		return worldPermissions;
	}
	public List<String> getGroups() {
		return groups;
	}
	
	public void addGroup(final String groupName) {
		groups.add(groupName.toLowerCase());
		setHighestGroup();
	}
	
	public void removeGroup(final String groupName) {
		groups.remove(groupName.toLowerCase());
		setHighestGroup();
	}
	
	public void addOrChangePermission(final String permission, final boolean bool) {
		permissions.put(permission, bool);
	}
	
	public void addOrChangeWorldPermission(final String permission, final boolean bool, final String world) {
		if (worldPermissions.get(world) == null) {
			worldPermissions.put(world, new HashMap<String, Boolean>());
		}
		worldPermissions.get(world).put(permission, bool);
	}
	
	public void removePermission(final String permission) {
		permissions.remove(permission);
	}

	public void removeWorldPermission(final String permission, final String world) {
		if (worldPermissions.get(world) == null) {
			return;
		}
		worldPermissions.get(world).remove(permission);
	}
	
	public void removeGroups(final List<String> groups) {
		final Iterator<String> it = groups.iterator();
		while (it.hasNext()) {
			groups.add(it.next().toLowerCase());
			it.remove();
		}
		this.groups.removeAll(groups);
		setHighestGroup();
	}
	
	public void addGroups(final List<String> groups) {
		final Iterator<String> it = groups.iterator();
		while (it.hasNext()) {
			groups.add(it.next().toLowerCase());
			it.remove();
		}
		this.groups.addAll(groups);
		setHighestGroup();
	}
	
	public void setGroups(final List<String> groups) {
		this.groups.clear();
		for (String group : groups) {
			this.groups.add(group.toLowerCase());
		}
		setHighestGroup();
	}
	
	public void setCalculatedPerms(final Map<String, Boolean> perms) {
		for (Map.Entry<String, Boolean> entry : perms.entrySet()) {
			if (!entry.getValue()) {
				calculatedPerms.add(entry.getKey());
			}
		}
	}
	
	public Set<String> getCalculatedPerms() {
		return calculatedPerms;
	}
}