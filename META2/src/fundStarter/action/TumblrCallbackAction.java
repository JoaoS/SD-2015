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
public class TumblrCallbackAction extends ActionSupport implements SessionAware {

    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;
    private String code;
    private static final String PROTECTED_RESOURCE_URL = "http://api.tumblr.com/v2/user/info";
    private String username = null;
    private String blogUrl = null;

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
            username = obj.getJSONObject("response").getJSONObject("user").get("name").toString();
            blogUrl = obj.getJSONObject("response").getJSONObject("user").getJSONArray("blogs").getJSONObject(0).get("url").toString();
            blogUrl = blogUrl.replace("http://","");
            blogUrl = blogUrl.replace("/","");
        } catch (Exception e) {
            e.printStackTrace();
        }
        //---------------------------create user account-------------------------------------
        this.getFundStarterBean().setUsername(username);
        this.getFundStarterBean().setTumblrUser(1);
        // check if already exists a tumblraccount with that username associated to that fundStarter account
        if(this.getFundStarterBean().checkAssociated(username).equals("That tumblr account is already associated with another account"))
        {
            session.put("error","That tumblr account is already associated with another fundStarter account");
            return  ERROR;
        }
        if(this.getFundStarterBean().checkTumblrAccount(username))
        {
            session.put("username", username);
            session.put("loggedin", true);
            session.put("tumblr",true);
            session.put("blogUrl",blogUrl);
            session.put("success","Login with tumblr account made with success");
            ArrayList<String> tokens = this.getFundStarterBean().getAccessToken(username,1);
            Token access = new Token(tokens.get(0),tokens.get(1));
            if(access != accessToken)
            {
                tumblrBean.setAccessToken(accessToken);
                this.getFundStarterBean().updateAccessToken(accessToken.getSecret(),accessToken.getToken(),access.getSecret(),access.getToken());
            }
            else
            {
                tumblrBean.setAccessToken(access);
            }
            return SUCCESS;
        }
        else
        {
            if(this.getFundStarterBean().addTumblrUser(username,accessToken.getSecret(),accessToken.getToken()))
            {
                session.put("username", username);
                session.put("loggedin", true);
                session.put("tumblr",true);
                session.put("blogUrl",blogUrl);
                session.put("success","Login with tumblr account made with success");
                tumblrBean.setAccessToken(accessToken);
                return SUCCESS;
            }
            else
            {
                session.put("error","Error occurred logging in with your tumblr account.");
                return  ERROR;
            }
        }
    }


    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }

    public void setUsername(String username) {
        this.username = username;
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
