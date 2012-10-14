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

import de.Lathanael.AdminPerms.Logging.DebugLog;
import de.Lathanael.AdminPerms.Permissions.PermPlayer;
import de.Lathanael.AdminPerms.Permissions.PermissionsHandler;
import de.Lathanael.AdminPerms.Permissions.PlayerHandler;

/**
 * @author Lathanael (aka Philippe Leipold)
 * @author SpaceManiac (original author) *
 */
public class Rank extends BaseCommand {

	public Rank() {
		name = "ap_rank";
		permNode = "adminperms.rank";
	}

	/* (non-Javadoc)
	 * @see de.Lathanael.AdminPerms.Command.BaseCommand#execute(org.bukkit.command.CommandSender,
	 * java.lang.String[])
	 */
	@Override
	public void execute(final CommandSender sender, final String[] args) {
		final String player = args[1].toLowerCase();
		final String stat = args[0].toLowerCase();
		boolean console = false;
		if (sender instanceof ConsoleCommandSender) {
			console = true;
		}
		if (PlayerHandler.getInstance().getPlayer(player) == null) {
			sender.sendMessage(ChatColor.RED + "A player with the name " + ChatColor.AQUA
					+ player + ChatColor.RED + " does not exist.");
			return;
		}
		if (!console) {
			if (PlayerHandler.getInstance().getPlayer(((Player) sender)
					.getName().toLowerCase()) == null) {
				sender.sendMessage(ChatColor.RED + "Error while performing the rank command."
						+ " Refer to the debug.log.");
				DebugLog.INSTANCE.warning("There was no PermPlayer object for a player named "
						+ ((Player) sender).getName());
				DebugLog.INSTANCE.warning("Possible error in the register process!");
				return;
			}
		}
		final PermPlayer p1 = PlayerHandler.getInstance().getPlayer(((Player) sender)
				.getName().toLowerCase());
		final PermPlayer p2 = PlayerHandler.getInstance().getPlayer(player);
		if (stat.contains("pro")) {
			if (console) {
				p2.promote();
				PermissionsHandler.getInstance().refreshPermissions(player);
			} else if (sender.hasPermission(permNode + ".promote") 
					&& p1.getHighestGroup().getRank() >= p2.getHighestGroup().getRank()){
				p2.promote();
				PermissionsHandler.getInstance().refreshPermissions(player);
			}
		} else if (stat.contains("de")) {
			if (console) {
				p2.demote();
				PermissionsHandler.getInstance().refreshPermissions(player);
			} else if (sender.hasPermission(permNode + ".demote") 
					&& p1.getHighestGroup().getRank() >= p2.getHighestGroup().getRank()){
				p2.demote();
				PermissionsHandler.getInstance().refreshPermissions(player);
			}
			sender.sendMessage(ChatColor.GREEN + "Player " + ChatColor.WHITE + player
					+ ChatColor.GREEN + " has been promoted.");	
		}
		PermissionsHandler.getInstance().refreshPermissions(player);	
	}

	/* (non-Javadoc)
	 * @see de.Lathanael.AdminPerms.Command.BaseCommand#checkPerm(org.bukkit.command.CommandSender)
	 */
	@Override
	public boolean checkPerm(final CommandSender sender) {
		return true;
	}

	/* (non-Javadoc)
	 * @see de.Lathanael.AdminPerms.Command.BaseCommand#checkArgs(java.lang.String[])
	 */
	@Override
	public boolean checkArgs(final String[] args) {
		return args != null && args.length >= 2;
	}
}
