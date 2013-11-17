/* 
 * $Id$
 * 
 * Copyright (C) 2004-2007 St&eacute;phane GALLAND, Nicolas GAUD
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * This program is free software; you can redistribute it and/or modify
 */

package org.arakhne.tinyMAS.core;

import java.util.concurrent.TimeUnit;

/** Time Manager for that was a filter to another time manager.
 * 
 * @author St&eacute;phane GALLAND &lt;galland@arakhne.org&gt;
 * @author Nicolas GAUD &lt;nicolas.gaud@utbm.fr&gt;
 * @deprecated See {@link "http://www.janus-project.org"}
 */
@Deprecated
public abstract class FilteringTimeManager extends TimeManager {

	private final TimeManager __manager;
	
	public FilteringTimeManager(TimeManager manager) {
		assert(manager!=null);
		this.__manager = manager;
	}
	
	/** Replies the current date of simulation.
	 * 
	 * @param desired_unit indicates the unit to use
	 * @return the simulation's time espressed in the specified unit.
	 */
	@Override
	public final double getSimulationTime(TimeUnit desired_unit) {
		assert(desired_unit!=null);
		return this.__manager.getSimulationTime(desired_unit);
	}

	/** Replies the duration of a simulation loop for this environment.
	 * <p>
	 * <code>simulation_time(t+1) = simulation_time(t) + step_duration</code>
	 * 
	 * @param desired_unit indicates the unit to use
	 * @return the simulation's step time.
	 */
	@Override
	public final double getSimulationStepDuration(TimeUnit desired_unit) {
		assert(desired_unit!=null);
		return this.__manager.getSimulationStepDuration(desired_unit);
	}

	/** Called by the environment before the scheduling of the agents.
	 * 
	 * @param kernel_time is the current kernel date.
	 * @param last_kernel_step_duration is the duration of the last
	 * kernel step in milliseconds.
	 */
	@Override
	protected void preAgentScheduling(long kernel_time, long last_kernel_step_duration) {
		this.__manager.preAgentScheduling(kernel_time,last_kernel_step_duration);
	}

	/** Called by the environment before the scheduling of the agents.
	 * 
	 * @param kernel_time is the current kernel date.
	 * @param last_kernel_step_duration is the duration of the last
	 * kernel step in milliseconds.
	 */
	@Override
	protected void postAgentScheduling(long kernel_time, long last_kernel_step_duration) {
		this.__manager.preAgentScheduling(kernel_time,last_kernel_step_duration);
	}

	/** Set the current simulation time. It is the count of
	 * seconds since the starting of the simulation.
	 * <p>
	 * <code>simulation_time(t+1) = simulation_time(t) + step_duration</code>
	 * 
	 * @param time is the new simulation time expresed in seconds.
	 */
	@Override
	protected final void setSimulationTime(double time) {
		this.__manager.setSimulationTime(time);
	}
	
	/** Set the current simulation step duration. It is the duration
	 * of one simulation step expressed in the simulation time
	 * coordinate system.
	 * <p>
	 * <code>simulation_time(t+1) = simulation_time(t) + step_duration</code>
	 * 
	 * @param step_duration is the new simulation time expresed in milliseconds.
	 */
	@Override
	protected final void setSimulationStepDuration(double step_duration) {
		this.__manager.setSimulationTime(step_duration);
	}

}