package com.hp.application.automation.tools.sse.sdk.request;

import com.hp.application.automation.tools.rest.RESTConstants;
import com.hp.application.automation.tools.sse.common.RestXmlUtils;
import com.hp.application.automation.tools.sse.sdk.Client;
import com.hp.application.automation.tools.sse.sdk.ResourceAccessLevel;
import com.hp.application.automation.tools.sse.sdk.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by barush on 03/11/2014.
 */
public abstract class GeneralPutRequest extends GeneralRequest {

    protected GeneralPutRequest(Client client) {
        super(client);
    }
    
    protected abstract List<Map<String, String>> getFields();
    
    @Override
    protected Map<String, String> getHeaders() {

        Map<String, String> ret = new HashMap<String, String>();
        ret.put(RESTConstants.CONTENT_TYPE, RESTConstants.APP_XML);
        ret.put(RESTConstants.ACCEPT, RESTConstants.APP_XML);

        return ret;
    }
    
    @Override
    protected Response perform() {
        return _client.httpPut(
                getUrl(),
                getDataBytes(),
                getHeaders(),
                ResourceAccessLevel.PROTECTED);
    }
    
    private byte[] getDataBytes() {
        
        StringBuilder builder = new StringBuilder();
        for (Map<String, String> values : getFields()) {
            builder.append("<Entity><Fields>");
            for (String key : values.keySet()) {
                builder.append(RestXmlUtils.fieldXml(key, values.get(key)));
            }
            builder.append("</Fields></Entity>");
        }
        
        return builder.toString().getBytes();
        
    }
}
