package fundStarter.action;


import com.opensymphony.xwork2.ActionSupport;
import fundStarter.model.FundStarterBean;
import org.apache.struts2.interceptor.SessionAware;
import sun.invoke.empty.Empty;

import java.rmi.RemoteException;
import java.util.Map;

public class LoginAction extends ActionSupport implements SessionAware
{
    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;
    private String username = null, password = null;

    @Override
    public String execute() throws RemoteException
    {
        // any username is accepted without confirmation (should check using RMI)
        if(this.username != null && !username.equals(""))
        {
            this.getFundStarterBean().setUsername(this.username);
            this.getFundStarterBean().setPassword(this.password);
            if(this.getFundStarterBean().checkLogin() > 0)
            {
                session.put("username", username);
                session.put("password",password);
                session.put("loggedin", true);
                return SUCCESS;
            }
            else
                return LOGIN;

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

}
