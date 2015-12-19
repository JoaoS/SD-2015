package fundStarter.action;

import com.github.scribejava.core.model.*;
import com.github.scribejava.core.oauth.OAuthService;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.sun.javaws.Globals;
import fundStarter.model.FundStarterBean;
import fundStarter.model.TumblrBean;
import org.apache.struts2.RequestUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.SessionAware;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by joaogoncalves on 17/12/15.
 */
public class AssociateCallbackAction extends ActionSupport implements SessionAware {

    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;
    private String code;
    private static final String PROTECTED_RESOURCE_URL = "http://api.tumblr.com/v2/user/info";
    private String tumblrUsername;

    @Override
    public String execute() throws RemoteException {
        //--------------------------grant access--------------------------------------------
        HttpServletRequest request = ServletActionContext.getRequest();
        String  verifierString = request.getParameter("oauth_verifier");
        /////////////////////////////////////////////////////////////////
        TumblrBean tumblrBean =  (TumblrBean) session.get("tumblrBean");
        OAuthService service = tumblrBean.getService();
        Verifier verifier = new Verifier(verifierString);
        Token accessToken = service.getAccessToken(tumblrBean.getRequestToken(), verifier);
        //------------------------fetch user info--------------------------------------------
        OAuthRequest requestOauth = new OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL, service);
        service.signRequest(accessToken, requestOauth);
        Response response = requestOauth.send();
        //-------------------------get username----------------------------------------------
        JSONObject obj = null;
        try {
            obj = new JSONObject(response.getBody());
            tumblrUsername = obj.getJSONObject("response").getJSONObject("user").get("name").toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //-----------------------associate account---------------------
        String error = this.getFundStarterBean().associateAccount(this.getFundStarterBean().getUsername(),this.getFundStarterBean().getTumblrUser(),tumblrUsername,accessToken.getSecret(),accessToken.getToken());
        if(error.equals("Account associated with success"))
        {
            session.put("success",error);
        }
        else
        {
            session.put("error",error);
        }
        return SUCCESS;
    }


    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }



    public FundStarterBean getFundStarterBean() {
        if(!session.containsKey("fundStarterBean"))
            this.setFundStarterBean(new FundStarterBean());
        return (FundStarterBean) session.get("fundStarterBean");
    }

    public void setFundStarterBean(FundStarterBean fundStarterBean) {
        this.session.put("fundStarterBean", fundStarterBean);
    }
}
