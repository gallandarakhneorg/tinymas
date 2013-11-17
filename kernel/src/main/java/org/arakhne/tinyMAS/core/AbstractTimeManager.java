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

/** Time Manager.
 * <p>
 * <code>simulation_time(t+1) = simulation_time(t) + step_duration</code>
 * 
 * @author St&eacute;phane GALLAND &lt;galland@arakhne.org&gt;
 * @author Nicolas GAUD &lt;nicolas.gaud@utbm.fr&gt;
 * @deprecated See {@link "http://www.janus-project.org"}
 */
@Deprecated
public abstract class AbstractTimeManager extends TimeManager {
	
	/** Current simulation time (in seconds).
	 */
	private volatile double __simulation_time = 0; 
	
	/** Current simulation step duration (in milliseconds).
	 */
	private volatile double __simulation_step_duration = 0; 

	/** Set the current simulation time. It is the count of
	 * seconds since the starting of the simulation.
	 * <p>
	 * <code>simulation_time(t+1) = simulation_time(t) + step_duration</code>
	 * 
	 * @param time is the new simulation time expresed in seconds.
	 */
	@Override
	protected void setSimulationTime(double time) {
		this.__simulation_time = time;
	}
	
	/** Set the current simulation step duration. It is the duration
	 * of one simulation step expressed in the simulation time
	 * coordinate system.
	 * <p>
	 * <code>simulation_time(t+1) = simulation_time(t) + step_duration</code>
	 * 
	 * @param step_duration is the new simulation time expressed in milliseconds.
	 */
	@Override
	protected void setSimulationStepDuration(double step_duration) {
		this.__simulation_step_duration = step_duration;
	}

	/** Replies the current date of simulation.
	 * 
	 * @param desired_unit indicates the unit to use
	 * @return the simulation's time espressed in the specified unit.
	 */
	@Override
	public final double getSimulationTime(TimeUnit desired_unit) {
		assert(desired_unit!=null);
		double time = 0;
		switch(desired_unit) {
		case MICROSECONDS:
			time = MeasureUtil.unit2micro(this.__simulation_time);
			break;
		case MILLISECONDS:
			time = MeasureUtil.unit2milli(this.__simulation_time);
			break;
		case NANOSECONDS:
			time = MeasureUtil.unit2nano(this.__simulation_time);
			break;
		case SECONDS:
			time = this.__simulation_time;
			break;
		default:
		}
		return time;
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
		double time = 0;
		switch(desired_unit) {
		case MICROSECONDS:
			time = MeasureUtil.milli2micro(this.__simulation_step_duration);
			break;
		case MILLISECONDS:
			time = this.__simulation_step_duration;
			break;
		case NANOSECONDS:
			time = MeasureUtil.milli2nano(this.__simulation_step_duration);
			break;
		case SECONDS:
			time = MeasureUtil.milli2unit(this.__simulation_step_duration);
			break;
		default:
		}
		return time;
	}

}