package io.buji.oauth;

import org.apache.shiro.authc.AuthenticationException;

/**
 *  * <p>User: Srikanth Gundu 
 *   * <p>Date: 4-12-2017
 *    * <p>Version: 1.0
 *     */



/**
 *  * This class is the exception that is thrown when OAuth authentication process fails in OAuthRealm.
 *   * 
 *    * @author Srikanth Gundu
 *     * @since 1.0.0
 *      */
public final class OAuthAuthenticationException extends AuthenticationException {
    
    private static final long serialVersionUID = -9060319148695558222L;
    
    public OAuthAuthenticationException() {
        super();
    }
    
    public OAuthAuthenticationException(String message) {
        super(message);
    }
    
    public OAuthAuthenticationException(Throwable cause) {
        super(cause);
    }
    
    public OAuthAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
