package neo4j.driver.reactive;

import static org.neo4j.driver.v1.Values.parameters;

import com.google.common.collect.Multiset;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.neo4j.driver.v1.*;

import neo4j.driver.reactive.data.RecordChangeSet;
import neo4j.driver.reactive.impl.Neo4jReactiveDriver;
import neo4j.driver.reactive.interfaces.ReactiveDriver;
import neo4j.driver.reactive.interfaces.ReactiveSession;
import neo4j.driver.testkit.EmbeddedTestkitDriver;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Neo4jDriverTest {
	private ReactiveSession session;

	@Before
	public void before() {
		Driver embeddedTestkitDriver = new EmbeddedTestkitDriver();
		ReactiveDriver driver = new Neo4jReactiveDriver(embeddedTestkitDriver);
		session = driver.session();
	}

	@Test(expected = IllegalStateException.class)
	public void testDuplicateRegistration() {
		// Arrange
		final String PERSONS_QUERY = "persons";
		session.registerQuery(PERSONS_QUERY, "MATCH (a:Person) RETURN a");

		// Act
		session.registerQuery(PERSONS_QUERY, "MATCH (a:Person) RETURN a");
	}

	@Test
	public void testCreation() throws Exception {
		// Arrange
		final String PERSONS_QUERY = "persons";
		session.registerQuery(PERSONS_QUERY, "MATCH (a:Person) RETURN a");

		// Act
		runUpdate(session, "CREATE (a:Person {name: $name, title: $title})",
			      parameters("name", "Arthur", "title", "King"));

		// Assert
		RecordChangeSet changes = session.getDeltas(PERSONS_QUERY);
		assertEquals(0, changes.getNegative().size());
		assertEquals(1, changes.getPositive().size());
		assertPersons(changes.getPositive(), new Person("a", "Arthur", "King"));
	}

	@Test
	public void testCreateMultiple() throws Exception {
		// Arrange
		final String PERSONS_QUERY = "persons";
		session.registerQuery(PERSONS_QUERY, "MATCH (a:Person) RETURN a");

		runUpdate(session, "CREATE (a:Person {name: $name, title: $title})",
				  parameters("name", "Alice", "title", "Foo"));
		runUpdate(session, "CREATE (a:Person {name: $name, title: $title})",
				  parameters("name", "Bob", "title", "Bar"));

		// Assert
		RecordChangeSet changes = session.getDeltas(PERSONS_QUERY);
		assertEquals(0, changes.getNegative().size());
		assertEquals(2, changes.getPositive().size());
		assertPersons(changes.getPositive(),
			new Person("a", "Alice", "Foo"),
			new Person("a", "Bob", "Bar"));
	}

	@Test
	public void testDeletion() throws Exception {
		// Arrange
		final String PERSONS_QUERY = "persons";
		session.registerQuery(PERSONS_QUERY, "MATCH (a:Person) RETURN a");

		// Act
		runUpdate(session, "CREATE (a:Person {name: $name, title: $title})",
				  parameters("name", "Arthur", "title", "King"));

		// Flush changes
		session.getDeltas(PERSONS_QUERY);
		runUpdate(session, "MATCH (a:Person {name: $name, title: $title}) DELETE a",
				  parameters("name", "Arthur", "title", "King"));

		// Assert
		RecordChangeSet changes = session.getDeltas(PERSONS_QUERY);
		assertEquals(1, changes.getNegative().size());
		assertEquals(0, changes.getPositive().size());
		assertPersons(changes.getNegative(), new Person("a", "Arthur", "King"));
	}

	@Test
	public void testDeletionIfNotExists() throws Exception {
		// Arrange
		final String PERSONS_QUERY = "persons";
		session.registerQuery(PERSONS_QUERY, "MATCH (a:Person) RETURN a");

		// Act
		runUpdate(session, "MATCH (a:Person {name: $name, title: $title}) DELETE a",
			      parameters("name", "Arthur", "title", "King"));

		// Assert
		RecordChangeSet changes = session.getDeltas(PERSONS_QUERY);
		assertEquals(0, changes.getNegative().size());
		assertEquals(0, changes.getPositive().size());
	}


	@Test
	public void testUpdateBeforeRegister() throws Exception {
		// Arrange
		final String PERSONS_QUERY = "persons";

		// Act
		runUpdate(session, "CREATE (a:Person {name: $name, title: $title})",
				  parameters("name", "Alice", "title", "Foo"));
		session.registerQuery(PERSONS_QUERY, "MATCH (a:Person) RETURN a");

		// Assert
		RecordChangeSet changes = session.getDeltas(PERSONS_QUERY);
		assertEquals(0, changes.getNegative().size());
		assertEquals(1, changes.getPositive().size());
		assertPersons(changes.getPositive(), new Person("a", "Alice", "Foo"));
	}

	@Test
	public void testStringStatement() throws Exception {
		// Act
		Value result = session.run("RETURN [1, 2, 3]").next().get(0);

		// Assert
		assertArrayEquals(result.asList().toArray(), new Long[] { 1L, 2L, 3L });
	}

	@Test
	public void testClassStatement() throws Exception {
		// Arrange
		Statement statement = new Statement("RETURN [1, 2, 3]");

		// Act
		Value result = session.run(statement).next().get(0);

		// Assert
		assertArrayEquals(result.asList().toArray(), new Long[] { 1L, 2L, 3L });
	}

	@Test
	public void testStringStatementWithValueParams() throws Exception {
		// Act
		Value result = session.run("RETURN [$a, $b, $c]", parameters("a", 1L, "b", 2L, "c", 3L))
			.next().get(0);

		// Assert
		assertArrayEquals(result.asList().toArray(), new Long[] { 1L, 2L, 3L });
	}

	@Test
	public void testStringStatementWithMapParams() throws Exception {
		// Arrange
		Map<String, Object> params = new HashMap<String, Object>() {{
			put("a", 1L);
			put("b", 2L);
			put("c", 3L);
		}};

		// Act
		Value result = session.run("RETURN [$a, $b, $c]", params)
			.next().get(0);

		// Assert
		assertArrayEquals(result.asList().toArray(), new Long[] { 1L, 2L, 3L });
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testWriteTransaction() {
		// Act
		session.writeTransaction(null);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testReadTransaction() {
		// Act
		session.readTransaction(null);
	}

	private void assertPersons(Multiset<Record> changes, final Person... persons) {
		changes.forEach(record -> assertTrue(hasMatching(record, persons)));
	}

	private boolean hasMatching(Record record, Person... persons) {
		return Arrays.stream(persons)
			.anyMatch(expected -> {
				Value actual = record.get(expected.key);
				return actual != null &&
					   actual.get("name").asString().equals(expected.name) &&
					   actual.get("title").asString().equals(expected.title);
			});
	}

	private void runUpdate(ReactiveSession session, String statementTemplate, Value parameters) {
		try (Transaction tx = session.beginTransaction()) {
			tx.run(statementTemplate, parameters);
			tx.success();
		}
	}

	private static class Person {
		final String key;
		final String name;
		final String title;

		Person(String key, String name, String title) {
			this.key = key;
			this.name = name;
			this.title = title;
		}
	}
}
