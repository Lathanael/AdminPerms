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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.entity.Player;

import de.Lathanael.AdminPerms.Permissions.Group;
import de.Lathanael.AdminPerms.Permissions.GroupHandler;
import de.Lathanael.AdminPerms.Permissions.PermPlayer;
import de.Lathanael.AdminPerms.Permissions.PlayerHandler;

/**
 * API for AdminPerms and its functions. Use this class in your plugin to
 * communicate with AdminPerms!
 * 
 * @author Lathanael (aka Philippe Leipold)
 *
 */
public class AdminPerms {
	
	// Player Section
	/**
	 * Returns a {@code Set<PermPlayer>} which contains all currently with AdminPerms registered
	 * players. Might be empty if no players are registered. 
	 */
	public Set<PermPlayer> getPlayers() {
		return PlayerHandler.getInstance().getPlayers();
	}
	
	/**
	 * Get a {@link PermPlayer} object for a given player name
	 * @param playerName The name of the wanted player
	 * @return A {@link PermPlayer} object or <b>null</b> if none exists
	 */
	public PermPlayer getPlayer(final String playerName) {
		return PlayerHandler.getInstance().getPlayer(playerName);
	}
	
	/**
	 * @see AdminPerms#getPlayer(String)
	 */
	public PermPlayer getPlayer(final Player player) {
		return PlayerHandler.getInstance().getPlayer(player);
	}
	
	/**
	 * Gets all groups of a player
	 * @param playerName The player from whom the groups are returned
	 * @return A {@code Set<String>} containing all the group names
	 * or an <b>empty</b> Set if the player does not exist
	 */
	public Set<String> getPlayerGroups(final String playerName) {
		return PlayerHandler.getInstance().getPlayer(playerName) != null
				? PlayerHandler.getInstance().getPlayer(playerName).getGroups()
				: new HashSet<String>();
	}
	
	/**
	 * Gets a {@code Map<String, String>} containing all information entries
	 * @param playerName The name of the player in question
	 * @return A {@code Map<String, String>} containing all information entries
	 * or an <b>empty</b> Map if the player does not exist.
	 */
	public Map<String, String> getPlayerInfos(final String playerName) {
		return PlayerHandler.getInstance().getPlayer(playerName) != null
				? PlayerHandler.getInstance().getPlayer(playerName).getInfos()
				: new HashMap<String, String>();
	}
	
	/**
	 * Gets an information entry from a player
	 * @param playerName The name of the wanted player
	 * @param infoEntry The String to which the information is tied
	 * @return The information as a String or an <b>empty</b> String if
	 * the player does not exist. 
	 * </br></br>
	 * This might also be an empty String if there was no
	 * information tied to the given Entry.
	 */
	public String getPlayerInfo(final String playerName, final String infoEntry) {
		return PlayerHandler.getInstance().getPlayer(playerName) != null
				? PlayerHandler.getInstance().getPlayer(playerName).getInfo(infoEntry) : "";
	}
	
	// Group Section
	/**
	 * Returns a {@code Set<Group>} of all currently in AdminPerms
	 * existing {@link Group}s
	 * 
	 * @return A {@code Set<Group>} of {@link Group}s or an <b>empty {@code Set}</b>
	 * if no group exists! 
	 */
	public Set<Group> getGroups() {
		return GroupHandler.getInstance().getGroups();
	}
	
	/**
	 * Returns the {@link Group} linked to the given name.
	 * 
	 * @param groupName - The name of the wanted group as a String
	 * @return The {@link Group} corresponding to the name or <b>null</b>
	 * if it does not exist.
	 */
	public Group getGroup(final String groupName) {
		return GroupHandler.getInstance().getGroup(groupName);
	}
	
	/**
	 * Returns the Names of all currently in AdminPerms existing groups.
	 * 
	 * @return A {@code Set<String>} containing the names of all groups or an <b>empty</b>
	 * {@code Set} if no group exists!  
	 */
	public Set<String> getAllGroupNames() {
		return GroupHandler.getInstance().getGroupNames();
	}
	
	/**
	 * Gets the default {@link Group}
	 * 
	 * @return A {@link Group} object or <b>null</b> if there is not default group.
	 */
	public Group getDefaultGroup() {
		return GroupHandler.getInstance().getDefaultGroup();
	}
	
	/**
	 * Gets a list of all players belonging to a group
	 * 
	 * @param groupName The name of the group
	 * @return A {@code Set<String>} of all players in the group or an <b>empty</b> {@code Set}
	 * if the group does not exist.
	 */
	public Set<String> getPlayersFromGroup(final String groupName) {
		return GroupHandler.getInstance().getPlayers(groupName);
	}
	
	/**
	 * Gets a list of all online players belonging to a group
	 * 
	 * @param groupName The name of the group
	 * @return A {@code Set<Player>} of all online players in the group or an
	 * <b>empty</b> {@code Set} if the group does not exist.
	 */
	public Set<Player> getOnlinePlayersFromGroup(final String groupName) {
		return GroupHandler.getInstance().getOnlinePlayers(groupName);
	}
	
	/**
	 * Gets a {@link Map} of all information entries from the given group.
	 *  
	 * @param groupName The name of the wanted group
	 * @return A {@code Map<String, String>} containing all information entries or
	 * an <b>empty</b> Map if the group does not exist.
	 */
	public Map<String, String> getGroupInfos(final String groupName) {
		return GroupHandler.getInstance().getGroup(groupName) != null
				? GroupHandler.getInstance().getGroup(groupName).getInfos()
				: new HashMap<String, String>();
	}
	
	/**
	 * Gets an information entry from a Group
	 * @param groupName The name of the wanted player
	 * @param infoEntry The String to which the information is tied
	 * @return The information as a String or an <b>empty</b> String if
	 * the group does not exist. 
	 * </br></br>
	 * This might also be an empty String if there was no
	 * information tied to the given Entry.
	 */
	public String getGroupInfo(final String groupName, final String infoEntry) {
		return GroupHandler.getInstance().getGroup(groupName) != null
				? GroupHandler.getInstance().getGroup(groupName).getInfo(infoEntry) : "";
	}
}
