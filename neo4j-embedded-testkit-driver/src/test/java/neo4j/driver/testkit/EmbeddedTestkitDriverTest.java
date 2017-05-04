package neo4j.driver.testkit;


import org.junit.Before;
import org.junit.Test;
import org.neo4j.driver.v1.*;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.event.KernelEventHandler;
import org.neo4j.graphdb.event.TransactionEventHandler;
import org.neo4j.graphdb.index.IndexManager;
import org.neo4j.graphdb.schema.Schema;
import org.neo4j.graphdb.traversal.BidirectionalTraversalDescription;
import org.neo4j.graphdb.traversal.TraversalDescription;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class EmbeddedTestkitDriverTest {
	private EmbeddedTestkitDriver etd;

	private EmbeddedTestkitDriver mockedEtd;
	private GraphDatabaseService mgds;

	@Before
	public void init() {
		mgds = mock(GraphDatabaseService.class);
		mockedEtd = new EmbeddedTestkitDriver(mgds);

		etd = new EmbeddedTestkitDriver();
	}

	@Test
	public void isEncripted_valueCheck() {
		//act
		boolean value = etd.isEncrypted();
		//Assert
		assertEquals(false, value);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void session_invalid_parameter() {
		//act
		Session s = etd.session("string");
		s.close(); //so findBugs won't complain
	}

	@Test(expected = UnsupportedOperationException.class)
	public void session_invalid_parameter_2() {
		//act
		Session s = etd.session(AccessMode.READ, "string");

		s.close(); //so findBugs won't complain
	}

	@Test
	public void getUnderlyingDatabaseService_returned() {
		//act
		GraphDatabaseService gds_ret = mockedEtd.getUnderlyingDatabaseService();

		//assert
		assertEquals(mgds, gds_ret);
	}
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
//}

