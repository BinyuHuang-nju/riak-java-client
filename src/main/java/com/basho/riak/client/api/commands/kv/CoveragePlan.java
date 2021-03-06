/*
 * Copyright 2013-2015 Basho Technologies Inc
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
package com.basho.riak.client.api.commands.kv;

import com.basho.riak.client.api.GenericRiakCommand;
import com.basho.riak.client.core.FutureOperation;
import com.basho.riak.client.core.operations.CoveragePlanOperation;
import com.basho.riak.client.core.query.Namespace;

/**
 * Command used to retrieve a coverage plan from Riak.
 *
 * @author Sergey Galkin <sgalkin at basho dot com>
 */
public class CoveragePlan  extends GenericRiakCommand.GenericRiakCommandWithSameInfo<CoveragePlan.Response,
        Namespace,CoveragePlanOperation.Response>
{
    private final CoveragePlanOperation operation;

    private CoveragePlan(Builder builder)
    {
        this.operation = builder.buildOperation();
    }

    @Override
    protected CoveragePlanOperation buildCoreOperation() {
        return operation;
    }

    @Override
    protected Response convertResponse(FutureOperation<CoveragePlanOperation.Response, ?, Namespace> request,
                                       CoveragePlanOperation.Response coreResponse)
    {
        return new Response(coreResponse);
    }

    /**
     * Used to construct a CoveragePlan command.
     */
    public static class Builder extends CoveragePlanOperation.AbstractBuilder<CoveragePlan>
    {
        public Builder(Namespace ns)
        {
            super(ns);
        }

        @Override
        public CoveragePlan build()
        {
            return new CoveragePlan(this);
        }

        public static Builder create(Namespace ns)
        {
            return new Builder(ns);
        }
    }

    public static class Response extends CoveragePlanOperation.Response
    {
        private Response(CoveragePlanOperation.Response coreResponse)
        {
            super(coreResponse);
        }
    }
}
