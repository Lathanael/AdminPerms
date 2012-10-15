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

/**
 * @author Lathanael (aka Philippe Leipold)
 *
 */
public abstract class IConverter {

	protected String pluginsFolder;
	
	public IConverter() {
		pluginsFolder = "";
	}
	
	public abstract boolean run();
	
	public String getPluginsFolder(final File file) {
		final String path = file.getAbsolutePath();
		return path.substring(0, path.lastIndexOf(File.separator));
	}
	
	public abstract void convertPlayerData();
	
	public abstract void convertGroupData();
}
