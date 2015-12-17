package fundStarter.model;


import com.github.scribejava.core.model.Token;
import com.github.scribejava.core.oauth.OAuthService;

public class TumblrBean {

    private OAuthService service;
    private Token requestToken;
    private Token accessToken;

    public TumblrBean(OAuthService service) {
        this.service = service;
    }

    public void setService(OAuthService service) {
        this.service = service;
    }

    public OAuthService getService() {
        return this.service;
    }

    public Token getRequestToken() {
        return this.requestToken;
    }

    public void setRequestToken(Token requestToken) {
        this.requestToken = requestToken;
    }

    public Token getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(Token accessToken) {
        this.accessToken = accessToken;
    }
}
