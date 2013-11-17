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

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

/** The white page system is able to list all the agents that exist on a kernel or
 * on a set of kernel (depending of its implementation).
 * <p>
 * The white page system has also the role to compute an unique identifier for
 * each agent.
 * 
 * @author St&eacute;phane GALLAND &lt;galland@arakhne.org&gt;
 * @author Nicolas GAUD &lt;nicolas.gaud@utbm.fr&gt;
 * @deprecated See {@link "http://www.janus-project.org"}
 */
@Deprecated
class WhitePages<AT extends Agent> {

	/** Repository
	 */	
	private final Map<AgentIdentifier,AT> __annuaire = new TreeMap<AgentIdentifier,AT>() ;

	/** Register an agent and compute its identifier.
	 */	
	public synchronized void register(AT a) {
		assert(a!=null);
		AgentIdentifier id = new AgentIdentifier();
		while (this.__annuaire.containsKey(id)) {
			id = new AgentIdentifier();
		}
		this.__annuaire.put(id, a) ;
		a.setId(id);
	}

	/** Register an agent and give it the specified identifier.
	 */	
	public synchronized void register(AgentIdentifier id, AT a) {
		assert(id!=null);
		assert(a!=null);
		if (!this.__annuaire.containsKey(id)) {
			this.__annuaire.put(id, a) ;
			a.setId(id);
		}
	}

	/** Unregister an agent.
	 */
	public synchronized AT unregister(AgentIdentifier id) {
		assert(id!=null);
		AT ag = this.__annuaire.get(id);
		if (ag!=null) {
			ag.setId(null);
		}
		return this.__annuaire.remove(id) ;
	}

	/** Replies the count of registered agents.
	 */
	public synchronized int size() {
		return this.__annuaire.size() ;
	}

	/** Replies the list of the identifiers of the registered agents.
	 */
	public synchronized AgentIdentifier[] getAllAgentIdentifiers() {
		Set<AgentIdentifier> theSet = this.__annuaire.keySet();
		AgentIdentifier[] tab = new AgentIdentifier[theSet.size()];
		theSet.toArray(tab);
		return tab;
	}

	/** Replies all the agents.
	 */
	synchronized Collection<AT> getAllAgents() {
		return this.__annuaire.values();
	}

	/** Replies the agent that corresponds to the specified identifier.
	 */
	synchronized AT getAgent(AgentIdentifier id) {
		assert(id!=null);
		return this.__annuaire.get(id) ;
	}

	/** Replies the identifier of the specified agent.
	 */	
	synchronized AgentIdentifier getId(AT a) {
		assert(a!=null);
		Iterator<Entry<AgentIdentifier,AT>> itEntries = this.__annuaire.entrySet().iterator();
		while(itEntries.hasNext()) {
			Entry<AgentIdentifier,AT> entry = itEntries.next();
			if (entry.getValue() == a) {
				return entry.getKey() ;
			}
		}
		return null ;
	}
		
}