package neo4j.driver.reactive;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.TreeMultiset;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import neo4j.driver.reactive.data.ChangeSet;

public class ChangeSetTest {
	ChangeSet<Integer> emptyChangeSet;

	@Before
	public void before() {
		emptyChangeSet = new ChangeSet<>(HashMultiset.create(), HashMultiset.create());
	}

	@Test
	public void testConstruction() {
		// Arrange
		Multiset<Integer> positiveExpected = HashMultiset.create();
		Multiset<Integer> negativeExpected = TreeMultiset.create();

		// Act
		ChangeSet<Integer> set = new ChangeSet<>(positiveExpected, negativeExpected);

		// Assert
		assertEquals(positiveExpected, set.getPositive());
		assertEquals(negativeExpected, set.getNegative());
	}

	@Test
	public void testToStringDoesNotThrow() {
		emptyChangeSet.toString();
	}
}
