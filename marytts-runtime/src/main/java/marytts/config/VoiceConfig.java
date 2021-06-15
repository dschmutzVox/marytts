/**
 * Copyright 2011 DFKI GmbH.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * This file is part of MARY TTS.
 *
 * MARY TTS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package marytts.config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Locale;

import marytts.exceptions.MaryConfigurationException;
import marytts.exceptions.NoSuchPropertyException;
import marytts.server.MaryProperties;
import marytts.util.MaryUtils;

/**
 * @author marc
 *
 */
public class VoiceConfig extends MaryConfig {

	public VoiceConfig(InputStream propertyStream) throws MaryConfigurationException {
		super(propertyStream);
		if (getName() == null) {
			throw new MaryConfigurationException("Voice does not have a name");
		}
		if (getLocale() == null) {
			throw new MaryConfigurationException("Voice '" + getName() + "' does not have a locale");
		}
	}

	@Override
	public boolean isVoiceConfig() {
		return true;
	}

	/**
	 * The voice's name. Guaranteed not to be null.
	 * 
	 * @return getProperties().getProperty("name")
	 */
	public String getName() {
		return getProperties().getProperty("name");
	}

	/**
	 * The voice's locale. Guaranteed not to be null.
	 * 
	 * @return null if localeString is null, return MaryUtils.string2locale(localeString) otherwise
	 */
	public Locale getLocale() {
		String localeString = getProperties().getProperty("locale");
		if (localeString == null) {
			return null;
		}
		return MaryUtils.string2locale(localeString);
	}
	
	public String getPropertyByName(String name) {
		return getPropertyByName(name, null);
	}
	
	public String getPropertyByName(String name, String baseLocation) {
		String property = getProperties().getProperty(name);
		if((baseLocation != null) && (property != null)) {
			return property.replace("MARY_BASE", baseLocation);
		} else {
			return property;
		}
	}
		
	public InputStream getStreamByPropertyName(String propertyName, String baseLocation) throws FileNotFoundException, MaryConfigurationException {
		InputStream stream;
		String propertyValue = getPropertyByName(propertyName, baseLocation);
		if (propertyValue == null) {
			return null;
		} else if (propertyValue.startsWith("jar:")) { // read from classpath
			String classpathLocation = propertyValue.substring("jar:".length());
			stream = MaryProperties.class.getResourceAsStream(classpathLocation);
			if (stream == null) {
				throw new MaryConfigurationException("For property '" + propertyName + "', no classpath resource available at '"
						+ classpathLocation + "'");
			}
		} else {
			String fileName = getPropertyByName(propertyName, baseLocation);
			stream = new FileInputStream(fileName);
		}
		return stream;
	}
	
	public int getIntegerByPropertyName(String propertyName, int defaultValue) {
		String propertyValue = getPropertyByName(propertyName);
		if(propertyValue == null) {
			return defaultValue;
		}
		try {
			return Integer.decode(propertyValue);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}
	
	public float getFloatByPropertyName(String propertyName, float defaultValue) {
		String propertyValue = getPropertyByName(propertyName);
		if(propertyValue == null) {
			return defaultValue;
		}
		try {
			return Float.parseFloat(propertyValue);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}
	
	public boolean getBoolanByPropertyName(String propertyName, boolean defaultValue) {
		String propertyValue = getPropertyByName(propertyName);
		if(propertyValue == null) {
			return defaultValue;
		}
		try {
			return Boolean.valueOf(propertyName).booleanValue();
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}
	
	public String needPropertyByName(String propertyName) throws NoSuchPropertyException {
		String propertyValue = getPropertyByName(propertyName);
		if (propertyValue == null) {
			throw new NoSuchPropertyException("Missing value `" + propertyValue + "' in configuration files");
		}
		return propertyValue;
	}
}
