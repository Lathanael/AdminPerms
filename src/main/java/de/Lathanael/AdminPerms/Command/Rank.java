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
	 * @see de.Lathanael.AdminPerms.Command.BaseCommand#execute(org.bukkit.command.CommandSender, java.lang.String[])
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
			PlayerHandler.getInstance().addPlayer(player);
		}
		
		if (stat.contains("pro")) {
			if (console) {
				PlayerHandler.getInstance().getPlayer(player).promote();
				PermissionsHandler.getInstance().refreshPermissions(player);
			} else if (sender.hasPermission(permNode + ".promote")){
				PlayerHandler.getInstance().getPlayer(player).demote();
				PermissionsHandler.getInstance().refreshPermissions(player);
			}
		} else if (stat.contains("de")) {
			if (console) {
				PlayerHandler.getInstance().getPlayer(player).demote();
				PermissionsHandler.getInstance().refreshPermissions(player);
			} else if (sender.hasPermission(permNode + ".demote")){
				PlayerHandler.getInstance().getPlayer(player).demote();
				PermissionsHandler.getInstance().refreshPermissions(player);
			}
			sender.sendMessage(ChatColor.GREEN + "Player " + ChatColor.WHITE + player + ChatColor.GREEN + " has been promoted.");	
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
