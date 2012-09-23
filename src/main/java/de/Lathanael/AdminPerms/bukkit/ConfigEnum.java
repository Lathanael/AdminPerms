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

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * @author Lathanael (aka Philippe Leipold)
 *
 */
public enum ConfigEnum {
	
	BACKEND("data.backend", "yml"),
	VERBOSE_LOG("verboseLog", false),
	MYSQL_HOST("mysql.host", "localhost"),
	MYSQL_USER("mysql.user", "root"),
	MYSQL_PASS("mysql.password", "toor"),
	MYSQL_DB("mysql.database", "minecraft"),
	CONVERT("data.convertInto", "yml"),
	BUILD("messages.build", ChatColor.RED + "You do not have permission to build here."),
	FIRSTSTART("firstStart", true),
	DEFAULT_GROUP("defaultGroup", "%rank%");

	private final String confVal;
	private final Object defaultVal;
	private static YamlConfiguration config;
	private static File configFile;

	/**
	 * @param confVal
	 * @param defaultVal
	 */
	private ConfigEnum(final String confVal, final Object defaultVal) {
		this.confVal = confVal;
		this.defaultVal = defaultVal;
	}

	public String getString() {
		return config.getString(confVal);
	}

	public int getInt() {
		return config.getInt(confVal);
	}

	public double getDouble() {
		return config.getDouble(confVal);
	}

	public boolean getBoolean() {
		return config.getBoolean(confVal);
	}

	public long getLong() {
		return config.getLong(confVal);
	}

	public float getFloat() {
		return Float.parseFloat(config.getString(confVal));
	}

	public List<String> getStringList() {
		return config.getStringList(confVal);
	}

	public void setValue(final Object value) {
		config.set(confVal, value);
	}

	/**
	 * @return the defaultvalues
	 */
	private static Map<String, Object> getDefaultvalues() {
		final Map<String, Object> values = new LinkedHashMap<String, Object>();
		for (final ConfigEnum ce : values()) {
			values.put(ce.confVal, ce.defaultVal);
		}
		return values;
	}

	/**
	 * @param config
	 *            the config to set
	 */
	public static void setConfig(final File configFile) {
		ConfigEnum.configFile = configFile;
		if (!configFile.exists()) {
			try {
				configFile.createNewFile();
			} catch (IOException e) {
				// TODO: Make sure config is created.
			}
		}
		config = YamlConfiguration.loadConfiguration(configFile);
		config.options().copyDefaults(true);
		config.addDefaults(getDefaultvalues());
		try {
			ConfigEnum.save();
		} catch (final IOException e) {
		}
	}

	public static void save() throws IOException {
		config.save(configFile);
	}

	public static void reload() throws IOException,
			InvalidConfigurationException {
		save();
		config.load(configFile);
	}	
}
