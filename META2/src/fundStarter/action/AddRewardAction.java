package fundStarter.action;

import com.opensymphony.xwork2.ActionSupport;
import fundStarter.commons.Reward;
import fundStarter.model.FundStarterBean;
import org.apache.struts2.interceptor.SessionAware;

import java.rmi.RemoteException;
import java.util.Map;

/**
 * Created by joaosubtil on 07/12/15.
 */
public class AddRewardAction extends ActionSupport implements SessionAware {

    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;
    private String idSelected = null;
    private String description=null;
    private double value=0;


    //redirects to reward jsp
    public String gotoRewardpage() throws RemoteException
    {
        this.getFundStarterBean().setIdSelected(this.idSelected);
        return SUCCESS;


    }

    //adds a reward to project
    public String addRewardToProject() throws Exception {

        if (description != null && value != 0){
            Reward rew = new Reward(description,value);
            // id, reward, username
            String s[]=this.getFundStarterBean().getIdSelected().split(" ");
            Long l=Long.parseLong(s[2]);
            String error = this.getFundStarterBean().addRewards(l,rew,this.getFundStarterBean().getUsername());
            if ("Reward added successfully.".equalsIgnoreCase(error))
            {
                System.out.println("Reward added");
                this.getFundStarterBean().setIdSelected("");
                session.put("success",error);
                return SUCCESS;
            }
            else
            {
                System.out.println("error");
                session.put("error",error);
                return SUCCESS;
            }

        }

        return SUCCESS;
    }


    public FundStarterBean getFundStarterBean() {
        if(!session.containsKey("fundStarterBean"))
            this.setFundStarterBean(new FundStarterBean());
        return (FundStarterBean) session.get("fundStarterBean");
    }

    public void setFundStarterBean(FundStarterBean fundStarterBean) {
        this.session.put("fundStarterBean", fundStarterBean);
    }


    public void setIdSelected(String idSelected) {
        this.idSelected = idSelected;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }

}
