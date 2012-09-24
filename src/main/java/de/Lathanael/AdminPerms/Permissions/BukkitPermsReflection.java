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

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.PermissibleBase;

import de.Lathanael.AdminPerms.Logging.DebugLog;

/**
 * @author Lathanael (aka Philippe Leipold)
 *
 */
public class BukkitPermsReflection {

	private Set<Player> alreadyReplaced = new HashSet<Player>();
	
	public boolean replacePermissible(final Player player, final Permissible perm) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		if (alreadyReplaced.contains(player)) {
			return false;
		}
		Class<?> humanEntity;
		try {
			humanEntity = Class.forName("org.bukkit.craftbukkit.entity.CraftHumanEntity");
		} catch (ClassNotFoundException e) {
			DebugLog.INSTANCE.log(Level.WARNING, "Could not retrieve HumanEntity Class. This may be caused by an unsupported server implementation.", e);
			return false;
		}
		final Field fieldPerm = humanEntity.getDeclaredField("perm");
		fieldPerm.setAccessible(true);
		final PermissibleBase perms = (PermissibleBase) fieldPerm.get(player);
		final Field fieldPermissions = PermissibleBase.class.getDeclaredField("permissions");
		fieldPermissions.setAccessible(true);
		fieldPermissions.set(perm, fieldPermissions.get(perms));
		final Field fieldAttachment = PermissibleBase.class.getDeclaredField("attachments");
		fieldAttachment.setAccessible(true);
		fieldAttachment.set(perm, fieldAttachment.get(perms));
		fieldPerm.set(player, perm);
		alreadyReplaced.add(player);
		return true;
	}
}
