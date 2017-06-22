package com.hp.application.automation.tools.sse.sdk;

import com.hp.application.automation.tools.common.SSEException;
import com.hp.application.automation.tools.model.SseModel;
import com.hp.application.automation.tools.rest.RestClient;
import com.hp.application.automation.tools.sse.common.StringUtils;
import com.hp.application.automation.tools.sse.common.XPathUtils;
import com.hp.application.automation.tools.sse.result.PublisherFactory;
import com.hp.application.automation.tools.sse.result.model.junit.Testsuites;
import com.hp.application.automation.tools.sse.sdk.handler.*;
import com.hp.application.automation.tools.sse.sdk.request.GetRunEntityByParentRequest;
import com.hp.application.automation.tools.sse.sdk.request.PutRunRequest;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Effi Bar-She'an
 * @author Dani Schreiber
 *
 */
public class RunManager {

    private RunHandler _runHandler;
    private PollHandler _pollHandler;
    private Logger _logger;
    private boolean _running = false;
    private boolean _polling = false;

    public Testsuites execute(RestClient client, Args args, Logger logger)
            throws InterruptedException {

        Testsuites ret = null;
        _logger = logger;
        _running = true;
        if (login(client, args)) {
            initialize(args, client);
            if (start(args)) {
                _polling = true;
                if (poll()) {
                    ret =
                            new PublisherFactory().create(
                                    client,
                                    args.getRunType(),
                                    args.getEntityId(),
                                    _runHandler.getRunId()).publish(
                                    _runHandler.getNameSuffix(),
                                    args.getUrl(),
                                    args.getDomain(),
                                    args.getProject(),
                                    logger);
                }
                _polling = false;

                if (_pollHandler instanceof LabPollHandler) {
                    LabPollHandler handler = (LabPollHandler) _pollHandler;
                    String parentId = handler.getParentId();
                    System.out.println(parentId);

//                    Get run entities
                    List<Map<String, String>> entities = handler.getRunEntitiesByParent(client, parentId, logger);
                    for (Map entity:entities) {
                        Object id = entity.get("id");
                        System.out.println(id);
                    }


                }
            }
        }

        return ret;
    }

    public Testsuites execute(RestClient client, Args args, SseModel model, Logger logger)
            throws InterruptedException {

        Testsuites ret = null;
        _logger = logger;
        _running = true;
        if (login(client, args)) {
            initialize(args, client);
            if (start(args)) {
                _polling = true;
                if (poll()) {
                    ret =
                            new PublisherFactory().create(
                                    client,
                                    args.getRunType(),
                                    args.getEntityId(),
                                    _runHandler.getRunId()).publish(
                                    _runHandler.getNameSuffix(),
                                    args.getUrl(),
                                    args.getDomain(),
                                    args.getProject(),
                                    logger);
                }
                _polling = false;

                if (_pollHandler instanceof LabPollHandler) {
                    LabPollHandler handler = (LabPollHandler) _pollHandler;
                    String parentId = handler.getParentId();

                    PutRunRequest request = new PutRunRequest(client);
                    Map<String, String> map = new HashMap<>();
                    map.put("user-01", model.getBranch());
                    map.put("user-02", model.getRelease());
                    ArrayList<Map<String, String>> fields = new ArrayList<>();
                    fields.add(map);
                    request.setFields(fields);

                    List<Map<String, String>> entities = handler.getRunEntitiesByParent(client, parentId, logger);
                    for (Map entity:entities) {
                        String id = (String) entity.get("id");
                        request.perform(id);
                    }


                }
            }
        }

        return ret;
    }

    private void initialize(Args args, RestClient client) {

        String entityId = args.getEntityId();
        appendQCSessionCookies(client);
        _runHandler = new RunHandlerFactory().create(client, args.getRunType(), entityId);
        _pollHandler = new PollHandlerFactory().create(client, args.getRunType(), entityId);
    }

    private void appendQCSessionCookies(RestClient client) {

        // issue a post request so that cookies relevant to the QC Session will be added to the RestClient
        Response response =
                client.httpPost(
                        client.build("rest/site-session"),
                        null,
                        null,
                        ResourceAccessLevel.PUBLIC);
        if (!response.isOk()) {
            throw new SSEException("Cannot appned QCSession cookies", response.getFailure());
        }
    }

    private boolean poll() throws InterruptedException {

        return _pollHandler.poll(_logger);
    }

    public void stop() {

        _logger.log("Stopping run...");
        if (_runHandler != null) {
            _runHandler.stop();
            _running = false;
        }
        if (_pollHandler != null) {
            _polling = false;
        }
    }

    private boolean login(Client client, Args args) {

        boolean ret = true;
        try {
            ret =
                    new RestAuthenticator().login(
                            client,
                            args.getUsername(),
                            args.getPassword(),
                            _logger);
        } catch (Throwable cause) {
            ret = false;
            _logger.log(String.format(
                    "Failed login to ALM Server URL: %s. Exception: %s",
                    args.getUrl(),
                    cause.getMessage()));
        }

        return ret;
    }

    private boolean start(Args args) {

        boolean ret = false;
        Response response =
                _runHandler.start(
                        args.getDuration(),
                        args.getPostRunAction(),
                        args.getEnvironmentConfigurationId(),
                        args.getCdaDetails());
        if (isOk(response, args)) {
            RunResponse runResponse = getRunResponse(response);
            setRunId(runResponse);
            if (runResponse.isSucceeded()) {
                ret = true;
            }
        }
        logReportUrl(ret, args);

        return ret;
    }

    private void setRunId(RunResponse runResponse) {

        String runId = runResponse.getRunId();
        if (StringUtils.isNullOrEmpty(runId)) {
            _logger.log("No run ID");
            throw new SSEException("No run ID");
        } else {
            _runHandler.setRunId(runId);
            _pollHandler.setRunId(runId);
        }
    }

    private void logReportUrl(boolean isSucceeded, Args args) {

        if (isSucceeded) {
            _logger.log(String.format(
                    "%s run report for run id %s is at: %s",
                    args.getRunType(),
                    _runHandler.getRunId(),
                    _runHandler.getReportUrl(args)));
        } else {
            _logger.log(String.format(
                    "Failed to start %s ID:%s, run id: %s "
                            + "\nNote: You can run only functional test sets and build verification suites using this plugin. "
                            + "Check to make sure that the configured ID is valid "
                            + "(and that it is not a performance test ID).",
                    args.getRunType(),
                    args.getEntityId(),
                    _runHandler.getRunId()));
        }
    }

    private RunResponse getRunResponse(Response response) {

        return _runHandler.getRunResponse(response);
    }

    private boolean isOk(Response response, Args args) {

        boolean ret = false;
        if (response.isOk()) {
            _logger.log(String.format(
                    "Executing %s ID: %s in %s/%s %sDescription: %s",
                    args.getRunType(),
                    args.getEntityId(),
                    args.getDomain(),
                    args.getProject(),
                    StringUtils.NEW_LINE,
                    args.getDescription()));
            ret = true;
        } else {
            Throwable cause = response.getFailure();
            if (cause != null) {
                _logger.log(String.format(
                        "Failed to start %s ID: %s, ALM Server URL: %s (Exception: %s)",
                        args.getRunType(),
                        args.getEntityId(),
                        args.getUrl(),
                        cause.getMessage()));
            } else {
                _logger.log(String.format(
                        "Failed to execute %s ID: %s, ALM Server URL: %s (Response: %s)",
                        args.getRunType(),
                        args.getEntityId(),
                        args.getUrl(),
                        response.getStatusCode()));
            }
        }

        return ret;
    }

    public boolean getRunning() {

        return _running;
    }

    public boolean getPolling() {

        return _polling;
    }

}