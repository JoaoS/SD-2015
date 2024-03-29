package fundStarter.action;

import com.opensymphony.xwork2.ActionSupport;
import fundStarter.model.FundStarterBean;
import org.apache.struts2.interceptor.SessionAware;
import javax.print.attribute.SupportedValuesAttribute;
import java.rmi.RemoteException;
import java.util.Map;

/**
 * Created by joaosubtil on 09/12/15.
 */
public class RemoveRewardAction extends ActionSupport implements SessionAware {

    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;
    private String idSelected = null;
    private String idtoRemove;


    public String gotoRemoveReward()throws RemoteException{

        String s[]=this.idSelected.split(" ");
        Long l=Long.parseLong(s[2]);
        this.getFundStarterBean().setIdSelected(this.idSelected);
        return SUCCESS;


    }

    public String removeReward() throws RemoteException{


            String s[]=this.getFundStarterBean().getIdSelected().split(" ");
            Long l=Long.parseLong(s[2]);
            s = idtoRemove.split(" ");
            Long l2 = Long.parseLong(s[2]);
            //removeReward(long idProject,long idReward,String username)
            if ("Reward removed successfully.".equalsIgnoreCase(this.getFundStarterBean().removeRewards(l,l2,this.getFundStarterBean().getUsername())))
            {
                System.out.println("Reward removed");
                this.getFundStarterBean().setIdSelected("");
                session.put("success", "Reward removed sucessfully");
                return SUCCESS;


            }
            else
            {
                System.out.println("error");
                String error = "Failed to remove reward";
                session.put("error",error);
                return SUCCESS;
            }
    }








    public String getIdtoRemove() {
        return idtoRemove;
    }

    public void setIdtoRemove(String idtoRemove) {
        this.idtoRemove = idtoRemove;
    }

    public void setIdSelected(String idSelected) {
        this.idSelected = idSelected;
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
