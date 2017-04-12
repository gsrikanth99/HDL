package io.buji.oauth;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.apache.shiro.web.filter.authc.AuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.util.StringUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.apache.shiro.web.util.WebUtils;
import org.scribe.up.credential.OAuthCredential;
import org.scribe.up.profile.ProfileHelper;
import org.scribe.up.provider.OAuthProvider;
import org.scribe.up.provider.ProvidersDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * <p>User: Srikanth Gundu
 * <p>Date: 4-12-2017
 * <p>Version: 1.0
 */
public class OAuthAuthenticationFilter extends AuthenticatingFilter {
	private static Logger log = LoggerFactory.getLogger(OAuthAuthenticationFilter.class);
    private String authcCodeParam = "code";
    private String clientId;
    private String redirectUrl = "http://ec2-54-236-18-151.compute-1.amazonaws.com:8890/";
    private String responseType = "code";

    private String failureUrl;
private ProvidersDefinition providersDefinition;
    public void setAuthcCodeParam(String authcCodeParam) {
        this.authcCodeParam = authcCodeParam;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = "http://ec2-54-236-18-151.compute-1.amazonaws.com:8890/";
        //this.redirectUrl = redirectUrl;
    }

    public void setResponseType(String responseType) {
        this.responseType = responseType;
    }

    public void setFailureUrl(String failureUrl) {
        this.failureUrl = failureUrl;
    }
	private ShiroUserSession shiroUserSession = new ShiroUserSession();

    @Override
    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) throws Exception {
/*
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String code = httpRequest.getParameter(authcCodeParam);
        return new OAuthToken(code);
*/
	Map<String, String[]> parameters = request.getParameterMap();
        OAuthProvider provider = providersDefinition.findProvider(parameters);
        log.debug("provider : {}", provider);
        OAuthCredential credential = provider.getCredential(shiroUserSession, parameters);
        log.debug("credential : {}", credential);
        return new OAuthToken(credential);  
  }

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        return false;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {


        String error = request.getParameter("error");
        String errorDescription = request.getParameter("error_description");
        if(!StringUtils.isEmpty(error)) {
            WebUtils.issueRedirect(request, response, failureUrl + "?error=" + error + "error_description=" + errorDescription);
            return false;
        }

        Subject subject = getSubject(request, response);
        if(!subject.isAuthenticated()) {
            if(StringUtils.isEmpty(request.getParameter(authcCodeParam))) {
                saveRequestAndRedirectToLogin(request, response);
                return false;
            }
        }

        return executeLogin(request, response);
    }

    @Override
    protected boolean onLoginSuccess(AuthenticationToken token, Subject subject, ServletRequest request,
                                     ServletResponse response) throws Exception {
        issueSuccessRedirect(request, response);
        return false;
    }

   public void setProvider(OAuthProvider provider) {
        providersDefinition = new ProvidersDefinition(provider);
        providersDefinition.init();
    }

	public void setProvidersDefinition(ProvidersDefinition providersDefinition) {
        this.providersDefinition = providersDefinition;
        this.providersDefinition.init();
	}

    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException ae, ServletRequest request,
                                     ServletResponse response) {
        Subject subject = getSubject(request, response);
        if (subject.isAuthenticated() || subject.isRemembered()) {
            try {
                issueSuccessRedirect(request, response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                WebUtils.issueRedirect(request, response, failureUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

}
