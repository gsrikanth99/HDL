package io.buji.oauth;
import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthBearerClientRequest;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthAccessTokenResponse;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.oltu.oauth2.client.response.OAuthResourceResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.scribe.up.provider.OAuthProvider;
import org.scribe.up.provider.ProvidersDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.util.CollectionUtils;
import org.apache.shiro.util.StringUtils;
import org.scribe.up.credential.OAuthCredential;
import org.scribe.up.profile.ProfileHelper;
import org.scribe.up.profile.UserProfile;

/**
 *  * <p>User: Srikanth Gundu
 *   * <p>Date: 4-12-2017
 *    * <p>Version: 1.0
 *     */
public class OAuthRealm extends AuthorizingRealm {

    private String clientId;
    private String clientSecret;
    private String accessTokenUrl;
    private String userInfoUrl;
    private String redirectUrl = "http://ec2-54-236-18-151.compute-1.amazonaws.com:8890";
private String defaultRoles;
private String defaultPermissions;
private ProvidersDefinition providersDefinition;


private static Logger log = LoggerFactory.getLogger(OAuthRealm.class);
	public OAuthRealm() {
        setAuthenticationTokenClass(OAuthToken.class);
        ProfileHelper.setKeepRawData(false);
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public void setAccessTokenUrl(String accessTokenUrl) {
        this.accessTokenUrl = accessTokenUrl;
    }

    public void setUserInfoUrl(String userInfoUrl) {
        this.userInfoUrl = userInfoUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        //this.redirectUrl = redirectUrl;
        this.redirectUrl = "http://ec2-54-236-18-151.compute-1.amazonaws.com:8890";
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof OAuthToken;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
	// create simple authorization info
        final SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        // add default roles
        simpleAuthorizationInfo.addRoles(split(this.defaultRoles));
        // add default permissions
        simpleAuthorizationInfo.addStringPermissions(split(this.defaultPermissions));
        return simpleAuthorizationInfo;    


}

public void setProvider(OAuthProvider provider) {
        providersDefinition = new ProvidersDefinition(provider);
        providersDefinition.init();
    }

	public void setProvidersDefinition(ProvidersDefinition providersDefinition) {
        this.providersDefinition = providersDefinition;
        this.providersDefinition.init();
	}

	public void setDefaultRoles(final String defaultRoles) {
        this.defaultRoles = defaultRoles;
    }
    
    public void setDefaultPermissions(final String defaultPermissions) {
        this.defaultPermissions = defaultPermissions;
    }


	protected List<String> split(final String s) {
        final List<String> list = new ArrayList<String>();
        final String[] elements = StringUtils.split(s, ',');
        if (elements != null && elements.length > 0) {
            for (final String element : elements) {
                if (StringUtils.hasText(element)) {
                    list.add(element.trim());
                }
            }
        }
        return list;
    }
    

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        final OAuthToken oauthToken = (OAuthToken) authenticationToken;
        log.debug("oauthToken : {}", oauthToken);
        // token must be provided
        if (oauthToken == null) {
            return null;
        }
        
        // OAuth credential
        final OAuthCredential credential = (OAuthCredential) oauthToken.getCredentials();
        log.debug("credential : {}", credential);
        // credential should be not null
        if (credential == null) {
            return null;
        }

        // OAuth provider
        OAuthProvider provider = providersDefinition.findProvider(credential.getProviderType());
        log.debug("provider : {}", provider);
        // no provider found
        if (provider == null) {
            return null;
        }
        
        // finish OAuth authentication process : get the user profile
         UserProfile userProfile = provider.getUserProfile(credential);
        log.debug("userProfile : {}", userProfile.getId());
        if (userProfile == null ) {
            log.error("Sri1 Unable to get user profile for useProfile.getId : [{}]", userProfile.getId());
            log.error("Sri2 Unable to get user profile for credential : [{}]", credential );
            log.error("Sri3 Unable to get user profile for provider : [{}]", provider );
            log.error("Sri4 Unable to get user profile for oauthToken : [{}]", oauthToken );
            log.error("Sri5 Unable to get user profile for UserProfile : [{}]", userProfile );
            throw new OAuthAuthenticationException("Sri2 Unable to get user profile for OAuth credential : [" + userProfile.getId()
                                                   + "]");
        }
        
        // refresh authentication token with user id
        final String userId = userProfile.getTypedId();
        oauthToken.setUserId(userId);
        // create simple authentication info
        final List<? extends Object> principals = CollectionUtils.asList(userId, userProfile);
        final PrincipalCollection principalCollection = new SimplePrincipalCollection(principals, getName());
        return new SimpleAuthenticationInfo(principalCollection, credential);
    }


    private String extractUsername(String code) {

        try {
            OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
		String redirectUrl = "http://ec2-54-236-18-151.compute-1.amazonaws.com:8890";
            OAuthClientRequest accessTokenRequest = OAuthClientRequest
                    .tokenLocation(accessTokenUrl)
                    .setGrantType(GrantType.AUTHORIZATION_CODE)
                    .setClientId(clientId)
                    .setClientSecret(clientSecret)
                    .setCode(code)
                    .setRedirectURI(redirectUrl)
                    .buildQueryMessage();

            OAuthAccessTokenResponse oAuthResponse = oAuthClient.accessToken(accessTokenRequest, OAuth.HttpMethod.POST);

            String accessToken = oAuthResponse.getAccessToken();
            Long expiresIn = oAuthResponse.getExpiresIn();

            OAuthClientRequest userInfoRequest = new OAuthBearerClientRequest(userInfoUrl)
                    .setAccessToken(accessToken).buildQueryMessage();

            OAuthResourceResponse resourceResponse = oAuthClient.resource(userInfoRequest, OAuth.HttpMethod.GET, OAuthResourceResponse.class);
            String username = resourceResponse.getBody();
            return username;
        } catch (Exception e) {
            e.printStackTrace();
            throw new OAuthAuthenticationException(e);
        }
    }
}
