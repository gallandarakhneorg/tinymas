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

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/** Manager a set of services.
 * <p>
 * A service is provided by a set of agents
 * and is identified by its name.
 * 
 * @author St&eacute;phane GALLAND &lt;galland@arakhne.org&gt;
 * @author Nicolas GAUD &lt;nicolas.gaud@utbm.fr&gt;
 * @deprecated See {@link "http://www.janus-project.org"}
 */
@Deprecated
public class YellowPages {

	/** Repository
	 */	
	private final Map<String,Set<AgentIdentifier>> __annuaire = new TreeMap<String,Set<AgentIdentifier>>() ;
	
	/** Register a service provided by the specified agent.
	 * 
	 * @param service is the name of the service.
	 * @param agent is the agent that provides the service
	 */	
	public void registerService(String service, AgentIdentifier agent) {
		assert((service!=null)&&(!"".equals(service))); //$NON-NLS-1$
		assert(agent!=null);
		Set<AgentIdentifier> list = this.__annuaire.get(service);
		if (list==null) {
			list = new TreeSet<AgentIdentifier>();
			this.__annuaire.put(service,list);
		}
		if (!list.contains(agent)) {
			list.add(agent);
		}
	}

	/** Remove the specified agent-service association. 
	 * 
	 * @param service is the name of the service.
	 * @param agent is the agent that no more provides the service
	 */
	public void unregisterService(String service, AgentIdentifier agent) {
		assert((service!=null)&&(!"".equals(service))); //$NON-NLS-1$
		assert(agent!=null);
		Set<AgentIdentifier> list = this.__annuaire.get(service);
		if (list!=null) {
			list.remove(agent);
		}
	}

	/** Remove a service from the system.
	 * <p>
	 * All associated agents will be unlinked to the specified service.
	 */
	public void unregisterService(String service) {
		assert((service!=null)&&(!"".equals(service))); //$NON-NLS-1$
		this.__annuaire.remove(service);
	}

	/** Remove the specified agent from the yellow pages.
	 */
	public void unregisterServices(AgentIdentifier agent) {
		assert(agent!=null);
		Iterator<Set<AgentIdentifier>> itEntry = this.__annuaire.values().iterator();
		while (itEntry.hasNext()) {
			Set<AgentIdentifier> list = itEntry.next();
			if (list!=null) {
				list.remove(agent);
			}
		}
	}

	/** Replies if the specified agent provides the given service.
	 */
	public boolean isRegisteredAgent(String service, AgentIdentifier agent) {
		assert((service!=null)&&(!"".equals(service))); //$NON-NLS-1$
		assert(agent!=null);
		Set<AgentIdentifier> list = this.__annuaire.get(service);
		if (list!=null) {
			return list.contains(agent);
		}
		return false;
	}

	/** Replies if the specified service is provided by at least one agent.
	 */
	public boolean isRegisteredService(String service) {
		assert((service!=null)&&(!"".equals(service))); //$NON-NLS-1$
		return this.__annuaire.containsKey(service);
	}
	
	/** Replies if the specified agent provides a service.
	 */
	public boolean isRegisteredAgent(AgentIdentifier agent) {
		assert(agent!=null);
		Iterator<Set<AgentIdentifier>> itEntry = this.__annuaire.values().iterator();
		while (itEntry.hasNext()) {
			Set<AgentIdentifier> list = itEntry.next();
			if ((list!=null)&&
				(list.contains(agent))) {
				return true;
			}
		}
		return false;
	}

	/** Replies all the agents that provide services
	 */
	public AgentIdentifier[] getAgents(String service) {
		assert((service!=null)&&(!"".equals(service))); //$NON-NLS-1$
		Set<AgentIdentifier> list = this.__annuaire.get(service);
		if (list!=null) {
			AgentIdentifier[] tab = new AgentIdentifier[list.size()];
			list.toArray(tab);
			return tab;
		}
		return new AgentIdentifier[0];
	}
	
	/** Replies an agent that provides the specified service.
	 * <p>
	 * The agent is randomly selected inside the list of agents
	 * that provide the specified service.
	 */
	public AgentIdentifier getAgentFor(String service) {
		assert((service!=null)&&(!"".equals(service))); //$NON-NLS-1$
		Set<AgentIdentifier> list = this.__annuaire.get(service);
		if (list!=null) {
			try {
				int r = (int)(Math.random()*list.size());
				return (AgentIdentifier)list.toArray()[r];
			}
			catch(IndexOutOfBoundsException _) {
				//
			}
		}
		return null;
	}

	/** Replies all the services registered inside this repository.
	 */
	public String[] getAllServices() {
		Set<String> serv = this.__annuaire.keySet();
		String[] services = new String[serv.size()];
		serv.toArray(services);
		return services;
	}

	@SuppressWarnings("unchecked")
	protected final <T extends MessageTransportService> T getMTS(Class<T> clazz) {
		assert(clazz!=null);
		Kernel<?,?,?,?> kernel = Kernel.getSingleton();
		if (kernel!=null) {
			MessageTransportService mts = kernel.getMTS();
			if (clazz.isInstance(mts)) return (T)mts;
		}
		return null;
	}
	
}