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

package org.arakhne.tinyMAS.situatedEnvironment.body;


import org.arakhne.tinyMAS.core.AgentIdentifier;
import org.arakhne.tinyMAS.core.Probe;
import org.arakhne.tinyMAS.situatedEnvironment.environment.SituatedObject;
import org.arakhne.tinyMAS.situatedEnvironment.influence.Influence;
import org.arakhne.tinyMAS.situatedEnvironment.perception.PerceptionBody;

/** Agent body.
 * 
 * @author St&eacute;phane GALLAND &lt;galland@arakhne.org&gt;
 * @deprecated See {@link "http://www.janus-project.org"}
 */
@Deprecated
public interface AgentBody<PB extends PerceptionBody, IT extends Influence> extends SituatedObject {

	/** Replies the identifier of the agent that owns this body.
	 */
	public AgentIdentifier getAgent();
	
	/** Replies the probe of the agent that owns this body.
	 */
	public Probe getAgentProbe();

	/** Replies the perception characteristics of thi body.
	 */
	public PB getPerceptionBody();
	
	/** Replies if this body was crashed.
	 * A freezed body can not be moved.
	 */
	public boolean isFreezed();

	/** Send an influence to the environment.
	 */
	public boolean influence(IT... influences);

}