package neo4j.driver.testkit;

import neo4j.driver.testkit.data.EmbeddedTestkitStatementResult;
import neo4j.driver.util.PrettyPrinter;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.driver.internal.value.IntegerValue;
import org.neo4j.driver.internal.value.MapValue;
import org.neo4j.driver.internal.value.StringValue;
import org.neo4j.driver.v1.*;
import org.neo4j.driver.v1.Transaction;
import org.neo4j.driver.v1.types.TypeSystem;
import org.neo4j.graphdb.*;

import java.io.PrintWriter;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class EmbeddedTestkitSessionTest {
	//private EmbeddedTestkitSession ets;

	private EmbeddedTestkitSession mEts;
	private GraphDatabaseService mockGds;

	@Before
	public void init(){
		mockGds = mock(GraphDatabaseService.class);
		mEts = new EmbeddedTestkitSession(mockGds);
	}

	@Test
	public void isOpen_returnValue(){
		//Act
		assertEquals(true, mEts.isOpen());
	}

	@Test
	public void run_string_mapString(){
		//Arrange
		String statementTemplate = "test statement";
		Map<String, Object> statementParams = new HashMap<>();
		statementParams.put("param1",  Integer.valueOf(42));
		statementParams.put("param2", "value2");
		StatementResult exp_res = new EmbeddedTestkitStatementResult(dummyResult);

		when(mockGds.execute(statementTemplate, statementParams)).thenReturn(dummyResult);

		//Act
		StatementResult result =  mEts.run(statementTemplate, statementParams);

		//Assert
		verify(mockGds, times(1)).execute(statementTemplate, statementParams);
		assertEquals(true, compareStatementResults(exp_res, result));
	}

	@Test
	public void run_string_noparam(){
		//Arrange
		String statementTemplate = "test statement";
		StatementResult exp_res = new EmbeddedTestkitStatementResult(dummyResult);

		when(mockGds.execute(statementTemplate, Collections.emptyMap())).thenReturn(dummyResult);

		//Act
		StatementResult result =  mEts.run(statementTemplate);

		//Assert
		verify(mockGds, times(1)).execute(statementTemplate, Collections.emptyMap());
		assertEquals(true, compareStatementResults(exp_res, result));
	}

//	@Test
//	public void run_string_value(){
//		//Arrange
//		String statementTemplate = "test statement";
//		Value statementParam = new MapValue("valueKey", );
//		StatementResult exp_res = new EmbeddedTestkitStatementResult(dummyResult);
//
//		when(mockGds.execute(statementTemplate, statementParam.asMap())).thenReturn(dummyResult);
//
//		//Act
//		StatementResult result =  mEts.run(statementTemplate, statementParam);
//
//		//Assert
//		verify(mockGds, times(1)).execute(statementTemplate, statementParam.asMap());
//		assertEquals(true, compareStatementResults(exp_res, result));
//	}

	@Test
	public void run_statement(){
		//Arrange
		String statementTemplate = "test statement";
		Statement stmnt = new Statement(statementTemplate);
		StatementResult exp_res = new EmbeddedTestkitStatementResult(dummyResult);

		when(mockGds.execute(statementTemplate)).thenReturn(dummyResult);

		//Act
		StatementResult result =  mEts.run(stmnt);

		//Assert
		verify(mockGds, times(1)).execute(statementTemplate);
		assertEquals(true, compareStatementResults(exp_res, result));
	}

	@Test
	public void beginTransaction_noparam(){
		//Arrange
		Transaction exp_res = new EmbeddedTestkitTransaction(mEts, dummyTransaction);
		when(mockGds.beginTx()).thenReturn(dummyTransaction);

		//Act
		Transaction result = mEts.beginTransaction();

		//Assert
		verify(mockGds, times(1)).beginTx();
		assertEquals(exp_res.typeSystem(), result.typeSystem());
		assertEquals((((EmbeddedTestkitTransaction)exp_res).session), ((EmbeddedTestkitTransaction)result).session);
		assertEquals((((EmbeddedTestkitTransaction)exp_res).internalTransaction)
			, ((EmbeddedTestkitTransaction)result).internalTransaction);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void typeSystem_notsupported(){
		mEts.typeSystem();
	}

	@Test(expected = UnsupportedOperationException.class)
	public void begintransaction_param_notsupported(){
		mEts.beginTransaction("param");
	}

	@Test(expected = UnsupportedOperationException.class)
	public void lastBookmark_notsupported(){
		mEts.lastBookmark();
	}

	@Test(expected = UnsupportedOperationException.class)
	public void reset_notsupported(){
		mEts.reset();
	}


	@Test(expected = UnsupportedOperationException.class)
	public void readTransaction_notsupported(){
		TransactionWork<String> trw = new TransactionWork<String>() {
			@Override
			public String execute(Transaction tx) {
				return "test transaction";
			}
		};

		mEts.readTransaction(trw);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void writeTransaction_notsupported(){
		TransactionWork<String> trw = new TransactionWork<String>() {
			@Override
			public String execute(Transaction tx) {
				return "test transaction";
			}
		};

		mEts.writeTransaction(trw);
	}

	@Test
	public void close(){
		//Act
		mEts.close();

		verify(mockGds, times(0)).beginTx();
	}

	boolean compareStatementResults(StatementResult res1, StatementResult res2){
		if( ! res1.keys().equals(res2.keys())){
			//System.out.println("keysMisMatch");
			return false;
		}
		if( ! res1.list().equals(res2.list())){
			return false;
		}
		return true;
	}

	boolean compareEmbeddedTestkitTransaction(EmbeddedTestkitTransaction tr1, EmbeddedTestkitTransaction tr2){
		if(tr1.session.equals(tr2.session) && tr1.internalTransaction.equals(tr2.internalTransaction)){
			return true;
		}
		return false;
	}

	org.neo4j.graphdb.Transaction dummyTransaction = new org.neo4j.graphdb.Transaction() {
		@Override
		public void terminate() {

		}

		@Override
		public void failure() {

		}

		@Override
		public void success() {

		}

		@Override
		public void close() {

		}

		@Override
		public Lock acquireWriteLock(PropertyContainer entity) {
			return null;
		}

		@Override
		public Lock acquireReadLock(PropertyContainer entity) {
			return null;
		}
	};

	Result dummyResult = new Result() {
		@Override
		public QueryExecutionType getQueryExecutionType() {
			return null;
		}

		@Override
		public List<String> columns() {
			String column1 = "test column 1";
			String column2 = "test column 2";
			List<String> ret = new ArrayList<String>();
			ret.add(column1);
			ret.add(column2);

			return ret;
		}

		@Override
		public <T> ResourceIterator<T> columnAs(String name) {
			return null;
		}

		@Override
		public boolean hasNext() {
			return false;
		}

		@Override
		public Map<String, Object> next() {
			throw new NoSuchElementException();
			//return null;
		}

		@Override
		public void close() {

		}

		@Override
		public QueryStatistics getQueryStatistics() {
			return null;
		}

		@Override
		public ExecutionPlanDescription getExecutionPlanDescription() {
			return null;
		}

		@Override
		public String resultAsString() {
			return null;
		}

		@Override
		public void writeAsStringTo(PrintWriter writer) {

		}

		@Override
		public void remove() {

		}

		@Override
		public Iterable<Notification> getNotifications() {
			return null;
		}

		@Override
		public <VisitationException extends Exception> void accept(ResultVisitor<VisitationException> visitor) throws VisitationException {

		}
	};
}

