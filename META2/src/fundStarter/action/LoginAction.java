package fundStarter.action;


import com.opensymphony.xwork2.ActionSupport;
import fundStarter.model.FundStarterBean;
import org.apache.struts2.interceptor.SessionAware;

import java.rmi.RemoteException;
import java.util.Map;

public class LoginAction extends ActionSupport implements SessionAware
{
    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;
    private String username = null, password = null;
    private String error;

    @Override
    public String execute() throws RemoteException
    {
        //ensure this is only connected user
        this.setFundStarterBean(new FundStarterBean());
        session.clear();


        /*
        * todo ver se esta sessao já contém alguma coisa
        * */
        if(this.username != null && !username.equals(""))
        {
            this.getFundStarterBean().setUsername(this.username);
            this.getFundStarterBean().setPassword(this.password);
            this.getFundStarterBean().setTumblrUser(0);
            if(this.getFundStarterBean().checkLogin() > 0)
            {
                session.put("username", username);
                session.put("password",password);
                session.put("loggedin", true);
                session.put("tumblr",false);
                this.getFundStarterBean().setTumblrUser(0);
                if(this.getFundStarterBean().isAssociatedAccount(this.getFundStarterBean().getUsername(),this.getFundStarterBean().getTumblrUser()))
                {
                    return INPUT;
                }
                return SUCCESS;
            }
            else
            {
                error = "Login failed. Wrong username or password";
                session.put("login_error",error);
                return LOGIN;
            }

        }
        else
            return NONE;

    }


    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public FundStarterBean getFundStarterBean() {
        if(!session.containsKey("fundStarterBean"))
            this.setFundStarterBean(new FundStarterBean());
        return (FundStarterBean) session.get("fundStarterBean");
    }

    public void setFundStarterBean(FundStarterBean fundStarterBean) {
        this.session.put("fundStarterBean", fundStarterBean);
    }

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }

    public Map<String, Object> getHTTPSession(){
        return this.session;
    }

}
