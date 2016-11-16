/*
 * $Id$
 *
 * SARL is an general-purpose agent programming language.
 * More details on http://www.sarl.io
 *
 * Copyright (C) 2014-2015 the original authors or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.arakhne.tinyMAS.core;

import java.lang.reflect.Constructor;
import java.util.UUID;

import io.sarl.lang.core.BuiltinCapacitiesProvider;

/**
 * Spawner.
 *
 * @author St&eacute;phane GALLAND &lt;galland@arakhne.org&gt;
 */
@SuppressWarnings({"deprecation"})
final class Spawner {

	public static UUID spawn(
			Boot kernel,
			TMDefaultSpace defaultSpace, 
			Class<? extends io.sarl.lang.core.Agent> aAgent,
			UUID parentID, UUID agentID,
			Object... params) {
		assert kernel != null;
		TMSarlAgent agent = createAgent(defaultSpace, aAgent, parentID, agentID, params);
		if (agent != null) {
			spawn(kernel, agent);
			return agent.getID();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static void spawn(
			Boot kernel,
			TMSarlAgent agent) {
		assert kernel != null;
		assert agent != null;
		AgentIdentifier tmid = new AgentIdentifier(kernel.getKernelId(), agent.getID().toString());
		kernel.addAgent(tmid, agent);
	}

	public static TMSarlAgent createAgent(
			TMDefaultSpace defaultSpace, 
			Class<? extends io.sarl.lang.core.Agent> aAgent,
			UUID parentID, UUID agentID,
			Object... params) {
		assert defaultSpace != null;
		assert aAgent != null;
		assert parentID != null;
		if (io.sarl.lang.core.Agent.class.isAssignableFrom(aAgent)) {
			final UUID theAgentID = (agentID == null) ? UUID.randomUUID() : agentID;
			try {
				Constructor<? extends io.sarl.lang.core.Agent> cons = ((Class<? extends io.sarl.lang.core.Agent>) aAgent).getConstructor(
						BuiltinCapacitiesProvider.class, UUID.class, UUID.class);
				io.sarl.lang.core.Agent sarlAgent = cons.newInstance(null, parentID, theAgentID);
				TMSarlAgent tmAgent = new TMSarlAgent(defaultSpace, sarlAgent, params);
				return tmAgent;
			} catch (Exception ex) {
				//
			}
		}
		return null;
	}

}
