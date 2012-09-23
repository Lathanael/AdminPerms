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

package de.Lathanael.AdminPerms.Backend.File;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import de.Lathanael.AdminPerms.Backend.IBackend;
import de.Lathanael.AdminPerms.Interface.SubDirFileFilter;
import de.Lathanael.AdminPerms.Interface.SubDirFileFilter.Type;
import de.Lathanael.AdminPerms.Logging.DebugLog;
import de.Lathanael.AdminPerms.Permissions.Group;
import de.Lathanael.AdminPerms.Permissions.GroupHandler;
import de.Lathanael.AdminPerms.Permissions.PermPlayer;
import de.Lathanael.AdminPerms.Permissions.PlayerHandler;
import de.Lathanael.AdminPerms.bukkit.Main;

/**
 * @author Lathanael (aka Philippe Leipold)
 *
 */
public class FileBackend implements IBackend {

	private String path;
	
	public FileBackend(final String path) {
		this.path = path;
	}
	/*
	 * (non-Javadoc)
	 * @see de.Lathanael.AdminPerms.Backend.IBackend#save()
	 */
	@Override
	public void save() {
		SubDirFileFilter filter = new SubDirFileFilter();
		List<File> files = filter.getFiles(new File(path + File.separator + "groups"),
				filter.new PatternFilter(Type.FILE, ".yml"), true);
		YamlConfiguration configFile;
		String name;
		Map<String, Object> values = new HashMap<String, Object>();
		Group group;
		for (File file : files) {
			//file.delete();
			configFile = YamlConfiguration.loadConfiguration(file);
			name = configFile.getName().toLowerCase();
			group = GroupHandler.getInstance().getGroup(name);
			values.put("permissions", GroupHandler.getInstance().getGroupPermissions(name));
			values.put("info", group.getInfos());
			values.put("inheritance", group.getInheritance());
			values.put("worlds", group.getAllWorldPermissions());
			values.put("promoteTo", group.getPromoteTo());
			values.put("demoteTo", group.getDemoteTo());
			configFile.addDefaults(values);
			configFile.options().copyDefaults(true);
			try {
				configFile.save(file);
			} catch (IOException e) {
				DebugLog.INSTANCE.log(Level.SEVERE, "Failure while saving file for group: " + name, e);
				Main.getInstance().getLogger().severe("Failure while saving file for group: " + name);
				Main.getInstance().getLogger().severe("Cause: " + e.getCause());
				Main.getInstance().getLogger().severe("For exact cause refer to the debug.log");
			}
		}
		files = filter.getFiles(new File(path + File.separator + "players"),
				filter.new PatternFilter(Type.FILE, ".yml"), true);
		for (File file : files) {
			//file.delete();
			configFile = YamlConfiguration.loadConfiguration(file);
			name = configFile.getName();
			values.put("permissions", PlayerHandler.getInstance().getPlayer(name).getPermissions());
			values.put("info", PlayerHandler.getInstance().getPlayer(name).getInfos());
			values.put("worlds", PlayerHandler.getInstance().getPlayer(name).getAllWorldPermissions());
			values.put("groups", PlayerHandler.getInstance().getPlayer(name).getGroups());
			configFile.addDefaults(values);
			configFile.options().copyDefaults(true);
			try {
				configFile.save(file);
			} catch (IOException e) {
				DebugLog.INSTANCE.log(Level.SEVERE, "Failure while saving file for player: " + name, e);
				Main.getInstance().getLogger().severe("Failure while saving file for player: " + name);
				Main.getInstance().getLogger().severe("Cause: " + e.getCause());
				Main.getInstance().getLogger().severe("For exact cause refer to the debug.log");
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see de.Lathanael.AdminPerms.Backend.IBackend#load()
	 */
	@Override
	public void load() {
		SubDirFileFilter filter = new SubDirFileFilter();
		List<File> files = filter.getFiles(new File(path + File.separator + "groups"),
				filter.new PatternFilter(Type.FILE, ".yml"), true);
		YamlConfiguration configFile;
		ConfigurationSection sec;
		int rank;
		String name, promoteTo, demoteTo;
		List<String> inheritance, groups;
		Map<String, Boolean> perms = new HashMap<String, Boolean>();
		Map<String, Map<String, Boolean>> worldPerms = new HashMap<String, Map<String, Boolean>>();
		Map<String, Boolean> worldPerm = new HashMap<String, Boolean>();
		Map<String, String> info = new HashMap<String, String>();
		for (File file : files) {
			configFile = YamlConfiguration.loadConfiguration(file);
			rank = configFile.getInt("rank");
			name = configFile.getName().toLowerCase();
			promoteTo = configFile.getString("promoteTo");
			demoteTo = configFile.getString("demoteTo");
			inheritance = configFile.getStringList("inheritance");
			sec = configFile.getConfigurationSection("permissions");
			for (Map.Entry<String, Object> entry : sec.getValues(false).entrySet()) {
				try {
					perms.put(entry.getKey(),(Boolean) entry.getValue());
				} catch (ClassCastException ex) {
					DebugLog.INSTANCE.warning("Could not read permission entry '" + entry.getKey() 
							+ "' from group: " + name);
				}
			}
			sec = configFile.getConfigurationSection("info");
			for (String entry : sec.getKeys(false)) {
				info.put(entry, sec.getString(entry));
			}
			sec = configFile.getConfigurationSection("worlds");
			for (String w : sec.getKeys(false)) {
				for (Map.Entry<String, Object> entry : sec.getValues(false).entrySet()) {
					try {
						worldPerm.put(entry.getKey(), (Boolean) entry.getValue());
					} catch (ClassCastException ex) {
						DebugLog.INSTANCE.warning("Could not read permission entry '" + entry.getKey() 
								+ "' from world " + w + "in group: " + name);
					}
				}
				worldPerms.put(w, worldPerm);
			}
			GroupHandler.getInstance().addGroup(new Group(name, rank, perms, inheritance, info, worldPerms, promoteTo, demoteTo));
		}
		GroupHandler.getInstance().checkDefaultGroup();
		GroupHandler.getInstance().copyInheritatedPerms();
		
		files = filter.getFiles(new File(path + File.separator + "players"),
				filter.new PatternFilter(Type.FILE, ".yml"), true);
		for (File file : files) {
			configFile = YamlConfiguration.loadConfiguration(file);
			name = file.getName().toLowerCase();
			groups = configFile.getStringList("groups");
			sec = configFile.getConfigurationSection("info");
			for (String entry : sec.getKeys(false)) {
				info.put(entry, sec.getString(entry));
			}
			sec = configFile.getConfigurationSection("permissions");
			for (Map.Entry<String, Object> entry : sec.getValues(false).entrySet()) {
				try {
					perms.put(entry.getKey(),(Boolean) entry.getValue());
				} catch (ClassCastException ex) {
					DebugLog.INSTANCE.warning("Could not read permission entry '" + entry.getKey() 
							+ "' from player: " + name);
				}
			}
			sec = configFile.getConfigurationSection("worlds");
			for (String w : sec.getKeys(false)) {
				for (Map.Entry<String, Object> entry : sec.getValues(false).entrySet()) {
					try {
						worldPerm.put(entry.getKey(), (Boolean) entry.getValue());
					} catch (ClassCastException ex) {
						DebugLog.INSTANCE.warning("Could not read permission entry '" + entry.getKey() 
								+ "' from world " + w + "from player: " + name);
					}
				}
				worldPerms.put(w, worldPerm);
			}
			PlayerHandler.getInstance().addPlayer(new PermPlayer(name, perms, groups, info, worldPerms));
		}
	}

	/*
	 * (non-Javadoc)
	 * @see de.Lathanael.AdminPerms.Backend.IBackend#reload()
	 */
	@Override
	public void reload() {
		save();
		PlayerHandler.getInstance().resetHandler();
		GroupHandler.getInstance().resetHandler();
		load();
	}
	@Override
	public void save(final String playerName) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void load(final String playerName) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void reload(final String playerName) {
		save(playerName);
		load(playerName);
	}
}
