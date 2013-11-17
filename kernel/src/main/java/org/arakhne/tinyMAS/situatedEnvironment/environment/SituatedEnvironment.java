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

package org.arakhne.tinyMAS.situatedEnvironment.environment;


import java.util.Collection;

import org.arakhne.tinyMAS.core.AgentIdentifier;
import org.arakhne.tinyMAS.core.Environment;
import org.arakhne.tinyMAS.core.Identifier;
import org.arakhne.tinyMAS.situatedEnvironment.agent.SituatedAgent;
import org.arakhne.tinyMAS.situatedEnvironment.body.AgentBody;
import org.arakhne.tinyMAS.situatedEnvironment.influence.Influence;
import org.arakhne.tinyMAS.situatedEnvironment.perception.Perception;

/** Interface representant un environment.
 * 
 * @author St&eacute;phane GALLAND &lt;galland@arakhne.org&gt;
 * @deprecated See {@link "http://www.janus-project.org"}
 */
@Deprecated
public interface SituatedEnvironment<AT extends SituatedAgent<AB,OB,PT,IT>, AB extends AgentBody<?,IT>, OB extends SituatedObject, PT extends Perception<?>, IT extends Influence> extends Environment<AT> {

	/** Replies all the bodies of the agents inside the environment.
	 */
	public Collection<AB> getAllAgentBodies();

	/** Replies the body of the agent inside the environment.
	 * 
	 * @return the location according to the environment dimension, ie.
	 * an array of size 1 if the environment is 1D, of size 2 for
	 * an 2D environment...
	 */
	public <T extends AB> T getAgentBody(AgentIdentifier agent);

	/** Replies the location of the object (excluding
	 * agents) inside the environment.
	 * 
	 * @return the spatial description of the object. 
	 */
	public OB getObjectBody(Identifier object);
	
	/** Send an influence to the environment.
	 * 
	 * @param influences is the list of influences to send to this environment.
	 * @return <code>true</code> if the influences was registered,
	 * otherwise <code>false</code> 
	 */
	public boolean influence(IT... influences);
	
	/** Compute and replies the perceptions for the specified agent.
	 */
	public PT[] perceive(AgentIdentifier agent);	

}