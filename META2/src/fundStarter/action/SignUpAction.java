package fundStarter.action;


import com.opensymphony.xwork2.ActionSupport;
import fundStarter.model.FundStarterBean;
import org.apache.struts2.interceptor.SessionAware;

import java.rmi.RemoteException;
import java.util.Map;

public class SignUpAction extends ActionSupport implements SessionAware
{

    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;
    private String username = null, password = null,bi = null,email = null;
    private int age;

    @Override
    public String execute() throws RemoteException
    {
        String error = this.getFundStarterBean().checkSignUp(username,password,bi,age,email);
        /*if(session.get("login_error") != null)
        {
            session.remove("login_error");
        }
        if(session.get("signup_error") != null)
        {
            session.remove("signup_error");
        }*/
        if(error == null)
        {
            this.getFundStarterBean().addUser(username,password,bi,age,email);
            return SUCCESS;
        }

        else
        {

            session.put("error",error);
            return ERROR;
        }


    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setBi(String bi) { this.bi = bi;}

    public void setEmail(String email) { this.email = email;}


    public void setAge(int age) { this.age = age;}

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
