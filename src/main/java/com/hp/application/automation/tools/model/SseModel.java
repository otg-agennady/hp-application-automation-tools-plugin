package com.hp.application.automation.tools.model;

import java.util.Arrays;
import java.util.List;

import org.kohsuke.stapler.DataBoundConstructor;

/**
 * 
 * @author Effi Bar-She'an
 * @author Dani Schreiber
 * 
 */
public class SseModel {
    
    public static final String TEST_SET = "TEST_SET";
    public static final String BVS = "BVS";
    public static final String PC = "PC";
    
    public static final String COLLATE = "Collate";
    public static final String COLLATE_ANALYZE = "CollateAndAnalyze";
    public static final String DO_NOTHING = "DoNothing";
    
    private final String _almServerName;
    private String _almServerUrl;
    private final String _almUserName;
    private final SecretContainer _almPassword;
    private final String _almDomain;
    private final String _almProject;
    private final String _timeslotDuration;
    private final String _description;
    private final String _runType;
    private final String _almEntityId;
    private final String _postRunAction;
    private final String _environmentConfigurationId;
    private final String _release;
    private final String _branch;
    private String resolvedBranch;
    private final CdaDetails _cdaDetails;
    private final SseProxySettings _proxySettings;
    
    private final static EnumDescription _runTypeTestSet =
            new EnumDescription(TEST_SET, "Test Set");
    private final static EnumDescription _runTypeBVS = new EnumDescription(
            BVS,
            "Build Verification Suite");
    private final static List<EnumDescription> _runTypes = Arrays.asList(
            _runTypeTestSet,
            _runTypeBVS);
    
    private final static EnumDescription _postRunActionCollate = new EnumDescription(
            COLLATE,
            "Collate");
    private final static EnumDescription _postRunActionCollateAnalyze = new EnumDescription(
            COLLATE_ANALYZE,
            "CollateAndAnalyze");
    private final static EnumDescription _postRunActionDoNothing = new EnumDescription(
            DO_NOTHING,
            "DoNothing");
    private final static List<EnumDescription> _postRunActions = Arrays.asList(
            _postRunActionCollate,
            _postRunActionCollateAnalyze,
            _postRunActionDoNothing);
    private String resolvedRelease;

    @DataBoundConstructor
    public SseModel(
            String almServerName,
            String almUserName,
            String almPassword,
            String almDomain,
            String almProject,
            String runType,
            String almEntityId,
            String timeslotDuration,
            String description,
            String postRunAction,
            String environmentConfigurationId,
            CdaDetails cdaDetails,
            SseProxySettings proxySettings,
            String release,
            String branch) {
        
        _almServerName = almServerName;
        _almDomain = almDomain;
        _almProject = almProject;
        _timeslotDuration = timeslotDuration;
        _almEntityId = almEntityId;
        _almUserName = almUserName;
        _almPassword = setPassword(almPassword);
        _runType = runType;
        _description = description;
        _postRunAction = postRunAction;
        _environmentConfigurationId = environmentConfigurationId;
        _cdaDetails = cdaDetails;
        _proxySettings = proxySettings;
        _release = release;
        _branch = branch;

    }
    
    protected SecretContainer setPassword(String almPassword) {
        
        SecretContainer secretContainer = new SecretContainerImpl();
        secretContainer.initialize(almPassword);
        
        return secretContainer;
    }
    
    public String getAlmServerName() {
        
        return _almServerName;
    }
    
    public String getAlmServerUrl() {
        
        return _almServerUrl;
    }
    
    public void setAlmServerUrl(String almServerUrl) {
        
        _almServerUrl = almServerUrl;
    }
    
    public String getAlmUserName() {
        
        return _almUserName;
    }
    
    public String getAlmPassword() {
        
        return _almPassword.toString();
    }
    
    public String getAlmDomain() {
        
        return _almDomain;
    }
    
    public String getAlmProject() {
        
        return _almProject;
    }
    
    public String getTimeslotDuration() {
        
        return _timeslotDuration;
    }
    
    public String getAlmEntityId() {
        
        return _almEntityId;
    }
    
    public String getRunType() {
        return _runType;
    }
    
    public String getDescription() {
        
        return _description;
    }
    
    public String getEnvironmentConfigurationId() {
        
        return _environmentConfigurationId;
    }
    
    public static List<EnumDescription> getRunTypes() {
        
        return _runTypes;
    }
    
    public static List<EnumDescription> getPostRunActions() {
        
        return _postRunActions;
    }
    
    public CdaDetails getCdaDetails() {
        
        return _cdaDetails;
    }
    
    public String getPostRunAction() {
        
        return _postRunAction;
    }
    
    public SseProxySettings getProxySettings() {
        return _proxySettings;
    }

    public String getRelease() {
        return _release;
    }

    public String getBranch() {
        return _branch;
    }

    public String getResolvedBranch() {
        return resolvedBranch;
    }

    public void setResolvedBranch(String resolvedBranch) {
        this.resolvedBranch = resolvedBranch;
    }

    public String getResolvedRelease() {
        return resolvedRelease;
    }

    public void setResolvedRelease(String resolvedRelease) {
        this.resolvedRelease = resolvedRelease;
    }
}
