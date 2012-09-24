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
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;

/**
 * @author Lathanael (aka Philippe Leipold)
 *
 */
public class AdminPermsPermissible extends PermissibleBase {

	protected final Player player;
	
	public AdminPermsPermissible(final Player player) {
		super(player);
		
		this.player = player;
	}

	@Override
	public boolean hasPermission(final String permission) {
		if (PermissionsHandler.getInstance().hasFalsePerm(player, permission))
			return false;
		else
			return super.hasPermission(permission); 
	}
	
	@Override
	public boolean hasPermission(final Permission perm) {
		return hasPermission(perm.getName().toLowerCase());
	}
}
