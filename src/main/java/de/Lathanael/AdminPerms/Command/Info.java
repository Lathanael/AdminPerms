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

package de.Lathanael.AdminPerms.Command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import de.Lathanael.AdminPerms.bukkit.Main;

/**
 * @author Lathanael (aka Philippe Leipold)
 * @author SpaceManiac (original author)
 *
 */
public class Info extends BaseCommand {

	public Info() {
		name = "ap_info";
		permNode = "adminperms.info";
	}
	
	/* (non-Javadoc)
	 * @see de.Lathanael.AdminPerms.Command.BaseCommand#execute(org.bukkit.command.CommandSender, java.lang.String[])
	 */
	@Override
	public void execute(final CommandSender sender, final String[] args) {
		final Permission perm = Main.getInstance().getServer().getPluginManager().getPermission(args[0]);
		if (perm == null) {
			sender.sendMessage(ChatColor.RED + "Permission " + ChatColor.WHITE + args[0] + ChatColor.RED + " not found.");
		} else {
			sender.sendMessage(ChatColor.GREEN + "Info on permission " + ChatColor.WHITE + perm.getName() + ChatColor.GREEN + ":");
			sender.sendMessage(ChatColor.GREEN + "Default: " + ChatColor.WHITE + perm.getDefault());
			if (perm.getDescription() != null && perm.getDescription().length() > 0) {
				sender.sendMessage(ChatColor.GREEN + "Description: " + ChatColor.WHITE + perm.getDescription());
			}
			if (perm.getChildren() != null && perm.getChildren().size() > 0) {
				sender.sendMessage(ChatColor.GREEN + "Children: " + ChatColor.WHITE + perm.getChildren().size());
			}
		}
	}

	/* (non-Javadoc)
	 * @see de.Lathanael.AdminPerms.Command.BaseCommand#checkPerm(org.bukkit.command.CommandSender)
	 */
	@Override
	public boolean checkPerm(final CommandSender sender) {
		if (sender instanceof ConsoleCommandSender)
			return true;
		final Player player = (Player) sender;
		if (player.hasPermission(permNode))
			return true;
		return false;
	}

	/* (non-Javadoc)
	 * @see de.Lathanael.AdminPerms.Command.BaseCommand#checkArgs(java.lang.String[])
	 */
	@Override
	public boolean checkArgs(final String[] args) {
		return args != null && args.length >= 1;
	}

}
