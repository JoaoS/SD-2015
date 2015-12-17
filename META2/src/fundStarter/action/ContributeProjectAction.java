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
    private String alternativeVotedId;
    private float pledgeValue;

    @Override
    public String execute() throws RemoteException
    {
        String error;
        if(alternativeVotedId.equalsIgnoreCase("There are no alternatives for this project."))
        {
            this.getFundStarterBean().setAlternativeVotedId(0);
        }
        else
        {
            this.getFundStarterBean().setAlternativeVotedId(Long.valueOf(alternativeVotedId));
        }
        this.getFundStarterBean().setPledgeValue(pledgeValue);
        error = this.getFundStarterBean().contributeToProject();
        if(!error.equals("Donation made successfully."))
        {
            session.put("error",error);
        }
        else
        {
            session.put("success",error);

            //send message and update message history

            long idProject=this.getFundStarterBean().getViewDetailsId();
            String contributionMessage="["+this.getFundStarterBean().getUsername()+"]Donated to your project("+idProject+")";
           // String originUser=this.getFundStarterBean().getUsername();
            String projectAdmin=this.getFundStarterBean().getProjectAdmin(idProject);

            //enviar id e valor novo do projecto
            //fazer split na getCurrentValue
            //mandar o valor actual do projecto em baixo (getCurrentValue no dataserver)
            Long projectValue=this.getFundStarterBean().getProjectValue(idProject);

            GenericNotification.donationNotification(contributionMessage,projectAdmin,"UPDT|ID : "+idProject+"|"+projectValue);

        }

        return  SUCCESS;

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

    public String getAlternativeVotedId() {return alternativeVotedId;}

    public void setAlternativeVotedId(String alternativeVotedId) {this.alternativeVotedId = alternativeVotedId;}

    public float getPledgeValue() {return pledgeValue;}

    public void setPledgeValue(float pledgeValue) {this.pledgeValue = pledgeValue;}
}
