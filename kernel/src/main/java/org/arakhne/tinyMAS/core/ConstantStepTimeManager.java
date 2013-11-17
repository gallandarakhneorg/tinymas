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

/** Time Manager based on constant time steps.
 * 
 * @author St&eacute;phane GALLAND &lt;galland@arakhne.org&gt;
 * @author Nicolas GAUD &lt;nicolas.gaud@utbm.fr&gt;
 * @deprecated See {@link "http://www.janus-project.org"}
 */
@Deprecated
public class ConstantStepTimeManager extends AbstractTimeManager {

	/**
	 * @param time_step is the constant time step used by this manager.
	 * @param time_unit is the unit to express the constant time step.
	 */
	public ConstantStepTimeManager(long time_step, TimeUnit time_unit) {
		assert(time_unit!=null);
		switch(time_unit) {
		case SECONDS:
			setSimulationStepDuration(MeasureUtil.unit2milli(time_step));
			break;
		case MILLISECONDS:
			setSimulationStepDuration(time_step);
			break;
		case MICROSECONDS:
			setSimulationStepDuration(MeasureUtil.micro2milli(time_step));
			break;
		case NANOSECONDS:
			setSimulationStepDuration(MeasureUtil.nano2milli(time_step));
			break;
		default:
		}
	}
	
	/** Called by the environment before the scheduling of the agents.
	 * 
	 * @param kernel_time is the current kernel date.
	 * @param last_kernel_step_duration is the duration of the last
	 * kernel step in milliseconds.
	 */
	@Override
	protected void preAgentScheduling(long kernel_time, long last_kernel_step_duration) {
		setSimulationTime(kernel_time * getSimulationStepDuration(TimeUnit.SECONDS));
	}

	/** Called by the environment before the scheduling of the agents.
	 * 
	 * @param kernel_time is the current kernel date.
	 * @param last_kernel_step_duration is the duration of the last
	 * kernel step in milliseconds.
	 */
	@Override
	protected void postAgentScheduling(long kernel_time, long last_kernel_step_duration) {
		//
	}

}