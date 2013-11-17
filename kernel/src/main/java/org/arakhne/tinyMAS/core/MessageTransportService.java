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


/** This component permits to route the message between agents.
 * <p>
 * The implementation of this MTS assumes that only one kernel
 * is available. It does not support more than one kernel and
 * network communication.
 * 
 * @author St&eacute;phane GALLAND &lt;galland@arakhne.org&gt;
 * @author Nicolas GAUD &lt;nicolas.gaud@utbm.fr&gt;
 * @deprecated See {@link "http://www.janus-project.org"}
 */
@Deprecated
public class MessageTransportService {

	/** Local Message boxes.
	 */	
	private final MessageBoxManager __msgboxes = new MessageBoxManager() ;
	
	/** Invoked by the kernel when the MTS should stop.
	 */
	public void stopMTS() {
		//
	}
	
	/** Invoked by the kernel when the MTS should start.
	 */
	public void startMTS() {
		//
	}

	/** Create a local message box for the specified agent.
	 */	
	void registerAgent(AgentIdentifier id) {
		assert(id!=null);
		this.__msgboxes.registerAgent(id);
	}

	/** Delete the local message box of the specified agent.
	 */	
	void unregisterAgent(AgentIdentifier id) {
		assert(id!=null);
		this.__msgboxes.unregisterAgent(id);
	}
	
	/** Send the specified message according to the
	 * knownledge of this MTS.
	 */
	public boolean send(Message m) {
		assert(m!=null);
		return registerInLocalBoxes(m);
	}

	/** Store the specified message inside the local message boxes.
	 * 
	 * @return <code>true</code> if the message was successfully stored,
	 * otherwise <code>false</code>
	 */
	protected boolean registerInLocalBoxes(Message m) {
		assert(m!=null);
		return this.__msgboxes.storeMessage(m);
	}

	/** Replies the next available message for the specified agent.
	 * <p>
	 * This method consumes the message. It means that the message will
	 * never be obtain another time.
	 * 
	 * @param id is the identifier of the agent for which the message was extracted.
	 * @return a message or <code>null</code> if none available
	 */
	public Message getNextMessage(AgentIdentifier id) {
		assert(id!=null);
		return this.__msgboxes.getNextMessage(id);
	}
	
	/** Replies if a message is available for the specified agent.
	 * <p>
	 * This method does not consume the message.
	 * 
	 * @param id is the identifier of the agent for which the message was extracted.
	 * @return <code>true</code> if a message is available, otherwise <code>false</code>
	 */
	protected boolean hasMessage(AgentIdentifier id) {
		assert(id!=null);
		return this.__msgboxes.hasMessage(id);
	}

	/** Replies the identifier of a kernel.
	 * <p>
	 * This method is invoked by the kernel to obtain its own identifier.
	 */
	protected KernelIdentifier getKernelId() {
		return new KernelIdentifier();
	}

}