package fundStarter.action;


import com.opensymphony.xwork2.ActionSupport;
import fundStarter.commons.Alternative;
import fundStarter.commons.Reward;
import fundStarter.model.FundStarterBean;
import org.apache.struts2.interceptor.SessionAware;
import sun.invoke.empty.Empty;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CreateProjectAction extends ActionSupport implements SessionAware
{
    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;
    private String username = null, password = null;
    private String error;

    private String projectName;
    private String description;
    private String projectDate;
    private long targetValue;
    private String enterprise;

    private List<String> rewardDescriptionList;
    private List<Float> minPledgeList;
    private List<String> alternativeDescriptionList;
    private List<Float> divisorList;

    private ArrayList <Reward> rewards = new ArrayList<>();
    private ArrayList <Alternative> alternatives = new ArrayList<>();

    @Override
    public String execute() throws RemoteException
    {
        for(int i = 0;i<rewardDescriptionList.size();i++)
        {
            Reward r = new Reward(rewardDescriptionList.get(i),minPledgeList.get(i));
            rewards.add(r);
        }
        for(int i=0;i<alternativeDescriptionList.size();i++)
        {
            Alternative a = new Alternative(alternativeDescriptionList.get(i),divisorList.get(i));
            alternatives.add(a);
        }
        this.getFundStarterBean().addProject(projectName,description, projectDate,targetValue,enterprise,rewards,alternatives);
        return  SUCCESS;
    }


    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProjectName() {return projectName;}

    public void setProjectName(String projectName) {this.projectName = projectName;}

    public String getDescription() {return description;}

    public void setDescription(String description) {this.description = description;}

    public String getProjectDate() {return projectDate;}

    public void setProjectDate(String projectDate) {this.projectDate = projectDate;}

    public long getTargetValue() {return targetValue;}

    public void setTargetValue(long targetValue) {this.targetValue = targetValue;}

    public String getEnterprise() {return enterprise;}

    public void setEnterprise(String enterprise) {this.enterprise = enterprise;}


    public List<String> getRewardDescriptionList() {
        return rewardDescriptionList;
    }

    public void setRewardDescriptionList(List<String> rewardDescriptionList) {
        this.rewardDescriptionList = rewardDescriptionList;
    }

    public List<Float> getMinPledgeList() {
        return minPledgeList;
    }

    public void setMinPledgeList(List<Float> minPledgeList) {
        this.minPledgeList = minPledgeList;
    }

    public List<String> getAlternativeDescriptionList() {
        return alternativeDescriptionList;
    }

    public void setAlternativeDescriptionList(List<String> alternativeDescriptionList) {
        this.alternativeDescriptionList = alternativeDescriptionList;
    }

    public List<Float> getDivisorList() {
        return divisorList;
    }

    public void setDivisorList(List<Float> divisorList) {
        this.divisorList = divisorList;
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
