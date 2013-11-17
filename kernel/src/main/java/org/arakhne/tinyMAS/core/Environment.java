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

/** This interface representes the environment inside a multiagent system.
 * <p>
 * The environment is also assumed to be a time reference for the agent society.
 * 
 * @author St&eacute;phane GALLAND &lt;galland@arakhne.org&gt;
 * @author Nicolas GAUD &lt;nicolas.gaud@utbm.fr&gt;
 * @deprecated See {@link "http://www.janus-project.org"}
 */
@Deprecated
public interface Environment<AT extends Agent> {

	/** Initialize this environment.
	 */
	public void init();

	/** Shutdown this environment.
	 */
	public void shutdown();

	/** Put the given agent inside the environment.
	 * <p>
	 * The implementation of this method is problem-dependent
	 * because several type of environments should not contains
	 * the agents. 
	 */
	public void putAgent(AT agent);

	/** Remove the given agent from the environment.
	 * <p>
	 * The implementation of this method is problem-dependent
	 * because several type of environments should not contains
	 * the agents. 
	 */
	public void removeAgent(AgentIdentifier agent);
	
	/** This function is invoked by the kernel before
	 * the scheduling of the agents.
	 * 
	 * @param runningKernel is the instance of the kernel that is running this environment.
	 */
	public void preAgentScheduling(final Kernel<AT,?,?,?> runningKernel);

	/** This function is invoked by the kernel after
	 * the scheduling of the agents.
	 * 
	 * @param runningKernel is the instance of the kernel that is running this environment.
	 */
	public void postAgentScheduling(final Kernel<AT,?,?,?> runningKernel);

	/** Replies if this environment is alive.
	 * <p>
	 * If the environment is not alive, the multiagent kernel
	 * will quit.
	 */
	public boolean isAlive();
	
	/** Replies the clock used by the environment.
	 * 
	 * @return never <code>null</code>
	 */
	public SimulationClock getSimulationClock();

}