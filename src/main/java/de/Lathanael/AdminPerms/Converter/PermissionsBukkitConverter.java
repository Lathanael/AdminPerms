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

package de.Lathanael.AdminPerms.Converter;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import de.Lathanael.AdminPerms.Backend.IBackend;
import de.Lathanael.AdminPerms.Logging.DebugLog;
import de.Lathanael.AdminPerms.Permissions.Group;
import de.Lathanael.AdminPerms.Permissions.GroupHandler;
import de.Lathanael.AdminPerms.Permissions.PermPlayer;
import de.Lathanael.AdminPerms.Permissions.PermissionsHandler;
import de.Lathanael.AdminPerms.Permissions.PlayerHandler;
import de.Lathanael.AdminPerms.bukkit.Main;

/**
 * @author Lathanael (aka Philippe Leipold)
 *
 */
public class PermissionsBukkitConverter extends IConverter {

	private static PermissionsBukkitConverter instance = new PermissionsBukkitConverter();
	private final String permBukkitPath;
	private IBackend copyBackend;
	
	public PermissionsBukkitConverter() {
		pluginsFolder = getPluginsFolder(Main.getInstance().getDataFolder());
		permBukkitPath = pluginsFolder + File.separator + "PermissionsBukkit";
		copyBackend = PermissionsHandler.getInstance().getBackend();
	}
	
	public static PermissionsBukkitConverter getInstance() {
		return instance;
	}


	@Override
	public boolean run() {
		final File permBukkit = new File(permBukkitPath);
		if (permBukkit.exists()) {
			convertGroupData();
			convertPlayerData();
			copyBackend.reload();
			return true;
		}
		DebugLog.INSTANCE.info("PermissionsBukkit not present!");
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.Lathanael.AdminPerms.Converter.IConverter#convertPlayerData()
	 */
	@Override
	public void convertPlayerData() {
		List<String> groups;
		Map<String, Boolean> perms = new HashMap<String, Boolean>();
		Map<String, Map<String, Boolean>> worldPerms = new HashMap<String, Map<String, Boolean>>();
		Map<String, Boolean> worldPerm = new HashMap<String, Boolean>();
		YamlConfiguration configFile;
		ConfigurationSection sec;
		configFile =  new YamlConfiguration();
		configFile.options().pathSeparator('/');
		try {
			configFile.load(permBukkitPath + File.separator + "config.yml");
		} catch (Exception e) {
			DebugLog.INSTANCE.log(Level.SEVERE, "Failure loading PermissionsBukkit config file!", e);
			return;
		}
		sec = configFile.getConfigurationSection("users");
		for (String player : sec.getKeys(false)) {
			groups = configFile.getStringList("users/" + player + "/groups");
			sec = configFile.getConfigurationSection("users/" + player + "/permissions");
			if (sec != null) {
				for (String entry : sec.getKeys(false)) {
					perms.put(entry, sec.getBoolean(entry));
				}
			}
			sec = configFile.getConfigurationSection("users/" + player + "/worlds");
			if (sec != null) {
				for (String w : sec.getKeys(false)) {
					for (String entry : sec.getConfigurationSection(w).getKeys(false)) {
						worldPerm.put(entry, sec.getBoolean("users/" + player + "/" + w + "/" + entry));
					}
					worldPerms.put(w, worldPerm);
				}
			}
			copyBackend.createDefaultPlayerEntry(player.toLowerCase());
			copyBackend.loadPlayer(player.toLowerCase());
			final PermPlayer copyPlayer = PlayerHandler.getInstance().getPlayer(player.toLowerCase());
			copyPlayer.setGroups(groups);
			copyPlayer.setPermissions(perms);
			copyPlayer.setWorldPermissions(worldPerms);
			copyPlayer.setInfos(copyPlayer.getInfos());
			copyBackend.savePlayer(player.toLowerCase());
		}
	}

	/*
	 * (non-Javadoc)
	 * @see de.Lathanael.AdminPerms.Converter.IConverter#convertGroupData()
	 */
	@Override
	public void convertGroupData() {
		List<String> inheritance;
		Map<String, Boolean> perms = new HashMap<String, Boolean>();
		Map<String, Map<String, Boolean>> worldPerms = new HashMap<String, Map<String, Boolean>>();
		Map<String, Boolean> worldPerm = new HashMap<String, Boolean>();
		YamlConfiguration configFile;
		ConfigurationSection sec;
		configFile =  new YamlConfiguration();
		configFile.options().pathSeparator('/');
		try {
			configFile.load(permBukkitPath + File.separator + "config.yml");
		} catch (Exception e) {
			DebugLog.INSTANCE.log(Level.SEVERE, "Failure loading PermissionsBukkit config file!", e);
			return;
		}
		sec = configFile.getConfigurationSection("groups");
		for (String group : sec.getKeys(false)) {
			inheritance = configFile.getStringList("groups/" + group + "/inheritance");
			sec = configFile.getConfigurationSection("groups/" + group + "/permissions");
			if (sec != null) {
				for (String entry : sec.getKeys(false)) {
					perms.put(entry, sec.getBoolean(entry));
				}
			}
			sec = configFile.getConfigurationSection("groups/" + group + "/worlds");
			if (sec != null) {
				for (String w : sec.getKeys(false)) {
					for (String entry : sec.getConfigurationSection(w).getKeys(false)) {
						worldPerm.put(entry, sec.getBoolean("groups/" + group + "/" + w + "/" + entry));
					}
					worldPerms.put(w, worldPerm);
				}
			}
			copyBackend.createDefaultGroupEntry(group.toLowerCase());
			copyBackend.loadGroup(group.toLowerCase());
			final Group copyGroup = GroupHandler.getInstance().getGroup(group.toLowerCase());
			copyGroup.resetPermissions(perms);
			copyGroup.resetWorldPermissions(worldPerms);
			copyGroup.setInheritance(inheritance);
			copyGroup.setRank(0);
			copyGroup.setDemoteTo("");
			copyGroup.setPromoteTo("");
			copyGroup.setInfos(copyGroup.getInfos());
			copyBackend.saveGroup(group.toLowerCase());
		}
	}
}
