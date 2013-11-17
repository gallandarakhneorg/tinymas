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

import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

/** Abstract environment model.
 * 
 * @author St&eacute;phane GALLAND &lt;galland@arakhne.org&gt;
 * @author Nicolas GAUD &lt;nicolas.gaud@utbm.fr&gt;
 * @deprecated See {@link "http://www.janus-project.org"}
 */
@Deprecated
public abstract class AbstractEnvironment<AT extends Agent> implements Environment<AT> {

	/** Unit used to compute the simulation time.
	 */
	protected final TimeManager _time_manager;
	
	/** Simulation clock.
	 */
	protected final EnvironmentClock _clock;

	/** Indicates if this environment must be killed.
	 */
	private boolean killme = false;
	
	public AbstractEnvironment(TimeManager time_manager) {
		this._time_manager = time_manager;
		this._clock = new EnvironmentClock(time_manager);
	}
	
	/** Replies if this environment is alive.
	 * <p>
	 * If the environment is not alive, the multiagent kernel
	 * will quit.
	 */
	public boolean isAlive() {
		return !this.killme;
	}

	/** Notify the kernel that this environment is requesting to be killed.
	 * <p>
	 * When a situated environment desappear, all the agents inside will be
	 * killed also.
	 */
	protected void killMe() {
		this.killme = true;
	}

	/** Initialize this environment.
	 */
	public void init() {
		//
	}

	/** Shutdown this environment.
	 */
	public void shutdown() {
		//
	}

	/** Put the given agent inside the environment.
	 */
	public abstract void putAgent(AT agent);
	
	/** Remove the given agent from the environment.
	 */
	public abstract void removeAgent(AgentIdentifier agent);

	/** This function os called by the called before
	 * the on scheduling step of the agents.
	 * 
	 * @param runningKernel is the instance of the kernel that is running this environment.
	 */
	public void preAgentScheduling(final Kernel<AT,?,?,?> runningKernel) {
		this._time_manager.beforeAgentScheduling(
				runningKernel.getKernelStep(),
				runningKernel.getKernelStepDuration());
	}

	/** This function os called by the called after
	 * the on scheduling step of the agents.
	 * 
	 * @param runningKernel is the instance of the kernel that is running this environment.
	 */
	public void postAgentScheduling(final Kernel<AT,?,?,?> runningKernel) {
		this._time_manager.afterAgentScheduling(
				runningKernel.getKernelStep(),
				runningKernel.getKernelStepDuration());
	}

	/** Replies the current date of simulation.
	 * <p>
	 * The unit depends on the environment's implementation.
	 * 
	 * @param desired_unit indicates the unit to use
	 * @return the simulation's time espressed in the specified unit.
	 */
	public final double getSimulationTime(TimeUnit desired_unit) {
		assert(desired_unit!=null);
		return this._time_manager.getSimulationTime(desired_unit);
	}

	/** Replies the current date of simulation.
	 * <p>
	 * The unit depends on the environment's implementation.
	 * 
	 * @return the simulation's time espressed in seconds.
	 */
	public final double getSimulationTime() {
		return this._time_manager.getSimulationTime();
	}

	/** Replies the duration of a simulation loop for this environment.
	 * 
	 * @param desired_unit indicates the unit to use
	 * @return the simulation's step time.
	 */
	public final double getSimulationStepDuration(TimeUnit desired_unit) {
		assert(desired_unit!=null);
		return this._time_manager.getSimulationStepDuration(desired_unit);
	}

	/** Replies the duration of a simulation loop for this environment.
	 * 
	 * @return the simulation's step time.
	 */
	public final double getSimulationStepDuration() {
		return this._time_manager.getSimulationStepDuration();
	}
	
	/** Replies the clock used by the environment.
	 * 
	 * @return never <code>null</code>
	 */
	public final SimulationClock getSimulationClock() {
		return this._clock;
	}

	/** Abstract environment model.
	 * 
	 * @author St&eacute;phane GALLAND &lt;galland@arakhne.org&gt;
	 * @author Nicolas GAUD &lt;nicolas.gaud@utbm.fr&gt;
	 * @deprecated See {@link "http://www.janus-project.org"}
	 */
	@Deprecated
	private static class EnvironmentClock implements SimulationClock {

		private final WeakReference<TimeManager> manager;
		
		public EnvironmentClock(TimeManager manager) {
			this.manager = new WeakReference<TimeManager>(manager);
		}
		
		/** {@inheritDoc}
		 */
		public double getSimulationTime(TimeUnit desired_unit) {
			TimeManager tm = this.manager.get();
			if (tm==null) throw new NullPointerException("no associated time manager"); //$NON-NLS-1$
			return tm.getSimulationTime(desired_unit);
		}

		/** {@inheritDoc}
		 */
		public double getSimulationTime() {
			TimeManager tm = this.manager.get();
			if (tm==null) throw new NullPointerException("no associated time manager"); //$NON-NLS-1$
			return tm.getSimulationTime();
		}

		/** {@inheritDoc}
		 */
		public double getSimulationStepDuration(TimeUnit desired_unit) {
			TimeManager tm = this.manager.get();
			if (tm==null) throw new NullPointerException("no associated time manager"); //$NON-NLS-1$
			return tm.getSimulationStepDuration(desired_unit);
		}

		/** {@inheritDoc}
		 */
		public double getSimulationStepDuration() {
			TimeManager tm = this.manager.get();
			if (tm==null) throw new NullPointerException("no associated time manager"); //$NON-NLS-1$
			return tm.getSimulationStepDuration();
		}

		/** Compute a value expressed in something per time unit into
		 * something per simulation loop.
		 */
		public double perTimeUnit(double value) {
			return perTimeUnit(value, TimeUnit.SECONDS);
		}

		/** Compute a value expressed in something per time unit into
		 * something per simulation loop.
		 */
		public double perTimeUnit(double value, TimeUnit desiredUnit) {
			return value * getSimulationStepDuration(desiredUnit);
		}

	}
	
}