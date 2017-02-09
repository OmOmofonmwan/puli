/*-
 * #%L
 * Proof Utility Library
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2014 - 2017 Live Ontologies Project
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
package org.liveontologies.puli;

import java.util.AbstractList;
import java.util.List;

class BaseProofStep<C> extends AbstractProofStep<C> {

	private final InferenceSet<C> inferenceSet_;

	private final Inference<C> inference_;

	BaseProofStep(InferenceSet<C> inferences, Inference<C> inference) {
		Util.checkNotNull(inferences);
		Util.checkNotNull(inference);
		this.inferenceSet_ = inferences;
		this.inference_ = inference;
	}

	public InferenceSet<C> getInferenceSet() {
		return inferenceSet_;
	}

	@Override
	public Inference<C> getInference() {
		return inference_;
	}

	@Override
	public String getName() {
		return inference_.getName();
	}

	@Override
	public ProofNode<C> getConclusion() {
		return convert(inference_.getConclusion());
	}

	@Override
	public List<? extends ProofNode<C>> getPremises() {
		return new AbstractList<ProofNode<C>>() {

			@Override
			public ProofNode<C> get(int index) {
				return convert(inference_.getPremises().get(index));
			}

			@Override
			public int size() {
				return inference_.getPremises().size();
			}

		};
	}

	ProofNode<C> convert(C member) {
		return new BaseProofNode<C>(inferenceSet_, member);
	}

}
