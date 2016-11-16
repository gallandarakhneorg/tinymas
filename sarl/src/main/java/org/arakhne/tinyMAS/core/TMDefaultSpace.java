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

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import org.eclipse.xtext.xbase.lib.Pure;

import io.sarl.lang.core.Address;
import io.sarl.lang.core.Event;
import io.sarl.lang.core.EventSpace;
import io.sarl.lang.core.Scope;
import io.sarl.lang.core.Space;
import io.sarl.lang.core.SpaceID;
import io.sarl.lang.util.SynchronizedSet;
import io.sarl.util.Collections3;

/**
 * Event driven Interaction {@link Space} for agents.
 *
 * @author St&eacute;phane GALLAND &lt;galland@arakhne.org&gt;
 */
@SuppressWarnings({"rawtypes", "deprecation"})
class TMDefaultSpace implements EventSpace {

	public static final UUID TINYMAS_DEFAULT_SPACE_ID = UUID.fromString("1db39309-8be7-4809-ad76-1ede6e792296"); //$NON-NLS-1$

	private WeakReference<TMAgentContext> context;
	
	private SpaceID spaceID;
	
	private WhitePages whitePages;
	private MessageTransportService mts;
	private KernelIdentifier kernelID;
	
	private List<TMSarlAgent> agentToLaunch = new ArrayList<>();
	
	public TMDefaultSpace(WhitePages whitePages, MessageTransportService mts) {
		this.whitePages = whitePages;
		this.mts = mts;
	}

	void setKernelID(KernelIdentifier kernelID) {
		this.kernelID = kernelID;
	}
	
	public TMAgentContext getAgentContext() {
		return this.context.get();
	}
	
	public UUID spawn(Class<? extends io.sarl.lang.core.Agent> aAgent, UUID agentID, Object... params) {
		final TMSarlAgent agent = Spawner.createAgent(this, aAgent, TINYMAS_DEFAULT_SPACE_ID, agentID, params);
		this.agentToLaunch.add(agent);
		return agent.getID();
	}
	
	Iterable<TMSarlAgent> consumeAgentToLaunch() {
		Iterable<TMSarlAgent> iterable = this.agentToLaunch;
		this.agentToLaunch = new ArrayList<>();
		return iterable;
	}
	
	void setAgentContext(TMAgentContext context) {
		this.context = new WeakReference<>(context);
		this.spaceID = new SpaceID(this.context.get().getID(), TINYMAS_DEFAULT_SPACE_ID, null);
	}
	
	@Override
	@Pure
	public Address getAddress(UUID id) {
		for (AgentIdentifier aid : this.whitePages.getAllAgentIdentifiers()) {
			UUID uid = Identifiers.toUUID(aid);
			if (uid.equals(id)) {
				return new Address(getID(), id);
			}
		}
		return null;
	}

	@Override
	public void emit(Event event, final Scope<Address> scope) {
		AgentIdentifier sender = null;
		if (event.getSource().getUUID().equals(TINYMAS_DEFAULT_SPACE_ID)) {
			sender = new AgentIdentifier(this.kernelID, TINYMAS_DEFAULT_SPACE_ID.toString());
		} else {
			for (AgentIdentifier id : this.whitePages.getAllAgentIdentifiers()) {
				UUID uid = Identifiers.toUUID(id);
				if (uid.equals(event.getSource().getUUID())) {
					sender = id;
					break;
				}
			}
		}
		if (sender != null) {
			for (AgentIdentifier id : this.whitePages.getAllAgentIdentifiers()) {
				UUID uid = Identifiers.toUUID(id);
				if (scope == null || scope.matches(new Address(getID(), uid))) {
					this.mts.send(new Message(sender, id, event));
				}
			}
		}
	}

	@Override
	public void emit(Event event) {
		emit(event, null);
	}

	@Override
	@Deprecated
	public SpaceID getID() {
		return getSpaceID();
	}

	@Override
	public SynchronizedSet<UUID> getParticipants() {
		Set<UUID> uuids = new TreeSet<>();
		for (AgentIdentifier id : this.whitePages.getAllAgentIdentifiers()) {
			uuids.add(Identifiers.toUUID(id));
		}
		return Collections3.unmodifiableSynchronizedSet(uuids, uuids);
	}

	@Override
	public SpaceID getSpaceID() {
		return this.spaceID;
	}

}
