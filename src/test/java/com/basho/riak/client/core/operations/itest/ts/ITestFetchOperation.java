package com.basho.riak.client.core.operations.itest.ts;

import com.basho.riak.client.core.RiakFuture;
import com.basho.riak.client.core.operations.ts.DeleteOperation;
import com.basho.riak.client.core.operations.ts.FetchOperation;
import com.basho.riak.client.core.operations.ts.StoreOperation;
import com.basho.riak.client.core.query.timeseries.Cell;
import com.basho.riak.client.core.query.timeseries.QueryResult;
import com.basho.riak.client.core.query.timeseries.Row;
import com.basho.riak.client.core.util.BinaryValue;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Time Series Fetch Operation Integration Tests
 * @author Alex Moore <amoore at basho dot com>
 * @since 2.0.3
 */

public class ITestFetchOperation extends ITestTsBase
{
    @BeforeClass
    public static void InsertData() throws ExecutionException, InterruptedException
    {
        StoreOperation storeOp = new StoreOperation.Builder(tableNameBV).withRows(rows).build();
        RiakFuture<Void, BinaryValue> future = cluster.execute(storeOp);

        future.get();
        assertTrue(future.isSuccess());
    }

    @Test
    public void testSingleFetch() throws ExecutionException, InterruptedException
    {
        final List<Cell> keyCells = Arrays.asList(new Cell("hash2"), new Cell("user4"), Cell.newTimestamp(fifteenMinsAgo));
        FetchOperation fetchOp = new FetchOperation.Builder(tableNameBV, keyCells).build();

        final RiakFuture<QueryResult, BinaryValue> future = cluster.execute(fetchOp);

        future.get();
        assertTrue(future.isSuccess());
        QueryResult result = future.get();

        assertEquals(1, result.getRows().size());

        Row row = result.getRows().get(0);
        assertEquals("rain", row.getCells().get(3).getUtf8String());
        assertEquals(79.0, row.getCells().get(4).getDouble(), Double.MIN_VALUE);
    }
}