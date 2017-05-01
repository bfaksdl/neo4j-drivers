package neo4j.driver.testkit;

public class EmbeddedTestkitDriverTest {

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
