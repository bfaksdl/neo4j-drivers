package neo4j.driver.testkit;


import neo4j.driver.util.PrettyPrinter;
import org.neo4j.driver.v1.*;
import neo4j.driver.testkit.*;
import org.junit.*;
import org.neo4j.graphdb.GraphDatabaseService;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class EmbeddedTestkitDriverTest {
	private EmbeddedTestkitDriver etd;

	//private GraphDatabaseService mgds;

	@Before
	public void init(){
		//mgds = mock(GraphDatabaseService.class);
		//etd = new EmbeddedTestkitDriverTest(mgds);

		etd = new EmbeddedTestkitDriver();
	}

	@Test
	public void isEncripted_valueCheck(){
		//act
		boolean value = etd.isEncrypted();
		//Assert
		assertEquals(false, value);
	}

//	@Test
//	public void session_correctReturn(){
//		//act
//		Session s = etd.session();
//		//s.
//	}

	@Test (expected = UnsupportedOperationException.class)
	public void session_invalid_parameter(){
		//act
		Session s = etd.session("string");

		s.close(); //so findBugs won't complain
	}

	@Test (expected = UnsupportedOperationException.class)
	public void session_invalid_parameter_2(){
		//act
		Session s = etd.session(AccessMode.READ, "string");

		s.close(); //so findBugs won't complain
	}



//	@Test
//	public void test() {
//		try (Driver driver = new EmbeddedTestkitDriver()) {
//			try (Session session = driver.session()) {
//				try (Transaction transaction = session.beginTransaction()) {
//					session.run("CREATE (n:Label)");
//					StatementResult statementResult = session.run("MATCH (n:Label) RETURN n");
//					while (statementResult.hasNext()) {
//						Record record = statementResult.next();
//						System.out.println(PrettyPrinter.toString(record));
//					}
//				}
//			}
//		}
//	}
//
//	@Test
//	public void testNodeList() {
//		try (Driver driver = new EmbeddedTestkitDriver()) {
//			try (Session session = driver.session()) {
//				try (Transaction transaction = session.beginTransaction()) {
//					session.run("CREATE (n:Label)");
//					StatementResult statementResult = session.run("MATCH (n) RETURN [n]");
//					while (statementResult.hasNext()) {
//						Record record = statementResult.next();
//						System.out.println(PrettyPrinter.toString(record));
//					}
//				}
//			}
//		}
//	}
//
//	@Test
//	public void testScalarList() {
//		try (Driver driver = new EmbeddedTestkitDriver()) {
//			try (Session session = driver.session()) {
//				try (Transaction transaction = session.beginTransaction()) {
//					session.run("CREATE (n:Label)");
//					StatementResult statementResult = session.run("RETURN [1, 2] AS list");
//					while (statementResult.hasNext()) {
//						Record record = statementResult.next();
//						System.out.println(PrettyPrinter.toString(record));
//					}
//				}
//			}
//		}
//	}
}
