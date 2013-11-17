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

import java.rmi.server.UID;
import java.util.UUID;


/** Unique identifier of an agent.
 * <p>
 * The agent identifier must be unique along all the kernel which are accessible by this agent.
 * <p>
 * This identifier is composed of two parts:
 * <ul>
 * <li>the kernel's identifier</li> where the agent was spawn for the first time; if the agent has migrated, this part does not reflect the
 * kernel where the agent is currently run.</li>
 * <li>a unique identifier (see {@link UUID})</li>
 * </ul>
 * 
 * @author St&eacute;phane GALLAND &lt;galland@arakhne.org&gt;
 * @author Nicolas GAUD &lt;nicolas.gaud@utbm.fr&gt;
 * @deprecated See {@link "http://www.janus-project.org"}
 */
@Deprecated
public class AgentIdentifier implements Identifier {

	private static final long serialVersionUID = 2423898722296398245L;
	
	private final KernelIdentifier __kernel_id;
	private final String __agent_id;
	
	private String representation;
	
	/** Create a new identifier.
	 */
	public AgentIdentifier() {
		this(Kernel.getSingleton().getKernelId(),
				new UID().toString());
	}

	/** Spawn an existing identifier.
	 * 
	 * @param kernelId is the kernel identifier where the agent was spawn for the first time.
	 * @param agentId is the agent identifier computed by the kernel.
	 */
	public AgentIdentifier(KernelIdentifier kernelId, String agentId) {
		assert(kernelId!=null);
		assert(agentId!=null);
		this.__kernel_id = kernelId;
		this.__agent_id = agentId;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append(this.__agent_id);
		buf.append(':');
		buf.append(Long.toHexString(serialVersionUID));
		buf.append('@');
		buf.append(this.__kernel_id.toString());
		return buf.toString();
	}

	/** Replies the string representation associated to this identifier or
	 * if it was never set, the result of {@link #toString()}
	 * 
	 * @return the string representation of this identifier.
	 */
	public String getString() {
		if (this.representation!=null) return this.representation;
		return toString();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object o) {
		if (o instanceof Identifier) {
			return compareTo((Identifier)o)==0;
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public int compareTo(Identifier id) {
		if (id==null) return 1;
		return toString().compareTo(id.toString());
	}

	/** Replies the kernel's identifier part of this agent's identifier.
	 * <p>
	 * The kernel identifier basically corresponds to the kernel that
	 * on which the agent was spawn for the first time.
	 * 
	 * @return the identifier of the kernel on which the agent is located.
	 */
	public KernelIdentifier getKernelId() {
		return this.__kernel_id;
	}
	
	/** Set the string representation of this identifier.
	 * 
	 * @param str is the string representation associated to this identifier.
	 */
	void setStringRepresentation(String str) {
		this.representation = str;
	}
	
}