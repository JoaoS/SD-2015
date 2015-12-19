package fundStarter.action;


import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuthService;
import com.opensymphony.xwork2.ActionSupport;
import fundStarter.commons.Alternative;
import fundStarter.commons.Reward;
import fundStarter.model.FundStarterBean;
import fundStarter.model.TumblrBean;
import org.apache.struts2.interceptor.SessionAware;
import org.json.JSONObject;
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
    private String targetValue;
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
        //if it does not have rewards
        if (!rewardDescriptionList.get(0).equals("")){
            for(int i = 0;i<rewardDescriptionList.size();i++)
            {
                Reward r = new Reward(rewardDescriptionList.get(i),minPledgeList.get(i));
                rewards.add(r);
            }
        }
        //if it does not have alternatives
        if (!alternativeDescriptionList.get(0).equals("")){

            for(int i=0;i<alternativeDescriptionList.size();i++)
            {
                Alternative a = new Alternative(alternativeDescriptionList.get(i),divisorList.get(i));
                alternatives.add(a);
            }
        }
        String checkCreated = this.getFundStarterBean().addProject(projectName,description, projectDate,Long.parseLong(targetValue),enterprise,rewards,alternatives);
        if(checkCreated.equals("Project created with success"))
        {
            if(session.get("blogUrl") != null)
            {
                String protected_resource_url = "http://api.tumblr.com/v2/blog/" + session.get("blogUrl") + "/post";
                TumblrBean tumblrBean =  (TumblrBean) session.get("tumblrBean");
                OAuthService service = tumblrBean.getService();
                OAuthRequest requestOauth = new OAuthRequest(Verb.POST, protected_resource_url, service);
                requestOauth.addBodyParameter("type","text");
                requestOauth.addBodyParameter("title","Created project " + projectName);
                requestOauth.addBodyParameter("body",description);
                service.signRequest(tumblrBean.getAccessToken(), requestOauth);
                Response response = requestOauth.send();
                System.out.println(response.getBody());
                //////////////////////////////////////////
                JSONObject obj = null;
                try {
                    obj = new JSONObject(response.getBody());
                    String postId = obj.getJSONObject("response").get("id").toString();
                    this.getFundStarterBean().setPostId(projectName,postId,(String) session.get("blogUrl"));
                } catch (Exception e) {
                    System.out.println("Exception with json");
                    e.printStackTrace();
                }
            }
            session.put("success",checkCreated);
        }
        else
        {
            session.put("error",checkCreated);
        }
        //////////////////////
        rewards.clear();
        alternatives.clear();
        rewardDescriptionList = null;
        minPledgeList = null;
        alternativeDescriptionList = null;
        divisorList = null;
        /////////////////////
        projectName = null;
        description = null;
        projectDate = null;
        enterprise = null;
        targetValue = null;
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

    public String getTargetValue() {return targetValue;}

    public void setTargetValue(String targetValue) {this.targetValue = targetValue;}

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
