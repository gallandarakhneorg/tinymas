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

import java.util.UUID;

/**
 * Spawner.
 *
 * @author St&eacute;phane GALLAND &lt;galland@arakhne.org&gt;
 */
@SuppressWarnings({"deprecation"})
final class Identifiers {

	public static UUID toUUID(AgentIdentifier aid) {
		final String aidstr = aid.toString();
		final int index = aidstr.indexOf(":");
		assert index >= 0;
		return UUID.fromString(aidstr.substring(0, index));
	}

}
