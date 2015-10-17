import javax.print.DocFlavor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.sql.*;
import java.util.Date;
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
        catch(SQLException e)
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


    public String listProjects(int current) throws RemoteException        // current projects-->1, old projects --->0,all-->2
    {
        ResultSet rt = null;
        String result = "";
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
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
                    if(!auxDate.before(today))  //todo accepted = 0 --> not yet, accepted = 1---> yes
                    {
                        result += "ID : "+ rt.getLong(1) + "Project : " + rt.getString(2) + " Target value : " + rt.getLong(3) + " Current value : " + rt.getLong(4) + "\n";
                    }
                }
                else if(current == 0)
                {
                    if(auxDate.before(today))
                    {
                        result += "ID : "+ rt.getLong(1) + "Project : " + rt.getString(2) + " Target value : " + rt.getLong(3) + " Current value : " + rt.getLong(4) + " Accepted : " + rt.getInt(6) + "\n";
                    }
                }
                else
                {
                    result += "ID : "+ rt.getLong(1) + "Project : " + rt.getString(2) + " Target value : " + rt.getLong(3) + " Current value : " + rt.getLong(4) + " Accepted : " + rt.getInt(6) + "\n";
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
        return result;
    }

    public String viewProject(long idProject) throws RemoteException        //todo niveis extra
    {
        ResultSet rt = null;
        String result = "";
        try
        {
            String s = "SELECT name,description,target_value,current_value,limit_date,accepted FROM project where id_project = '" + idProject + "'";
            rt = connection.createStatement().executeQuery(s);
            connection.commit();
            if(rt.next())                       //todo accepted = 0 --> not yet, accepted = 1---> yes
            {
                result +=  "Project : " + rt.getString(1) + "\nDescription: " + rt.getString(2) +  "\nTarget value : " + rt.getLong(3) + "\nCurrent value : " + rt.getLong(4) +"\nLimit date : "+ rt.getString(5) +"\nAccepted : " + rt.getInt(6) + "\n";
            }
            //fetch rewards
            result += "\nRewards :\n";
            s = "SELECT description,min_value FROM reward where id_project = '" + idProject + "'";
            rt = connection.createStatement().executeQuery(s);
            connection.commit();
            while(rt.next())
            {
                result += "\nDescription : " + rt.getString(1) + " Minimum value : " + rt.getDouble(2);
            }
            //fetch alternatives
            result += "\nAlternatives :\n";
            s = "SELECT description FROM alternative WHERE id_project = '" + idProject + "'";
            rt = connection.createStatement().executeQuery(s);
            connection.commit();
            while(rt.next())
            {
                result += "\nDescription : " + rt.getString(1);
            }
        }catch(SQLException  e)
        {
            try {
                System.out.println("\nException at viewProject.\n");
                e.printStackTrace();
                connection.rollback();
                return null;
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


   public boolean addProject(String username,String name,String description,String limitDate,long targetValue, String enterprise,ArrayList<Reward> rewards, ArrayList<Alternative> alternatives) throws RemoteException
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
              input = new FileInputStream("tcpProp.properties");
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
