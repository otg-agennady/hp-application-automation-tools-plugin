package com.hp.application.automation.tools.sse.sdk.request;

import com.hp.application.automation.tools.sse.sdk.Client;
import com.hp.application.automation.tools.sse.sdk.ResourceAccessLevel;
import com.hp.application.automation.tools.sse.sdk.Response;

import java.util.List;
import java.util.Map;

/**
 * Created by gin on 22.06.17.
 */
public class PutRunRequest extends GeneralPutRequest {
    private List<Map<String, String>> fields;
    private String runId;

    public PutRunRequest(Client client) {
        super(client);
    }

    @Override
    protected List<Map<String, String>> getFields() {
        return fields;
    }

    public void setFields(List<Map<String, String>> fields) {
        this.fields = fields;
    }

    public Response perform(String runId) {
        this.runId = runId;
        return this.perform();
    }


    @Override
    protected String getSuffix() {
        return String.format("runs/%s", runId);
    }
}
