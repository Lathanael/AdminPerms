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

import org.bukkit.entity.Player;

import de.Lathanael.AdminPerms.bukkit.Main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Lathanael (aka Philippe Leipold)
 * @author SpaceManiac (original author)
 *
 */
public class Group {

	private String name;
	private int rank;
	private String promoteTo;
	private String demoteTo;
	private final Map<String, Boolean> permissions = Collections.synchronizedMap(new HashMap<String, Boolean>());
	private final Map<String, Map<String, Boolean>> worldPermissions = Collections.synchronizedMap(new HashMap<String, Map<String, Boolean>>());
	private final List<String> inheritance = Collections.synchronizedList(new ArrayList<String>());
	private final Set<String> players = Collections.synchronizedSortedSet(new TreeSet<String>());
	private final Map<String, String> info = Collections.synchronizedMap(new HashMap<String, String>());
	
	public Group(final String name, final int rank, final Map<String, Boolean> permissions,
			final List<String> inheritance, final Map<String, String> info,
			final Map<String, Map<String, Boolean>> worldPrmissions, final String promoteTo,
			final String demoteTo) {
		this.name = name.toLowerCase();
		this.rank = rank;
		this.setDemoteTo(demoteTo.toLowerCase());
		this.promoteTo = promoteTo.toLowerCase();
		this.permissions.putAll(permissions);
		this.inheritance.addAll(inheritance);
		this.info.putAll(info);
		this.worldPermissions.putAll(worldPrmissions);
	}

	public Map<String, String> getInfos() {
		return info;
	}
	
	/**
	 * Returns a information entry of this group.
	 * 
	 * @param infoEntry The entry which should be looked up.
	 * @return A {@code String} with the wanted information or an <b>empty</b> {@code String}.
	 */
	public String getInfo(final String infoEntry) {
		return (info.get(infoEntry) != null) ? info.get(infoEntry) : "";
	}
	
	public void setInfo(final String infoEntry, final String entry) {
		info.put(infoEntry, entry);
	}
	
	public void setInfos(final Map<String, String> infos) {
		info.clear();
		info.putAll(infos);
	}
	
	public List<String> getInheritance() {
		return inheritance;
	}
	
	/**
	 * Retruns the name of this group as a {@code String}
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Returns the rank of this group as an {@code Integer}
	 * </br></br>
	 * If the rank equals <i>-1</i> this group is a special group.
	 */
	public int getRank() {
		return rank;
	}
	
	/**
	 * Returns the name as a {@code String} of the group to which a player
	 * of this group is promoted to, if the promoteTo is not set it is an
	 * <b>empty</b> {@code String}.
	 */
	public String getPromoteTo() {
		return promoteTo;
	}

	/**
	 * Returns the name as a {@code String} of the group to which a player
	 * of this group is demoted to, if the demoteTo is not set it is an
	 * <b>empty</b> {@code String}.
	 */
	public String getDemoteTo() {
		return demoteTo;
	}

	public void setDemoteTo(final String demoteTo) {
		this.demoteTo = demoteTo.toLowerCase();
	}

	public void setPromoteTo(final String promoteTo) {
		this.promoteTo = promoteTo.toLowerCase();
	}

	public void setRank(final int rank) {
		this.rank = rank;
	}

	public void addPlayers(final List<String> players) {
		this.players.addAll(players);	
	}
	
	public void addPlayer(final String player) {
		players.add(player.toLowerCase());
	}
	
	public void removePlayer(final String player) {
		players.remove(player.toLowerCase());
	}
	
	public void removePlayers(final List<String> players) {
		this.players.removeAll(players);
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
		permissions.put(permission, null);
	}
	
	public void removeWorldPermission(final String permission, final String world) {
		if (worldPermissions.get(world) == null) {
			return;
		}
		worldPermissions.get(world).put(permission, null);
	}
	
	public void copyNotExistingPermissions(final Map<String, Boolean> permissions) {
		for (String entry : permissions.keySet()) {
			if (!this.permissions.containsKey(entry)) {
				this.permissions.put(entry, permissions.get(entry));
			}
		}
	}
	
	public void copyNotExistingWorldPermissions(final Map<String, Map<String, Boolean>> worldPermissions) {
		Map<String, Boolean> wP;
		Map<String, Boolean> thisWP;
		for (String entry : worldPermissions.keySet()) {
			wP = worldPermissions.get(entry.toLowerCase());
			thisWP = this.worldPermissions.get(entry.toLowerCase());
			if (thisWP == null) {
				this.worldPermissions.put(entry.toLowerCase(), wP);
				continue;
			}
			for (String entry2 : wP.keySet()) {
				if (!thisWP.containsKey(entry2.toLowerCase())) {
					thisWP.put(entry, wP.get(entry2.toLowerCase()));
				}
			}
			this.worldPermissions.put(entry.toLowerCase(), thisWP);
		}
	}
	
	public void setPermissions(final Map<String, Boolean> permissions) {
		this.permissions.putAll(permissions);
	}
	
	public void setWorldPermissions(final Map<String, Map<String,Boolean>> worldPermissions) {
		for (String world : worldPermissions.keySet()) {
			if (this.worldPermissions.get(world.toLowerCase()) == null) {
				this.worldPermissions.put(world, worldPermissions.get(world));
				continue;
			}
			this.worldPermissions.get(world.toLowerCase()).putAll(worldPermissions.get(world.toLowerCase()));
		}
	}
	
	public void setInheritance(final List<String> inheritance) {
		this.inheritance.clear();
		this.inheritance.addAll(inheritance);
	}
	
	public void resetPermissions(final Map<String, Boolean> permissions) {
		this.permissions.clear();
		this.permissions.putAll(permissions);
	}
	
	public void resetWorldPermissions(final Map<String, Map<String,Boolean>> worldPermissions) {
		this.worldPermissions.clear();
		for (String world : worldPermissions.keySet()) {
			this.worldPermissions.put(world, worldPermissions.get(world));
		}
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
	
	public Set<String> getPlayers() {
		return players;
	}

	public Set<Player> getOnlinePlayers() {
		Set<Player> result = new HashSet<Player>();
		for (String user : getPlayers()) {
			Player player = Main.getInstance().getServer().getPlayer(user);
			if (player != null && player.isOnline()) {
				result.add(player);
			}
		}
		return result;
	}

	@Override
	public boolean equals(Object o) {
		return !(o == null || !(o instanceof Group)) && name.equalsIgnoreCase(((Group) o).getName());
	}

	@Override
	public String toString() {
		return "Group{name=" + name + "}";
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}
}