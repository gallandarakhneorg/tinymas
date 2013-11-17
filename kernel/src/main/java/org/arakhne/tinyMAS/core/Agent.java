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

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/** Abstract class that represents an agent.
 * 
 * @author St&eacute;phane GALLAND &lt;galland@arakhne.org&gt;
 * @author Nicolas GAUD &lt;nicolas.gaud@utbm.fr&gt;
 * @deprecated See {@link "http://www.janus-project.org"}
 */
@Deprecated
public abstract class Agent implements Comparable<Agent> {
	
	/** Identifier of an agent.
	 */
	private volatile AgentIdentifier __id ;
	
	private boolean __started = false;
	
	/** Probe.
	 */
	private DefaultProbe __probe = null;
	
	/** Create an uninitialized agent.
	 */
	public Agent() {
		this.__id = null ;
	}
	
	/** Force the identifier of this agent to adopt the string representation
	 * of this agent.
	 * 
	 * @see #toString()
	 */
	protected void forceIdentifierStringRepresentation() {
		forceIdentifierStringRepresentation(toString());
	}
	
	/** Force the identifier of this agent to adopt the string representation
	 * of this agent.
	 */
	protected void forceIdentifierStringRepresentation(String str) {
		if (this.__id!=null)
			this.__id.setStringRepresentation(str);
	}

	/** Replies a probe on this agent.
	 * 
	 * @return a probe or <code>null</code> if this agent
	 * could not provide a probe.
	 */
	public Probe getProbe() {
		synchronized(this) {
			return this.__probe;
		}
	}

	/** Set value that could be probed by another entity.
	 * 
	 * @param probeName is the name of the value.
	 * @param value is the value that could be obtained by a probe user.
	 */
	protected void setProbe(String probeName, Object value) {
		synchronized(this) {
			assert((probeName!=null)&&(!"".equals(probeName))); //$NON-NLS-1$
			if (value==null) {
				if (this.__probe!=null) {
					this.__probe.removeProbeValue(probeName);
					if (!this.__probe.hasProbeValues()) {
						this.__probe = null;
					}
				}
			}
			else {
				if (this.__probe==null) {
					this.__probe = new DefaultProbe(this);
				}
				this.__probe.putProbeValue(probeName,value);
			}
		}
	}

	/** Clear the probes.
	 */
	protected void clearProbes() {
		synchronized(this) {
			this.__probe.clearProbeValues();
			this.__probe = null;
		}
	}

	/** Remove a probe.
	 */
	protected void removeProbe(String probeName) {
		synchronized(this) {
			assert((probeName!=null)&&(!"".equals(probeName))); //$NON-NLS-1$
			if (this.__probe!=null) {
				this.__probe.removeProbeValue(probeName);
				if (!this.__probe.hasProbeValues()) {
					this.__probe = null;
				}
			}
		}
	}

	/** Remove all the probes.
	 */
	protected void removeAllProbes() {
		synchronized(this) {
			if (this.__probe!=null) {
				this.__probe.clearProbeValues();
				this.__probe = null;
			}
		}
	}

	public int compareTo(Agent a) {
		if ((a==null)||(a.getId()==null)) return 1;
		if (this.__id==null) return -1;
		return this.__id.compareTo(a.getId());
	}

	@Override
	public boolean equals(Object o) {
		if ((o!=null)&&(o==this)) return true;
		if ((o!=null)&&(o instanceof Agent)) {
			return compareTo((Agent)o)==0;
		}
		if ((o!=null)&&(o instanceof AgentIdentifier)) {
			return ((AgentIdentifier)o).compareTo(this.__id)==0;
		}
		return false;
	}

	/** Replies the identifier of the agent.
	 */	
	public final AgentIdentifier getId() {
		return this.__id ;
	}
	
	/** Set the agent's identifier of this agent.
	 * 
	 * @param id is the identifier computed by the kernel.
	 */
	void setId(AgentIdentifier id) {
		this.__id = id ;
	}
	
	/** Replies if this agent living.
	 */
	boolean isAlive() {
		return this.__started && this.__id!=null;
	}

	/** Start the life of an agent.
	 *  <p>
	 *  This method must initialize the simulation's attributes of the agent.
	 * 
	 * @param id is the identifier of the agent.
	 */
	final void start(AgentIdentifier id) {
		assert(id!=null);
		this.__started = true;
		this.__id = id ;
		start();
	}
	
	/** Start the life of an agent.
	 *  <p>
	 *  This method must initialize the simulation's attributes of the agent.
	 */
	public void start() {
		//
	}

	/** Notify the agent that it must run one step of its life.
	 */
	public abstract void live() ;

	/** Invoked when the agent is dead and must deasappear.
	 * <p>
	 * This method is supposed to released any computer resource
	 * allocated by the agent.
	 */	
	public void stop() {
		this.__started = false;
		this.__id = null ;
	}

	/** Send a message to another agent.
	 * 
	 * @param to is the identifier of the message receiver.
	 * @param content is the content of the message. 
	 * @return <code>true</code> if the message was send, otherwise <code>false</code>
	 */	
	protected final boolean sendMessage(AgentIdentifier to, Serializable content) {
		assert(to!=null);
		if (this.__id!=null) {
			Message m = new Message(this.__id,to,content) ;
			return Kernel.getSingleton().getMTS().send(m) ;
		}
		return false ;
	}
	
	/** Replies the next message available for this agent.
	 * 
	 * @return the message or <code>null</code> if none was available
	 */
	protected final Message getNextMessage() {
		if (this.__id!=null) {
			return Kernel.getSingleton().getMTS().getNextMessage(this.__id) ;
		}
		return null ;
	}

	/** Replies if a message is available.
	 */
	protected final boolean hasMessage() {
		if (this.__id!=null) {
			return Kernel.getSingleton().getMTS().hasMessage(this.__id) ;
		}
		return false ;
	}

	/** The agent should invoke this method to kill itself.
	 */
	protected final void killMe() {
		Kernel.getSingleton().killAgent(this.__id);		
	}
	
	/** Replies the simulation times in milliseconds.
	 */
	protected final double getSimulationTime() {
		return Kernel.getSingleton().getSimulationClock().getSimulationTime(TimeUnit.MILLISECONDS);
	}

	/** Replies the simulation times.
	 */
	protected final double getSimulationTime(TimeUnit timeUnit) {
		assert(timeUnit!=null);
		return Kernel.getSingleton().getSimulationClock().getSimulationTime(timeUnit);
	}

	/** Replies the duration in milliseconds of one turn of the simulation.
	 */
	protected final double getSimulationStepDuration() {
		return getSimulationStepDuration(TimeUnit.MILLISECONDS);
	}

	/** Replies the duration of a simulation loop for this environment.
	 * 
	 * @param desired_unit indicates the unit to use
	 * @return the simulation's step time.
	 */
	protected double getSimulationStepDuration(TimeUnit desired_unit) {
		assert(desired_unit!=null);
		return Kernel.getSingleton().getSimulationClock().getSimulationStepDuration(desired_unit);
	}

	/** Convert the specified quantity specified in
	 * something per second to the same quantity
	 * expressed in something per simulation turn.
	 * 
	 * @see #getSimulationStepDuration()
	 * @see #getSimulationStepDuration(TimeUnit)
	 */
	protected double perSecond(double quantity) {
		return getSimulationStepDuration(TimeUnit.SECONDS) * quantity;
	}
	
	/** Register this agent as a provider of the specified services.
	 */
	protected final void registerService(String... services) {
		Kernel<?,?,?,?> k = Kernel.getSingleton();
		assert(k!=null);
		assert(this.__id!=null);
		YellowPages yp = k.getYellowPageSystem();
		if (yp!=null) {
			for (String service : services) {
				yp.registerService(service, this.__id);
			}
		}
	}

	/** Unregister this agent as a provider of the specified services.
	 */
	protected final void unregisterService(String... services) {
		Kernel<?,?,?,?> k = Kernel.getSingleton();
		assert(k!=null);
		assert(this.__id!=null);
		YellowPages yp = k.getYellowPageSystem();
		if (yp!=null) {
			for (String service : services) {
				yp.unregisterService(service, this.__id);
			}
		}
	}

}