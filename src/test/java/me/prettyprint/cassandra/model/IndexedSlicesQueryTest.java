package me.prettyprint.cassandra.model;

import static me.prettyprint.hector.api.factory.HFactory.createColumn;
import static me.prettyprint.hector.api.factory.HFactory.createKeyspace;
import static me.prettyprint.hector.api.factory.HFactory.createMutator;
import static me.prettyprint.hector.api.factory.HFactory.getOrCreateCluster;
import static org.junit.Assert.assertEquals;
import me.prettyprint.cassandra.BaseEmbededServerSetupTest;
import me.prettyprint.cassandra.serializers.LongSerializer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.OrderedRows;
import me.prettyprint.hector.api.query.QueryResult;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class IndexedSlicesQueryTest extends BaseEmbededServerSetupTest {

  private final static String KEYSPACE = "Keyspace1";
  private static final StringSerializer se = new StringSerializer();
  private static final LongSerializer le = new LongSerializer();
  private Cluster cluster;
  private Keyspace keyspace;

  @Before
  public void setupCase() {
    cluster = getOrCreateCluster("MyCluster", "127.0.0.1:9170");
    keyspace = createKeyspace(KEYSPACE, cluster);
  }

  @After
  public void teardownCase() {
    keyspace = null;
    cluster = null;
  }

  @Test
  public void testInsertGetRemove() {
    String cf = "Indexed1";

    createMutator(keyspace, se)
      .addInsertion("indexedSlicesTest_key1", cf, createColumn("birthdate", 1L, se, le))
      .addInsertion("indexedSlicesTest_key2", cf, createColumn("birthdate", 2L, se, le))
      .addInsertion("indexedSlicesTest_key3", cf, createColumn("birthdate", 3L, se, le))
      .addInsertion("indexedSlicesTest_key4", cf, createColumn("birthdate", 4L, se, le))
      .addInsertion("indexedSlicesTest_key5", cf, createColumn("birthdate", 5L, se, le))
      .execute();

    IndexedSlicesQuery<String, String, Long> indexedSlicesQuery = new IndexedSlicesQuery<String, String, Long>(keyspace, se, se, le);
    indexedSlicesQuery.addEqualsExpression("birthdate", 4L);
    indexedSlicesQuery.setColumnNames("birthdate");
    indexedSlicesQuery.setColumnFamily(cf);
    indexedSlicesQuery.setStartKey("");
    QueryResult<OrderedRows<String, String, Long>> result = indexedSlicesQuery.execute();
    assertEquals(1, result.get().getList().size());


  }


}
