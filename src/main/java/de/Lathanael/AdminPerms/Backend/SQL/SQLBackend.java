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

package de.Lathanael.AdminPerms.Backend.SQL;

import org.bukkit.entity.Player;

import de.Lathanael.AdminPerms.Backend.IBackend;
import de.Lathanael.AdminPerms.Permissions.GroupHandler;
import de.Lathanael.AdminPerms.Permissions.PlayerHandler;

/**
 * @author Lathanael (aka Philippe Leipold)
 *
 */
public class SQLBackend implements IBackend {

	/* (non-Javadoc)
	 * @see de.Lathanael.AdminPerms.Backend.IBackend#save()
	 */
	@Override
	public void save() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see de.Lathanael.AdminPerms.Backend.IBackend#reload()
	 */
	@Override
	public void reload() {
		save();
		PlayerHandler.getInstance().resetHandler();
		GroupHandler.getInstance().resetHandler();
		load();
	}

	/*
	 * (non-Javadoc)
	 * @see de.Lathanael.AdminPerms.Backend.IBackend#load()
	 */
	@Override
	public void load() {
		// TODO Auto-generated method stub
		
	}

	/*
	 * (non-Javadoc)
	 * @see de.Lathanael.AdminPerms.Backend.IBackend#save(java.lang.String)
	 */
	@Override
	public void savePlayer(final String playerName) {
		// TODO Auto-generated method stub
		
	}

	/*
	 * (non-Javadoc)
	 * @see de.Lathanael.AdminPerms.Backend.IBackend#load(java.lang.String)
	 */
	@Override
	public void loadPlayer(final String playerName) {
		// TODO Auto-generated method stub
		
	}

	/*
	 * (non-Javadoc)
	 * @see de.Lathanael.AdminPerms.Backend.IBackend#reload(java.lang.String)
	 */
	@Override
	public void reloadPlayer(final String playerName) {
		savePlayer(playerName);
		loadPlayer(playerName);
	}

	/*
	 * (non-Javadoc)
	 * @see de.Lathanael.AdminPerms.Backend.IBackend#saveGroup(java.lang.String)
	 */
	@Override
	public void saveGroup(String groupName) {
		// TODO Auto-generated method stub
		
	}

	/*
	 * (non-Javadoc)
	 * @see de.Lathanael.AdminPerms.Backend.IBackend#loadGroup(java.lang.String)
	 */
	@Override
	public void loadGroup(String groupName) {
		// TODO Auto-generated method stub
		
	}

	/*
	 * (non-Javadoc)
	 * @see de.Lathanael.AdminPerms.Backend.IBackend#reloadGroup(java.lang.String)
	 */
	@Override
	public void reloadGroup(String groupName) {
		// TODO Auto-generated method stub
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.Lathanael.AdminPerms.Backend.IBackend#createDefaultPlayerEntry(org.bukkit.entity.Player)
	 */
	@Override
	public void createDefaultPlayerEntry(final Player player) {
		createDefaultPlayerEntry(player.getName().toLowerCase());
	}

	/*
	 * (non-Javadoc)
	 * @see de.Lathanael.AdminPerms.Backend.IBackend#createDefaultPlayerEntry(java.lang.String)
	 */
	@Override
	public void createDefaultPlayerEntry(String playerName) {
		// TODO Auto-generated method stub
		
	}

	/*
	 * (non-Javadoc)
	 * @see de.Lathanael.AdminPerms.Backend.IBackend#createDefaultGroupEntry(java.lang.String)
	 */
	@Override
	public void createDefaultGroupEntry(String groupName) {
		// TODO Auto-generated method stub
		
	}
}
