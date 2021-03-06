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

/** Simulation clock that permits to obtain the simulation time.
 * 
 * @author St&eacute;phane GALLAND &lt;galland@arakhne.org&gt;
 * @deprecated See {@link "http://www.janus-project.org"}
 */
@Deprecated
public interface SimulationClock {
	
	/** Replies the current date of simulation.
	 * 
	 * @param desired_unit indicates the unit to use
	 * @return the simulation's time espressed in the specified unit.
	 */
	public double getSimulationTime(TimeUnit desired_unit);

	/** Replies the current date of simulation.
	 * <p>
	 * The unit depends on the environment's implementation.
	 * <p>
	 * <code>simulation_time(t+1) = simulation_time(t) + step_duration</code>
	 * 
	 * @return the simulation's time espressed in seconds.
	 */
	public double getSimulationTime();

	/** Replies the duration of a simulation loop for this environment.
	 * <p>
	 * <code>simulation_time(t+1) = simulation_time(t) + step_duration</code>
	 * 
	 * @param desired_unit indicates the unit to use
	 * @return the simulation's step time.
	 */
	public double getSimulationStepDuration(TimeUnit desired_unit);

	/** Replies the duration of a simulation loop for this environment.
	 * <p>
	 * <code>simulation_time(t+1) = simulation_time(t) + step_duration</code>
	 * 
	 * @return the simulation's step time.
	 */
	public double getSimulationStepDuration();

	/** Compute a value expressed in something per time unit into
	 * something per simulation loop.
	 */
	public double perTimeUnit(double value);

	/** Compute a value expressed in something per time unit into
	 * something per simulation loop.
	 */
	public double perTimeUnit(double value, TimeUnit desiredUnit);

}