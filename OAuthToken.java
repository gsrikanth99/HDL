package io.buji.oauth;
import org.apache.shiro.authc.AuthenticationToken;
import org.scribe.up.credential.OAuthCredential;
/**
 * <p>User: Srikanth Gundu
 */
public final class OAuthToken implements AuthenticationToken {


private static final long serialVersionUID = 3376624432421737333L;
    
    private OAuthCredential credential;
    
    private String userId;
    
    public OAuthToken(OAuthCredential credential) {
        this.credential = credential;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public Object getPrincipal() {
        return userId;
    }
    
    public Object getCredentials() {
        return credential;
    }
}
