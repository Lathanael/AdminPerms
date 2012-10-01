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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import de.Lathanael.AdminPerms.Permissions.PermissionsHandler;
import de.Lathanael.AdminPerms.Permissions.PlayerHandler;

/**
 * @author Lathanael (aka Philippe Leipold)
 * @author SpaceManiac (original author)
 *
 */
public class PlayerCommand extends BaseCommand {
	
	public PlayerCommand() {
		name = "ap_player";
		permNode = "adminperms.player";
	}

	/* (non-Javadoc)
	 * @see de.Lathanael.AdminPerms.Command.BaseCommand#execute(org.bukkit.command.CommandSender, java.lang.String[])
	 */
	@Override
	public void execute(final CommandSender sender, final String[] args) {
		final String cmd = args[0].toLowerCase();
		final String player	= args[1];
		
		if (PlayerHandler.getInstance().getPlayer(player) == null) {
			PlayerHandler.getInstance().addPlayer(player);
		}
		if (cmd.equals("groups") && checkPerm(sender, ".groups")) {
			final Set<String> groups = PlayerHandler.getInstance().getPlayer(player.toLowerCase()).getGroups();
			if (groups == null) {
				sender.sendMessage(ChatColor.GOLD + player + ChatColor.AQUA
						+ "is not member of any group!");
				return;
			}
			int count = 0;
			String msg = buildMessage(groups);
			sender.sendMessage(ChatColor.GOLD + player + ChatColor.AQUA + "is in the following groups(" 
						+ ChatColor.GOLD + count + ChatColor.AQUA + "):");
			sender.sendMessage(ChatColor.AQUA + msg);
			return;
		} else if (cmd.equals("setgroup") && checkPerm(sender, ".setgroup")) {
			if (args.length < 3) {
				insufficientParams(sender, "setgroup", "<player> <group(s)>", 3);
				return;
			}
			final String[] groups = args[2].split(",");
			PlayerHandler.getInstance().getPlayer(player).setGroups(Arrays.asList(groups));
			PermissionsHandler.getInstance().refreshPermissions(player);
			sender.sendMessage(ChatColor.AQUA + "Player " + ChatColor.GOLD + player 
					+ ChatColor.AQUA + " is now in the following group(s):");
			sender.sendMessage(ChatColor.GREEN + args[3]);
			return;
		} else if (cmd.equals("addgroup") && checkPerm(sender, ".addgroup")) {
			if (args.length < 3) {
				insufficientParams(sender, "addgroup", "<player> <group(s)>", 3);
				return;
			}
			final Set<String> list = PlayerHandler.getInstance().getPlayer(player).getGroups();
			final List<String> groups = new ArrayList<String>(Arrays.asList(args[2].split(",")));
			final List<String> inGroup = new ArrayList<String>();
			for (String group : groups) {
				if (list.contains(group.toLowerCase())) {
					inGroup.add(group);
				}
			}
			groups.removeAll(inGroup);
			String msg = "";
			if (!inGroup.isEmpty()) {
				msg = buildMessage(inGroup);
				sender.sendMessage(ChatColor.RED + "Player " + ChatColor.GOLD + player 
						+ ChatColor.RED + " was already in the following group(s):");
				sender.sendMessage(ChatColor.GRAY + msg);
			}
			list.addAll(groups);
			PlayerHandler.getInstance().getPlayer(player).addGroups(groups);
			PermissionsHandler.getInstance().refreshPermissions();
			msg = buildMessage(groups);
			sender.sendMessage(ChatColor.AQUA + "The following group(s) have been added to "
					+ ChatColor.GOLD + player + ChatColor.AQUA + ":");
			sender.sendMessage(ChatColor.GRAY + msg);
		} else if (cmd.equals("removegroup") && checkPerm(sender, ".removegroup")) {
			if (args.length < 3) {
				insufficientParams(sender, "removegroup", "<player> <group(s)>", 3);
				return;
			}
			final Set<String> list = PlayerHandler.getInstance().getPlayer(player).getGroups();
			final List<String> groups = new ArrayList<String>(Arrays.asList(args[2].split(",")));
			final List<String> inGroup = new ArrayList<String>();
			for (String group : groups) {
				if (list.contains(group.toLowerCase())) {
					inGroup.add(group);
				}
			}
			groups.removeAll(inGroup);
			String msg = "";
			if (!inGroup.isEmpty()) {
				msg = buildMessage(inGroup);
				sender.sendMessage(ChatColor.RED + "Player " + ChatColor.GOLD + player 
						+ ChatColor.RED + " was not in the following group(s):");
				sender.sendMessage(ChatColor.GRAY + msg);
			}
			list.addAll(groups);
			PlayerHandler.getInstance().getPlayer(player).removeGroups(groups);
			PermissionsHandler.getInstance().refreshPermissions();
			msg = buildMessage(groups);
			sender.sendMessage(ChatColor.AQUA + "The following group(s) have been removed from "
					+ ChatColor.GOLD + player + ChatColor.AQUA + ":");
			sender.sendMessage(ChatColor.GRAY + msg);
		} else if (cmd.equals("setperm") && checkPerm(sender, ".setperm")) {
			if (args.length < 3) {
				insufficientParams(sender, "setperm", "<player> <perm> (<value>)", 3);
				return;
			}
			String perm = args[2];
			String world = null;
			boolean value = (args.length != 4) || Boolean.parseBoolean(args[3]);
			if (perm.contains(":")) {
				world = perm.substring(0, perm.indexOf(':'));
				perm = perm.substring(perm.indexOf(':') + 1);
			}
			if (world != null) {
				PlayerHandler.getInstance().getPlayer(player).addOrChangeWorldPermission(perm, value, world);
			} else {
				PlayerHandler.getInstance().getPlayer(player).addOrChangePermission(perm, value);
			}
			PermissionsHandler.getInstance().refreshPermissions();
			sender.sendMessage(ChatColor.AQUA + "Player " + ChatColor.GOLD + player 
					+ ChatColor.AQUA + " now has " + ChatColor.WHITE + perm + ChatColor.AQUA + " = "
					+ ChatColor.WHITE + value + ChatColor.AQUA + ".");
		}  else if (cmd.equals("unsetperm") && checkPerm(sender, ".unsetperm")) {
			if (args.length < 3) {
				insufficientParams(sender, "unsetperm", "<player> <perm> ", 3);
				return;
			}
			String perm = args[2];
			String world = null;
			if (perm.contains(":")) {
				world = perm.substring(0, perm.indexOf(':'));
				perm = perm.substring(perm.indexOf(':') + 1);
			}
			if (world != null) {
				PlayerHandler.getInstance().getPlayer(player).removeWorldPermission(perm, world);
			} else {
				PlayerHandler.getInstance().getPlayer(player).removePermission(perm);
			}
			PermissionsHandler.getInstance().refreshPermissions();
			sender.sendMessage(ChatColor.AQUA + "Player " + ChatColor.WHITE + player + ChatColor.AQUA
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
	
	private String buildMessage (final List<String> list) {
		String msg = "";
		for (String s : list) {
			msg += ", " + s;
		}
		return msg.substring(0, msg.length() - 2);
	}
	
	private String buildMessage (final Set<String> list) {
		String msg = "";
		for (String s : list) {
			msg += ", " + s;
		}
		return msg.substring(0, msg.length() - 2);
	}
}
