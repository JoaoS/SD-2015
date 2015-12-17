package fundStarter.action;

import com.github.scribejava.core.model.Token;
import com.github.scribejava.core.model.Verifier;
import com.github.scribejava.core.oauth.OAuthService;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.sun.javaws.Globals;
import fundStarter.model.TumblrBean;
import org.apache.struts2.RequestUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.SessionAware;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created by joaogoncalves on 17/12/15.
 */
public class TumblrCallbackAction extends ActionSupport implements SessionAware {

    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;
    private String code;

    @Override
    public String execute() {
        HttpServletRequest request = ServletActionContext.getRequest();
        String  verifierString = request.getParameter("oauth_verifier");
        /////////////////////////////////////////////////////////////////
        TumblrBean tumblrBean =  (TumblrBean) session.get("tumblrBean");
        OAuthService service = tumblrBean.getService();
        Verifier verifier = new Verifier(verifierString);
        Token t = tumblrBean.getRequestToken();
        Token accessToken = service.getAccessToken(tumblrBean.getRequestToken(), verifier);
        tumblrBean.setAccessToken(accessToken);
        return SUCCESS;
    }


    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }
}
