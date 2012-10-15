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

package de.Lathanael.AdminPerms.bukkit;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import de.Lathanael.AdminPerms.Backend.File.FileBackend;
import de.Lathanael.AdminPerms.Command.Check;
import de.Lathanael.AdminPerms.Command.CommandsHandler;
import de.Lathanael.AdminPerms.Command.Dump;
import de.Lathanael.AdminPerms.Command.GroupCommand;
import de.Lathanael.AdminPerms.Command.Info;
import de.Lathanael.AdminPerms.Command.PlayerCommand;
import de.Lathanael.AdminPerms.Command.Rank;
import de.Lathanael.AdminPerms.Command.Reload;
import de.Lathanael.AdminPerms.Converter.PermissionsBukkitConverter;
import de.Lathanael.AdminPerms.Listener.AdminPermsPlayerListener;
import de.Lathanael.AdminPerms.Logging.DebugLog;
import de.Lathanael.AdminPerms.Permissions.PermissionsHandler;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;


/**
 * @author Lathanael (aka Philippe Leipold)
 * @author SpaceManiac (original author)
 *
 */
public class Main extends JavaPlugin {

	private AdminPermsPlayerListener playerListener = new AdminPermsPlayerListener();
	private File configFile;
	private static Main instance;;

	/*
	 * (non-Javadoc)
	 * @see org.bukkit.plugin.java.JavaPlugin#onEnable()
	 */
	@Override
	public void onEnable() {
		instance = this;
		configFile = new File(getDataFolder(), "config.yml");
		ConfigEnum.setConfig(configFile);
		DebugLog.setFile(getDataFolder().getPath());
		if (ConfigEnum.FIRSTSTART.getBoolean()) {
			File playerFolder = new File(getDataFolder() + File.separator + "players");
			File groupFolder = new File(getDataFolder() + File.separator + "groups");
			playerFolder.mkdirs();
			groupFolder.mkdirs();
			YamlConfiguration file1 = new YamlConfiguration(), file2 = new YamlConfiguration(), file3 = new YamlConfiguration(), file4 = new YamlConfiguration();
			file1.options().pathSeparator('/');
			file2.options().pathSeparator('/');
			file3.options().pathSeparator('/');
			file4.options().pathSeparator('/');
			ConfigEnum.FIRSTSTART.setValue(false);
			try {
				file1.load(getResource("default.yml"));
				file2.load(getResource("admin.yml"));
				file3.load(getResource("coolplayer.yml"));
				file4.load(getResource("vip.yml"));
				file1.save(new File(groupFolder, "default.yml"));
				file2.save(new File(groupFolder, "admin.yml"));
				file4.save(new File(groupFolder, "vip.yml"));
				file3.save(new File(playerFolder, "coolplayer.yml"));
				ConfigEnum.save();
			} catch (IOException e) {
				DebugLog.INSTANCE.log(Level.WARNING, "Could not save default files.", e.getStackTrace());
				getLogger().warning("Could not save default files, for more details open the debug.log!");
			} catch (InvalidConfigurationException e) {
				DebugLog.INSTANCE.log(Level.WARNING, "Could not load default files.", e.getStackTrace());
				getLogger().warning("Could not load default files, for more details open the debug.log!");
			}
		}
		final String backend = ConfigEnum.BACKEND.getString();
		if (backend.equalsIgnoreCase("yml")) {
			PermissionsHandler.getInstance().setBackend(new FileBackend(getDataFolder().getPath()));
		} else if (backend.equalsIgnoreCase("sqlite")) {
			// TODO: sql code
		} else if (backend.equalsIgnoreCase("mysql")) {
			// TODO: sql code
		} else {
			PermissionsHandler.getInstance().setBackend(new FileBackend(getDataFolder().getPath()));
		}
		if (ConfigEnum.IMPORT.getBoolean()) {
			getLogger().info("Importing activated, trying to find supported plugins and convert their settings...");
			boolean done = false;
			done = PermissionsBukkitConverter.getInstance().run();
			if (done) {
				getLogger().info("Successfully imported permissions!");
			} else {
				getLogger().info("No supported plugin was found!");
			}
			ConfigEnum.IMPORT.setValue(false);
			try {
				ConfigEnum.save();
			} catch (IOException e) {
				DebugLog.INSTANCE.log(Level.WARNING, "Could not save config file.", e.getStackTrace());
				getLogger().warning("Could not save the config file, for more details open the debug.log!");
			}
		}
		CommandsHandler.getInstance().registerCommand(Check.class);
		CommandsHandler.getInstance().registerCommand(Dump.class);
		CommandsHandler.getInstance().registerCommand(Info.class);
		CommandsHandler.getInstance().registerCommand(Rank.class);
		CommandsHandler.getInstance().registerCommand(Reload.class);
		CommandsHandler.getInstance().registerCommand(GroupCommand.class);
		CommandsHandler.getInstance().registerCommand(PlayerCommand.class);
		getServer().getPluginManager().registerEvents(playerListener, this);
		PermissionsHandler.getInstance().registerPlayer(getServer().getOnlinePlayers());
		int count = getServer().getOnlinePlayers().length;
		if (count > 0) {
			getLogger().info("Successfully registered " + count	+ " online players registered");
		}
		getLogger().info("AdminPerms version " + getDescription().getVersion() + " successfully enabled.");
	}

	/*
	 * (non-Javadoc)
	 * @see org.bukkit.plugin.java.JavaPlugin#onDisable()
	 */
	@Override
	public void onDisable() {
		PermissionsHandler.getInstance().unregisterPlayer(getServer().getOnlinePlayers());
		int count = getServer().getOnlinePlayers().length;
		if (count > 0) {
			getLogger().info("Successfully unregistered " + count + " online players.");
		}
		PermissionsHandler.getInstance().getBackend().save();
		getLogger().info("AdminPerms version " + getDescription().getVersion() + " successfully disabled.");
	}

	/**
	 * Getter method to retrieve the singleton Main-class object.
	 * 
	 * @return the instance of this class
	 */
	public static Main getInstance() {
		return instance;
	}
}
