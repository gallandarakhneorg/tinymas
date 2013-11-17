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

import java.util.Map;
import java.util.Queue;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;



/** This class manager a set of message boxes. Each message box is associated
 * to exactly one agent and contains a list of messages that were not consumed
 * by the agent.
 * 
 * @author St&eacute;phane GALLAND &lt;galland@arakhne.org&gt;
 * @author Nicolas GAUD &lt;nicolas.gaud@utbm.fr&gt;
 * @deprecated See {@link "http://www.janus-project.org"}
 */
@Deprecated
class MessageBoxManager {

	/** Message boxes.
	 */	
	private final Map<AgentIdentifier,Queue<Message>> __queues = new WeakHashMap<AgentIdentifier,Queue<Message>>() ;

	/** Create a message box for the specified agent
	 * 
	 * @param id is the identifier of the agent for which a box must be created.
	 */	
	synchronized void registerAgent(AgentIdentifier id) {
		assert(id!=null);
		this.__queues.put(id, new ConcurrentLinkedQueue<Message>()) ;
	}

	/** Delete the message box of the specified agent
	 * 
	 * @param id est l'identificateur de l'agent
	 */	
	synchronized void unregisterAgent(AgentIdentifier id) {
		assert(id!=null);
		this.__queues.remove(id) ;
	}
	
	/** Store a message inside a box.
	 * <p>
	 * The box depends on the message receiver.
	 *
	 * @param m is the message to store in the mailbox.
	 * @return <code>true</code> if the message was successfully stored, otherwise <code>false</code>
	 */
	synchronized public boolean storeMessage(Message m) {
		if (m!=null && m.TO!=null) {
			Queue<Message> queue = this.__queues.get(m.TO);
			if (queue!=null) {				
				return queue.offer(m);
			}
		}
		return false ;
	}

	/** Replies the next available message for the specified agent.
	 * <p>
	 * This method consumes the message. It means that the message will
	 * never be obtain another time.
	 * 
	 * @param id is the identifier of the agent for which the message was extracted.
	 * @return a message or <code>null</code> if none available
	 */
	public synchronized Message getNextMessage(AgentIdentifier id) {
		if (id!=null) {
			Queue<Message> queue = this.__queues.get(id);
			if ((queue!=null)&&(queue.size()>0)) {				
				return queue.poll() ;
			}
		}

		return null ;
	}
	
	/** Replies if a message is available for the specified agent.
	 * <p>
	 * This method does not consume the message.
	 * 
	 * @param id is the identifier of the agent for which the message was extracted.
	 * @return <code>true</code> if a message is available, otherwise <code>false</code>
	 */
	protected synchronized boolean hasMessage(AgentIdentifier id) {
		if (id!=null) {
			Queue<Message> queue = this.__queues.get(id);
			return ((queue!=null)&&(queue.size()>0));				
		}
		return false ;
	}
	
}