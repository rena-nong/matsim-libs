/* *********************************************************************** *
 * project: org.matsim.*
 * EvacuationConfigGroup.java
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2007 by the members listed in the COPYING,        *
 *                   LICENSE and WARRANTY file.                            *
 * email           : info at matsim dot org                                *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *   See also COPYING, LICENSE and WARRANTY file                           *
 *                                                                         *
 * *********************************************************************** */

package org.matsim.core.config.groups;

import org.matsim.core.config.Module;

public class EvacuationConfigGroup  extends Module{

	private static final long serialVersionUID = 1L;

	public static final String GROUP_NAME = "evacuation";

	/**
	 * name of the evacuation area file parameter in config
	 */
	private static final String EVACUATION_AREA_FILE = "inputEvacuationAreaLinksFile";

	/**
	 * file name of the evacutation area file
	 */
	private String evacuationAreaFile;

	/**
	 * name of the flooding data file parameter in config
	 */
	private static final String FLOODING_DATA_FILE = "floodingDataFile";
	
	/**
	 * file name of the flooding data file
	 */
	private String floodingDataFile;
	
	public EvacuationConfigGroup(){
		super(GROUP_NAME);
	}

	@Override
	public String getValue(final String key) {
		if (EVACUATION_AREA_FILE.equals(key)) {
			return getEvacuationAreaFile();
		} else if (FLOODING_DATA_FILE.equals(key)) {
			return getFloodingDataFile();
		}
		throw new IllegalArgumentException(key);
	}

	@Override
	public void addParam(final String key, final String value) {
		if (EVACUATION_AREA_FILE.equals(key)) {
			setEvacuationAreaFile(value.replace('\\', '/'));
		}else if(FLOODING_DATA_FILE.equals(key)){
			setFloodingDataFile(value.replace('\\', '/'));
		} else {
			throw new IllegalArgumentException(key);
		}
	}


	/**
	 *
	 * @return the file name of the evacuation area file
	 */
	public String getEvacuationAreaFile() {
		return this.evacuationAreaFile;
	}
	/**
	 *
	 * @param evacuationAreaFile
	 * the evacuation area filename to set
	 */
	public void setEvacuationAreaFile(String evacuationAreaFile) {
		this.evacuationAreaFile = evacuationAreaFile;

	}

	/**
	 * 
	 * @return the file name of the flooding data file
	 */
	public String getFloodingDataFile() {
		return this.floodingDataFile;
	}
	
	/**
	 * 
	 * @param floodingDataFile
	 * the flooding data filename to set
	 */
	public void setFloodingDataFile(String floodingDataFile) {
		this.floodingDataFile = floodingDataFile;
	}

}
