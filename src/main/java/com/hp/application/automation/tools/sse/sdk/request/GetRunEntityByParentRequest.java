package com.hp.application.automation.tools.sse.sdk.request;

import com.hp.application.automation.tools.sse.sdk.Client;

/***
 * 
 * @author Effi Bar-She'an
 * @author Dani Schreiber
 * 
 */

public class GetRunEntityByParentRequest extends GetRequest {

    private String parentId;

    public GetRunEntityByParentRequest(Client client, String parentId) {
        
        super(client);
        this.parentId = parentId;
    }
    
    @Override
    protected String getSuffix() {
        
        return "runs";
    }

    @Override
    protected String getQueryString() {

        return String.format("query={pc-procedure-id[%s]}&page-size=2000", parentId);
    }
}
