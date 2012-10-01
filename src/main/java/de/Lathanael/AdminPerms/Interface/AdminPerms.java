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

import java.util.Set;

import de.Lathanael.AdminPerms.Permissions.Group;
import de.Lathanael.AdminPerms.Permissions.GroupHandler;

/**
 * API for AdminPerms and its functions. Use this class in your plugin to
 * communicate with AdminPerms!
 * 
 * @author Lathanael (aka Philippe Leipold)
 *
 */
public class AdminPerms {
	
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
	
}
