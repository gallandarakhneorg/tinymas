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

package org.arakhne.tinyMAS.situatedEnvironment.environment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.WeakHashMap;

import org.arakhne.tinyMAS.core.AbstractEnvironment;
import org.arakhne.tinyMAS.core.AgentIdentifier;
import org.arakhne.tinyMAS.core.Identifier;
import org.arakhne.tinyMAS.core.Kernel;
import org.arakhne.tinyMAS.core.TimeManager;
import org.arakhne.tinyMAS.situatedEnvironment.agent.SituatedAgent;
import org.arakhne.tinyMAS.situatedEnvironment.body.AgentBody;
import org.arakhne.tinyMAS.situatedEnvironment.influence.Influence;
import org.arakhne.tinyMAS.situatedEnvironment.perception.Perception;

/** Situated environment model.
 * 
 * @author St&eacute;phane GALLAND &lt;galland@arakhne.org&gt;
 * @deprecated See {@link "http://www.janus-project.org"}
 */
@Deprecated
public abstract class AbstractSituatedEnvironment<AT extends SituatedAgent<AB,OB,PT,IT>, AB extends AgentBody<?,IT>, OB extends SituatedObject, PT extends Perception<?>, IT extends Influence> extends AbstractEnvironment<AT> implements SituatedEnvironment<AT,AB,OB,PT,IT> {

	/** List of the agent's bodies.
	 */
	private final Map<AgentIdentifier,AB> __agent_bodies = new WeakHashMap<AgentIdentifier,AB>();
	
	/** List of the object's bodies.
	 */
	private final Map<Identifier,OB> __objects = new TreeMap<Identifier,OB>();

	/** List of influences.
	 */
	private final List<IT> __influences = new ArrayList<IT>();
	
	public AbstractSituatedEnvironment(TimeManager time_manager) {
		super(time_manager);
	}
	
	/** Shutdown this environment.
	 */
	@Override
	public void shutdown() {
		super.shutdown();
		this.__agent_bodies.clear();
		this.__objects.clear();
	}
	
	/** This function os called by the called after
	 * the on scheduling step of the agents.
	 */
	@Override
	public void postAgentScheduling(Kernel<AT,?,?,?> runningKernel) {
		// Applies the influences and computes the reactions
		applyInfluences(this.__influences);
		this.__influences.clear();

		//
		// Finish the loop
		//
		super.postAgentScheduling(runningKernel);
	}

	/** Put the given agent inside the environment.
	 */
	@Override
	public void putAgent(AT agent) {
		assert(agent!=null);
		final AgentIdentifier id = agent.getId();
		final AB body = agent.createDefaultBody(this);
		if (body!=null) {
			this.__agent_bodies.put(id,body);
			onAgentBodyAdded(body);
		}
	}
	
	/** Put the given body onto its location.
	 */
	protected abstract void onAgentBodyAdded(AB body);
	
	/** Remove the given body from the environment.
	 */
	protected abstract void onAgentBodyRemoved(AB body);

	/** Put the given agent on the given segment

	/** Remove the given agent from the environment.
	 */
	@Override
	public void removeAgent(AgentIdentifier agent) {
		assert(agent!=null);
		AB body = this.__agent_bodies.get(agent);
		if (body!=null) {
			this.__agent_bodies.remove(agent);
			onAgentBodyRemoved(body);
		}
	}

	/** Replies all the bodies of the agents inside the environment.
	 */
	public Collection<AB> getAllAgentBodies() {
		ArrayList<AB> list = new ArrayList<AB>();
		list.addAll(this.__agent_bodies.values());
		return list;
	}

	/** Replies the body of the agent inside the environment.
	 * 
	 * @return the location according to the environment dimension, ie.
	 * an array of size 1 if the environment is 1D, of size 2 for
	 * an 2D environment...
	 */
	@SuppressWarnings("unchecked")
	public <T extends AB> T getAgentBody(AgentIdentifier agent) {
		assert(agent!=null);
		return (T)this.__agent_bodies.get(agent);
	}

	/** Replies the location of the object (excluding
	 * agents) inside the environment.
	 * 
	 * @return the spatial description of the object. 
	 */
	public OB getObjectBody(Identifier object) {
		assert(object!=null);
		return this.__objects.get(object);
	}

	/** This function is called when the environment must compute
	 * its reaction to the agent influences.
	 * <p>
	 * This method must fill the list of reactions according
	 * to its computation on the agent's influences. 
	 * 
	 * @return <code>true</code> if the reaction was successfully computed,
	 * otherwise <code>false</code>
	 */
	protected abstract boolean applyInfluences(Collection<IT> influences);

	/** Send influences to the environment.
	 */
	public final boolean influence(IT... influences) {
		assert((influences!=null)&&(influences.length>0));
		// Register the influences
		return this.__influences.addAll(Arrays.asList(influences));
	}

}