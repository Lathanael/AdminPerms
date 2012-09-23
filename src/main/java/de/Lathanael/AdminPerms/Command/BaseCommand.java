/*************************************************************************
 * Copyright (C) 2011-2012 Philippe Leipold
 *
 * This file is part of ForceCraft.
 *
 * ForceCraft is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ForceCraft is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ForceCraft. If not, see <http://www.gnu.org/licenses/>.
 *
 **************************************************************************/

package de.Lathanael.AdminPerms.Command;

import org.bukkit.command.CommandSender;

/**
 * @author Lathanael (aka Philippe Leipold)
 *
 * With help from Dark_Balors AdminCmd
 */
public abstract class BaseCommand {

	public String name = "";
	public String permNode = "";

	public abstract void execute(final CommandSender sender, final String[] args);

	public abstract boolean checkPerm(final CommandSender sender);

	public abstract boolean checkArgs(final String[] args);
}
