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

import java.util.List;

import de.Lathanael.AdminPerms.Interface.PermissionInfo;
import de.Lathanael.AdminPerms.Permissions.Group;
import de.Lathanael.AdminPerms.Permissions.PermissionsHandler;

/**
 * @author Lathanael (aka Philippe Leipold)
 *
 */
public class AdminPerms {


	/**
	 * Returns permission info on the given player.
	 * @param playerName The name of the player.
	 * @return A PermissionsInfo about this player.
	 */
	public PermissionInfo getPlayerInfo(String playerName) {
		if (getNode("users/" + playerName) == null) {
			return null;
		} else {
			return new PermissionInfo(this, getNode("users/" + playerName), "groups");
		}
	}
	
	public List<Group> getGroups() {
		return PermissionsHandler.getInstance().getBackend().getGroups();
	}
	
}
