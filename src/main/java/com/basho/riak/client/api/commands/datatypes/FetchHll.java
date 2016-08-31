/*
 * Copyright 2013 Basho Technologies Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.basho.riak.client.api.commands.datatypes;

import com.basho.riak.client.api.commands.CoreFutureAdapter;
import com.basho.riak.client.core.RiakCluster;
import com.basho.riak.client.core.RiakFuture;
import com.basho.riak.client.core.operations.DtFetchOperation;
import com.basho.riak.client.core.query.Location;
import com.basho.riak.client.core.query.crdt.types.RiakDatatype;
import com.basho.riak.client.core.query.crdt.types.RiakHll;

/**
 * Command used to fetch a HyperLogLog datatype from Riak.
 * <script src="https://google-code-prettify.googlecode.com/svn/loader/run_prettify.js"></script>
 * <p>
 * <pre class="prettyprint">
 * {@code
 *     Namespace ns = new Namespace("my_type", "my_bucket");
 *     Location loc = new Location(ns, "my_key");
 *     FetchHll fhll = new FetchHll.Builder(loc).build();
 *     FetchHll.Response resp = client.execute(fhll);
 *     RiakHll rHll = resp.getDatatype();
 *     long hllCardinality = rHll.view();
 * }
 * </pre>
 * @author Alex Moore <amoore at basho dot com>
 * @since 2.1
 */
public final class FetchHll extends FetchDatatype<RiakHll, FetchHll.Response, Location>
{
    private FetchHll(Builder builder)
    {
        super(builder);
    }

    @Override
    protected final RiakFuture<FetchHll.Response, Location> executeAsync(RiakCluster cluster)
    {
        RiakFuture<DtFetchOperation.Response, Location> coreFuture =
            cluster.execute(buildCoreOperation());

        CoreFutureAdapter<FetchHll.Response, Location, DtFetchOperation.Response, Location> future =
            new CoreFutureAdapter<FetchHll.Response, Location, DtFetchOperation.Response, Location>(coreFuture)
            {
                @Override
                protected FetchHll.Response convertResponse(DtFetchOperation.Response coreResponse)
                {
                    RiakDatatype element = coreResponse.getCrdtElement();

                    Context context = null;
                    if (coreResponse.hasContext())
                    {
                        context = new Context(coreResponse.getContext());
                    }

                    RiakHll datatype = extractDatatype(element);

                    return new Response(datatype, context);
                }

                @Override
                protected Location convertQueryInfo(Location coreQueryInfo)
                {
                    return coreQueryInfo;
                }
            };
        coreFuture.addListener(future);
        return future;
    }

    @Override
    public RiakHll extractDatatype(RiakDatatype element)
    {
        return element.getAsHLL();
    }

    /**
     * Builder used to construct a FetchHll command.
     */
    public static class Builder extends FetchDatatype.Builder<Builder>
    {
        /**
         * Construct a builder for a FetchHll command.
         * @param location the location of the HyperLogLog in Riak.
         */
        public Builder(Location location)
        {
            super(location);
        }

        @Override
        protected Builder self()
        {
            return this;
        }

        /**
         * Construct a FetchHll command.
         * @return a new FetchHll Command.
         */
        public FetchHll build()
        {
            return new FetchHll(this);
        }
    }

    /**
     * Response from a FetchHll command.
     * <p>
     * Encapsulates a RiakHll returned from the FetchHll command.
     * <pre>
     * {@code
     * ...
     *     RiakHll rHll = response.getDatatype();
     *     long hllCardinality = rHll.view();
     * }
     * </pre>
     * </p>
     */
    public static class Response extends FetchDatatype.Response<RiakHll>
    {
        Response(RiakHll hll, Context context)
        {
            super(hll, context);
        }
    }
}
