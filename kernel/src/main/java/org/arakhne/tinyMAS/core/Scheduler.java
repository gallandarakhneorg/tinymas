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

import java.util.List;


/** Scheduler for agents.
 * <p>
 * A scheduler must make the agents alive.
 * 
 * @param <AT> is the type of the agents to schedule.
 * @author St&eacute;phane GALLAND &lt;galland@arakhne.org&gt;
 * @author Nicolas GAUD &lt;nicolas.gaud@utbm.fr&gt;
 * @deprecated See {@link "http://www.janus-project.org"}
 */
@Deprecated
public interface Scheduler<AT extends Agent> {

	/** Run the elements of the simulation: environment and agents.
	 * <p>
	 * Invoked when the agents AND the environment must be scheduled.
	 * 
	 * @param runningKernel is the kernel that has invoked this function.
	 * @param environment is the environment object associated to this execution
	 * @param agents is the list of agents to schedule.
	 */
	public void schedule(Kernel<AT,?,?,?> runningKernel, Environment<AT> environment, List<AT> agents);

	/** Make the specified agents alive.
	 * <p>
	 * Invoked when the only agents must be scheduled.
	 * 
	 * @param agents is the list of agents to schedule.
	 */
	public void schedule(List<AT> agents);

}