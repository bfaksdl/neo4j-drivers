package neo4j.driver.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.driver.internal.InternalNode;
import org.neo4j.driver.internal.InternalRecord;
import org.neo4j.driver.internal.InternalRelationship;
import org.neo4j.driver.internal.value.NodeValue;
import org.neo4j.driver.internal.value.PathValue;
import org.neo4j.driver.internal.value.RelationshipValue;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Value;
import org.neo4j.driver.v1.Values;
import org.neo4j.driver.v1.types.Entity;
import org.neo4j.driver.v1.types.Node;
import org.neo4j.driver.v1.types.Path;
import org.neo4j.driver.v1.types.Relationship;
import org.neo4j.driver.v1.util.Function;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class PrettyPrinterTest {

	Node node;
	Relationship relationship;

	@Before
	public void before() {
		List<String> labels = ImmutableList.of("Person");
		Map<String, Value> nodeProperties = ImmutableMap.of("name", Values.value("John Doe"));
		node = new InternalNode(1, labels, nodeProperties);

		Map<String, Value> relationshipProperties = ImmutableMap.of("weight", Values.value(2));
		relationship = new InternalRelationship(5, 1, 2, "REL", relationshipProperties);
	}

	@Test
	public void testEntityListToString() throws Exception {
		//Arrange
		Entity customEntity = createCustomEntity();
		List<Entity> entities = ImmutableList.of(node, relationship, customEntity);

		//Act
		String entitiesDisplay = PrettyPrinter.toString(entities);

		//Assert
		String expectedDisplay = "[(:Person {name: \"John Doe\"}),(1)-[:REL {weight: 2}]-(2)]";
		assertEquals(expectedDisplay, entitiesDisplay);
	}

	@Test
	public void testRecordToString() {
		//Arrange
		List<String> keys = ImmutableList.of("person", "relationship");

		NodeValue nodeValue = new NodeValue(node);
		RelationshipValue relationshipValue = new RelationshipValue(relationship);

		Record record = new InternalRecord(keys, new Value[]{nodeValue, relationshipValue});

		//Act
		String recordDisplay = PrettyPrinter.toString(record);

		//Assert
		String expectedDisplay = "<person=(:Person {name: \"John Doe\"}), relationship=(1)-[:REL {weight: 2}]-(2)>";
		assertEquals(expectedDisplay, recordDisplay);
	}

	@Test
	public void testNodeToStringWithMultipleLabelsAndProperties() {
		//Arrange
		List<String> labels = ImmutableList.of("Workplace", "Main");
		Map<String, Value> nodeProperties = ImmutableMap.of("companyName", Values.value("Google"),
															"city", Values.value("Mountain View") );
		Node anotherNode = new InternalNode(2, labels, nodeProperties);

		//Act
		String nodeDisplay = PrettyPrinter.toString(anotherNode);

		//Assert
		String expectedDisplay = "(:Workplace:Main {companyName: \"Google\", city: \"Mountain View\"})";
		assertEquals(expectedDisplay, nodeDisplay);
	}

	@Test
	public void testNodeToStringWithNoLabels() {
		//Arrange
		Map<String, Value> nodeProperties = ImmutableMap.of("companyName", Values.value("Google"),
			"city", Values.value("Mountain View") );
		Node anotherNode = new InternalNode(2, Collections.emptyList(), nodeProperties);

		//Act
		String nodeDisplay = PrettyPrinter.toString(anotherNode);
		System.out.println(nodeDisplay);

		//Assert
		String expectedDisplay = "({companyName: \"Google\", city: \"Mountain View\"})";
		assertEquals(expectedDisplay, nodeDisplay);
	}

	@Test
	public void testNodeToStringWithNoProperties() {
		//Arrange
		List<String> labels = ImmutableList.of("Birth");
		Node anotherNode = new InternalNode(2, labels, Collections.emptyMap());

		//Act
		String nodeDisplay = PrettyPrinter.toString(anotherNode);

		//Assert
		String expectedDisplay = "(:Birth)";
		assertEquals(expectedDisplay, nodeDisplay);
	}



	@Test
	public void testRelationshipToStringWithNoProperties() {
		//Arrange
		Relationship anotherRelationship = new InternalRelationship(3, 1, 2, "REL",
																	Collections.emptyMap());

		//Act
		String relationshipDisplay = PrettyPrinter.toString(anotherRelationship);

		//Assert
		String expectedDisplay = "(1)-[:REL]-(2)";
		assertEquals(expectedDisplay, relationshipDisplay);
	}

	@Test
	public void testPrimitiveValueToString() {
		//Act
		String booleanValueDisplay = PrettyPrinter.toString(Values.value(true));
		String integerValueDisplay = PrettyPrinter.toString(Values.value(42));

		//Assert
		assertEquals("TRUE", booleanValueDisplay);
		assertEquals("42", integerValueDisplay);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testPathValueToString() {
		PathValue pathValue = mock(PathValue.class);
		when(pathValue.asPath()).thenReturn(mock(Path.class));

		PrettyPrinter.toString(pathValue);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testPathToString() {
		PrettyPrinter.toString(mock(Path.class));
	}

	private Entity createCustomEntity() {
		return new Entity() {
			@Override
			public long id() {
				return 13;
			}

			@Override
			public Iterable<String> keys() {
				return null;
			}

			@Override
			public boolean containsKey(String key) {
				return false;
			}

			@Override
			public Value get(String key) {
				return null;
			}

			@Override
			public int size() {
				return 0;
			}

			@Override
			public Iterable<Value> values() {
				return null;
			}

			@Override
			public <T> Iterable<T> values(Function<Value, T> mapFunction) {
				return null;
			}

			@Override
			public Map<String, Object> asMap() {
				return null;
			}

			@Override
			public <T> Map<String, T> asMap(Function<Value, T> mapFunction) {
				return null;
			}
		};
	}

}
