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

import java.util.List;

public class Inferences {

	public static <C> Inference<C> create(String name, C conclusion,
			List<? extends C> premises) {
		return new BaseInference<C>(name, conclusion, premises);
	}

	public static <C> boolean equals(Inference<C> inference, Object o) {
		if (o instanceof Inference<?>) {
			Inference<?> other = (Inference<?>) o;
			return (inference.getName().equals(other.getName())
					&& inference.getConclusion().equals(other.getConclusion())
					&& inference.getPremises().equals(other.getPremises()));
		}
		// else
		return false;
	}

	public static <C> int hashCode(Inference<C> inference) {
		if (inference == null) {
			return 0;
		}
		return inference.getName().hashCode()
				+ inference.getConclusion().hashCode()
				+ inference.getPremises().hashCode();
	}

	public static <C> String toString(Inference<C> inference) {
		Util.checkNotNull(inference);
		return inference.getConclusion() + " -| " + inference.getPremises()
				+ " by " + inference.getName();
	}

}
