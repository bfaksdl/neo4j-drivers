package neo4j.driver.testkit;

import com.google.common.collect.Multiset;
import neo4j.driver.testkit.data.EmbeddedTestkitStatementResult;
import org.neo4j.driver.v1.*;
import org.neo4j.driver.v1.types.TypeSystem;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Result;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class EmbeddedTestkitSession implements Session {

	final GraphDatabaseService gds;
	final Map<String, String> querySpecifications = new HashMap<>();
	final Map<String, Multiset<Record>> queryResults = new HashMap<>();
	final Map<String, Multiset<Record>> deltas = new HashMap<>();

	public EmbeddedTestkitSession(GraphDatabaseService gds) {
		this.gds = gds;
	}

	@Override
	public boolean isOpen() {
		return true;
	}

	@Override
	public StatementResult run(String statementTemplate, Map<String, Object> statementParameters) {
		final Result internalResult = gds.execute(statementTemplate, statementParameters);
		return new EmbeddedTestkitStatementResult(internalResult);
	}

	@Override
	public StatementResult run(String statementTemplate) {
		return run(statementTemplate, Collections.emptyMap());
	}

	@Override
	public StatementResult run(String statementTemplate, Value parameters) {
		return run(statementTemplate, parameters.asMap());
	}

	@Override
	public StatementResult run(String statementTemplate, Record statementParameters) {
		return run(statementTemplate, statementParameters.asMap());
	}

	@Override
	public StatementResult run(Statement statement) {
		final Result internalResult = gds.execute(statement.text());

		return new EmbeddedTestkitStatementResult(internalResult);
	}

	@Override
	public TypeSystem typeSystem() {
		throw new UnsupportedOperationException("Typesystem is not supported.");
	}

	@Override
	public Transaction beginTransaction() {
		org.neo4j.graphdb.Transaction transaction = gds.beginTx();
		return new EmbeddedTestkitTransaction(this, transaction);
	}

	@Override
	public Transaction beginTransaction(String bookmark) {
		throw new UnsupportedOperationException("Bookmarks are not supported.");
	}

	@Override
	public String lastBookmark() {
		throw new UnsupportedOperationException("Bookmarks are not supported.");
	}

	@Override
	public void reset() {
		throw new UnsupportedOperationException("Reset not supported.");
	}

	@Override
	public void close() {
		// does nothing
	}

	@Override
	public <T> T readTransaction(TransactionWork<T> work) {
		throw new UnsupportedOperationException("readTransaction method not supported.");
	}

	@Override
	public <T> T writeTransaction(TransactionWork<T> work) {
		throw new UnsupportedOperationException("writeTransaction method not supported.");
	}

}
