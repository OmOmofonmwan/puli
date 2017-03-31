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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

import org.junit.Test;

/**
 * @author Pavel Klinov
 *
 *         pavel.klinov@uni-ulm.de
 * 
 * @author Yevgeny Kazakov
 */
public class ProofTest {

	@Test
	public void inferenceSetTest() {
		InferenceSetBuilder<Integer> b = InferenceSetBuilder.create();
		b.conclusion(1).premise(2).add();
		b.conclusion(2).premise(3).premise(4).add();
		b.conclusion(2).premise(5).premise(6).add();
		InferenceSet<Integer> is = b.build();
		assertEquals(1, is.getInferences(1).size());
		assertEquals(2, is.getInferences(2).size());
		assertEquals(0, is.getInferences(3).size());
	}

	@Test
	public void blockCyclicProof() throws Exception {
		InferenceSetBuilder<String> b = InferenceSetBuilder.create();
		b.conclusion("A ⊑ B").premise("A ⊑ B ⊓ C").add();
		b.conclusion("A ⊑ B").premise("A ⊑ C").premise("C ⊑ B").add();
		b.conclusion("A ⊑ C").premise("A ⊑ D").premise("D ⊑ C").add();
		b.conclusion("A ⊑ D").premise("A ⊑ B").premise("B ⊑ D").add();
		InferenceSet<String> is = b.build();

		Set<String> stated = new HashSet<String>(
				Arrays.asList("A ⊑ B ⊓ C", "B ⊑ D", "D ⊑ C", "C ⊑ B"));

		assertTrue(
				ProofNodes.isDerivable(ProofNodes.create(is, "A ⊑ C"), stated));

		ProofNode<String> root = ProofNodes
				.addAssertedInferences(ProofNodes.create(is, "A ⊑ B"), stated);

		assertTrue(ProofNodes.isDerivable(root));

		assertEquals(2,
				ProofNodes.eliminateNotDerivable(root).getInferences().size());

		// only one inference remains since the other is cyclic
		assertEquals(1, ProofNodes.eliminateNotDerivableAndCycles(root)
				.getInferences().size());

		// testing the same but using derivability "from" methods

		root = ProofNodes.create(is, "A ⊑ B");
		assertTrue(ProofNodes.isDerivable(root, stated));

		assertEquals(2, ProofNodes.eliminateNotDerivable(root, stated)
				.getInferences().size());

		// only one inference remains since the other is cyclic
		assertEquals(1, ProofNodes.eliminateNotDerivableAndCycles(root, stated)
				.getInferences().size());

	}

	@Test
	public void blockCyclicProof2() throws Exception {
		InferenceSetBuilder<Integer> b = InferenceSetBuilder.create();
		b.conclusion(0).premise(1).premise(2).add();
		b.conclusion(0).premise(3).premise(4).add();
		b.conclusion(2).premise(0).premise(0).add();
		BaseInferenceSet.Projection<Integer> is = b.build();

		Set<Integer> stated = new HashSet<Integer>(Arrays.asList(1, 3, 4));

		ProofNode<Integer> root = ProofNodes
				.addAssertedInferences(ProofNodes.create(is, 0), stated);

		assertTrue(ProofNodes.isDerivable(root));

		// everything is derivable
		assertEquals(2,
				ProofNodes.eliminateNotDerivable(root).getInferences().size());

		// only one inference remains since the other is cyclic
		assertEquals(1, ProofNodes.eliminateNotDerivableAndCycles(root)
				.getInferences().size());

		// the same using derivability "from"

		root = ProofNodes.create(is, 0);

		assertTrue(ProofNodes.isDerivable(root, stated));

		assertEquals(2, ProofNodes.eliminateNotDerivable(root, stated)
				.getInferences().size());

		// only one inference remains since the other is cyclic
		assertEquals(1, ProofNodes.eliminateNotDerivableAndCycles(root, stated)
				.getInferences().size());

	}

	@Test
	public void recursiveBlocking() throws Exception {
		InferenceSetBuilder<Integer> b = InferenceSetBuilder.create();
		b.conclusion(0).premise(1).premise(2).add();
		b.conclusion(1).premise(3).premise(4).premise(5).add();
		b.conclusion(3).premise(6).premise(7).add();
		b.conclusion(4).premise(8).premise(9).add();
		BaseInferenceSet.Projection<Integer> is = b.build();

		Set<Integer> stated = new HashSet<Integer>(
				Arrays.asList(2, 5, 6, 7, 8, 9));

		ProofNode<Integer> root = ProofNodes
				.addAssertedInferences(ProofNodes.create(is, 0), stated);

		assertEquals(1,
				ProofNodes.eliminateNotDerivable(root).getInferences().size());

		stated.remove(6);

		// not derivable anymore
		assertEquals(null, ProofNodes.eliminateNotDerivable(root));

		// the same using "from" methods

		stated.add(6);

		root = ProofNodes.create(is, 0);

		assertEquals(1, ProofNodes.eliminateNotDerivable(root, stated)
				.getInferences().size());

		stated.remove(6);

		// not derivable anymore
		assertEquals(null, ProofNodes.eliminateNotDerivable(root, stated));
	}

	@Test
	public void limitInferences() throws Exception {
		Random random = new Random();
		long seed = random.nextLong();
		random.setSeed(seed);
		ProofNode<Integer> node = RandomProofNode.generate(random);

		try {

			for (int limit = 1; limit < 5; limit++) {

				ProofNode<Integer> root = ProofNodes
						.limitInferencesPerNode(node, limit);
				Queue<ProofNode<Integer>> toCheck = new LinkedList<ProofNode<Integer>>();
				toCheck.add(root);
				int tests = 0;

				while (tests < 10) {
					ProofNode<Integer> next = toCheck.poll();
					if (next == null) {
						return;
					}
					assertTrue(next.getInferences().size() <= limit);
					tests++;
					for (ProofStep<Integer> inf : next.getInferences()) {
						for (ProofNode<Integer> premise : inf.getPremises()) {
							toCheck.add(premise);
						}
					}
				}
			}
		} catch (Throwable e) {
			throw new RuntimeException("seed: " + seed, e);
		}
	}

}
