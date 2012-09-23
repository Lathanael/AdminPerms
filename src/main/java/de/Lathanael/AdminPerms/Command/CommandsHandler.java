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

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import de.Lathanael.AdminPerms.bukkit.Main;

/**
 * @author Lathanael (aka Philippe Leipold)
 *
 * Command map etc. taken from AdminCmd.
 */
public class CommandsHandler implements CommandExecutor {

	private static CommandsHandler instance = new CommandsHandler();
	private HashMap<Command, BaseCommand> cmdMap = new HashMap<Command, BaseCommand>();

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmdMap.get(cmd) != null) {
			return executeCommand(sender, cmdMap.get(cmd), args);
		}
		return false;
	}
	private boolean executeCommand(CommandSender sender, BaseCommand cmd, String[] args) {
		if (checkPermissions(sender, cmd.permNode)) {
			if (cmd.checkArgs(args)) {
				cmd.execute(sender, args);
				return true;
			} else
				return false;
		} else {
			sender.sendMessage(ChatColor.RED + "You do not have the permission to use the following command: "
					+ ChatColor.AQUA + cmd.name + ChatColor.RED + "!");
			return true;
		}
	}

	public void registerCommand(Class<? extends BaseCommand> class1) {
		BaseCommand cmd = null;
		try {
			cmd = (BaseCommand) class1.newInstance();
			Main.getInstance().getCommand(cmd.name).setExecutor(instance);
			cmdMap.put(Main.getInstance().getCommand(cmd.name), cmd);
		} catch (InstantiationException e) {
			Main.getInstance().getLogger().warning("Could not create an Instance for: " + class1.getName());
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			Main.getInstance().getLogger().warning("Could not create an Instance for: " + class1.getName());
			e.printStackTrace();
		}
	}

	public BaseCommand getCmd(Command cmd) {
		return cmdMap.get(cmd);
	}
	
	public boolean checkPermissions(final CommandSender sender, final String permission) {
		if (sender instanceof ConsoleCommandSender)
			return true;
		else if (sender instanceof Player)
			return ((Player) sender).hasPermission(permission);
		return false;
	}
}
