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

package org.arakhne.tinyMAS.situatedEnvironment.influence;

import java.lang.ref.WeakReference;

import org.arakhne.tinyMAS.core.AgentIdentifier;

/** This class describes an influence inside an environment.
 * <p>
 * An influence is a desired of action emitted by an agent.
 * The environment collects all the influences and tries
 * to applies them according to the environment's rules.
 * 
 * @author St&eacute;phane GALLAND &lt;galland@arakhne.org&gt;
 * @author Nicolas GAUD &lt;nicolas.gaud@utbm.fr&gt;
 * @deprecated See {@link "http://www.janus-project.org"}
 */
@Deprecated
public abstract class Influence {

	/** This is the identifier of the influenced object.
	 */
	private final WeakReference<AgentIdentifier> __emitter;
	
	protected Influence(AgentIdentifier emitter) {
		assert(emitter!=null);
		this.__emitter = new WeakReference<AgentIdentifier>(emitter);
	}

	/** Replies the identifier of the agent that sent this influence.
	 */
	public AgentIdentifier getEmitter() {
		return (this.__emitter==null) ? null : this.__emitter.get();
	}
	
}