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
import java.util.Map;
import java.util.Set;

import org.bukkit.entity.Player;

/**
 * @author Lathanael (aka Philippe Leipold)
 *
 */
public class PlayerHandler {
	
	private final static PlayerHandler instance = new PlayerHandler();
	private final Map<String, PermPlayer> players = Collections.synchronizedMap(new HashMap<String, PermPlayer>());

	public static PlayerHandler getInstance() {
		return instance;
	}
	
	public void resetHandler() {
		players.clear();
	}

	/**
	 * Gets a {@link PermPlayer} object for the given name
	 * @param playerName The name for which a PermPlayer should be returned
	 * @return The to the name associated {@link PermPlayer} object or <b>null</b>
	 * if there is no PermPlayer
	 */
	public PermPlayer getPlayer(final String playerName) {
		return players.get(playerName.toLowerCase());
	}
	
	/**
	 * Gets a {@link PermPlayer} object for the given {@link Player}
	 * @param player The {@link Player} for which a PermPlayer should be returned
	 * @return The to the {@link Player} associated {@link PermPlayer} object or <b>null</b>
	 * if there is no PermPlayer
	 */
	public PermPlayer getPlayer(final Player player) {
		return players.get(player.getName().toLowerCase());
	}
	
	public void addPlayer(final String playerName) {
		players.put(playerName.toLowerCase(), new PermPlayer(playerName.toLowerCase()));
	}
	
	public void addPlayer(final Player player) {
		players.put(player.getName().toLowerCase(), new PermPlayer(player));
	}
	
	public void addPlayer(final PermPlayer player) {
		players.put(player.getName(), player);
	}
	
	public void removePlayer(final PermPlayer player) {
		players.remove(player.getName());
	}
	
	public Set<PermPlayer> getPlayers() {
		final Set<PermPlayer> players = new HashSet<PermPlayer>();
		for (Map.Entry<String, PermPlayer> entry : this.players.entrySet()) {
			players.add(entry.getValue());
		}
		return players;
	}
}
