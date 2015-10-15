import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.sql.*;

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
