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

package org.arakhne.tinyMAS.situatedEnvironment.body;

import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

import org.arakhne.tinyMAS.core.AgentIdentifier;
import org.arakhne.tinyMAS.core.Environment;
import org.arakhne.tinyMAS.core.Kernel;
import org.arakhne.tinyMAS.core.Probe;
import org.arakhne.tinyMAS.situatedEnvironment.agent.SituatedAgent;
import org.arakhne.tinyMAS.situatedEnvironment.environment.SituatedEnvironment;
import org.arakhne.tinyMAS.situatedEnvironment.influence.Influence;
import org.arakhne.tinyMAS.situatedEnvironment.perception.PerceptionBody;

/** Agent body.
 * 
 * @author St&eacute;phane GALLAND &lt;galland@arakhne.org&gt;
 * @deprecated See {@link "http://www.janus-project.org"}
 */
@Deprecated
public abstract class AbstractAgentBody<PB extends PerceptionBody, IT extends Influence> implements AgentBody<PB,IT> {

	/** This is the identifier of the agent owns this body.
	 */
	private final WeakReference<SituatedAgent<?,?,?,?>> __agent;
	
	/** Description of the perception characteristics.
	 */
	private PB __perception_body;

	/** Indicates if the body was freezed.
	 * A freezed body can not be moved.
	 */
	private boolean __freezed = false;

	public AbstractAgentBody(SituatedAgent<?,?,?,?> agent, PB percepts) {
		assert(agent!=null);
		this.__agent = new WeakReference<SituatedAgent<?,?,?,?>>(agent);
		this.__perception_body = percepts;
	}
	
	public AbstractAgentBody(SituatedAgent<?,?,?,?> agent) {
		assert(agent!=null);
		this.__agent = new WeakReference<SituatedAgent<?,?,?,?>>(agent);
		this.__perception_body = null;
	}
	
	/** Set if this body was freezed.
	 * A freezed body can not be moved.
	 */
	public void freeze() {
		this.__freezed = true;
	}

	/** Set if this body was unfreezed.
	 * A freezed body can not be moved.
	 */
	public void unfreeze() {
		this.__freezed = false;
	}

	/** Replies if this body was crashed.
	 * A freezed body can not be moved.
	 */
	public boolean isFreezed() {
		return this.__freezed;
	}
	

	/** Replies the situated environment.
	 */
	@SuppressWarnings("unchecked")
	protected final SituatedEnvironment<?,?,?,?,IT> getEnvironment() {
		Environment env = Kernel.getSingleton().getEnvironment();
		if (env instanceof SituatedEnvironment) {
			return (SituatedEnvironment<?,?,?,?,IT>)env;
		}
		return null;
	}

	/** Replies the identifier of the agent that owns this body.
	 */
	public final AgentIdentifier getAgent() {
		return (this.__agent==null) ? null : this.__agent.get().getId();
	}
	
	/** Replies the probe of the agent that owns this body.
	 */
	public final Probe getAgentProbe() {
		SituatedAgent<?,?,?,?> ag = (this.__agent==null) ? null : this.__agent.get();
		if (ag!=null) {
			return ag.getProbe();
		}
		return null;
	}

	/** Replies the perception characteristics of thi body.
	 */
	public final PB getPerceptionBody() {
		return this.__perception_body;
	}

	/** Set the perception characteristics of thi body.
	 */
	public final void setPerceptionBody(PB perceptBody) {
		this.__perception_body = perceptBody;
	}

	/** Add influences.
	 */
	public final boolean influence(IT... influences) {
		assert((influences!=null)&&(influences.length>0));
		SituatedEnvironment<?,?,?,?,IT> env = getEnvironment();
		assert(env!=null);
		return env.influence(influences);
	}

	/** Replies the current date of simulation.
	 * <p>
	 * The unit depends on the environment's implementation.
	 * 
	 * @param desired_unit indicates the unit to use
	 * @return the simulation's time espressed in the specified unit.
	 */
	public double getSimulationTime(TimeUnit desired_unit) {
		assert(desired_unit!=null);
		return Kernel.getSingleton().getSimulationClock().getSimulationTime(desired_unit);
	}

	/** Replies the current date of simulation.
	 * <p>
	 * The unit depends on the environment's implementation.
	 * 
	 * @return the simulation's time espressed in seconds.
	 */
	public double getSimulationTime() {
		return Kernel.getSingleton().getSimulationClock().getSimulationTime();
	}

	/** Replies the duration of a simulation loop for this environment.
	 * 
	 * @param desired_unit indicates the unit to use
	 * @return the simulation's step time.
	 */
	public double getSimulationStepDuration(TimeUnit desired_unit) {
		assert(desired_unit!=null);
		return Kernel.getSingleton().getSimulationClock().getSimulationStepDuration(desired_unit);
	}

	/** Replies the duration of a simulation loop for this environment.
	 * 
	 * @return the simulation's step time in seconds.
	 */
	public double getSimulationStepDuration() {
		return Kernel.getSingleton().getSimulationClock().getSimulationStepDuration();
	}

	/** Convert the specified quantity specified in
	 * something per second to the same quantity
	 * expressed in something per simulation turn.
	 * 
	 * @see #getSimulationStepDuration()
	 * @see #getSimulationStepDuration(TimeUnit)
	 */
	public double perSecond(double quantity) {
		return getSimulationStepDuration(TimeUnit.SECONDS) * quantity;
	}
	
}