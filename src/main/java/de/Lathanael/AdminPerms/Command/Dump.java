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
import java.util.Collections;
import java.util.Comparator;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.PermissionAttachmentInfo;

import de.Lathanael.AdminPerms.bukkit.Main;

/**
 * @author Lathanael (aka Philippe Leipold)
 * @author SpaceManiac (original author) *
 */
public class Dump extends BaseCommand {

	public Dump() {
		name = "ap_dump";
		permNode = "adminperms.dump";
	}
	
	/* (non-Javadoc)
	 * @see de.Lathanael.AdminPerms.Command.BaseCommand#execute(org.bukkit.command.CommandSender, java.lang.String[])
	 */
	@Override
	public void execute(final CommandSender sender, final String[] args) {
		int page;
		Permissible permissible;
		if (args == null || args.length <= 0) {
				permissible = sender;
				page = 1;
		} else if (args.length == 1) {
			try {
				permissible = sender;
				page = Integer.parseInt(args[0]);
			} catch (NumberFormatException ex) {
				permissible = Main.getInstance().getServer().getPlayer(args[0]);
				page = 1;
			}
		} else {
			permissible = Main.getInstance().getServer().getPlayer(args[0]);
			try {
				page = Integer.parseInt(args[1]);
			} catch (NumberFormatException ex) {
				page = 1;
			}
		}
		if (permissible == null) {
			sender.sendMessage(ChatColor.RED + "Player " + ChatColor.WHITE + args[0] + ChatColor.RED + " not found.");
		} else {
			ArrayList<PermissionAttachmentInfo> dump = new ArrayList<PermissionAttachmentInfo>(permissible.getEffectivePermissions());
			Collections.sort(dump, new Comparator<PermissionAttachmentInfo>() {
				public int compare(PermissionAttachmentInfo a, PermissionAttachmentInfo b) {
					return a.getPermission().compareTo(b.getPermission());
				}
			});
			int numpages = 1 + (dump.size() - 1) / 8;
			if (page > numpages) {
				page = numpages;
			} else if (page < 1) {
				page = 1;
			}
			ChatColor g = ChatColor.GREEN, w = ChatColor.WHITE, r = ChatColor.RED;
			int start = 8 * (page - 1);
			sender.sendMessage(ChatColor.RED + "[==== " + ChatColor.GREEN + "Page " + page + " of " + numpages + ChatColor.RED + " ====]");
			for (int i = start; i < start + 8 && i < dump.size(); ++i) {
				PermissionAttachmentInfo info = dump.get(i);
				if (info.getAttachment() == null) {
					sender.sendMessage(g + "Node " + w + info.getPermission() + g + "=" + w + info.getValue() + g + " (" + r + "default" + g + ")");
				} else {
					sender.sendMessage(g + "Node " + w + info.getPermission() + g + "=" + w + info.getValue() + g + " (" + w + info.getAttachment().getPlugin().getDescription().getName() + g + ")");
				}
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
		return true;
	}
}
