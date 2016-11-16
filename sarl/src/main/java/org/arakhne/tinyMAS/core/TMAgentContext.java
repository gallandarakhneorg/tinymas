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

import java.util.Collections;
import java.util.UUID;

import org.eclipse.xtext.xbase.lib.Pure;

import io.sarl.lang.core.AgentContext;
import io.sarl.lang.core.Space;
import io.sarl.lang.core.SpaceSpecification;
import io.sarl.lang.util.SynchronizedCollection;
import io.sarl.util.Collections3;

@SuppressWarnings("deprecation")
class TMAgentContext implements AgentContext {

	public static final UUID TINYMAS_AGENT_CONTEXT_ID = UUID.fromString("cdb0d568-4059-40cf-96c4-d078fee91cb1"); //$NON-NLS-1$
	
	private final TMDefaultSpace defaultSpace;
	
	public TMAgentContext(TMDefaultSpace defaultSpace) {
		assert (defaultSpace != null);
		this.defaultSpace = defaultSpace;
		this.defaultSpace.setAgentContext(this);
	}
	
	@Override
	@Pure
	public UUID getID() {
		return TINYMAS_AGENT_CONTEXT_ID;
	}

	@Override
	@Pure
	public io.sarl.lang.core.EventSpace getDefaultSpace() {
		return this.defaultSpace;
	}

	@Override
	@Pure
	public SynchronizedCollection<? extends Space> getSpaces() {
		return Collections3.synchronizedCollection(Collections.singleton(this.defaultSpace), this);
	}

	@Override
	@Pure
	public <S extends Space> SynchronizedCollection<S> getSpaces(Class<? extends SpaceSpecification<S>> spec) {
		//XXX: Not implemented
		throw new UnsupportedOperationException();
	}

	@Override
	public <S extends Space> S createSpace(Class<? extends SpaceSpecification<S>> spec,
			UUID spaceUUID, Object... creationParams) {
		//XXX: Not implemented
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public <S extends Space> S getOrCreateSpace(Class<? extends SpaceSpecification<S>> spec,
			UUID spaceUUID, Object... creationParams) {
		return getOrCreateSpaceWithSpec(spec, spaceUUID, creationParams);
	}

	@Override
	public <S extends Space> S getOrCreateSpaceWithSpec(Class<? extends SpaceSpecification<S>> spec,
			UUID spaceUUID, Object... creationParams) {
		//XXX: Not implemented
		throw new UnsupportedOperationException();
	}

	@Override
	public <S extends Space> S getOrCreateSpaceWithID(UUID spaceUUID,
			Class<? extends SpaceSpecification<S>> spec, Object... creationParams) {
		//XXX: Not implemented
		throw new UnsupportedOperationException();
	}

	@SuppressWarnings("unchecked")
	@Override
	@Pure
	public <S extends Space> S getSpace(UUID spaceUUID) {
		if (spaceUUID.equals(this.defaultSpace.getID().getID())) {
			return (S) this.defaultSpace;
		}
		return null;
	}

}
