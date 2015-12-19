package fundStarter.DataServer;

/**
 * Created by joaosubtil on 03/12/15.
 */
import fundStarter.commons.*;
import org.apache.struts2.components.Token;
import org.json.simple.JSONObject;

import fundStarter.commons.Alternative;
import fundStarter.commons.Reward;

import javax.jms.Session;
import javax.swing.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataServer extends UnicastRemoteObject implements DataServer_I
{

    private static final long serialVersionUID = 1L;
    public static int rmiPort;
    public static String remoteName;
    public static String url;
    public static String dbName;
    public static String driver;
    public static String userName ;
    public static String password;
    public static java.sql.Connection connection = null;

    public DataServer()  throws RemoteException
    {
        super();
    }

    public synchronized int dummyMethod() throws RemoteException
    {
        return 0;
    }


    public int checkLogin(String username,String password) throws RemoteException
    {
        ResultSet rt = null;
        try
        {

            String s="SELECT ID_USER FROM USER WHERE name = '" + username + "'AND password = '" + password + "'";
            rt = connection.createStatement().executeQuery(s);
            connection.commit();
            if(rt.next())
            {
                return rt.getInt(1);
            }
        }
        catch(SQLException e)
        {
            try {
                System.out.println("\nException at checkLogin.\n");
                e.printStackTrace();
                connection.rollback();
                return -1;
            } catch (SQLException ex) {
                Logger.getLogger(DataServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return 0;
    }


    public String checkSignUp(String username,String password,String bi, int age, String email) throws RemoteException
    {
        ResultSet rt = null;
        try
        {
            // check username
            String s="SELECT ID_USER FROM USER WHERE name = '" + username + "'";
            rt = connection.createStatement().executeQuery(s);
            connection.commit();
            if(rt.next())
            {
                return "Already exists one user with that name.";
            }
            //check bi
            s="SELECT ID_USER FROM USER WHERE bi = '" + bi + "'";
            rt = connection.createStatement().executeQuery(s);
            connection.commit();
            if(rt.next())
            {
                return "Already exists one user with that BI";
            }
            //check email
            s="SELECT ID_USER FROM USER WHERE email = '" + email + "'";
            rt = connection.createStatement().executeQuery(s);
            connection.commit();
            if(rt.next())
            {
                return "Already exists one user with that email";
            }
        }
        catch(Exception e)
        {
            try {
                System.out.println("\nException at checkUserPass.\n");
                e.printStackTrace();
                connection.rollback();
                return "Some error occurred while signing up.";
            } catch (SQLException ex) {
                Logger.getLogger(DataServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    public boolean checkTumblrAccount(String username) throws  RemoteException      //check if an account with tumblr username already exists. cannot exist mulitple accounts with same username in tumblr
    {
        ResultSet rt=null;
        String aux="";
        try
        {
            String s="SELECT id_user FROM USER WHERE name = '" + username + "' and is_tumblr_account = 1";
            rt = connection.createStatement().executeQuery(s);
            connection.commit();
            if(rt.next())
            {
                return true;
            }
        }
        catch(SQLException  e) {
            try {
                System.out.println("\nException at checkAccountBalance.\n");
                e.printStackTrace();
                connection.rollback();
                return false;
            } catch (SQLException ex) {
                Logger.getLogger(DataServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return false;
    }


    public boolean addUser(String username,String password,String bi,int age, String email) throws RemoteException
    {
        PreparedStatement ps;
        ResultSet rt = null;
        try
        {
            ps = connection.prepareStatement("INSERT INTO USER(name,password,bi,age,email) VALUES(?,?,?,?,?)");
            ps.setString(1,username);
            ps.setString(2,password);
            ps.setString(3,bi);
            ps.setInt(4,age);
            ps.setString(5,email);
            ps.execute();
            connection.commit();
        }catch(SQLException e){
            try {
                System.out.println("\nException at addUser.\n");
                e.printStackTrace();
                connection.rollback();
                return false;
            } catch (SQLException ex) {
                Logger.getLogger(DataServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return true;
    }

    public boolean addTumblrUser(String username, String secret, String userToken) throws RemoteException
    {
        PreparedStatement ps;
        ResultSet rt = null;
        try
        {
            ps = connection.prepareStatement("INSERT INTO USER(name,is_tumblr_account,secret_token,user_token) VALUES(?,?,?,?)");
            ps.setString(1,username);
            ps.setInt(2,1);
            ps.setString(3,secret);
            ps.setString(4,userToken);
            ps.execute();
            connection.commit();
        }catch(SQLException e){
            try {
                System.out.println("\nException at addTumblrUser.\n");
                e.printStackTrace();
                connection.rollback();
                return false;
            } catch (SQLException ex) {
                Logger.getLogger(DataServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return true;
    }


    public String listProjects(int current) throws RemoteException        // current projects-->1, old projects --->0,all-->2
    {
        ResultSet rt = null;
        String result = "";
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH");
        Date today = new Date();
        Date auxDate = null;
        try
        {
            String s="SELECT id_project,name,target_value,current_value,limit_date,accepted FROM project";
            rt = connection.createStatement().executeQuery(s);
            connection.commit();
            while(rt.next())
            {
                auxDate = formatter.parse(rt.getString(5));
                if(current == 1)
                {
                    if(!auxDate.before(today))
                    {
                        result += "ID : "+ rt.getLong(1) + "| Project : " + rt.getString(2) + "| Target value : " + rt.getLong(3) + "| Current value : " + rt.getLong(4) + "\n";
                    }
                }
                else if(current == 0)
                {
                    if(auxDate.before(today))
                    {
                        result += "ID : "+ rt.getLong(1) + "| Project : " + rt.getString(2) + "| Target value : " + rt.getLong(3) + "| Current value : " + rt.getLong(4) + "\n";
                    }
                }
                else
                {
                    if(rt.getInt(6) == 0)
                    {
                        result += "ID : "+ rt.getLong(1) + "| Project : " + rt.getString(2) + "| Target value : " + rt.getLong(3) + "| Current value : " + rt.getLong(4) + "| Status: In course.\n";
                    }
                    else
                    {
                        result += "ID : "+ rt.getLong(1) + "| Project : " + rt.getString(2) + "| Target value : " + rt.getLong(3) + "| Current value : " + rt.getLong(4) + "| Status: Finished with success.\n";
                    }
                }
            }
        }
        catch(Exception e)
        {
            try {
                System.out.println("\nException at listCurrentProjects.\n");
                e.printStackTrace();
                connection.rollback();
                return "Some error occurred while listing current projects.";
            } catch (SQLException ex) {
                Logger.getLogger(DataServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if(result.equals(""))
        {
            result += "There are no projects.\n";
        }
        return result;
    }

    public String getNumberProjects() throws RemoteException {

        ResultSet rt = null;
        PreparedStatement ps;
        String result = "";
        try {
            String s = "SELECT id_project FROM project order by id_project";
            rt = connection.createStatement().executeQuery(s);
            connection.commit();
            while(rt.next())
            {
                result += rt.getLong(1) + "\n";
            }
        } catch (SQLException e) {
            try {
                System.out.println("\nException at getNumberProjects.\n");
                e.printStackTrace();
                connection.rollback();
                return "Error occurred while accessing ids of projects.";
            } catch (SQLException ex) {
                Logger.getLogger(DataServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return result;
    }

    public String viewProject(long idProject) throws RemoteException
    {
        ResultSet rt = null;
        String result = "";
        try
        {
            String s = "SELECT name,description,target_value,current_value,limit_date,accepted FROM project where id_project = '" + idProject + "'";
            rt = connection.createStatement().executeQuery(s);
            connection.commit();
            if(rt.next())
            {
                if(rt.getInt(6) == 0)
                {
                    result +=  "Project : " + rt.getString(1) + " | Description: " + rt.getString(2) +  " | Target value : " + rt.getLong(3) + " | Current value : " + rt.getLong(4) +" | Limit date : "+ rt.getString(5) +" | Status: In course.\n";
                }
                else
                {
                    result +=  "Project : " + rt.getString(1) + " | Description: " + rt.getString(2) +  " | Target value : " + rt.getLong(3) + " | Current value : " + rt.getLong(4) +" | Limit date : "+ rt.getString(5) +" | Status: Finished with success.\n";
                }
                //fetch rewards
                result += "\nRewards :\n";
                s = "SELECT id_reward,description,min_value FROM reward where id_project = '" + idProject + "'";
                rt = connection.createStatement().executeQuery(s);
                connection.commit();
                if(rt.next())
                {
                    result += "\nID : " + rt.getLong(1) + " | Description : " + rt.getString(2) + " | Minimum value : " + rt.getDouble(3);
                    while(rt.next())
                    {
                        result += "\nID : " + rt.getLong(1) + " | Description : " + rt.getString(2) + " | Minimum value : " + rt.getDouble(3);
                    }
                }
                else
                {
                    result += "\nThis project has no rewards.";
                }
                //fetch alternatives
                result += "\nAlternatives :\n";
                s = "SELECT id_alternative,description FROM alternative WHERE id_project = '" + idProject + "'";
                rt = connection.createStatement().executeQuery(s);
                connection.commit();
                if(rt.next())
                {
                    result += "\nID : " + rt.getLong(1) + " | Description : " + rt.getString(2);
                    while(rt.next())
                    {
                        result += "\nID : " + rt.getLong(1) + " | Description : " + rt.getString(2);
                    }
                }
                else
                {
                    result += "\nThis project has no alternatives.";
                }
            }
            else
            {
                result += "There is no project with the entered ID.";
            }
        }catch(SQLException  e)
        {
            try {
                System.out.println("\nException at viewProject.\n");
                e.printStackTrace();
                connection.rollback();
                return "Some error occurred while fecthing the project information.";
            } catch (SQLException ex) {
                Logger.getLogger(DataServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return result;
    }

    public long checkAccountBalance(String username) throws RemoteException
{
    ResultSet rt=null;
    String aux="";
    long accountBalance = -1;
    try
    {

        String s="SELECT account_balance FROM USER WHERE name = '" + username + "'";
        rt = connection.createStatement().executeQuery(s);
        connection.commit();
        if(rt.next())
        {
            accountBalance =  rt.getLong(1);
        }
    }
    catch(SQLException  e) {
        try {
            System.out.println("\nException at checkAccountBalance.\n");
            e.printStackTrace();
            connection.rollback();
            return -1;
        } catch (SQLException ex) {
            Logger.getLogger(DataServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    return accountBalance;
}
    public long checkAccountBalance(String username,int tumblrUser) throws RemoteException
    {
        ResultSet rt=null;
        String aux="";
        long accountBalance = -1;
        try
        {
            String s="SELECT account_balance FROM USER WHERE name = '" + username + "' and is_tumblr_account = '" + tumblrUser +  "'";
            rt = connection.createStatement().executeQuery(s);
            connection.commit();
            if(rt.next())
            {
                accountBalance =  rt.getLong(1);
            }
        }
        catch(SQLException  e) {
            try {
                System.out.println("\nException at checkAccountBalance.\n");
                e.printStackTrace();
                connection.rollback();
                return -1;
            } catch (SQLException ex) {
                Logger.getLogger(DataServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return accountBalance;
    }

    public String checkRewards(String username) throws RemoteException
    {
        ResultSet rt = null;
        long idUser = -1;
        String result = "";
        Date today = new Date();
        Date auxDate = null;
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH");
        try
        {
            // check username
            String s="SELECT ID_USER FROM USER WHERE name = '" + username + "'";
            rt = connection.createStatement().executeQuery(s);
            connection.commit();
            if(rt.next())
            {
                idUser = rt.getLong(1);
            }
            //fetch rewards
            s = "select r.id_reward,r.description, p.name, p.limit_date,d.pledge_value from reward r,project p, donation d " +
                    "where r.id_project = p.id_project and d.id_reward = r.id_reward and d.id_user = '" + idUser + "'";
            rt = connection.createStatement().executeQuery(s);
            connection.commit();
            if(rt.next())
            {
                if(rt.getLong(1) != 0)
                {
                    auxDate = formatter.parse(rt.getString(4));
                    if(auxDate.before(today))
                    {
                        result += "\nReward ID : " + rt.getLong(1) + "| Description : " + rt.getString(2) + " | Project : " + rt.getString(3) + "| Pledge value : " + rt.getFloat(5) + "| Status: Finished with success.";
                    }
                    else
                    {
                        result += "\nReward ID : " + rt.getLong(1) + " | Description : " + rt.getString(2) + " | Project : " + rt.getString(3) + " | Pledge value : " + rt.getFloat(5) + " | Status: In course.";
                    }
                }
                while(rt.next())
                {
                    if(rt.getLong(1) == 0)
                    {
                        continue;
                    }
                    auxDate = formatter.parse(rt.getString(4));
                    if(auxDate.before(today))
                    {
                        result += "\nReward ID : " + rt.getLong(1) + " | Description : " + rt.getString(2) + " | Project : " + rt.getString(3) + " | Pledge value : " + rt.getFloat(5) + " | Status: Finished with success.";
                    }
                    else
                    {
                        result += "\nReward ID : " + rt.getLong(1) + " | Description : " + rt.getString(2) + " | Project : " + rt.getString(3) + " | Pledge value : " + rt.getFloat(5) + " | Status: In course.";
                    }
                }
            }
            else
            {
                result += "\nYou have no rewards.";
            }

        }
        catch(Exception e)
        {
            try {
                System.out.println("\nException at checkRewards.\n");
                e.printStackTrace();
                connection.rollback();
                return "Some error occurred while listing your rewards.";
            } catch (SQLException ex) {
                Logger.getLogger(DataServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return result;
    }


    public String checkRewards(String username,int tumblrUser) throws RemoteException
    {
        ResultSet rt = null;
        long idUser = -1;
        String result = "";
        Date today = new Date();
        Date auxDate = null;
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH");
        try
        {
            // check username
            String s="SELECT id_user FROM USER WHERE name = '" + username + "' and is_tumblr_account = '" + tumblrUser +  "'";
            rt = connection.createStatement().executeQuery(s);
            connection.commit();
            if(rt.next())
            {
                idUser = rt.getLong(1);
            }
            //fetch rewards
            s = "select r.id_reward,r.description, p.name, p.limit_date,d.pledge_value from reward r,project p, donation d " +
                    "where r.id_project = p.id_project and d.id_reward = r.id_reward and d.id_user = '" + idUser + "'";
            rt = connection.createStatement().executeQuery(s);
            connection.commit();
            if(rt.next())
            {
                if(rt.getLong(1) != 0)
                {
                    auxDate = formatter.parse(rt.getString(4));
                    if(auxDate.before(today))
                    {
                        result += "\nReward ID : " + rt.getLong(1) + "| Description : " + rt.getString(2) + " | Project : " + rt.getString(3) + "| Pledge value : " + rt.getFloat(5) + "| Status: Finished with success.";
                    }
                    else
                    {
                        result += "\nReward ID : " + rt.getLong(1) + " | Description : " + rt.getString(2) + " | Project : " + rt.getString(3) + " | Pledge value : " + rt.getFloat(5) + " | Status: In course.";
                    }
                }
                while(rt.next())
                {
                    if(rt.getLong(1) == 0)
                    {
                        continue;
                    }
                    auxDate = formatter.parse(rt.getString(4));
                    if(auxDate.before(today))
                    {
                        result += "\nReward ID : " + rt.getLong(1) + " | Description : " + rt.getString(2) + " | Project : " + rt.getString(3) + " | Pledge value : " + rt.getFloat(5) + " | Status: Finished with success.";
                    }
                    else
                    {
                        result += "\nReward ID : " + rt.getLong(1) + " | Description : " + rt.getString(2) + " | Project : " + rt.getString(3) + " | Pledge value : " + rt.getFloat(5) + " | Status: In course.";
                    }
                }
            }
            else
            {
                result += "\nYou have no rewards.";
            }

        }
        catch(Exception e)
        {
            try {
                System.out.println("\nException at checkRewards.\n");
                e.printStackTrace();
                connection.rollback();
                return "Some error occurred while listing your rewards.";
            } catch (SQLException ex) {
                Logger.getLogger(DataServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return result;
    }


    public boolean addProject(String username, String name, String description, String limitDate, long targetValue, String enterprise, ArrayList<Reward> rewards, ArrayList<Alternative> alternatives) throws RemoteException
    {
        PreparedStatement ps;
        ResultSet rt = null;
        long idUser = -1;
        long idProject = -1;
        try
        {   //check administrator
            String s="SELECT ID_USER FROM USER WHERE name = '" + username + "'";
            rt = connection.createStatement().executeQuery(s);
            if(rt.next())
            {
                idUser =  rt.getLong(1);
            }
            //check project with same name
            s="SELECT ID_PROJECT FROM PROJECT WHERE name = '" + name + "'";
            rt = connection.createStatement().executeQuery(s);
            if(rt.next())
            {
                System.out.println("Already exists a project with the same name");
                return false;
            }
            ps = connection.prepareStatement("INSERT INTO PROJECT(name,description,limit_date,target_value,enterprise,id_user) VALUES(?,?,?,?,?,?)");
            ps.setString(1,name);
            ps.setString(2,description);
            ps.setString(3,limitDate);
            ps.setLong(4, targetValue);
            ps.setString(5,enterprise);
            ps.setLong(6,idUser);
            ps.execute();
            //fetch project id
            s="SELECT ID_project FROM project WHERE name = '" + name + "'";
            rt = connection.createStatement().executeQuery(s);
            if(rt.next())
            {
                idProject =  rt.getLong(1);
            }
            //insert rewards
            for(int i =0;i<rewards.size();i++)
            {
                ps = connection.prepareStatement("INSERT INTO REWARD(description,min_value,id_project) VALUES(?,?,?)");
                ps.setString(1,rewards.get(i).getDescription());
                ps.setDouble(2, rewards.get(i).getMinValue());
                ps.setLong(3, idProject);
                ps.execute();
            }
            //insert alternatives
            for(int i =0;i<alternatives.size();i++)
            {
                ps = connection.prepareStatement("INSERT INTO ALTERNATIVE(description,divisor,id_project) VALUES(?,?,?)");
                ps.setString(1, alternatives.get(i).getDescription());
                ps.setDouble(2, alternatives.get(i).getDivisor());
                ps.setLong(3, idProject);
                ps.execute();

            }
            connection.commit();

        }catch(SQLException e){
            try {
                System.out.println("\nException at addProject.\n");
                e.printStackTrace();
                connection.rollback();
                return false;
            } catch (SQLException ex) {
                Logger.getLogger(DataServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return true;
    }



    public String addProject2(String username,int tumblrUser,String name, String description, String limitDate, long targetValue, String enterprise, ArrayList<Reward> rewards, ArrayList<Alternative> alternatives) throws RemoteException
    {
        PreparedStatement ps;
        ResultSet rt = null;
        long idUser = -1;
        long idProject = -1;
        try
        {   //check administrator
            String s="SELECT id_user FROM USER WHERE name = '" + username + "' and is_tumblr_account = '" + tumblrUser +  "'";
            rt = connection.createStatement().executeQuery(s);
            if(rt.next())
            {
                idUser =  rt.getLong(1);
            }
            //check project with same name
            s="SELECT ID_PROJECT FROM PROJECT WHERE name = '" + name + "'";
            rt = connection.createStatement().executeQuery(s);
            if(rt.next())
            {
                return "Already exists a project with the same name";
            }
            ps = connection.prepareStatement("INSERT INTO PROJECT(name,description,limit_date,target_value,enterprise,id_user) VALUES(?,?,?,?,?,?)");
            ps.setString(1,name);
            ps.setString(2,description);
            ps.setString(3,limitDate);
            ps.setLong(4, targetValue);
            ps.setString(5,enterprise);
            ps.setLong(6,idUser);
            ps.execute();
            //fetch project id
            s="SELECT ID_project FROM project WHERE name = '" + name + "'";
            rt = connection.createStatement().executeQuery(s);
            if(rt.next())
            {
                idProject =  rt.getLong(1);
            }
            //insert rewards
            for(int i =0;i<rewards.size();i++)
            {
                if(rewards.get(i).getMinValue() <0)
                {
                    connection.rollback();
                    return "Invalid minimum pledge value";
                }
                ps = connection.prepareStatement("INSERT INTO REWARD(description,min_value,id_project) VALUES(?,?,?)");
                ps.setString(1,rewards.get(i).getDescription());
                ps.setDouble(2, rewards.get(i).getMinValue());
                ps.setLong(3, idProject);
                ps.execute();
            }
            //insert alternatives
            for(int i =0;i<alternatives.size();i++)
            {
                if(alternatives.get(i).getDivisor() <0)
                {
                    connection.rollback();
                    return "Invalid divisor";
                }
                ps = connection.prepareStatement("INSERT INTO ALTERNATIVE(description,divisor,id_project) VALUES(?,?,?)");
                ps.setString(1, alternatives.get(i).getDescription());
                ps.setDouble(2, alternatives.get(i).getDivisor());
                ps.setLong(3, idProject);
                ps.execute();

            }
            connection.commit();

        }catch(SQLException e){
            try {
                System.out.println("\nException at addProject.\n");
                e.printStackTrace();
                connection.rollback();
                return "Error creating project";
            } catch (SQLException ex) {
                Logger.getLogger(DataServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return "Project created with success";
    }



    public String checkProject(String name) throws RemoteException
    {
        ResultSet rt = null;
        try
        {
            // check username
            String s="SELECT ID_Project FROM PROJECT WHERE name = '" + name + "'";
            rt = connection.createStatement().executeQuery(s);
            connection.commit();
            if(rt.next())
            {
                return "Already exists one project with that name.";
            }
        }
        catch(SQLException e)
        {
            try {
                System.out.println("\nException at checkProject.\n");
                e.printStackTrace();
                connection.rollback();
                return "Some error occurred during the creation of the project.";
            } catch (SQLException ex) {
                Logger.getLogger(DataServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    public String contributeToProject(long idProject,String username,float pledgeValue,long alternativeChoosen) throws RemoteException
    {
        ResultSet rt = null;
        PreparedStatement ps;
        long idUser = -1;
        long idReward = 0;
        long accountBalance = 0;
        try
        {
            if(pledgeValue < 1)
            {
                return "Pledge value invalid.";
            }
            //verify if project is open or exists
            String s="SELECT accepted FROM project WHERE id_project = '" + idProject + "'";
            rt = connection.createStatement().executeQuery(s);
            if(rt.next())
            {
                if(rt.getInt(1) ==1)
                {
                    return "You cannot contribute to this project anymore.";
                }
            }
            else
            {
                return "There is no project with the id entered.";
            }
            //verify if the alternative choosen is valid
            if(alternativeChoosen != 0) {
                s = "SELECT id_alternative FROM alternative WHERE id_project = '" + idProject + " 'and id_alternative = '" + alternativeChoosen + "'";
                rt = connection.createStatement().executeQuery(s);
                if (!rt.next()) {
                    return "There is no alternative for this project with the entered ID.";
                }
            }
            // fetch id_user
            s = "SELECT id_user,account_balance FROM USER WHERE name = '" + username + "'";
            rt = connection.createStatement().executeQuery(s);
            connection.commit();
            if (rt.next())
            {
                idUser = rt.getLong(1);
                accountBalance = rt.getLong(2);
                if(accountBalance - pledgeValue <0)
                {
                    return "You do not have enough money to make this donation.";
                }
            }
            //select the corresponding reward
            s = "SELECT id_reward,min_value FROM reward where id_project = '" + idProject + "'";
            rt = connection.createStatement().executeQuery(s);
            connection.commit();
            while(rt.next())
            {
                if(pledgeValue >= rt.getDouble(2))
                {
                    idReward = rt.getLong(1);
                }
            }
            //insert the new donation
            ps = connection.prepareStatement("INSERT INTO DONATION(pledge_value,id_user,id_reward,id_alternative,id_project) VALUES(?,?,?,?,?)");
            ps.setFloat(1, pledgeValue);
            ps.setLong(2, idUser);
            ps.setLong(3, idReward);
            ps.setLong(4,alternativeChoosen);
            ps.setLong(5,idProject);
            ps.execute();
            //update account balance
            s = "UPDATE user SET account_balance = account_balance - ? WHERE id_user = ?";
            ps = connection.prepareStatement(s);
            ps.setFloat(1, pledgeValue);
            ps.setLong(2,idUser);
            ps.executeUpdate();
            //update project current_value
            s = "UPDATE project SET current_value = current_value + ? WHERE id_project = ?";
            ps = connection.prepareStatement(s);
            ps.setFloat(1, pledgeValue);
            ps.setLong(2,idProject);
            ps.executeUpdate();
            //add to project_has_user
            ps = connection.prepareStatement("INSERT INTO PROJECT_HAS_USER(id_project,id_user) VALUES(?,?)");
            ps.setLong(1, idProject);
            ps.setLong(2, idUser);
            ps.execute();
            //increment votes of the alternative choosen
            s = "UPDATE ALTERNATIVE SET n_votes = n_votes+1 WHERE id_alternative = ?";
            ps = connection.prepareStatement(s);
            ps.setLong(1, alternativeChoosen);
            ps.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            try {
                System.out.println("\nException at contributeToProject.\n");
                e.printStackTrace();
                connection.rollback();
                return "Some error occurred during the creation of the donation.";
            } catch (SQLException ex) {
                Logger.getLogger(DataServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return "Donation made successfully.";
    }


    public String contributeToProject(long idProject,int tumblrUser,String username,float pledgeValue,long alternativeChoosen) throws RemoteException
    {
        ResultSet rt = null;
        PreparedStatement ps;
        long idUser = -1;
        long idReward = 0;
        long accountBalance = 0;
        try
        {
            if(pledgeValue < 1)
            {
                return "Pledge value invalid.";
            }
            //verify if project is open or exists
            String s="SELECT accepted FROM project WHERE id_project = '" + idProject + "'";
            rt = connection.createStatement().executeQuery(s);
            if(rt.next())
            {
                if(rt.getInt(1) ==1)
                {
                    return "You cannot contribute to this project anymore.";
                }
            }
            else
            {
                return "There is no project with the id entered.";
            }
            //verify if the alternative choosen is valid
            if(alternativeChoosen != 0) {
                s = "SELECT id_alternative FROM alternative WHERE id_project = '" + idProject + " 'and id_alternative = '" + alternativeChoosen + "'";
                rt = connection.createStatement().executeQuery(s);
                if (!rt.next()) {
                    return "There is no alternative for this project with the entered ID.";
                }
            }
            // fetch id_user
            s="SELECT id_user FROM USER WHERE name = '" + username + "' and is_tumblr_account = '" + tumblrUser +  "'";
            rt = connection.createStatement().executeQuery(s);
            connection.commit();
            if (rt.next())
            {
                idUser = rt.getLong(1);
                accountBalance = rt.getLong(2);
                if(accountBalance - pledgeValue <0)
                {
                    return "You do not have enough money to make this donation.";
                }
            }
            //select the corresponding reward
            s = "SELECT id_reward,min_value FROM reward where id_project = '" + idProject + "'";
            rt = connection.createStatement().executeQuery(s);
            connection.commit();
            while(rt.next())
            {
                if(pledgeValue >= rt.getDouble(2))
                {
                    idReward = rt.getLong(1);
                }
            }
            //insert the new donation
            ps = connection.prepareStatement("INSERT INTO DONATION(pledge_value,id_user,id_reward,id_alternative,id_project) VALUES(?,?,?,?,?)");
            ps.setFloat(1, pledgeValue);
            ps.setLong(2, idUser);
            ps.setLong(3, idReward);
            ps.setLong(4,alternativeChoosen);
            ps.setLong(5,idProject);
            ps.execute();
            //update account balance
            s = "UPDATE user SET account_balance = account_balance - ? WHERE id_user = ?";
            ps = connection.prepareStatement(s);
            ps.setFloat(1, pledgeValue);
            ps.setLong(2,idUser);
            ps.executeUpdate();
            //update project current_value
            s = "UPDATE project SET current_value = current_value + ? WHERE id_project = ?";
            ps = connection.prepareStatement(s);
            ps.setFloat(1, pledgeValue);
            ps.setLong(2,idProject);
            ps.executeUpdate();
            //add to project_has_user
            ps = connection.prepareStatement("INSERT INTO PROJECT_HAS_USER(id_project,id_user) VALUES(?,?)");
            ps.setLong(1, idProject);
            ps.setLong(2, idUser);
            ps.execute();
            //increment votes of the alternative choosen
            s = "UPDATE ALTERNATIVE SET n_votes = n_votes+1 WHERE id_alternative = ?";
            ps = connection.prepareStatement(s);
            ps.setLong(1, alternativeChoosen);
            ps.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            try {
                System.out.println("\nException at contributeToProject.\n");
                e.printStackTrace();
                connection.rollback();
                return "Some error occurred during the creation of the donation.";
            } catch (SQLException ex) {
                Logger.getLogger(DataServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return "Donation made successfully.";
    }




    public String showCommentsProject(long idProject,int mode) throws RemoteException       // mode --> 0 to user, 1 to admin
    {
        ResultSet rt = null;
        String result = "";
        ResultSet rt2;
        try
        {
            if(mode == 0)
            {
                // fetch id_user
                String s = "SELECT id_message,text FROM MESSAGE WHERE id_project = '" + idProject + "'";
                rt = connection.createStatement().executeQuery(s);
                connection.commit();
                while (rt.next())
                {
                    result += "\n" + rt.getString(2) + "\n";
                    s = "SELECT id_reply,text FROM REPLY WHERE id_message = '" + rt.getLong(1) + "'order by id_reply";
                    rt2 = connection.createStatement().executeQuery(s);
                    while(rt2.next())
                    {
                        result += "\n" + rt2.getString(2) + "\n";
                    }
                }
                result += "\n\nType a message :";
            }
            else
            {
                // fetch id_user
                String s = "SELECT id_message,text FROM MESSAGE WHERE id_project = '" + idProject + "'";
                rt = connection.createStatement().executeQuery(s);
                connection.commit();
                while (rt.next())
                {
                    result += "\nMessage ID  : "+ rt.getLong(1) +" Message : " + rt.getString(2) + "\n";
                    s = "SELECT id_reply,text FROM REPLY WHERE id_message = '" + rt.getLong(1) + "'order by id_reply";
                    rt2 = connection.createStatement().executeQuery(s);
                    while(rt2.next())
                    {
                        result += "\n" + rt2.getString(2) + "\n";
                    }
                }
                result += "\n\nReply to message with the ID :";
            }
        }catch (Exception e) {
            try {
                System.out.println("\nException at showCommentsProject.\n");
                e.printStackTrace();
                connection.rollback();
                return "Some error occurred listing previous messages.";
            } catch (SQLException ex) {
                Logger.getLogger(DataServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return result;
    }


    public ArrayList<String> showCommentsProject2(long idProject,int mode) throws RemoteException       // mode --> 0 to user, 1 to admin
    {
        ResultSet rt = null;
        String result = "";
        ResultSet rt2;
        String idMessage,username,comment,date;
        String splits[],splits2[];
        JSONObject obj = new JSONObject();
        ArrayList<String> comments = new ArrayList<>();
        try
        {
            if(mode == 0)
            {
                // fetch id_user
                String s = "SELECT id_message,text FROM MESSAGE WHERE id_project = '" + idProject + "'";
                rt = connection.createStatement().executeQuery(s);
                connection.commit();
                while (rt.next())
                {
                    ///////////////////////////////////////////
                    idMessage = String.valueOf(rt.getLong(1));
                    splits = rt.getString(2).split(":",2);
                    username = splits[0];
                    splits2 = splits[1].split("\n\t\t");
                    comment = splits2[0];
                    date = splits2[1];
                    obj = new JSONObject();
                    obj.put("idMessage", idMessage);
                    obj.put("from", username);
                    obj.put("text", comment);
                    obj.put("date", date);
                    comments.add(obj.toString());
                    ///////////////////////////////////////////
                    s = "SELECT id_reply,text FROM REPLY WHERE id_message = '" + rt.getLong(1) + "'order by id_reply";
                    rt2 = connection.createStatement().executeQuery(s);
                    while(rt2.next())
                    {
                        idMessage = String.valueOf(rt2.getLong(1));
                        splits = rt2.getString(2).split(":",2);
                        username = splits[0];
                        splits2 = splits[1].split("\n\t\t");
                        comment = splits2[0];
                        date = splits2[1];
                        obj = new JSONObject();
                        obj.put("idReply", idMessage);
                        obj.put("from", username);
                        obj.put("text", comment);
                        obj.put("date", date);
                        comments.add(obj.toString());
                    }
                }
            }
            else
            {
                // fetch id_user
                String s = "SELECT id_message,text FROM MESSAGE WHERE id_project = '" + idProject + "'";
                rt = connection.createStatement().executeQuery(s);
                connection.commit();
                while (rt.next())
                {
                    idMessage = String.valueOf(rt.getLong(1));
                    splits = rt.getString(2).split(":",2);
                    username = splits[0];
                    splits2 = splits[1].split("\n\t\t");
                    comment = splits2[0];
                    date = splits2[1];
                    obj = new JSONObject();
                    obj.put("idMessage", idMessage);
                    obj.put("from", username);
                    obj.put("text", comment);
                    obj.put("date", date);
                    comments.add(obj.toString());
                    s = "SELECT id_reply,text FROM REPLY WHERE id_message = '" + rt.getLong(1) + "'order by id_reply";
                    rt2 = connection.createStatement().executeQuery(s);
                    while(rt2.next())
                    {
                        result += "\n" + rt2.getString(2) + "\n";
                    }
                }
            }
        }catch (Exception e) {
            try {
                System.out.println("\nException at showCommentsProject2.\n");
                e.printStackTrace();
                connection.rollback();
                comments.add("Some error occurred listing previous messages.");
            } catch (SQLException ex) {
                Logger.getLogger(DataServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return comments;
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    public String commentProject(long idProject,String username,String comment) throws RemoteException
    {
        ResultSet rt = null;
        PreparedStatement ps;
        long idUser = -1;
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date now = new Date();
        try {
            // fetch id_user
            String s = "SELECT ID_USER FROM USER WHERE name = '" + username + "'";
            rt = connection.createStatement().executeQuery(s);
            connection.commit();
            if (rt.next())
            {
                idUser = rt.getLong(1);
            }
            //insert comment
            comment += "\n\t\t" +  formatter.format(now);
            ps = connection.prepareStatement("INSERT INTO MESSAGE(text,id_user,id_project) VALUES(?,?,?)");
            ps.setString(1, comment);
            ps.setLong(2, idUser);
            ps.setLong(3, idProject);
            ps.execute();
            connection.commit();
        }catch (Exception e) {
            try {
                System.out.println("\nException at contributeToProject.\n");
                e.printStackTrace();
                connection.rollback();
                return "Some error occurred sending the message.";
            } catch (SQLException ex) {
                Logger.getLogger(DataServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return "Commented with success";
    }

    public String commentProject(long idProject,int tumblrUser,String username,String comment) throws RemoteException
    {
        ResultSet rt = null;
        PreparedStatement ps;
        long idUser = -1;
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date now = new Date();
        try {
            // fetch id_user
            String s="SELECT id_user FROM USER WHERE name = '" + username + "' and is_tumblr_account = '" + tumblrUser +  "'";
            rt = connection.createStatement().executeQuery(s);
            connection.commit();
            if (rt.next())
            {
                idUser = rt.getLong(1);
            }
            //insert comment
            comment += "\n\t\t" +  formatter.format(now);
            ps = connection.prepareStatement("INSERT INTO MESSAGE(text,id_user,id_project) VALUES(?,?,?)");
            ps.setString(1, comment);
            ps.setLong(2, idUser);
            ps.setLong(3, idProject);
            ps.execute();
            connection.commit();
        }catch (Exception e) {
            try {
                System.out.println("\nException at contributeToProject.\n");
                e.printStackTrace();
                connection.rollback();
                return "Some error occurred sending the message.";
            } catch (SQLException ex) {
                Logger.getLogger(DataServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return "Commented with success";
    }

    public String showAdminProjects(String username) throws RemoteException
{
    ResultSet rt = null;
    long idUser = -1;
    String result = "";
    try {
        // fetch id_user
        String s = "SELECT ID_USER FROM USER WHERE name = '" + username + "'";
        rt = connection.createStatement().executeQuery(s);
        connection.commit();
        if (rt.next())
        {
            idUser = rt.getLong(1);
        }
        //fetch projects
        s = "SELECT id_project,name FROM project WHERE id_user = '" + idUser + "'";
        rt = connection.createStatement().executeQuery(s);
        connection.commit();
        if(rt.next())
        {
            result += "\nID : " + rt.getLong(1) + " | Name: " + rt.getString(2);
            while(rt.next())
            {
                result += "\nID : " + rt.getLong(1) + " | Name: " + rt.getString(2);
            }
        }
        else
        {
            result += "\nYou do not administrate any project.";
        }
    }catch (Exception e) {
        try {
            System.out.println("\nException at showAdminProjects.\n");
            e.printStackTrace();
            connection.rollback();
            return "Some error occurred listing your projects.";
        } catch (SQLException ex) {
            Logger.getLogger(DataServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    return result;
}

    public String showAdminProjects(String username, int tumblrUser) throws RemoteException
    {
        ResultSet rt = null;
        long idUser = -1;
        String result = "";
        try {
            // fetch id_user
            String s="SELECT id_user FROM USER WHERE name = '" + username + "' and is_tumblr_account = '" + tumblrUser +  "'";
            rt = connection.createStatement().executeQuery(s);
            connection.commit();
            if (rt.next())
            {
                idUser = rt.getLong(1);
            }
            //fetch projects
            s = "SELECT id_project,name FROM project WHERE id_user = '" + idUser + "'";
            rt = connection.createStatement().executeQuery(s);
            connection.commit();
            if(rt.next())
            {
                result += "\nID : " + rt.getLong(1) + " | Name: " + rt.getString(2);
                while(rt.next())
                {
                    result += "\nID : " + rt.getLong(1) + " | Name: " + rt.getString(2);
                }
            }
            else
            {
                result += "\nYou do not administrate any project.";
            }
        }catch (Exception e) {
            try {
                System.out.println("\nException at showAdminProjects.\n");
                e.printStackTrace();
                connection.rollback();
                return "Some error occurred listing your projects.";
            } catch (SQLException ex) {
                Logger.getLogger(DataServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return result;
    }

    public String getAdminProjectIds(String username,int tumblrUser) throws RemoteException
    {
        ResultSet rt = null;
        long idUser = -1;
        String result = "";
        try {
            // fetch id_user
            String s="SELECT id_user FROM USER WHERE name = '" + username + "' and is_tumblr_account = '" + tumblrUser +  "'";
            rt = connection.createStatement().executeQuery(s);
            connection.commit();
            if (rt.next())
            {
                idUser = rt.getLong(1);
            }
            //fetch projects
            s = "SELECT id_project FROM project WHERE id_user = '" + idUser + "'";
            rt = connection.createStatement().executeQuery(s);
            connection.commit();
            if(rt.next())
            {
                //dot to facilitate jsp parsing
                result += "\nID : " + rt.getLong(1)+"." ;
                while(rt.next())
                {
                    result += "\nID : " + rt.getLong(1)+".";
                }
            }
            else
            {
                result += "\nYou do not administrate any project.";
            }
        }catch (Exception e) {
            try {
                System.out.println("\nException at showAdminProjects.\n");
                e.printStackTrace();
                connection.rollback();
                return "Some error occurred listing your projects.";
            } catch (SQLException ex) {
                Logger.getLogger(DataServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return result;
    }

    ////////////////////////////////////////////////////////

    public String addReward(long idProject,Reward r,String username) throws RemoteException
    {
        PreparedStatement ps;
        ResultSet rt;
        long idUser = -1, idAdmin = -1;
        try
        {
            // fetch id_user
            String s = "SELECT ID_USER FROM USER WHERE name = '" + username + "'";
            rt = connection.createStatement().executeQuery(s);
            connection.commit();
            if (rt.next())
            {
                idUser = rt.getLong(1);
            }
            //fetch admin
            s = "SELECT id_user FROM project WHERE id_project = '" + idProject + "'";
            rt = connection.createStatement().executeQuery(s);
            connection.commit();
            if (rt.next())
            {
                idAdmin = rt.getLong(1);
            }
            else
            {
                return "There is no project with the entered ID.";
            }
            if(idAdmin == idUser)
            {
                if(r.getMinValue() <0)
                {
                    return "Invalid pledge value";
                }
                ps = connection.prepareStatement("INSERT INTO REWARD(description,min_value,id_project) VALUES(?,?,?)");
                ps.setString(1, r.getDescription());
                ps.setDouble(2, r.getMinValue());
                ps.setLong(3, idProject);
                ps.execute();
                connection.commit();
            }else
            {
                return "You are not the administrator of the project.";
            }
        } catch (SQLException e) {
            try {
                System.out.println("\nException at addReward.\n");
                e.printStackTrace();
                connection.rollback();
                return "Some error occurred while adding reward.";
            } catch (SQLException ex) {
                Logger.getLogger(DataServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return "Reward added successfully.";
    }

    public String addReward(long idProject,Reward r,String username,int tumblrUser) throws RemoteException
    {
        PreparedStatement ps;
        ResultSet rt;
        long idUser = -1, idAdmin = -1;
        try
        {
            // fetch id_user
            String s="SELECT id_user FROM USER WHERE name = '" + username + "' and is_tumblr_account = '" + tumblrUser +  "'";
            rt = connection.createStatement().executeQuery(s);
            connection.commit();
            if (rt.next())
            {
                idUser = rt.getLong(1);
            }
            //fetch admin
            s = "SELECT id_user FROM project WHERE id_project = '" + idProject + "'";
            rt = connection.createStatement().executeQuery(s);
            connection.commit();
            if (rt.next())
            {
                idAdmin = rt.getLong(1);
            }
            else
            {
                return "There is no project with the entered ID.";
            }
            if(idAdmin == idUser)
            {
                if(r.getMinValue() <0)
                {
                    return "Invalid pledge value";
                }
                ps = connection.prepareStatement("INSERT INTO REWARD(description,min_value,id_project) VALUES(?,?,?)");
                ps.setString(1, r.getDescription());
                ps.setDouble(2, r.getMinValue());
                ps.setLong(3, idProject);
                ps.execute();
                connection.commit();
            }else
            {
                return "You are not the administrator of the project.";
            }
        } catch (SQLException e) {
            try {
                System.out.println("\nException at addReward.\n");
                e.printStackTrace();
                connection.rollback();
                return "Some error occurred while adding reward.";
            } catch (SQLException ex) {
                Logger.getLogger(DataServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return "Reward added successfully.";
    }

    public String listRewardsProject(long idProject) throws RemoteException
    {
        ResultSet rt = null;
        String result = "";
        try {
            // fetch id_rewards
            String s = "SELECT id_reward,description FROM reward WHERE id_project = '" + idProject + "'";
            rt = connection.createStatement().executeQuery(s);
            connection.commit();
            result += "Rewards of this project: \n";
            if(rt.next())
            {
                result += "\nReward ID: " + rt.getLong(1) + "| Description : " + rt.getString(2);
                while (rt.next())
                {
                    result += "\nReward ID: " + rt.getLong(1) + "| Description : " + rt.getString(2);
                }
            }
            else
            {
                result += "\nThis project has no rewards.";
            }
        }catch (Exception e) {
            try {
                System.out.println("\nException at listRewardsProject.\n");
                e.printStackTrace();
                connection.rollback();
                return "Some error occurred listing the rewards of the choosen project.";
            } catch (SQLException ex) {
                Logger.getLogger(DataServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return result;
    }

    public String getRewardsProjectIds(long idProject) throws RemoteException
    {
        ResultSet rt = null;
        String result = "";
        try {
            // fetch id_rewards
            String s = "SELECT id_reward FROM reward WHERE id_project = '" + idProject + "'";
            rt = connection.createStatement().executeQuery(s);
            connection.commit();
            if(rt.next())
            {
                result += "ID : " + rt.getLong(1) + "\n";
                while (rt.next())
                {
                    result += "ID : " + rt.getLong(1) + "\n";
                }
            }
            else
            {
                result += "This project has no rewards.\n";
            }
        }catch (Exception e) {
            try {
                System.out.println("\nException at getRewardsProjectIds.\n");
                e.printStackTrace();
                connection.rollback();
                return "Some error occurred listing the rewards of the choosen project.";
            } catch (SQLException ex) {
                Logger.getLogger(DataServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return result;
    }

    //////////////////////////////////////////////////////

    public String removeReward(long idProject,long idReward,String username) throws RemoteException
    {
        PreparedStatement ps;
        ResultSet rt;
        long idUser = -1,idAdmin = -1;
        ArrayList<Long> ids = new ArrayList<Long>();
        float pledgeValue;
        long idDonation;
        try
        {
            // fetch id_user
            String s = "SELECT ID_USER FROM USER WHERE name = '" + username + "'";
            rt = connection.createStatement().executeQuery(s);
            connection.commit();
            if (rt.next())
            {
                idUser = rt.getLong(1);
            }
            //fetch admin
            s = "SELECT id_user FROM project WHERE id_project = '" + idProject + "'";
            rt = connection.createStatement().executeQuery(s);
            connection.commit();
            if (rt.next())
            {
                idAdmin = rt.getLong(1);
            }
            else
            {
                return "There is no project with the entered ID.";
            }
            if(idAdmin == idUser)
            {
                // fetch id_rewards
                s = "SELECT id_reward FROM reward WHERE id_project = '" + idProject + "'";
                rt = connection.createStatement().executeQuery(s);
                connection.commit();
                while (rt.next())
                {
                    ids.add(rt.getLong(1));
                }
                if(ids.contains(idReward))
                {
                    ps = connection.prepareStatement("DELETE FROM reward WHERE id_reward = ?");
                    ps.setLong(1, idReward);
                    ps.execute();
                    //fetch donations with reward = idReward
                    s = "SELECT id_donation,pledge_value FROM donation WHERE id_reward = '" + idReward + "'";
                    rt = connection.createStatement().executeQuery(s);
                    while(rt.next())
                    {
                        idDonation = rt.getLong(1);
                        pledgeValue = rt.getFloat(2);
                        s = "SELECT id_reward,min_value FROM reward where id_project = '" + idProject + "'";
                        rt = connection.createStatement().executeQuery(s);
                        while (rt.next())
                        {
                            if (pledgeValue >= rt.getDouble(2))
                            {
                                idReward = rt.getLong(1);
                            }
                            ps = connection.prepareStatement("UPDATE donation SET id_reward = ? where id_donation = ?");
                            ps.setLong(1, idReward);
                            ps.setLong(2, idDonation);
                            ps.execute();
                        }
                    }
                    connection.commit();
                }
                else
                {
                    return "There is no reward for this project with the entered ID.";
                }
            }
            else
            {
                return "You are not the administrator of the project.";
            }
        } catch (SQLException e) {
            try {
                System.out.println("\nException at removeReward.\n");
                e.printStackTrace();
                connection.rollback();
                return "Some error occurred while removing reward.";
            } catch (SQLException ex) {
                Logger.getLogger(DataServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return "Reward removed successfully.";
    }

    public String removeReward(long idProject,long idReward,String username,int tumblrUser) throws RemoteException
    {
        PreparedStatement ps;
        ResultSet rt;
        long idUser = -1,idAdmin = -1;
        ArrayList<Long> ids = new ArrayList<Long>();
        float pledgeValue;
        long idDonation;
        try
        {
            // fetch id_user
            String s="SELECT id_user FROM USER WHERE name = '" + username + "' and is_tumblr_account = '" + tumblrUser +  "'";
            rt = connection.createStatement().executeQuery(s);
            connection.commit();
            if (rt.next())
            {
                idUser = rt.getLong(1);
            }
            //fetch admin
            s = "SELECT id_user FROM project WHERE id_project = '" + idProject + "'";
            rt = connection.createStatement().executeQuery(s);
            connection.commit();
            if (rt.next())
            {
                idAdmin = rt.getLong(1);
            }
            else
            {
                return "There is no project with the entered ID.";
            }
            if(idAdmin == idUser)
            {
                // fetch id_rewards
                s = "SELECT id_reward FROM reward WHERE id_project = '" + idProject + "'";
                rt = connection.createStatement().executeQuery(s);
                connection.commit();
                while (rt.next())
                {
                    ids.add(rt.getLong(1));
                }
                if(ids.contains(idReward))
                {
                    ps = connection.prepareStatement("DELETE FROM reward WHERE id_reward = ?");
                    ps.setLong(1, idReward);
                    ps.execute();
                    //fetch donations with reward = idReward
                    s = "SELECT id_donation,pledge_value FROM donation WHERE id_reward = '" + idReward + "'";
                    rt = connection.createStatement().executeQuery(s);
                    while(rt.next())
                    {
                        idDonation = rt.getLong(1);
                        pledgeValue = rt.getFloat(2);
                        s = "SELECT id_reward,min_value FROM reward where id_project = '" + idProject + "'";
                        rt = connection.createStatement().executeQuery(s);
                        while (rt.next())
                        {
                            if (pledgeValue >= rt.getDouble(2))
                            {
                                idReward = rt.getLong(1);
                            }
                            ps = connection.prepareStatement("UPDATE donation SET id_reward = ? where id_donation = ?");
                            ps.setLong(1, idReward);
                            ps.setLong(2, idDonation);
                            ps.execute();
                        }
                    }
                    connection.commit();
                }
                else
                {
                    return "There is no reward for this project with the entered ID.";
                }
            }
            else
            {
                return "You are not the administrator of the project.";
            }
        } catch (SQLException e) {
            try {
                System.out.println("\nException at removeReward.\n");
                e.printStackTrace();
                connection.rollback();
                return "Some error occurred while removing reward.";
            } catch (SQLException ex) {
                Logger.getLogger(DataServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return "Reward removed successfully.";
    }

    public String cancelProject(long idProject) throws RemoteException
    {
        ResultSet rt = null;
        float refund = 0;
        long idUser = -1;
        PreparedStatement ps;
        try {

            // fetch donation value and its user
            String s = "SELECT pledge_value,id_user from donation where id_project = '" + idProject + "'" ;
            rt = connection.createStatement().executeQuery(s);
            connection.commit();
            while (rt.next())
            {
                refund = rt.getFloat(1);
                idUser = rt.getLong(2);
                //refund user
                s = "UPDATE USER SET account_balance = account_balance + ? WHERE id_user = ?";
                ps = connection.prepareStatement(s);
                ps.setFloat(1, refund);
                ps.setLong(2,idUser);
                ps.executeUpdate();
            }
            //delete donations from project
            s = "SELECT id_reward FROM reward WHERE id_project = '" + idProject + "'";
            rt = connection.createStatement().executeQuery(s);
            while (rt.next())
            {
                s = "DELETE from donation where id_reward = ?";
                ps = connection.prepareStatement(s);
                ps.setLong(1, rt.getLong(1));
                ps.execute();
            }
            //cancel project
            s = "DELETE from project where id_project = ?";
            ps = connection.prepareStatement(s);
            ps.setLong(1, idProject);
            ps.execute();
            connection.commit();

        }catch (Exception e) {
            try {
                System.out.println("\nException at cancelProject.\n");
                e.printStackTrace();
                connection.rollback();
                return "Some error occurred canceling the project.";
            } catch (SQLException ex) {
                Logger.getLogger(DataServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return "Project canceled successfully.";
    }

    public String cancelProject(long idProject,String username) throws RemoteException
    {
        ResultSet rt = null;
        float refund = 0;
        long idUser = -1,idAdmin = -1;
        PreparedStatement ps;
        try
        {
            // fetch id_user
            String s = "SELECT ID_USER FROM USER WHERE name = '" + username + "'";
            rt = connection.createStatement().executeQuery(s);
            connection.commit();
            if (rt.next())
            {
                idUser = rt.getLong(1);
            }
            //fetch admin
            s = "SELECT id_user FROM project WHERE id_project = '" + idProject + "'";
            rt = connection.createStatement().executeQuery(s);
            connection.commit();
            if (rt.next())
            {
                idAdmin = rt.getLong(1);
            }
            else
            {
                return "There is no project with the entered ID.";
            }
            if(idAdmin == idUser)
            {
                // fetch donation value and its user
                s = "SELECT pledge_value,id_user from donation where id_project = '" + idProject + "'" ;
                rt = connection.createStatement().executeQuery(s);
                connection.commit();
                while (rt.next()) {
                    refund = rt.getFloat(1);
                    idUser = rt.getLong(2);
                    //refund user
                    s = "UPDATE USER SET account_balance = account_balance + ? WHERE id_user = ?";
                    ps = connection.prepareStatement(s);
                    ps.setFloat(1, refund);
                    ps.setLong(2, idUser);
                    ps.executeUpdate();
                }
                //delete donations from project
                s = "SELECT id_reward FROM reward WHERE id_project = '" + idProject + "'";
                rt = connection.createStatement().executeQuery(s);
                while (rt.next())
                {
                    s = "DELETE from donation where id_reward = ?";
                    ps = connection.prepareStatement(s);
                    ps.setLong(1, rt.getLong(1));
                    ps.execute();
                    connection.commit();
                }
                //cancel project
                s = "DELETE from project where id_project = ?";
                ps = connection.prepareStatement(s);
                ps.setLong(1, idProject);
                ps.execute();
                connection.commit();
            }
            else
            {
                return "You are not the administrator of the project.";
            }

        }catch (Exception e) {
            try {
                System.out.println("\nException at cancelProject.\n");
                e.printStackTrace();
                connection.rollback();
                return "Some error occurred canceling the project.";
            } catch (SQLException ex) {
                Logger.getLogger(DataServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return "Project canceled successfully.";
    }


    public String cancelProject(long idProject,String username,int tumblrUser) throws RemoteException
    {
        ResultSet rt = null;
        float refund = 0;
        long idUser = -1,idAdmin = -1;
        PreparedStatement ps;
        try
        {
            // fetch id_user
            String s="SELECT id_user FROM USER WHERE name = '" + username + "' and is_tumblr_account = '" + tumblrUser +  "'";
            rt = connection.createStatement().executeQuery(s);
            connection.commit();
            if (rt.next())
            {
                idUser = rt.getLong(1);
            }
            //fetch admin
            s = "SELECT id_user FROM project WHERE id_project = '" + idProject + "'";
            rt = connection.createStatement().executeQuery(s);
            connection.commit();
            if (rt.next())
            {
                idAdmin = rt.getLong(1);
            }
            else
            {
                return "There is no project with the entered ID.";
            }
            if(idAdmin == idUser)
            {
                // fetch donation value and its user
                s = "SELECT pledge_value,id_user from donation where id_project = '" + idProject + "'" ;
                rt = connection.createStatement().executeQuery(s);
                connection.commit();
                while (rt.next()) {
                    refund = rt.getFloat(1);
                    idUser = rt.getLong(2);
                    //refund user
                    s = "UPDATE USER SET account_balance = account_balance + ? WHERE id_user = ?";
                    ps = connection.prepareStatement(s);
                    ps.setFloat(1, refund);
                    ps.setLong(2, idUser);
                    ps.executeUpdate();
                }
                //delete donations from project
                s = "SELECT id_reward FROM reward WHERE id_project = '" + idProject + "'";
                rt = connection.createStatement().executeQuery(s);
                while (rt.next())
                {
                    s = "DELETE from donation where id_reward = ?";
                    ps = connection.prepareStatement(s);
                    ps.setLong(1, rt.getLong(1));
                    ps.execute();
                    connection.commit();
                }
                //cancel project
                s = "DELETE from project where id_project = ?";
                ps = connection.prepareStatement(s);
                ps.setLong(1, idProject);
                ps.execute();
                connection.commit();
            }
            else
            {
                return "You are not the administrator of the project.";
            }

        }catch (Exception e) {
            try {
                System.out.println("\nException at cancelProject.\n");
                e.printStackTrace();
                connection.rollback();
                return "Some error occurred canceling the project.";
            } catch (SQLException ex) {
                Logger.getLogger(DataServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return "Project canceled successfully.";
    }

    ////////////////////////////////////////////////////////////////////////

    public String replyMessage(long idProject,String username ,long idMessage, String reply) throws RemoteException
    {
        ResultSet rt = null;
        PreparedStatement ps;
        long idUser = -1,idAdmin = -1;
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date now = new Date();
        try
        {
            // fetch id_user
            String s = "SELECT ID_USER FROM USER WHERE name = '" + username + "'";
            rt = connection.createStatement().executeQuery(s);
            connection.commit();
            if (rt.next())
            {
                idUser = rt.getLong(1);
            }
            //fetch admin
            s = "SELECT id_user FROM project WHERE id_project = '" + idProject + "'";
            rt = connection.createStatement().executeQuery(s);
            connection.commit();
            if (rt.next())
            {
                idAdmin = rt.getLong(1);
            }
            else
            {
                return "There is no project with the entered ID.";
            }
            if(idAdmin == idUser)
            {
                //check if project has messages to reply
                s = "SELECT id_message FROM message WHERE id_project = '" + idProject + "'";
                rt = connection.createStatement().executeQuery(s);
                connection.commit();
                if (!rt.next()) {
                    return "This project have no messages.";
                }
                //validate idMessage
                s = "SELECT id_message FROM message WHERE id_project = '" + idProject + "' and id_message = '" + idMessage + "'";
                rt = connection.createStatement().executeQuery(s);
                connection.commit();
                if (!rt.next()) {
                    return "There is no message por this project with the entered ID.";
                }
                // fetch id_user
                s = "SELECT ID_USER FROM USER WHERE name = '" + username + "'";
                rt = connection.createStatement().executeQuery(s);
                connection.commit();
                if (rt.next()) {
                    idUser = rt.getLong(1);
                }
                //insert reply
                reply += "\n\t\t" + formatter.format(now);
                String text = "Admin's reply : " + reply;
                ps = connection.prepareStatement("INSERT INTO REPLY(text,id_user,id_project,id_message) VALUES(?,?,?,?)");
                ps.setString(1, text);
                ps.setLong(2, idUser);
                ps.setLong(3, idProject);
                ps.setLong(4, idMessage);
                ps.execute();
                connection.commit();
            }
            else
            {
                return "Only the administrator of the project can reply to messages from supporters.";
            }
        }catch (Exception e) {
            try {
                System.out.println("\nException at replyMessage.\n");
                e.printStackTrace();
                connection.rollback();
                return "Some error occurred sending the message.";
            } catch (SQLException ex) {
                Logger.getLogger(DataServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return "Replied with success";
    }


    public String replyMessage(long idProject,String username ,long idMessage, String reply,int tumblrUser) throws RemoteException
    {
        ResultSet rt = null;
        PreparedStatement ps;
        long idUser = -1,idAdmin = -1;
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date now = new Date();
        try
        {
            // fetch id_user
            String s="SELECT id_user FROM USER WHERE name = '" + username + "' and is_tumblr_account = '" + tumblrUser +  "'";
            rt = connection.createStatement().executeQuery(s);
            connection.commit();
            if (rt.next())
            {
                idUser = rt.getLong(1);
            }
            //fetch admin
            s = "SELECT id_user FROM project WHERE id_project = '" + idProject + "'";
            rt = connection.createStatement().executeQuery(s);
            connection.commit();
            if (rt.next())
            {
                idAdmin = rt.getLong(1);
            }
            else
            {
                return "There is no project with the entered ID.";
            }
            if(idAdmin == idUser)
            {
                //check if project has messages to reply
                s = "SELECT id_message FROM message WHERE id_project = '" + idProject + "'";
                rt = connection.createStatement().executeQuery(s);
                connection.commit();
                if (!rt.next()) {
                    return "This project have no messages.";
                }
                //validate idMessage
                s = "SELECT id_message FROM message WHERE id_project = '" + idProject + "' and id_message = '" + idMessage + "'";
                rt = connection.createStatement().executeQuery(s);
                connection.commit();
                if (!rt.next()) {
                    return "There is no message por this project with the entered ID.";
                }
                // fetch id_user
                s = "SELECT ID_USER FROM USER WHERE name = '" + username + "'";
                rt = connection.createStatement().executeQuery(s);
                connection.commit();
                if (rt.next()) {
                    idUser = rt.getLong(1);
                }
                //insert reply
                reply += "\n\t\t" + formatter.format(now);
                String text = "Admin's reply : " + reply;
                ps = connection.prepareStatement("INSERT INTO REPLY(text,id_user,id_project,id_message) VALUES(?,?,?,?)");
                ps.setString(1, text);
                ps.setLong(2, idUser);
                ps.setLong(3, idProject);
                ps.setLong(4, idMessage);
                ps.execute();
                connection.commit();
            }
            else
            {
                return "Only the administrator of the project can reply to messages from supporters.";
            }
        }catch (Exception e) {
            try {
                System.out.println("\nException at replyMessage.\n");
                e.printStackTrace();
                connection.rollback();
                return "Some error occurred sending the message.";
            } catch (SQLException ex) {
                Logger.getLogger(DataServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return "Replied with success";
    }


    public boolean checkProjectsDate() throws RemoteException
    {
        ResultSet rt = null;
        PreparedStatement ps;
        String result = "";
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH");
        Date today = new Date();
        Date auxDate = null;
        try
        {
            String s="SELECT id_project,target_value,current_value,limit_date,id_user FROM project WHERE accepted = 0";
            rt = connection.createStatement().executeQuery(s);
            connection.commit();
            while(rt.next())
            {
                auxDate = formatter.parse(rt.getString(4));
                if(auxDate.before(today))
                {
                    if(rt.getLong(2) > rt.getLong(3))   //target_value > current_value
                    {
                        cancelProject(rt.getLong(1));
                    }
                    else
                    {
                        //deposit donations in the admin's project account
                        s = "UPDATE USER SET account_balance = account_balance + ? WHERE id_user = ?";
                        ps = connection.prepareStatement(s);
                        ps.setFloat(1, rt.getLong(3));
                        ps.setLong(2,rt.getLong(5));
                        ps.executeUpdate();
                        //update accepted to 1 in project
                        s = "UPDATE project SET accepted = 1 WHERE id_project = ?";
                        ps = connection.prepareStatement(s);
                        ps.setLong(1, rt.getLong(1));
                        ps.executeUpdate();
                        connection.commit();
                    }
                }
            }
        }catch (Exception e) {
            try {
                System.out.println("\nException at checkProjectsDate.\n");
                e.printStackTrace();
                connection.rollback();
                return false;
            } catch (SQLException ex) {
                Logger.getLogger(DataServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return true;
    }

    public String getAlternativeIdsProject(long idProject) throws RemoteException
    {
        ResultSet rt = null;
        PreparedStatement ps;
        String result = "";
        try {
            String s = "SELECT id_alternative FROM alternative WHERE id_project = '" + idProject + "'";
            rt = connection.createStatement().executeQuery(s);
            connection.commit();
            while(rt.next())
            {
                result += String.valueOf(rt.getLong(1)) + "\n";
            }
        } catch (SQLException e) {
            try {
                System.out.println("\nException at getAlternativeIdsProject.\n");
                e.printStackTrace();
                connection.rollback();
                return "Error occurred while accessing alternatives of this project.";
            } catch (SQLException ex) {
                Logger.getLogger(DataServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if(result.equals(""))
        {
            result += "There are no alternatives for this project.";
        }
        return result;
    }


    public String getMessagesProjectIds(long idProject) throws RemoteException
    {
        ResultSet rt = null;
        PreparedStatement ps;
        String result = "";
        try {
            String s = "SELECT id_message FROM message WHERE id_project = '" + idProject + "'";
            rt = connection.createStatement().executeQuery(s);
            connection.commit();
            while(rt.next())
            {
                result += String.valueOf(rt.getLong(1)) + "\n";
            }
        } catch (SQLException e) {
            try {
                System.out.println("\nException at getMessagesProjectIds.\n");
                e.printStackTrace();
                connection.rollback();
                return "Error occurred while accessing messages of this project.";
            } catch (SQLException ex) {
                Logger.getLogger(DataServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return result;
    }


    public String getProjectAdmin(long idProject) throws RemoteException
    {
        ResultSet rt = null;
        PreparedStatement ps;
        String result = "";
        long idUser=0;
        try {
            String s = "SELECT id_user FROM project WHERE id_project = '" + idProject + "'";
            rt = connection.createStatement().executeQuery(s);
            connection.commit();
            if(rt.next())
            {
                idUser =  rt.getLong(1);
            }
            s = "SELECT name FROM user WHERE id_user = '" + idUser + "'";
            rt = connection.createStatement().executeQuery(s);
            connection.commit();
            if(rt.next())
            {
                result =  rt.getString(1);
            }

        } catch (SQLException e) {
            try {
                System.out.println("\nException at getMessagesProjectIds.\n");
                e.printStackTrace();
                connection.rollback();
                return "Error occurred while accessing messages of this project.";
            } catch (SQLException ex) {
                Logger.getLogger(DataServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return result;
    }
    

    public long getCurrentValue(long idProject) throws RemoteException
    {
        ResultSet rt = null;
        PreparedStatement ps;
        long currentValue =-1;
        try {
            String s = "SELECT current_value FROM project WHERE id_project = '" + idProject + "'";
            rt = connection.createStatement().executeQuery(s);
            connection.commit();
            if(rt.next())
            {
                currentValue =  rt.getLong(1);
            }

        } catch (SQLException e) {
            try {
                System.out.println("\nException at getCurrentValue.\n");
                e.printStackTrace();
                connection.rollback();
                return currentValue;
            } catch (SQLException ex) {
                Logger.getLogger(DataServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return currentValue;
    }


    public ArrayList<String> getAccessToken(String username,int tumblrUser) throws RemoteException
    {
        ResultSet rt = null;
        PreparedStatement ps;
        ArrayList<String> tokens = new ArrayList<>();
        try {
            String s="SELECT secret_token,user_token FROM USER WHERE name = '" + username + "' and is_tumblr_account = '" + tumblrUser +  "'";
            rt = connection.createStatement().executeQuery(s);
            connection.commit();
            if(rt.next())
            {
                tokens.add(rt.getString(1));
                tokens.add(rt.getString(2));
            }

        } catch (SQLException e) {
            try {
                System.out.println("\nException at getCurrentValue.\n");
                e.printStackTrace();
                connection.rollback();
                return tokens;
            } catch (SQLException ex) {
                Logger.getLogger(DataServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return tokens;
    }

    public String associateAccount(String username,int tumblrUser,String tumblrUsername,String secret, String userToken) throws RemoteException
    {
        ResultSet rt = null;
        PreparedStatement ps;
        long idUser=-1;
        try {
            // fetch id_user
            String s="SELECT id_user FROM USER WHERE name = '" + username + "' and is_tumblr_account = '" + tumblrUser +  "'";
            rt = connection.createStatement().executeQuery(s);
            connection.commit();
            if (rt.next())
            {
                idUser = rt.getLong(1);
            }
            else
            {
                return "Error occurred associating your account";
            }

            /*//check if it is already associated
            // fetch id_user
            s="SELECT tumblr_username FROM USER WHERE name = '" + username + "' and is_tumblr_account = '" + tumblrUser +  "'";
            rt = connection.createStatement().executeQuery(s);
            connection.commit();
            if (rt.next())
            {
                if(rt.getString(1) != null)
                {
                    return "You have this account associated already";
                }
            }*/
            s = "UPDATE user SET tumblr_username = ?, secret_token = ?, user_token = ?  WHERE id_user = ?";
            ps = connection.prepareStatement(s);
            ps.setString(1, tumblrUsername);
            ps.setString(2,secret);
            ps.setString(3,userToken);
            ps.setLong(4,idUser);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Exception at associateAccount");
            try {
                connection.rollback();
                return "Error occurred associating your account";
            } catch (SQLException e1) {
                Logger.getLogger(DataServer.class.getName()).log(Level.SEVERE, null, e1);
            }
        }
        return "Account associated with success";
    }

    public boolean isAssociatedAccount(String username,int tumblrUser) throws RemoteException
    {
        ResultSet rt = null;
        PreparedStatement ps;
        String result="";
        try {
            // fetch id_user
            String s="SELECT tumblr_username FROM USER WHERE name = '" + username + "' and is_tumblr_account = '" + tumblrUser +  "'";
            rt = connection.createStatement().executeQuery(s);
            connection.commit();
            if (rt.next())
            {
                if(rt.getString(1) != null)
                {
                    return true;
                }
            }
        }catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Exception at checkAssociated");
            try {
                connection.rollback();
                return false;
            } catch (SQLException e1) {
                Logger.getLogger(DataServer.class.getName()).log(Level.SEVERE, null, e1);
            }
        }
        return false;
    }


    public String checkAssociated(String tumblrUsername) throws RemoteException
    {
        ResultSet rt = null;
        PreparedStatement ps;
        String result="";
        try {
            // fetch id_user
            String s = "SELECT tumblr_username FROM USER WHERE tumblr_username = '" + tumblrUsername + "'";
            rt = connection.createStatement().executeQuery(s);
            connection.commit();
            if (rt.next())
            {
                if(rt.getString(1) != null)
                {
                    return "That tumblr account is already associated with another fundStarter account";
                }
            }
        }catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Exception at checkAssociated");
            try {
                connection.rollback();
                return "Error occurred";
            } catch (SQLException e1) {
                Logger.getLogger(DataServer.class.getName()).log(Level.SEVERE, null, e1);
            }
        }
        return result;
    }

    public boolean updateAccessToken(String secret,String userToken,String oldSecret, String oldUserToken) throws RemoteException
    {
        PreparedStatement ps;
        String s = "UPDATE user SET secret_token = ?, user_token = ?  WHERE secret_token = ? and user_token = ?";
        try {
            ps = connection.prepareStatement(s);
            ps.setString(1, secret);
            ps.setString(2,userToken);
            ps.setString(3,oldSecret);
            ps.setString(4,oldUserToken);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException e1) {
                Logger.getLogger(DataServer.class.getName()).log(Level.SEVERE, null, e1);
            }
            return false;
        }
        return true;
    }

    public boolean updateAccessToken(String secret,String userToken,String username,int tumblrUser)
    {
        PreparedStatement ps;
        String s = "UPDATE user SET secret_token = ?, user_token = ?  WHERE name = ? and is_tumblr_account = ?";
        try {
            ps = connection.prepareStatement(s);
            ps.setString(1, secret);
            ps.setString(2,userToken);
            ps.setString(3,username);
            ps.setInt(4,tumblrUser);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException e1) {
                Logger.getLogger(DataServer.class.getName()).log(Level.SEVERE, null, e1);
            }
            return false;
        }
        return true;
    }


    public boolean setPostId(String projectName,String postId) throws RemoteException
    {
        ResultSet rt;
        PreparedStatement ps;
        long idProject=-1;
        String s="SELECT ID_PROJECT FROM PROJECT WHERE name = '" + projectName + "'";
        try {
            rt = connection.createStatement().executeQuery(s);
            if(rt.next())
            {
                idProject = rt.getLong(1);
            }
            s = "UPDATE project SET post_id= ? WHERE id_project = ?";
            ps = connection.prepareStatement(s);
            ps.setString(1, postId);
            ps.setLong(2,idProject);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    public void connectDb() throws RemoteException, InstantiationException, IllegalAccessException
    {
        try
        {
            Class.forName(driver).newInstance();
        }catch(ClassNotFoundException e)
        {
            System.out.println("[DATABASE] MySQL JDBC Driver missing!");
            return;
        }
        try
        {
            connection = DriverManager.getConnection(url+dbName,userName,password);
            connection.setAutoCommit(false);
        }catch(SQLException e)
        {
            System.out.println("[DATABASE] Connection Failed! Check output console!");
            e.printStackTrace();
            return;
        }
        if (connection != null)
        {
            System.out.println("[DATABASE] Database is now operational!");
        }
        else
        {
            System.out.println("[DATABASE] Failed to make connection!");
        }
    }

    public static void main(String args[]) throws SQLException
    {
        try
        {

            //-----------------load properties-----------------
            Properties prop = new Properties();
            InputStream input = null;
            try {
                input = new FileInputStream("new.properties");
                prop.load(input);
                rmiPort=Integer.parseInt(prop.getProperty("rmiPort"));
                remoteName=prop.getProperty("rmiName");
                url = prop.getProperty("url");
                dbName = prop.getProperty("dbName");
                driver = prop.getProperty("driver");
                userName = prop.getProperty("userName");
                password = prop.getProperty("password");
            } catch (IOException ex) {
                System.out.println("Error loading DataServer properties file. The default values were set");
                remoteName = "DataServer";
                rmiPort = 7000;
                url = "jdbc:mysql://localhost:8889/";
                dbName = "SD";
                driver = "com.mysql.jdbc.Driver";
                userName = "root";
                password = "root";
                ex.printStackTrace();
            } finally {
                if (input != null) {
                    try {
                        input.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            //-----------------connect to database-----------------
            DataServer ds = new DataServer();
            ds.connectDb();

            //-----------------bind remote object-----------------
            Registry r = LocateRegistry.createRegistry(rmiPort);
            r.rebind(remoteName,ds);
            System.out.println("DataServer ready.");
        }catch(RemoteException re)
        {
            System.out.println("Remote exception in DataServer main : " + re);
        }
        catch(InstantiationException ie)
        {
            System.out.println("Instantiation exception in DataServer main : " + ie);
        }
        catch(IllegalAccessException iae)
        {
            System.out.println("Illegal Access exception in DataServer main : " + iae);
        }
    }
}
