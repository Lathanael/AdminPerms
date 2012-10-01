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

import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import de.Lathanael.AdminPerms.Permissions.GroupHandler;
import de.Lathanael.AdminPerms.Permissions.PermissionsHandler;

/**
 * @author Lathanael (aka Philippe Leipold)
 * @author SpaceManiac (original author)
 */
public class GroupCommand extends BaseCommand {

	public GroupCommand() {
		name = "ap_group";
		permNode = "adminperms.group";
	}
	
	/* (non-Javadoc)
	 * @see de.Lathanael.AdminPerms.Command.BaseCommand#execute(org.bukkit.command.CommandSender, java.lang.String[])
	 */
	@Override
	public void execute(final CommandSender sender, final String[] args) {
		final String cmd = args[0].toLowerCase();
		
		if (cmd.equals("list") && checkPerm(sender, ".list")) {
			final String msg = buildMessage(GroupHandler.getInstance().getGroupNames());
			sender.sendMessage(ChatColor.AQUA + "Groups: " + ChatColor.GOLD + msg);
			return;
		} else if (cmd.equals("players") && checkPerm(sender, ".players")) {
			if (args.length < 3) {
				insufficientParams(sender, "players", "<group>", 3);
				return;
			}
			final String group = args[1].toLowerCase();
			int count = GroupHandler.getInstance().getGroup(group).getPlayers().size();
			final String msg = buildMessage(GroupHandler.getInstance().getGroup(group).getPlayers());
			sender.sendMessage(ChatColor.AQUA + "Users in " + ChatColor.GOLD + group 
					+ ChatColor.AQUA + " (" + ChatColor.GOLD + count + ChatColor.AQUA 
					+ "): " + ChatColor.GOLD + msg);
			return;
		} else if (cmd.equals("setperm") && checkPerm(sender, ".setperm")) {
			if (args.length < 3) {
				insufficientParams(sender, "setperm", "<group> <perm> (<value>)", 3);
				return;
			}
			final String group = args[1].toLowerCase();
			String perm = args[2];
			String world = null;
			boolean value = (args.length != 4) || Boolean.parseBoolean(args[3]);
			if (perm.contains(":")) {
				world = perm.substring(0, perm.indexOf(':'));
				perm = perm.substring(perm.indexOf(':') + 1);
			}
			if (world != null) {
				GroupHandler.getInstance().getGroup(group).addOrChangeWorldPermission(perm, value, world);
			} else {
				GroupHandler.getInstance().getGroup(group).addOrChangePermission(perm, value);
			}
			PermissionsHandler.getInstance().refreshPermissions();
			sender.sendMessage(ChatColor.AQUA + "Player " + ChatColor.GOLD + group 
					+ ChatColor.AQUA + " now has " + ChatColor.WHITE + perm + ChatColor.AQUA + " = "
					+ ChatColor.WHITE + value + ChatColor.AQUA + ".");
		}  else if (cmd.equals("unsetperm") && checkPerm(sender, ".unsetperm")) {
			if (args.length < 3) {
				insufficientParams(sender, "unsetperm", "<groupr> <perm> ", 3);
				return;
			}
			final String group = args[1].toLowerCase();
			String perm = args[2];
			String world = null;
			if (perm.contains(":")) {
				world = perm.substring(0, perm.indexOf(':'));
				perm = perm.substring(perm.indexOf(':') + 1);
			}
			if (world != null) {
				GroupHandler.getInstance().getGroup(group).removeWorldPermission(perm, world);
			} else {
				GroupHandler.getInstance().getGroup(group).removePermission(perm);
			}
			PermissionsHandler.getInstance().refreshPermissions();
			sender.sendMessage(ChatColor.AQUA + "Player " + ChatColor.WHITE + group + ChatColor.AQUA
					+ " no longer has " + ChatColor.WHITE + perm + ChatColor.AQUA + " set.");
		}
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

	private boolean checkPerm(final CommandSender sender, final String subNode) {
		if (sender instanceof ConsoleCommandSender)
			return true;
		final Player player = (Player) sender;
		if (player.hasPermission(permNode + subNode))
			return true;
		return false;
	}
	
	private void insufficientParams(final CommandSender sender, final String command,
			final String cmdArgs, final int argNr) {
		sender.sendMessage(ChatColor.RED + "The player " + command + " command requires "
				+ argNr + " arguments as input:");
		sender.sendMessage(ChatColor.RED + "/pplayer " + command + " " + cmdArgs);
		return;
	}
	
	private String buildMessage (final Set<String> list) {
		String msg = "";
		for (String s : list) {
			msg += ", " + s;
		}
		return msg.substring(0, msg.length() - 2);
	}
}
