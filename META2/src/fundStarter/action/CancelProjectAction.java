package fundStarter.action;

import com.opensymphony.xwork2.ActionSupport;
import fundStarter.model.FundStarterBean;
import org.apache.struts2.interceptor.SessionAware;

import java.rmi.RemoteException;
import java.util.Map;

/**
 * Created by joaosubtil on 09/12/15.
 */
public class CancelProjectAction extends ActionSupport implements SessionAware {

    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;
    private String idSelected = null;

    public String cancelProject() throws RemoteException
    {

        if (this.idSelected!=null){

            String s[]=this.idSelected.split(" ");
            Long l=Long.parseLong(s[2]);

            if ("Project canceled successfully.".equalsIgnoreCase(this.getFundStarterBean().
                    cancelProject(l)))
            {
                System.out.println("Project canceled");
                this.getFundStarterBean().setIdSelected("");
                session.put("success", "Project canceled sucessfully");
                return SUCCESS;


            }
            else
            {
                System.out.println("error");
                String error = "Failed to cancel project";
                session.put("error",error);
                return SUCCESS;
            }


        }


        else
        {

            System.out.println("error");
            String error = "Failed to cancel project";
            session.put("error",error);
            return SUCCESS;
        }





    }












    public String getIdSelected() {
        return idSelected;
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
        this.session=session;
    }
}
