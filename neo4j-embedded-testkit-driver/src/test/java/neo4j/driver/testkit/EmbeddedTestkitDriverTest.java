package neo4j.driver.testkit;


import org.junit.Before;
import org.junit.Test;
import org.neo4j.driver.v1.*;
import org.neo4j.graphdb.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

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
	public void session_correctReturn(){
		//Arrange
		EmbeddedTestkitSession exp_session = new EmbeddedTestkitSession(mgds);

		//Act
		Session ret_session = mockedEtd.session();

		//Assert
		assertEquals(exp_session.gds, ((EmbeddedTestkitSession)ret_session).gds);
		assertEquals(EmbeddedTestkitSession.class, ret_session.getClass());
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

	@Test
	public void close(){
		//Act
		mockedEtd.close();
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

