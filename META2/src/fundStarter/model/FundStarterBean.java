package fundStarter.model;

import fundStarter.DataServer.*;
import fundStarter.commons.Alternative;
import fundStarter.commons.Reward;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;


public class FundStarterBean {

    private DataServer_I server;
    private String username;
    private String password;
    private String bi;
    private int age;
    private String email;
    private String idSelected;
    private long viewDetailsId;
    private long alternativeVotedId;
    private float pledgeValue;

    private ArrayList<String> oldWebsocketMessages=new ArrayList<String>();


    public FundStarterBean() {
        try {
            server = (DataServer_I) LocateRegistry.getRegistry("localhost",5000).lookup("DataServer");
        }
        catch(NotBoundException |RemoteException e) {
            e.printStackTrace();
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setBi(String bi) {this.bi = bi;}

    public void setAge(int age) {this.age = age;}

    public void setEmail(String email) {this.email = email;}

    public String getIdSelected() {
        return idSelected;
    }

    public void setIdSelected(String idSelected) {
        this.idSelected = idSelected;
    }

    public long getViewDetailsId() { return viewDetailsId;}

    public void setViewDetailsId(long viewDetailsId) {this.viewDetailsId = viewDetailsId;}

    public long getAlternativeVotedId() {return alternativeVotedId;}

    public float getPledgeValue() {return pledgeValue;}

    public void setPledgeValue(float pledgeValue) {this.pledgeValue = pledgeValue;}

    public void setAlternativeVotedId(long alternativeVotedId) {
        this.alternativeVotedId = alternativeVotedId;
    }

    public ArrayList<String> getOldWebsocketMessages() { return oldWebsocketMessages;    }

    public void addOldWebsocketMessages(String oldmsg) { this.oldWebsocketMessages.add(oldmsg); }


    public int checkLogin() throws RemoteException  {
        return this.server.checkLogin(this.username,this.password);
    }

    public String checkSignUp(String username,String password,String bi, int age, String email) throws RemoteException  {
        return this.server.checkSignUp(username,password,bi,age,email);
    }

    public boolean addUser(String username,String password,String bi,int age, String email) throws RemoteException
    {
        return this.server.addUser(username,password,bi,age,email);
    }

    public String listProjects(int current) throws RemoteException
    {
        return this.server.listProjects(current);
    }

    public long checkAccountBalance() throws RemoteException
    {
        return this.server.checkAccountBalance(username);
    }

    public String checkRewards() throws RemoteException
    {
        return this.server.checkRewards(username);
    }

    public String getNumberProjects() throws RemoteException
    {

        return this.server.getNumberProjects();
    }

    public String showAdminProjects() throws RemoteException
    {
        getAdminProjectIds();
        return this.server.showAdminProjects(username);
    }
    public String getAdminProjectIds() throws RemoteException
    {
        return this.server.getAdminProjectIds(username);
    }

    public String addRewards(Long id, Reward r, String username)throws RemoteException
    {
        return  this.server.addReward(id, r,username);
    }

    public String viewProject() throws RemoteException
    {
        return this.server.viewProject(viewDetailsId);
    }

    public String getAlternativeIdsProject() throws RemoteException
    {
        return this.server.getAlternativeIdsProject(viewDetailsId);
    }

    public String contributeToProject() throws RemoteException
    {
        return this.server.contributeToProject(viewDetailsId,username,pledgeValue,alternativeVotedId);
    }

    public String addProject(String name, String description, String limitDate, long targetValue, String enterprise, ArrayList<Reward> rewards, ArrayList<Alternative> alternatives) throws  RemoteException {
        return this.server.addProject2(username, name, description, limitDate, targetValue, enterprise, rewards, alternatives);

    }

    public String listRewards() throws RemoteException
    {
        String s[]=this.getIdSelected().split(" ");
        Long l=Long.parseLong(s[2]);
       return this.server.listRewardsProject(l);
    }
    public String removeRewards(long idProject,long idReward,String username) throws RemoteException
    {
        return this.server.removeReward(idProject,idReward,username);
    }

    public String cancelProject(long idProject) throws RemoteException
    {
        return this.server.cancelProject(idProject);
    }

    public String commentProject(String comment) throws RemoteException
    {
        return this.server.commentProject(this.viewDetailsId,this.username,comment);
    }


    public ArrayList<String> showCommentsProject() throws RemoteException
    {
        return this.server.showCommentsProject2(viewDetailsId,0);
    }

    public ArrayList<String> showCommentsProjectAdmin() throws RemoteException
    {
        String s[]=this.getIdSelected().split(" ");
        Long l=Long.parseLong(s[2]);
        return this.server.showCommentsProject2(l,0);
    }


    public String getMessagesProjectIds() throws RemoteException
    {
        String s[]=this.getIdSelected().split(" ");
        Long l=Long.parseLong(s[2]);
        return this.server.getMessagesProjectIds(l);
    }

    public String replyMessage(String reply, long messageSelected) throws RemoteException
    {
        String s[]=this.getIdSelected().split(" ");
        Long l=Long.parseLong(s[2]);
        return this.server.replyMessage(l,username,messageSelected,reply);
    }

    public String getRewardsProjectIds() throws RemoteException
    {
        String s[]=this.idSelected.split(" ");
        Long l=Long.parseLong(s[2]);
        return this.server.getRewardsProjectIds(l);
    }
}
