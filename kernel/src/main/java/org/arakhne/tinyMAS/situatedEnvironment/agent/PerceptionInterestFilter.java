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


import java.util.ArrayList;
import java.util.List;

import org.arakhne.tinyMAS.core.AgentIdentifier;
import org.arakhne.tinyMAS.situatedEnvironment.perception.EntityPerception;
import org.arakhne.tinyMAS.situatedEnvironment.perception.Perception;

/** This class defines a filter for agent's perceptions.
 * <p>
 * This filter is mainly used to focus the perception of
 * the agent on objects/agents that are directly under
 * interest.
 * <p>
 * This 
 * 
 * @author St&eacute;phane GALLAND &lt;galland@arakhne.org&gt;
 * @deprecated See {@link "http://www.janus-project.org"}
 */
@Deprecated
public abstract class PerceptionInterestFilter<PT extends Perception<?>> {
	
	private PerceptionInterestFilter<PT> __parent;
	
	private List<PT> __objects = null;
	
	/** Describes the filtering state of the percepts.
	 * 
	 * @author Stephane GALLAND
	 * @deprecated See {@link "http://www.janus-project.org"}
	 */
	@Deprecated
	protected enum PerceptionState {
		/** The percept is for an agent */
		AGENT,
		/** The percept is for an object (not an agent) */
		OBJECT,
		/** The percept must be ignored */
		IGNORE,
	}
	
	/** Called each time this filter must be re-init.
	 */
	protected void initFilter() {
		this.__objects = null;
	}
	
	public PerceptionInterestFilter() {
		this.__parent = null;
	}
	
	void setParent(PerceptionInterestFilter<PT> parent) {
		this.__parent = parent;
	}

	PerceptionInterestFilter<PT> getParent() {
		return this.__parent;
	}

	/** Make a filter of the percepts.
	 * 
	 * @param percepts is the percepts replied by the environment. It could be
	 * <code>null</code> or empty.
	 */
	public final void filter(PT[] percepts) {
		initFilter();
		
		if (percepts!=null && percepts.length>0) {
			final ArrayList<PT> objects = new ArrayList<PT>();
			
			for (PT pt : percepts) {
				if (pt!=null) {
					
					// Initialize the default state of the percept
					// => OBJECT or AGENT
					PerceptionState st = PerceptionState.OBJECT;
					if ((pt instanceof EntityPerception)&&
						(((EntityPerception)pt).getPerceivedObject() instanceof AgentIdentifier)) {
						st = PerceptionState.AGENT;
					}
					
					// Call the filtering function of the parent
					if (this.__parent!=null) {
						st = this.__parent.filter(pt);
					}
					
					// Do the local filtering
					boolean saveAsGeneralObject = false;
					if (st!=PerceptionState.IGNORE) {
						st = filter(pt);
						
						if (st!=PerceptionState.IGNORE)
							saveAsGeneralObject = !savePerception(pt,st);
					}
					
					// Save the percept if it must not be ignored
					if (saveAsGeneralObject) {
						objects.add(pt);
					}
				}
			}
			
			this.__objects = objects;
		}
	}
	
	/** Make a filter of the percepts.
	 * <p>
	 * The system already make a filtering depending
	 * on the agent's body.
	 * 
	 * @param percept is the percept replied by the environment.
	 * @return the state of the given percept.
	 */
	protected abstract PerceptionState filter(PT percept);
	
	/** Save the given perception inside internal structures.
	 * 
	 * @param percept is the perception to save
	 * @param state is the detected state of the given perception. It
	 * will permit to this function to know what to do with the
	 * perception.
	 * @return <code>true</code> if the specified percept was saved by
	 * this function, otherwise <code>false</code>. In this last
	 * case, the percept will be save inside the generic data structure
	 * that containing perceived objects.
	 */
	protected abstract boolean savePerception(PT percept, PerceptionState state);
	
	/** Replied the perceived objects.
	 */
	public List<PT> getPerceivedObjects() {
		return this.__objects;
	}

	/** Replies if objects were perceived.
	 */
	public boolean hasPerceivedObjects() {
		return ((this.__objects!=null)&&(this.__objects.size()>0));
	}

}