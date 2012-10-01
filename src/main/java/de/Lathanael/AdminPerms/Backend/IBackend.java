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

package de.Lathanael.AdminPerms.Backend;

import org.bukkit.entity.Player;

/**
 * @author Lathanael (aka Philippe Leipold)
 *
 */
public interface IBackend {
	
	public abstract void save();
	
	public abstract void load();
	
	public abstract void reload();
	
	public abstract void save(final String playerName);
	
	public abstract void load(final String playerName);
	
	public abstract void reload(final String playerName);
	
	public abstract void createDefaultPlayerEntry(final Player player);
	
	abstract void createDefaultPlayerEntry(final String playerName);
}
