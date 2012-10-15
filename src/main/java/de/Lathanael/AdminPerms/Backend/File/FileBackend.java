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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

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
		Group group;
		for (File file : files) {
			configFile = new YamlConfiguration();
			configFile.options().pathSeparator('/');
			name = file.getName().toLowerCase().substring(0, file.getName().lastIndexOf('.'));
			if (GroupHandler.getInstance().getGroup(name) == null)
				continue;
			group = GroupHandler.getInstance().getGroup(name);
			for (Map.Entry<String, String> entry : group.getInfos().entrySet()) {
				configFile.set("info" + "/" + entry.getKey(), entry.getValue());
			}
			for (Map.Entry<String, Boolean> entry : GroupHandler.getInstance().getGroupPermissions(name).entrySet()) {
				configFile.set("permissions" + "/" + entry.getKey(), entry.getValue());
			}
			for (Map.Entry<String, Map<String, Boolean>> entry : group.getAllWorldPermissions().entrySet()) {
				for (Map.Entry<String, Boolean> entry2 : entry.getValue().entrySet()) {
					configFile.set("worlds" + "/" + entry.getKey() + "/" + entry2.getKey() , entry2.getValue());
				}
			}			
			configFile.set("inheritance", group.getInheritance());
			configFile.set("promoteTo", group.getPromoteTo());
			configFile.set("demoteTo", group.getDemoteTo());
			configFile.set("rank", group.getRank());
			// TODO: AutoRank feature
			configFile.set("time", -1);
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
			configFile = new YamlConfiguration();
			configFile.options().pathSeparator('/');
			name = file.getName().toLowerCase().substring(0, file.getName().lastIndexOf('.'));
			for (Map.Entry<String, String> entry : PlayerHandler.getInstance().getPlayer(name).getInfos().entrySet()) {
				configFile.set("info/" + entry.getKey(), entry.getValue());
			}
			for (Map.Entry<String, Boolean> entry : PlayerHandler.getInstance().getPlayer(name).getPermissions().entrySet()) {
				configFile.set("permissions/" + entry.getKey(), entry.getValue());
			}
			for (Map.Entry<String, Map<String, Boolean>> entry : PlayerHandler.getInstance().getPlayer(name).getAllWorldPermissions().entrySet()) {
				for (Map.Entry<String, Boolean> entry2 : entry.getValue().entrySet()) {
					configFile.set("worlds/" + entry.getKey() + "/" + entry2.getKey() , entry2.getValue());
				}
			}
			configFile.set("groups", new ArrayList<String>(PlayerHandler.getInstance().getPlayer(name).getGroups()));
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
			worldPerm.clear();
			perms.clear();
			worldPerm.clear();
			info.clear();
			configFile =  new YamlConfiguration();
			configFile.options().pathSeparator('/');
			try {
				configFile.load(file);
			} catch (Exception e) {
				DebugLog.INSTANCE.log(Level.SEVERE, "Failure while loadig the following group file:" + file.getName(), e);
			}
			rank = configFile.getInt("rank", 0);
			name = file.getName().toLowerCase().substring(0, file.getName().lastIndexOf('.'));
			promoteTo = configFile.getString("promoteTo", "");
			demoteTo = configFile.getString("demoteTo", "");
			inheritance = configFile.getStringList("inheritance");
			sec = configFile.getConfigurationSection("permissions");
			if (sec != null) {
				for (String entry : sec.getKeys(false)) {
					perms.put(entry, sec.getBoolean(entry));
				}
			}
			sec = configFile.getConfigurationSection("info");
			if (sec != null) {
				for (String entry : sec.getKeys(false)) {
					info.put(entry, sec.getString(entry));
				}
			}
			sec = configFile.getConfigurationSection("worlds");
			if (sec != null) {
				for (String w : sec.getKeys(false)) {
					for (String entry : sec.getConfigurationSection(w).getKeys(false)) {
						worldPerm.put(entry, sec.getBoolean(w + "/" + entry));
					}
					worldPerms.put(w, worldPerm);
				}
			}
			GroupHandler.getInstance().addGroup(new Group(name, rank, perms, inheritance, info, worldPerms, promoteTo, demoteTo));
		}
		GroupHandler.getInstance().checkDefaultGroup();
		GroupHandler.getInstance().copyInheritatedPerms();
		
		files = filter.getFiles(new File(path + File.separator + "players"),
				filter.new PatternFilter(Type.FILE, ".yml"), true);
		for (File file : files) {
			worldPerm.clear();
			perms.clear();
			worldPerm.clear();
			info.clear();
			configFile = new YamlConfiguration();
			configFile.options().pathSeparator('/');
			try {
				configFile.load(file);
			} catch (Exception e) {
				DebugLog.INSTANCE.log(Level.SEVERE, "Failure while loadig the following player file:" + file.getName(), e);
			}
			name = file.getName().toLowerCase().substring(0, file.getName().lastIndexOf('.'));
			groups = configFile.getStringList("groups");
			sec = configFile.getConfigurationSection("info");
			if (sec != null) {
				for (String entry : sec.getKeys(false)) {
					info.put(entry, sec.getString(entry));
				}
			}
			sec = configFile.getConfigurationSection("permissions");
			if (sec != null) {
				for (String entry : sec.getKeys(false)) {
					perms.put(entry, sec.getBoolean(entry));
				}
			}
			sec = configFile.getConfigurationSection("worlds");
			if (sec != null) {
				for (String w : sec.getKeys(false)) {
					for (String entry : sec.getConfigurationSection(w).getKeys(false)) {
						worldPerm.put(entry, sec.getBoolean(w + "/" + entry));
					}
					worldPerms.put(w, worldPerm);
				}
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
	
	/*
	 * (non-Javadoc)
	 * @see de.Lathanael.AdminPerms.Backend.IBackend#save(java.lang.String)
	 */
	@Override
	public void savePlayer(final String playerName) {
		YamlConfiguration configFile;
		Map<String, Object> values = new HashMap<String, Object>();
		File file = new File(Main.getInstance().getDataFolder() + File.separator + "players", playerName.toLowerCase() + ".yml");
		if (!file.exists()) {
			createDefaultPlayerEntry(playerName);
		}
		configFile = new YamlConfiguration();
		configFile.options().pathSeparator('/');
		values.clear();
		String name = file.getName().toLowerCase().substring(0, file.getName().lastIndexOf('.'));
		for (Map.Entry<String, String> entry : PlayerHandler.getInstance().getPlayer(name).getInfos().entrySet()) {
			configFile.set("info/" + entry.getKey(), entry.getValue());
		}
		for (Map.Entry<String, Boolean> entry : PlayerHandler.getInstance().getPlayer(name).getPermissions().entrySet()) {
			configFile.set("permissions/" + entry.getKey(), entry.getValue());
		}
		for (Map.Entry<String, Map<String, Boolean>> entry : PlayerHandler.getInstance().getPlayer(name).getAllWorldPermissions().entrySet()) {
			for (Map.Entry<String, Boolean> entry2 : entry.getValue().entrySet()) {
				configFile.set("worlds/" + entry.getKey() + "/" + entry2.getKey() , entry2.getValue());
			}
		}
		configFile.set("groups", new ArrayList<String>(PlayerHandler.getInstance().getPlayer(name).getGroups()));
		try {
			configFile.save(file);
		} catch (IOException e) {
			DebugLog.INSTANCE.log(Level.SEVERE, "Failure while saving file for player: " + name, e);
			Main.getInstance().getLogger().severe("Failure while saving file for player: " + name);
			Main.getInstance().getLogger().severe("Cause: " + e.getCause());
			Main.getInstance().getLogger().severe("For exact cause refer to the debug.log");
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.Lathanael.AdminPerms.Backend.IBackend#load(java.lang.String)
	 */
	@Override
	public void loadPlayer(final String playerName) {
		YamlConfiguration configFile;
		ConfigurationSection sec;
		Map<String, Boolean> perms = new HashMap<String, Boolean>();
		Map<String, Map<String, Boolean>> worldPerms = new HashMap<String, Map<String, Boolean>>();
		Map<String, Boolean> worldPerm = new HashMap<String, Boolean>();
		Map<String, String> info = new HashMap<String, String>();
		List<String> groups;
		File file = new File(Main.getInstance().getDataFolder() + File.separator + "players", playerName.toLowerCase() + ".yml");
		if (!file.exists()) {
			createDefaultPlayerEntry(playerName);
		}
		configFile = new YamlConfiguration();
		configFile.options().pathSeparator('/');
		try {
			configFile.load(file);
		} catch (Exception e) {
			DebugLog.INSTANCE.log(Level.SEVERE, "Failure while loadig the following player file:" + file.getName(), e);
			return;
		}
		String name = file.getName().toLowerCase().substring(0, file.getName().lastIndexOf('.'));
		groups = configFile.getStringList("groups");
		sec = configFile.getConfigurationSection("info");
		if (sec != null) {
			for (String entry : sec.getKeys(false)) {
				info.put(entry, sec.getString(entry));
			}
		}
		sec = configFile.getConfigurationSection("permissions");
		if (sec != null) {
			for (String entry : sec.getKeys(false)) {
				perms.put(entry, sec.getBoolean(entry));
			}
		}
		sec = configFile.getConfigurationSection("worlds");
		if (sec != null) {
			for (String w : sec.getKeys(false)) {
				for (String entry : sec.getConfigurationSection(w).getKeys(false)) {
					worldPerm.put(entry, sec.getBoolean(w + "/" + entry));
				}
				worldPerms.put(w, worldPerm);
			}
		}
		PlayerHandler.getInstance().addPlayer(new PermPlayer(name, perms, groups, info, worldPerms));
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.Lathanael.AdminPerms.Backend.IBackend#reload(java.lang.String)
	 */
	@Override
	public void reloadPlayer(final String playerName) {
		savePlayer(playerName);
		loadPlayer(playerName);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.Lathanael.AdminPerms.Backend.IBackend#saveGroup(java.lang.String)
	 */
	@Override
	public void saveGroup(String groupName) {
		File file = new File(Main.getInstance().getDataFolder() + File.separator + "groups", groupName.toLowerCase() + ".yml");
		if (!file.exists()) {
			createDefaultGroupEntry(groupName.toLowerCase());
		}
		YamlConfiguration configFile;
		configFile = new YamlConfiguration();
		configFile.options().pathSeparator('/');
		final String name = file.getName().toLowerCase().substring(0, file.getName().lastIndexOf('.'));
		if (GroupHandler.getInstance().getGroup(name) == null)
			return;
		final Group group = GroupHandler.getInstance().getGroup(name);
		for (Map.Entry<String, String> entry : group.getInfos().entrySet()) {
			configFile.set("info" + "/" + entry.getKey(), entry.getValue());
		}
		for (Map.Entry<String, Boolean> entry : GroupHandler.getInstance().getGroupPermissions(name).entrySet()) {
			configFile.set("permissions" + "/" + entry.getKey(), entry.getValue());
		}
		for (Map.Entry<String, Map<String, Boolean>> entry : group.getAllWorldPermissions().entrySet()) {
			for (Map.Entry<String, Boolean> entry2 : entry.getValue().entrySet()) {
				configFile.set("worlds" + "/" + entry.getKey() + "/" + entry2.getKey() , entry2.getValue());
			}
		}			
		configFile.set("inheritance", group.getInheritance());
		configFile.set("promoteTo", group.getPromoteTo());
		configFile.set("demoteTo", group.getDemoteTo());
		configFile.set("rank", group.getRank());
		// TODO: AutoRank feature
		configFile.set("time", -1);
		try {
			configFile.save(file);
		} catch (IOException e) {
			DebugLog.INSTANCE.log(Level.SEVERE, "Failure while saving file for group: " + name, e);
			Main.getInstance().getLogger().severe("Failure while saving file for group: " + name);
			Main.getInstance().getLogger().severe("Cause: " + e.getCause());
			Main.getInstance().getLogger().severe("For exact cause refer to the debug.log");
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.Lathanael.AdminPerms.Backend.IBackend#loadGroup(java.lang.String)
	 */
	@Override
	public void loadGroup(String groupName) {
		File file = new File(Main.getInstance().getDataFolder() + File.separator + "groups", groupName.toLowerCase() + ".yml");
		if (!file.exists()) {
			createDefaultGroupEntry(groupName.toLowerCase());
		}
		YamlConfiguration configFile;
		ConfigurationSection sec;
		int rank;
		String name, promoteTo, demoteTo;
		List<String> inheritance;
		Map<String, Boolean> perms = new HashMap<String, Boolean>();
		Map<String, Map<String, Boolean>> worldPerms = new HashMap<String, Map<String, Boolean>>();
		Map<String, Boolean> worldPerm = new HashMap<String, Boolean>();
		Map<String, String> info = new HashMap<String, String>();
		worldPerm.clear();
		perms.clear();
		worldPerm.clear();
		info.clear();
		configFile =  new YamlConfiguration();
		configFile.options().pathSeparator('/');
		try {
			configFile.load(file);
		} catch (Exception e) {
			DebugLog.INSTANCE.log(Level.SEVERE, "Failure while loadig the following group file:" + file.getName(), e);
		}
		rank = configFile.getInt("rank", 0);
		name = file.getName().toLowerCase().substring(0, file.getName().lastIndexOf('.'));
		promoteTo = configFile.getString("promoteTo", "");
		demoteTo = configFile.getString("demoteTo", "");
		inheritance = configFile.getStringList("inheritance");
		sec = configFile.getConfigurationSection("permissions");
		if (sec != null) {
			for (String entry : sec.getKeys(false)) {
				perms.put(entry, sec.getBoolean(entry));
			}
		}
		sec = configFile.getConfigurationSection("info");
		if (sec != null) {
			for (String entry : sec.getKeys(false)) {
				info.put(entry, sec.getString(entry));
			}
		}
		sec = configFile.getConfigurationSection("worlds");
		if (sec != null) {
			for (String w : sec.getKeys(false)) {
				for (String entry : sec.getConfigurationSection(w).getKeys(false)) {
					worldPerm.put(entry, sec.getBoolean(w + "/" + entry));
				}
				worldPerms.put(w, worldPerm);
			}
		}
		GroupHandler.getInstance().addGroup(new Group(name, rank, perms, inheritance, info, worldPerms, promoteTo, demoteTo));
		GroupHandler.getInstance().checkDefaultGroup();
		GroupHandler.getInstance().copyInheritatedPerms();
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.Lathanael.AdminPerms.Backend.IBackend#reloadGroup(java.lang.String)
	 */
	@Override
	public void reloadGroup(String groupName) {
		saveGroup(groupName);
		loadGroup(groupName);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.Lathanael.AdminPerms.Backend.IBackend#createDefaultPlayerEntry(org.bukkit.entity.Player)
	 */
	@Override
	public void createDefaultPlayerEntry(final Player player) {
		createDefaultPlayerEntry(player.getName());
	}

	/*
	 * (non-Javadoc)
	 * @see de.Lathanael.AdminPerms.Backend.IBackend#createDefaultPlayerEntry(java.lang.String)
	 */
	@Override
	public void createDefaultPlayerEntry(final String playerName) {
		YamlConfiguration configFile =  new YamlConfiguration();
		configFile.options().pathSeparator('/');
		final String filePath = Main.getInstance().getDataFolder() + File.separator + "players" + File.separator + playerName.toLowerCase() + ".yml";
		try {
			configFile.load(Main.getInstance().getResource("defaultPlayer.yml"));
			File file = new File(filePath);
			file.createNewFile();
			configFile.save(file);
		} catch (Exception e) {
			DebugLog.INSTANCE.log(Level.SEVERE, "Failure while creating the default player file for: " + playerName, e);
			DebugLog.INSTANCE.log(Level.SEVERE, "Path used: " + filePath);
			return;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.Lathanael.AdminPerms.Backend.IBackend#createDefaultGroupEntry(java.lang.String)
	 */
	@Override
	public void createDefaultGroupEntry(String groupName) {
		YamlConfiguration configFile =  new YamlConfiguration();
		configFile.options().pathSeparator('/');
		final String filePath = Main.getInstance().getDataFolder() + File.separator + "groups" + File.separator + groupName.toLowerCase() + ".yml";
		try {
			configFile.load(Main.getInstance().getResource("default.yml"));
			File file = new File(filePath);
			file.createNewFile();
			configFile.save(file);
		} catch (Exception e) {
			DebugLog.INSTANCE.log(Level.SEVERE, "Failure while creating the default group file for: " + groupName, e);
			DebugLog.INSTANCE.log(Level.SEVERE, "Path used: " + filePath);
			return;
		}
	}
}
