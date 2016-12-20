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

class DerivableProofNode<C> extends ConvertedProofNode<C> {

	private final DerivabilityChecker<ProofNode<C>> checker_;

	DerivableProofNode(ProofNode<C> delegate,
			DerivabilityChecker<ProofNode<C>> checker) {
		super(delegate);
		Util.checkNotNull(checker);
		this.checker_ = checker;
	}

	DerivableProofNode(ProofNode<C> delegate) {
		this(delegate, new ProofNodeDerivabilityChecker<C>());
	}

	DerivabilityChecker<ProofNode<C>> getDerivabilityChecker() {
		return checker_;
	}

	@Override
	final void convert(ConvertedProofStep<C> step) {
		ProofStep<C> delegate = step.getDelegate();
		for (ProofNode<C> premise : delegate.getPremises()) {
			if (!checker_.isDerivable(premise)) {
				return;
			}
		}
		convert(new DerivableProofStep<C>(delegate, checker_));
	}

	void convert(DerivableProofStep<C> step) {
		super.convert(step);
	}

}
