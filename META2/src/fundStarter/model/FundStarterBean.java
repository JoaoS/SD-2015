package fundStarter.model;
import fundStarter.DataServer.*;
import fundStarter.commons.Reward;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;


public class FundStarterBean {

    private DataServer_I server;
    private String username;
    private String password;
    private String bi;
    private int age;
    private String email;
    private String idSelected;



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

    public long getNumberProjects() throws RemoteException
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

}
