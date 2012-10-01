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

import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;

import de.Lathanael.AdminPerms.Permissions.PermissionsHandler;
import de.Lathanael.AdminPerms.bukkit.ConfigEnum;

/**
 * @author Lathanael (aka Philippe Leipold)
 * @author SpaceManiac (original author)
 *
 */
public class Reload extends BaseCommand {

	public Reload() {
		name = "ap_reload";
		permNode = "adminperms.reload";
	}
	
	/* (non-Javadoc)
	 * @see de.Lathanael.AdminPerms.Command.BaseCommand#execute(org.bukkit.command.CommandSender, java.lang.String[])
	 */
	@Override
	public void execute(final CommandSender sender, final String[] args) {
		try {
			ConfigEnum.reload();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
		PermissionsHandler.getInstance().refreshPermissions();
		sender.sendMessage(ChatColor.GREEN + "Configuration and permissions reloaded.");
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
		return true;
	}

}
