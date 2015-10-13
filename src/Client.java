/**
 * Created by joaosubtil on 09/10/15.
 */

import java.net.*;
import java.io.*;
import java.util.*;

public class Client {

    public static boolean DEBUG=true;


    public static   int clientPort;
    public static 	int reconnection;
    public static 	String firstIP;
    public static 	String secondIP;
    public static 	Socket s = null;
    public static   SendToServer th;

    public static ObjectOutputStream objOut=null;
    public static ObjectInputStream  objIn=null;


    public static void main(String args[]) {
        
        //read properties file
        Properties prop = new Properties();
        InputStream input = null;
        try {
            input = new FileInputStream("tcpProp.properties");
            prop.load(input);
            firstIP=prop.getProperty("firstIP");
            secondIP=prop.getProperty("secondIP");
            reconnection =Integer.parseInt(prop.getProperty("reconnection"));
            clientPort=Integer.parseInt(prop.getProperty("clientPort"));
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("Error reading properties file Client.java");
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        int tries=reconnection;
        while(tries !=0)
        {
            try{
                
                if(tries>=reconnection/2){
                    createChannels(new Socket(firstIP, clientPort));
                }
                else{
                    createChannels(new Socket(secondIP, clientPort));
                }
            }catch(Exception ex) {

                if(s!=null){
                    try {
                        s.close();
                    } catch (Exception e){
                        e.printStackTrace();
                        System.out.println("cannot close socket");
                    }
                }

                try {
                    Thread.sleep(1000);
                }catch(InterruptedException threadex) {
                    System.out.println("Program error(thread), restart please");
                }
            }
            tries--;
        }

        if(tries == 0){
            System.out.println("Server not found, try again later");
            System.exit(0);
        }


    }
    public static void createChannels(Socket s) throws Exception 
    {
        objOut = new ObjectOutputStream(s.getOutputStream());
        objIn = new ObjectInputStream(s.getInputStream());
        Guide g = new Guide();
        th=new SendToServer(s,g,objIn,objOut);
        th.start();
        
        //reading from server
        while (true) {
            Message reply = (Message )objIn.readObject();
            synchronized(g)
            {
            	g.getOperations().add(reply.getOperation());
            }
            /*if(reply.getMessage().equals("EXIT")){
                System.out.println("Connection issues, try again later");
                System.exit(-1);
            }*/
            System.out.println("->"+ reply.getMessage());
        }
    }
}

class SendToServer extends Thread{

    public static Socket s;
    public static ObjectOutputStream objOut;
    public static ObjectInputStream objIn;
    public static Guide g;
   
    SendToServer(Socket s,Guide g,ObjectInputStream objIn,ObjectOutputStream objOut) 
    {
    	this.s = s;
    	this.g = g;
    	this.objOut = objOut;
    	this.objIn = objIn;
    }
     
    public void setSocket(Socket s) { this.s = s; }
    public void closeSocket() throws IOException { this.s.close();  }

    public void run()
    {
    	String currentOp = "";
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);
        while(true)
        {
            try 
            {
            	int check = 0;
                synchronized(g)
                {
                	if(!g.getOperations().isEmpty())
                	{
                		currentOp = g.getOperations().poll();
                		check = 1;
                	}
                }
                if(check != 0)
                {
                	switch(currentOp)
                	{
                		case "initial menu":
                			initialMenu();
                			break;
                		default:
                			continue;
                	}
                }
            }catch(Exception e)	{
                //assumir que o server foi abaixo e matar esta thread
                //comer a excepção
                System.out.println("Error in the sender thread.");
                e.printStackTrace();
            	try {
                    s.close();
                    reader.close();
                    input.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                    System.out.println("erro ao fechar o socket");
                }
            }
        }
    }
    
    
    public void initialMenu()
    {
    	Scanner sc = new Scanner(System.in);
    	int op = sc.nextInt();
    	if(op  ==1)
    	{
    		login();
    	}
    	else
    	{
    		;
    	}
    }
    
    public void login() 
    {
    	Message reply = new Message();
    	Scanner sc = new Scanner(System.in);
    	System.out.println("Username : ");
    	String username = sc.nextLine();
    	reply.setUsername(username);
    	System.out.println("Password : ");
    	String password = sc.nextLine();
    	reply.setPassword(password);
    	reply.setOperation("login");
    	try
    	{
    		objOut.writeObject(reply);
    	}catch(IOException ioe)
    	{
    		System.out.println("Error sending object to server at login.");
    	}
    }

}
