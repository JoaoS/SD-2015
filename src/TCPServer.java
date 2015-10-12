import java.net.*;
import java.io.*;
import java.util.*;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.text.*;

public class TCPServer 
{
	public static 	DataServer_I ds;
	public static   String rmiName;
    public static   String rmiIp;
    public static   int rmiPort;
    public static 	int serverPort;
    public static   int WAIT=500; //milisseconds response thread wait
    public static 	int tries = 10;
    
	public static void main(String args[]) 
	{
		Properties prop = new Properties();
		InputStream input = null;
		//-----------------load properties-----------------
		try 
		{
	        input = new FileInputStream("TCPServerconfigs.properties");
	        prop.load(input);
	        
	        rmiPort=Integer.parseInt(prop.getProperty("rmiPort"));
	        rmiName=prop.getProperty("rmiName");
	        rmiIp=prop.getProperty("rmiIp");   
	        serverPort=Integer.parseInt(prop.getProperty("serverPort"));
	        WAIT = Integer.parseInt(prop.getProperty("WAIT"));
	        tries = Integer.parseInt(prop.getProperty("tries"));
		}catch (IOException ioe) 
		{
			System.out.println("Error loading TCPServerconfigs properties file. The default values were set");
			
			rmiPort=7000;
			rmiName= "DataServer";
			rmiIp= "localhost";
			serverPort = 6000;
			WAIT = 500;
			tries = 10;
			
			ioe.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException ioe) {
                    System.out.println("Error closing InputStream.");
                	ioe.printStackTrace();
                }
            }
        }
		//-----------------establish connection with RMI-----------------
	      try 
	      {
	         System.getProperties().put("java.security.policy", "security.policy");
	         System.setSecurityManager(new RMISecurityManager());   
	         ds = (DataServer_I)LocateRegistry.getRegistry(rmiPort).lookup(rmiName); 
	      } catch (Exception e) 
	      {
	    	  System.out.println("Error establishing connection with RMI.");
	          e.printStackTrace();
	      }
	    //-----------------start accepting connections-----------------
	      try
	      { 
              System.out.println("Accepting clients in port "+serverPort + ".");
              ServerSocket listenSocket = new ServerSocket(serverPort);
              System.out.println("LISTEN SOCKET="+listenSocket);
              while(true) 
              {
                  Socket clientSocket = listenSocket.accept(); 
                  System.out.println("CLIENT_SOCKET (created at accept())="+clientSocket);
                  new Connection(clientSocket,ds);      
              }
          }catch(Exception e)
          {
        	  System.out.println("Error occurred while accepting clients.");
        	  System.out.println("Listen:" + e.getMessage());
          }
      }
	}


class Connection extends Thread 
{
    
    public Socket clientSocket;
    public DataServer_I ds;
    public DataInputStream in;
    public DataOutputStream out;
    public ObjectInputStream objIn;
    public ObjectOutputStream objOut;
    
    
    public Connection (Socket aClientSocket, DataServer_I ds)
    {
        try{
        	this.ds = ds;
            this.clientSocket = aClientSocket;
            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());
            objIn = new ObjectInputStream(in);
            objOut = new ObjectOutputStream(out);
            this.start();
        }catch(Exception e)	//-----------------------------------------------------------------> IO exception
        {
        	System.out.println("Connection:" + e.getMessage());
        }
    }
    

    public void run()
    {
    	;
    }
    
    public void restartRmi() throws IOException
    {
    	int tries = TCPServer.tries;
        while(tries!=0)
        {
               try
               {
                   Thread.sleep(TCPServer.WAIT);
               }catch (InterruptedException ie) 
               {
                   ie.printStackTrace();
               }
               try 
               {
            	   System.getProperties().put("java.security.policy", "security.policy");
            	   System.setSecurityManager(new RMISecurityManager());   
            	   ds = (DataServer_I)LocateRegistry.getRegistry(TCPServer.rmiPort).lookup(TCPServer.rmiName);   
            	   if(ds.dummyMethod()==0)
            	   {
                      System.out.println("RMI back online....");
                      break;
            	   }  
               	}catch (Exception e)
               	{
                    System.out.println("Exception in restartRmi, dataserver not online yet...");
                    //e.printStackTrace();
                }
               	tries--;
        }
        if(tries==0)
        {
            // out.writeUTF("EXIT");//shuts down client   <----------------------------------------------------------    
            System.out.println("RMI disconnected, shuting down...");
            System.exit(0);   
        } 
    }
    
}
