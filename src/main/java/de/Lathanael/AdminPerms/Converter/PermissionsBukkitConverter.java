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

package de.Lathanael.AdminPerms.Converter;

import de.Lathanael.AdminPerms.bukkit.Main;

/**
 * @author Lathanael (aka Philippe Leipold)
 *
 */
public class PermissionsBukkitConverter extends IConverter {

	public static PermissionsBukkitConverter instance = new PermissionsBukkitConverter();
	
	public PermissionsBukkitConverter() {
		path = getPluginsFolder(Main.getInstance().getDataFolder());
	}
	
	public static PermissionsBukkitConverter getInstance() {
		return instance;
	}

	/*
	 * (non-Javadoc)
	 * @see de.Lathanael.AdminPerms.Converter.IConverter#convertPlayerData()
	 */
	@Override
	public void convertPlayerData() {
		// TODO Auto-generated method stub
		
	}

	/*
	 * (non-Javadoc)
	 * @see de.Lathanael.AdminPerms.Converter.IConverter#convertGroupData()
	 */
	@Override
	public void convertGroupData() {
		// TODO Auto-generated method stub
		
	}	
}
