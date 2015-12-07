package fundStarter.action;

import com.opensymphony.xwork2.ActionSupport;
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


    @Override
    public String execute() throws RemoteException
    {

        System.out.println("indice da cena ="+idSelected);

        return SUCCESS;
    }
    @Override
    public void setSession(Map<String, Object> map) {this.session = session;  }

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
}
