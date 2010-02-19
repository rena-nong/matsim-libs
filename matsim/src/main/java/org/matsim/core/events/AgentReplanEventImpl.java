/* *********************************************************************** *
 * project: org.matsim.*
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2008 by the members listed in the COPYING,        *
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

package org.matsim.core.events;

import org.matsim.api.core.v01.Id;
import org.matsim.core.population.routes.NetworkRoute;

/**
 * @author dgrether
 */
public class AgentReplanEventImpl extends PersonEventImpl {

	public static final String EVENT_TYPE = "replan";

	private final NetworkRoute replannedRoute;

	public AgentReplanEventImpl(final double time, final Id agentId, final NetworkRoute alternativeRoute) {
		super(time, agentId);
		this.replannedRoute = alternativeRoute;
	}

	@Override
	public String getEventType() {
		return EVENT_TYPE;
	}

	public NetworkRoute getReplannedRoute() {
		return this.replannedRoute;
	}

}
