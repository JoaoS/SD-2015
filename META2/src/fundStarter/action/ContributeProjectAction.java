package fundStarter.action;


import com.opensymphony.xwork2.ActionSupport;
import fundStarter.model.FundStarterBean;
import org.apache.struts2.interceptor.SessionAware;
import ws.GenericNotification;
import sun.invoke.empty.Empty;

import java.rmi.RemoteException;
import java.util.Map;

public class ContributeProjectAction extends ActionSupport implements SessionAware
{
    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;
    private String username = null, password = null;
    private String error;
    private long alternativeVotedId;
    private float pledgeValue;

    @Override
    public String execute() throws RemoteException
    {
        this.getFundStarterBean().setAlternativeVotedId(alternativeVotedId);
        this.getFundStarterBean().setPledgeValue(pledgeValue);
        this.getFundStarterBean().contributeToProject();

        GenericNotification.sendNotification("["+this.getFundStarterBean().getUsername()+"]Donated to project");
        return  SUCCESS;
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

    public long getAlternativeVotedId() {return alternativeVotedId;}

    public void setAlternativeVotedId(long alternativeVotedId) {this.alternativeVotedId = alternativeVotedId;}

    public float getPledgeValue() {return pledgeValue;}

    public void setPledgeValue(float pledgeValue) {this.pledgeValue = pledgeValue;}
}
