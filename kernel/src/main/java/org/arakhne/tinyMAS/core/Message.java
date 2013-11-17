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



/** Message exchange by agents.
 * 
 * @author St&eacute;phane GALLAND &lt;galland@arakhne.org&gt;
 * @author Nicolas GAUD &lt;nicolas.gaud@utbm.fr&gt;
 * @deprecated See {@link "http://www.janus-project.org"}
 */
@Deprecated
public class Message implements Serializable {

	private static final long serialVersionUID = 48820292710180478L;

	/** Content of this message
	 */	
	public final Serializable CONTENT ;

	/** Sender.
	 */	
	public final AgentIdentifier FROM ;

	/** Receiver.
	 */	
	public final AgentIdentifier TO ;

	/**
	 * @param from is the emitter
	 * @param to is the received
	 * @param content is the content of the message.
	 */
	public Message(AgentIdentifier from, AgentIdentifier to, Serializable content) {
		assert(from!=null);
		assert(to!=null);
		this.FROM = from ;
		this.TO = to ;
		this.CONTENT = content ;
	}
	
	/** Replies if the content of this message has the specified type.
	 * 
	 * @param type is the type to test against the message content
	 * @return <code>true</code> if the message content is an instance of the given type,
	 * otherwise <code>false</code>
	 */
	public boolean isTypeOf(Class<? extends Serializable> type) {
		assert(type!=null);
		return ((this.CONTENT!=null)&&
				(type.isInstance(this.CONTENT)));
	}
	
	/** Replies the content.
	 * 
	 * @param <T> is the type of the message content.
	 * @throws ClassCastException if the content could not be casted as type {@code T}.
	 * @return the message content. 
	 */
	@SuppressWarnings("unchecked")
	public <T extends Serializable> T getContent() {
		return (T)this.CONTENT;
	}

}