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

package org.arakhne.tinyMAS.situatedEnvironment.agent;


import org.arakhne.tinyMAS.core.Agent;
import org.arakhne.tinyMAS.core.Environment;
import org.arakhne.tinyMAS.core.Kernel;
import org.arakhne.tinyMAS.situatedEnvironment.body.AgentBody;
import org.arakhne.tinyMAS.situatedEnvironment.environment.SituatedEnvironment;
import org.arakhne.tinyMAS.situatedEnvironment.environment.SituatedObject;
import org.arakhne.tinyMAS.situatedEnvironment.influence.Influence;
import org.arakhne.tinyMAS.situatedEnvironment.perception.Perception;

/** Situated agent.
 * 
 * @author St&eacute;phane GALLAND &lt;galland@arakhne.org&gt;
 * @deprecated See {@link "http://www.janus-project.org"}
 */
@Deprecated
public abstract class SituatedAgent<AB extends AgentBody<?,IT>, OB extends SituatedObject, PT extends Perception<?>, IT extends Influence> extends Agent {

	private PerceptionInterestFilter<PT> __perceptionFilter = null;
	
	/** Register the specified filter for the perceptions.
	 */
	protected void registerPerceptionFilter(PerceptionInterestFilter<PT> filter) {
		assert(filter!=null);
		filter.setParent(this.__perceptionFilter);
		this.__perceptionFilter = filter;
	}

	/** Replace the specified filter for the perceptions.
	 */
	protected void replacePerceptionFilter(PerceptionInterestFilter<PT> filter) {
		assert(filter!=null);
		if (this.__perceptionFilter!=null)
			filter.setParent(this.__perceptionFilter.getParent());
		this.__perceptionFilter = filter;
	}

	/** Replies the current perception filter.
	 */
	@SuppressWarnings("unchecked")
	protected <T extends PerceptionInterestFilter<PT>> T getPerceptionFilter() {
		return (T)this.__perceptionFilter;
	}

	/** The function live() implements the classical
	 * perception-decision-action cycle.
	 */
	@Override
	public final void live() {
		doPerception();
		doDecisionAndAction();
	}
	
	/** Replies the situated environment.
	 */
	@SuppressWarnings("unchecked")
	protected final SituatedEnvironment<?,AB,OB,PT,IT> getEnvironment() {
		Environment env = Kernel.getSingleton().getEnvironment();
		if (env instanceof SituatedEnvironment) {
			return (SituatedEnvironment<?,AB,OB,PT,IT>)env;
		}
		return null;
	}
	
	/** Do the perception.
	 */
	protected void doPerception() {
		SituatedEnvironment<?,?,?,PT,?> env = getEnvironment();
		if (env!=null) {
			if (this.__perceptionFilter==null) {
				this.__perceptionFilter = new PerceptionInterestFilter<PT>() {
						@Override
						protected PerceptionState filter(PT percept) {
							return PerceptionState.OBJECT;
						}
						@Override
						protected boolean savePerception(PT percept, PerceptionState state) {
							return false;
						}
					};
			}
			PT[] percepts = env.perceive(getId());
			this.__perceptionFilter.filter(percepts);
		}
	}
	
	/** Replies the body of the agent.
	 */
	public final <T extends AB> T getAgentBody() {
		SituatedEnvironment<?,AB,?,?,?> env = getEnvironment();
		if (env!=null) {
			return env.<T>getAgentBody(getId());
		}
		return null;
	}

	/** Do the decision.
	 */
	protected abstract void doDecisionAndAction();

	/** Replies the default body for this agent.
	 * <p>
	 * This method must be called by the environment when
	 * the agent arrived inside a situated environment.
	 */
	public abstract AB createDefaultBody(SituatedEnvironment<? extends SituatedAgent<AB,OB,PT,IT>,AB,OB,PT,IT> in);
			
}