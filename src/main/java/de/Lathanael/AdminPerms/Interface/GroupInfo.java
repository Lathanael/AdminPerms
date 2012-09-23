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

import java.util.ArrayList;
import java.util.List;

import de.Lathanael.AdminPerms.Permissions.Group;
import de.Lathanael.AdminPerms.bukkit.Main;;

/**
 * @author Lathanael (aka Philippe Leipold)
 * @author platymuus (original author)
 * 
 */
public class GroupInfo {

	/**
	 * Get the group with the given name.
	 * @param groupName The name of the group.
	 * @return A Group if it exists or null otherwise.
	 */
	public Group getGroup(String groupName) {
		if (getNode("groups") != null) {
			for (String key : getNode("groups").getKeys(false)) {
				if (key.equalsIgnoreCase(groupName)) {
					return new Group(this, key);
				}
			}
		}
		return null;
	}

	/**
	 * Returns a list of groups a player is in.
	 * @param playerName The name of the player.
	 * @return The groups this player is in. May be empty.
	 */
	public List<Group> getGroups(String playerName) {
		ArrayList<Group> result = new ArrayList<Group>();
		if (getNode("users/" + playerName) != null) {
			for (String key : getNode("users/" + playerName).getStringList("groups")) {
				result.add(new Group(this, key));
			}
		} else {
			result.add(new Group(this, "default"));
		}
		return result;
	}

	/**
	 * Returns a list of all defined groups.
	 * @return The list of groups.
	 */
	public List<Group> getAllGroups() {
		ArrayList<Group> result = new ArrayList<Group>();
		if (getNode("groups") != null) {
			for (String key : getNode("groups").getKeys(false)) {
				result.add(new Group(this, key));
			}
		}
		return result;
	}	
}
