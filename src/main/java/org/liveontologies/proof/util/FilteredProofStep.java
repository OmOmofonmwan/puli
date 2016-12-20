package org.liveontologies.proof.util;

/*-
 * #%L
 * OWL API Proof Extension
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2014 - 2016 Live Ontologies Project
 * %%
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
 * #L%
 */

import java.util.Set;

class FilteredProofStep<C> extends ConvertedProofStep<C> {

	private final Set<? extends ProofNode<C>> forbidden_;

	FilteredProofStep(ProofStep<C> delegate,
			Set<? extends ProofNode<C>> forbidden) {
		super(delegate);
		Util.checkNotNull(forbidden);
		this.forbidden_ = forbidden;
	}

	@Override
	protected FilteredProofNode<C> convert(ProofNode<C> premise) {
		return new FilteredProofNode<C>(premise, forbidden_);
	}

}
