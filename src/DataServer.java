import javax.print.DocFlavor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.sql.*;
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
                System.out.println("\nException at checkUserPass.\n");
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
