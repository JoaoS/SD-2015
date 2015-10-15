/**
 * Created by joaosubtil on 09/10/15.
 */

import java.net.*;
import java.io.*;
import java.util.*;

public class Client {

    public static boolean DEBUG=false;


    public static   int clientPort;
    public static 	int reconnection;
    public static 	String firstIP;
    public static 	String secondIP;
    public static 	Socket sock = null;
    public static   SendToServer th;
    public static   int threadWait;
    public static   ObjectOutputStream objOut=null;
    public static   ObjectInputStream  objIn=null;
    public static   Guide guide;
    public static   Message loginData=null;



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
            threadWait = Integer.parseInt(prop.getProperty("threadWait"));
        } catch (IOException ex) {

            ex.printStackTrace();
            System.out.println("Error reading properties file at Client.");
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        guide=new Guide();
        int tries=reconnection*2;
        while(tries !=0){

            if (sock==null){
                try {
                    sock = new Socket(firstIP,clientPort);
                    tries=reconnection;
                } catch (IOException e) {
                    tries-=2;
                }

            }
            else if(sock==null){
                try {
                    sock = new Socket(secondIP,clientPort);
                    tries=reconnection;
                } catch (IOException e) {
                    tries-=2;
                }
            }

            if (sock!=null){
                try {
                    createChannels(sock);

                }catch (IOException e){

                    if (DEBUG){
                        System.out.println("Connection lost");
                    }

                    try {

                        //th.interrupt();
                        th.join();
                        if(DEBUG){
                            System.out.println("thread killed");
                        }

                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }

                    if(sock!=null){
                        try {
                            sock.close();
                            sock=null;

                            if(DEBUG){
                                System.out.println("closed socket");
                            }

                        } catch (Exception e2){
                            if(DEBUG){
                                e2.printStackTrace();
                                System.out.println("cannot close socket");
                            }
                        }
                    }

                    try {
                        Thread.sleep(threadWait);
                    }catch(InterruptedException e2) {
                        if (DEBUG){
                            e2.printStackTrace();
                            System.out.println("Program error(thread), restart please");
                        }
                    }
                    tries--;
                }

            }
            //
            try {
                Thread.sleep(threadWait);
            }catch(InterruptedException e2) {
                if (DEBUG){
                    e2.printStackTrace();
                    System.out.println("Program error(thread), restart please");
                }
            }
        }
        if(tries == 0){
            System.out.println("Server not found, try again later");
            System.exit(0);

        }


    }
    public static void createChannels(Socket sock) throws IOException
    {
        objOut = new ObjectOutputStream(sock.getOutputStream());
        objOut.flush();
        objIn = new ObjectInputStream(sock.getInputStream());
        th=null;
        th=new SendToServer(sock,objIn,objOut);
        th.start();


        if(DEBUG)
            System.out.println("Aqui->Reader thread");

        //reading from server
        while (true) {
            Message reply = null;
            try {
                reply = (Message )objIn.readObject();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            synchronized(guide)
            {
                guide.getOperations().add(reply.getOperation());
            }
            if (reply.getMessage()!=null)
                System.out.println("Server->"+ reply.getMessage());
        }
    }
}


class SendToServer extends Thread{

    public static Socket sock;
    public static ObjectOutputStream objOut;
    public static ObjectInputStream objIn;
    BufferedReader reader;
   
    SendToServer(Socket sock,ObjectInputStream objIn,ObjectOutputStream objOut)
    {
    	this.sock = sock;
    	this.objOut = objOut;
    	this.objIn = objIn;
    }
     
    public void setSocket(Socket sock) { this.sock = sock; }
    public void closeSocket() throws IOException { this.sock.close();  }

    public void run()
    {
    	String currentOp = "";
        InputStreamReader input = new InputStreamReader(System.in);
        reader = new BufferedReader(input);

            try 
            {
                while(true)
                {
                    int check = 0;
                    synchronized(Client.guide)
                    {
                        if(!Client.guide.getOperations().isEmpty())
                        {
                            currentOp = Client.guide.getOperations().poll();
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
                                break;
                        }
                    }
                }

            }catch (Exception e){
                if (Client.DEBUG) {
                    e.printStackTrace();
                    System.out.println("exiting");
                }
            }

        if (Client.DEBUG)
            System.out.println("thread dead");
        }

    
    
    public void initialMenu() throws IOException
    {
            int op = Integer.parseInt(reader.readLine());
            if(op  ==1) //todo testar aqui se a sessao já foi iniciada
            {
                login();
            }
            else
            {
                signUp();
            }
    }
    
    public void login() throws IOException
    {
        Message request = new Message();
        if (Client.loginData != null) {
            request = Client.loginData;
        }
        else
        {
            System.out.println("Username : ");
            String username = null;
            username = reader.readLine();
            request.setUsername(username);
            System.out.println("Password : ");
            String password = reader.readLine();
            request.setPassword(password);
            request.setOperation("login");
            Client.loginData=request;
        }
        objOut.writeObject(request);
        objOut.flush();
    }

    public void signUp() throws IOException
    {
        int check = 0;
            Message request = new Message();
            System.out.println("Username : ");
            String username = reader.readLine();
            System.out.println("Password : ");
            String password = reader.readLine();
            System.out.println("BI : ");
            String bi = reader.readLine();
            System.out.println("Age : ");
            int age = Integer.parseInt(reader.readLine());
            System.out.println("Email : ");
            String email = reader.readLine();
            while(check == 0) {
                String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
                java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
                java.util.regex.Matcher m = p.matcher(email);
                if (m.matches()) {
                    check = 1;
                } else {
                    System.out.println("Invalid e-mail address.\n\nEmail : ");
                    email = reader.readLine();
                }
            }
            request.setUsername(username);
            request.setPassword(password);
            request.setBi(bi);
            request.setAge(age);
            request.setEmail(email);
            request.setOperation("sign up");
            objOut.writeObject(request);
            objOut.flush();
    }
}

