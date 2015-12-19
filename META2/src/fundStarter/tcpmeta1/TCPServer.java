package fundStarter.tcpmeta1;


import fundStarter.DataServer.DataServer_I;
import fundStarter.commons.Message;

import java.io.*;
import java.net.*;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by joaosubtil on 07/10/15.
 */
public class TCPServer {

    private static  boolean DEBUG = true;
    public static DataServer_I dataServerInterface;

    public static 	int reconnection;
    private static int maxDropHeartbeats;
    public  static int WAIT; //milisseconds response thread wait
    private static int udpPort;
    private static int clientPort;
    private static int rmiPort;
    private static int replyNumber;

    private static String rmiName;
    private static String rmiIp;
    private static String firstIP;
    private static String secondIP;
    private static TimerTask timerTask;

    public static CopyOnWriteArrayList<Connection> onlineUsers=new CopyOnWriteArrayList<>();

    public static void main(String args[]) {

        //read java properties
        Properties prop = new Properties();
        InputStream input = null;

        try {

            input = new FileInputStream("new.properties");
            // load a properties file
            prop.load(input);
            // get the property value and print it out
            reconnection =Integer.parseInt(prop.getProperty("reconnection"));
            rmiPort=Integer.parseInt(prop.getProperty("rmiPort"));
            rmiName=prop.getProperty("rmiName");
            rmiIp=prop.getProperty("rmiIp");
            clientPort=Integer.parseInt(prop.getProperty("clientPort"));
            WAIT=Integer.parseInt(prop.getProperty("WAIT"));
            maxDropHeartbeats=Integer.parseInt(prop.getProperty("maxDropHeartbeats"));
            udpPort = Integer.parseInt(prop.getProperty("udpPort"));
            firstIP = prop.getProperty("firstIP");
            secondIP = prop.getProperty("secondIP");

        } catch (Exception ex) {
            if(DEBUG)
                ex.printStackTrace();

            System.out.println("Error reading properties file, default values will be set");
            rmiPort=5000;
            rmiName= "DataServer";
            rmiIp= "localhost";
            clientPort = 6000;
            WAIT = 500;
            maxDropHeartbeats = 10;
            firstIP= "169.254.15.178";
            secondIP= "169.254.107.114";
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    if(DEBUG)
                        e.printStackTrace();
                }
            }
        }
        //-----------------establish connection with RMI-----------------
        try
        {
            System.getProperties().put("java.security.policy", "security.policy");
            System.setSecurityManager(new RMISecurityManager());
            dataServerInterface = (DataServer_I)LocateRegistry.getRegistry(rmiIp,rmiPort).lookup(rmiName);
        } catch (Exception e){
            if(DEBUG)
                e.printStackTrace();

            System.out.println("Error establishing connection with RMI.\nPlease try again later");
            System.exit(1);


        }
        //---------------------------------------------------------------------------


        //attempt to connect to primary if it exists
        secundaryServer();
        //if not create thread to be primary and respond
        new PrimaryThread(udpPort).start();
        //accept incoming connections from client
        int connectedUsers = 0;
        try {

            System.out.println("Listenning for clients on port:" + clientPort);
            ServerSocket listenSocket = new ServerSocket(clientPort);
            System.out.println("LISTEN SOCKET=" + listenSocket);
            try {
                if (replyNumber==0)
                    dataServerInterface.checkProjectsDate();
            } catch (RemoteException e) {
                System.out.println("Exception running checkProjectsDate.");
                e.printStackTrace();
            }
            /////////task to check finished projects////////////////////////
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.HOUR, 1);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND,0);
            Date date = cal.getTime();

            timerTask = new MyTimerTask(dataServerInterface);
            Timer timer = new Timer(true);
            timer.scheduleAtFixedRate(timerTask, date, 1000*60*60);
            while (true) {
                Socket clientSocket = listenSocket.accept();
                System.out.println("CLIENT_SOCKET (created at accept())=" + clientSocket);
                connectedUsers++;
                onlineUsers.add(new Connection(clientSocket, connectedUsers,dataServerInterface));
            }
        } catch (Exception e) {
            if(DEBUG)
                e.printStackTrace();
            System.out.println("Error connecting new client:" + e.getMessage());
        }

    }

    public static void secundaryServer() {
        replyNumber = 0;
        DatagramSocket aSocket = null;

        try {
            System.out.println("Connecting as Secundary Server");
            int tries = maxDropHeartbeats;

            //check my address an ping the other
            String myIP = InetAddress.getLocalHost().getHostAddress();
            String connectIP = myIP.equalsIgnoreCase(firstIP) ? secondIP : firstIP;
            System.out.println("Connecting to " + connectIP);

            byte[] m = new byte[1000];
            aSocket = new DatagramSocket();
            InetAddress aHost = InetAddress.getByName(connectIP);
            System.out.println("Pinging " + connectIP + " in the port:" + udpPort);
            DatagramPacket request = new DatagramPacket(m, m.length, aHost, udpPort);
            // System.out.println(" "+request);
            while (tries > 0) {
                aSocket.send(request);
                aSocket.setSoTimeout(WAIT * 2); //timeout between each request
                //waits to receive
                try {
                    byte[] buffer = new byte[1000];
                    DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
                    aSocket.receive(reply);
                    System.out.println("Received Reply: " + replyNumber);
                    replyNumber++;
                    tries = maxDropHeartbeats;
                    Thread.sleep(WAIT);
                } catch (SocketTimeoutException tme) {
                    System.out.println("Primary not found,trying again(remaining tries:" + tries + ", time interval=" + WAIT + "ms)");
                    tries--;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    System.out.println("Secundary server function");
                }
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
            if (aSocket != null) {
                aSocket.close();
            }
        }

    }

    static class PrimaryThread extends Thread {//will handle backup as primaryTCP

        public int serverPort;

        PrimaryThread(int s) {
            serverPort = s;
        }

        public void run() {
            System.out.println("Primary server in port: " + serverPort);
            DatagramSocket aSocket = null;
            String s;
            try {
                aSocket = new DatagramSocket(serverPort);
                byte[] buffer = new byte[1000];
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                while (true)//receive packet and respond
                {
                    aSocket.receive(request);
                    DatagramPacket reply = new DatagramPacket(request.getData(), request.getLength(), request.getAddress(), request.getPort());
                    aSocket.send(reply);
                }
            } catch (SocketException e) {
                if(DEBUG)
                    e.printStackTrace();
                System.out.println("Socket: " + e.getMessage());

            } catch (IOException e) {
                if(DEBUG)
                    e.printStackTrace();
                System.out.println("IO: " + e.getMessage());
            } finally {
                if (aSocket != null) aSocket.close();
            }
        }
    }

    static class MyTimerTask extends TimerTask
    {
        DataServer_I dataServerInterface;

        MyTimerTask(DataServer_I dataServerInterface)
        {
            this.dataServerInterface = dataServerInterface;
        }

        public void run()
        {
            try {
                dataServerInterface.checkProjectsDate();
            } catch (RemoteException e) {
                restartRmiTask();
                try {
                    dataServerInterface.checkProjectsDate();
                } catch (RemoteException e1) {
                    if (TCPServer.DEBUG)
                        e1.printStackTrace();
                }

                System.out.println("Exception at MyTimerTask run.");
                if (TCPServer.DEBUG)
                    e.printStackTrace();
            }
        }
        public void restartRmiTask()
        {
            int tries = TCPServer.reconnection;
            while(tries!=0)
            {
                try
                {
                    Thread.sleep(TCPServer.WAIT*2);
                }catch (InterruptedException e){
                    if(DEBUG)
                        e.printStackTrace();
                }
                try
                {

                    System.getProperties().put("java.security.policy", "security.policy");
                    System.setSecurityManager(new RMISecurityManager());
                    dataServerInterface = (DataServer_I)LocateRegistry.getRegistry(rmiIp,rmiPort).lookup(rmiName);

                    if(dataServerInterface.dummyMethod()==0)
                    {
                        System.out.println("Timertask=RMI back online....");
                        break;
                    }
                }catch (Exception e){
                    /*if(DEBUG)
                        e.printStackTrace();
*/
                    System.out.println("Exception in Timertask, dataserver not online yet...");
                }
                tries--;
            }
            if(tries==0){
                System.out.println("RMI disconnected, shuting down...");
                System.exit(0);
            }
        }

    }

    //--------------Class to handle each client-------------------------------------------------------
    static class Connection extends Thread {

        public static 	DataServer_I dataServerInterface;
        public DataInputStream in;
        public DataOutputStream out;
        public Socket clientSocket;
        public int thread_number;//number of currently connected client
        public ObjectInputStream objIn;
        public ObjectOutputStream objOut;
        public String currentUser=null;

        public Connection(Socket aClientSocket, int numero, DataServer_I ds) {
            thread_number = numero;
            dataServerInterface=ds;
            try {
                clientSocket = aClientSocket;
                in = new DataInputStream(clientSocket.getInputStream());
                out = new DataOutputStream(clientSocket.getOutputStream());
                objOut = new ObjectOutputStream(out);
                objIn = new ObjectInputStream(in);
                this.start();
            } catch (IOException e) {
                if(DEBUG)
                    e.printStackTrace();
                System.out.println("Connection:" + e.getMessage());
            }
        }

        public String getCurrentUser() {
            return currentUser;
        }

        public void setCurrentUser(String currentUser) {
            this.currentUser = currentUser;
        }

        public int getThread_number() {
            return thread_number;
        }

        public ObjectOutputStream getObjOut() {
            return objOut;
        }

        public void setObjOut(ObjectOutputStream objOut) {
            this.objOut = objOut;
        }

        public void restartRmi() throws IOException
        {
            int tries = TCPServer.reconnection;
            while(tries!=0)
            {
                try
                {
                    Thread.sleep(TCPServer.WAIT*2);
                }catch (InterruptedException e){
                    if(DEBUG)
                        e.printStackTrace();
                }
                try
                {

                    System.getProperties().put("java.security.policy", "security.policy");
                    System.setSecurityManager(new RMISecurityManager());
                    dataServerInterface = (DataServer_I)LocateRegistry.getRegistry(rmiIp,rmiPort).lookup(rmiName);

                    if(dataServerInterface.dummyMethod()==0)
                    {
                        System.out.println("RMI back online....");
                        break;
                    }
                }catch (Exception e){
                    /*if(DEBUG)
                        e.printStackTrace();
*/
                    System.out.println("Exception in restartRmi, dataserver not online yet...");
                }
                tries--;
            }
            if(tries==0){
                //todo fechar aqui cliente
                Message shutdownn =new Message();
                shutdownn.setOperation("EXIT");

                objOut.writeObject(shutdownn);//shuts down client   <----------------------------------------------------------
                System.out.println("RMI disconnected, shuting down...");
                System.exit(1);
            }
        }
        //=============================

        public void run()
        {
            try {

                while (true) {
                    initialMenu();
                }

            }catch(EOFException e) {
                System.out.println("Client disconnected: "+currentUser);

                Message printNewUserLogin;
                //if size=1 only i am online
                for (int i=0;i<TCPServer.onlineUsers.size();i++){

                    if (TCPServer.onlineUsers.get(i).getCurrentUser() != null){
                        if (!TCPServer.onlineUsers.get(i).getCurrentUser().equalsIgnoreCase(currentUser)){

                            if (currentUser!=null){
                                printNewUserLogin= new Message();
                                printNewUserLogin.setMessage("User: "+currentUser+" offline");
                                printNewUserLogin.setOperation("New User");

                                try {
                                    TCPServer.onlineUsers.get(i).getObjOut().writeObject(printNewUserLogin);
                                    TCPServer.onlineUsers.get(i).getObjOut().flush();

                                } catch (IOException e1) {
                                    if (DEBUG)
                                        e1.printStackTrace();
                                }
                            }
                        }
                    }
                }
                //remove the current user
                for (int i=0;i<TCPServer.onlineUsers.size();i++){
                    //write my name in other users socket
                    if (TCPServer.onlineUsers.get(i).getThread_number()==thread_number){
                        TCPServer.onlineUsers.remove(i);
                        break;
                    }


                }

            }
            catch(IOException e) {
                System.out.println("IO:" + e);
            }
            catch(Exception e)
            {
                System.out.println("Please refer to stacktrace.");
                if (DEBUG)
                    e.printStackTrace();
            }
        }



        public void initialMenu() throws Exception  //done
        {

            String ini="-------------------Initial MENU-----------------\n\n1->Login\n\n2->Sign up\n\nChoose an option : ";
            Message request = new Message();
            request.setOperation("initial menu");
            request.setMessage(ini);
            objOut.writeObject(request);
            objOut.flush();
            Message reply = new Message();
            reply = (Message) objIn.readObject();
            if(reply.getOperation().equals("login"))
            {
                int validation=0;
                try{
                    validation=dataServerInterface.checkLogin(reply.getUsername(), reply.getPassword());
                }catch (RemoteException e){
                    restartRmi();
                    validation=dataServerInterface.checkLogin(reply.getUsername(), reply.getPassword());
                }

                if(validation != 0)
                {
                    request= new Message();
                    request.setOperation("login successful");
                    request.setMessage("Login made with success.");
                    objOut.writeObject(request);
                    objOut.flush();

                    currentUser=reply.getUsername();
                    Message printNewUserLogin;
                    //if size=1 only i am online
                    for (int i=0;i<TCPServer.onlineUsers.size();i++){
                        //write my name in other users socket
                        if (TCPServer.onlineUsers.get(i).getCurrentUser()!=null){
                            if (!(TCPServer.onlineUsers.get(i).getCurrentUser().equalsIgnoreCase(currentUser))){
                                printNewUserLogin= new Message();
                                printNewUserLogin.setMessage("User: "+currentUser+" online");
                                printNewUserLogin.setOperation("New User");
                                TCPServer.onlineUsers.get(i).getObjOut().writeObject(printNewUserLogin);
                                TCPServer.onlineUsers.get(i).getObjOut().flush();
                            }
                        }

                    }


                    secundaryMenu();
                }
                else {
                    request= new Message();
                    request.setOperation("login unsuccessful");
                    request.setMessage("Login credentials wrong please insert correct ones");
                    objOut.writeObject(request);
                    objOut.flush();

                }
            }
            else if(reply.getOperation().equals("sign up"))
            {
                String signUpresult = null;
                Boolean addUserReply=false;
                try {

                    signUpresult = dataServerInterface.checkSignUp(reply.getUsername(),reply.getPassword(),reply.getBi(),reply.getAge(),reply.getEmail());
                    addUserReply = dataServerInterface.addUser(reply.getUsername(),reply.getPassword(),reply.getBi(),reply.getAge(),reply.getEmail());
                }catch (RemoteException e){
                    restartRmi();
                    signUpresult = dataServerInterface.checkSignUp(reply.getUsername(),reply.getPassword(),reply.getBi(),reply.getAge(),reply.getEmail());
                    addUserReply = dataServerInterface.addUser(reply.getUsername(),reply.getPassword(),reply.getBi(),reply.getAge(),reply.getEmail());
                }

                if(signUpresult == null && addUserReply == true)
                {
                    request= new Message();
                    request.setOperation("sign up sucessful");
                    request.setMessage("Sign up made with success. You have a reward of 100 dollars in your account.");
                    objOut.writeObject(request);
                    objOut.flush();
                }
                else
                {
                    request= new Message();
                    request.setOperation("sign up unsucessful");
                    String send = "Sign up unsuccessfull. " + signUpresult;
                    request.setMessage(send);
                    objOut.writeObject(request);
                    objOut.flush();
                }
            }
        }

        public void secundaryMenu() throws Exception    //done
        {
            String ini = "-------------------Secundary Menu-----------------\n\n1->List current projects.\n\n2->List old projects.\n\n3.View details of a project.\n\n4.Check account balance.\n\n5.Check my rewards.\n\n6.Create project.\n\n7.Administrator menu.\n\n8.Exit.\n\nChoose an option:";
            Message reply = new Message();
            Message request;
            long accountBalance;
            request = new Message();
            request.setOperation("secundary menu");
            request.setMessage(ini);
            objOut.writeObject(request);
            objOut.flush();
            reply = (Message) objIn.readObject();
            while(reply.getOperation().equals("Exit secundary menu") == false)
            {

                if(reply.getOperation().equals("list current projects"))            //done
                {
                    String send;
                    try{
                        send = dataServerInterface.listProjects(1);
                    }catch (RemoteException e){
                        restartRmi();
                        send = dataServerInterface.listProjects(1);
                    }

                    if(send.equals(""))
                    {
                        send = "There are no projects open.";
                    }
                    request = new Message();
                    request.setOperation("list current projects successfull");
                    request.setMessage(send);
                    objOut.writeObject(request);
                    objOut.flush();
                }
                else if(reply.getOperation().equals("list old projects"))           //done
                {
                    String send;
                    try{
                        send = dataServerInterface.listProjects(0);
                    }catch (RemoteException e){
                        restartRmi();
                        send = dataServerInterface.listProjects(0);
                    }

                    if(send.equals(""))
                    {
                        send = "There are no old projects.";
                    }
                    request = new Message();
                    request.setOperation("list old projects successfull");
                    request.setMessage(send);
                    objOut.writeObject(request);
                    objOut.flush();
                }
                else if(reply.getOperation().equals("list all projects"))       //done
                {
                    String send;
                    try{
                        send = dataServerInterface.listProjects(2);
                    }catch (RemoteException e){
                        restartRmi();
                        send = dataServerInterface.listProjects(2);
                    }
                    if(send.equals(""))
                    {
                        send = "There are no projects.";
                    }
                    else
                    {
                        send += "\nWhich project do you want to view the details ?";
                    }
                    request = new Message();
                    request.setOperation("list all projects successfull");
                    request.setMessage(send);
                    objOut.writeObject(request);
                    objOut.flush();
                    ///////////////////////view project///////////////////////////////
                    reply = (Message) objIn.readObject();
                    try{

                        send = dataServerInterface.viewProject(reply.getIdProject());
                    }catch (RemoteException e){
                        restartRmi();
                        send = dataServerInterface.viewProject(reply.getIdProject());
                    }
                    request = new Message();
                    request.setOperation("view project successfull");
                    request.setMessage(send);
                    objOut.writeObject(request);
                    objOut.flush();
                    tertiaryMenu();
                }
                else if (reply.getOperation().equals("check account balance")) {        //done

                    try{

                        accountBalance = dataServerInterface.checkAccountBalance(reply.getUsername());
                    }catch (RemoteException e){
                        restartRmi();
                        accountBalance = dataServerInterface.checkAccountBalance(reply.getUsername());
                    }

                    if (accountBalance  >= 0){
                        request = new Message();
                        String send = "You have " + accountBalance + " dollars in your account.";
                        request.setOperation("check account balance sucessful");
                        request.setMessage(send);
                        objOut.writeObject(request);
                        objOut.flush();
                    } else {
                        request = new Message();
                        request.setOperation("check account balance unsucessful");
                        request.setMessage("Errors occured while checking your account balance.");
                        objOut.writeObject(request);
                        objOut.flush();
                    }
                }
                else if(reply.getOperation().equals("check rewards"))                   //done
                {
                    String send;
                    try{
                        send = dataServerInterface.checkRewards(reply.getUsername());
                    }catch (RemoteException e){
                        restartRmi();
                        send = dataServerInterface.checkRewards(reply.getUsername());
                    }
                    request = new Message();
                    request.setOperation("check rewards successfull");
                    request.setMessage(send);
                    objOut.writeObject(request);
                    objOut.flush();
                }
                else if (reply.getOperation().equals("create project"))         //done
                {
                    String checkResult;
                    boolean createProject;
                    try{
                        checkResult = dataServerInterface.checkProject(reply.getProjectName());
                        createProject=dataServerInterface.addProject(reply.getUsername(),reply.getProjectName(), reply.getProjectDescription(), reply.getProjectLimitDate(), reply.getProjectTargetValue(), reply.getProjectEnterprise(),reply.getRewards(),reply.getAlternatives());
                    }catch (RemoteException e){
                        restartRmi();
                        checkResult = dataServerInterface.checkProject(reply.getProjectName());
                        createProject=dataServerInterface.addProject(reply.getUsername(),reply.getProjectName(), reply.getProjectDescription(), reply.getProjectLimitDate(), reply.getProjectTargetValue(), reply.getProjectEnterprise(),reply.getRewards(),reply.getAlternatives());
                    }
                    if (checkResult == null && createProject == true) {
                        request = new Message();
                        request.setOperation("create project sucessful");
                        request.setMessage("Project created with success.");
                        objOut.writeObject(request);
                        objOut.flush();
                    } else {
                        request = new Message();
                        request.setOperation("create project unsucessful");
                        String send = "Creation of the project failed. " + checkResult;
                        request.setMessage(send);
                        objOut.writeObject(request);
                        objOut.flush();
                    }
                }
                else if(reply.getOperation().equals("admin menu"))          //done
                {
                    request = new Message();
                    request.setOperation("admins projects");
                    String send;
                    try{
                        send = dataServerInterface.showAdminProjects(reply.getUsername());
                    }catch (RemoteException e){
                        restartRmi();
                        send = dataServerInterface.showAdminProjects(reply.getUsername());
                    }
                    request.setMessage(send);
                    objOut.writeObject(request);
                    objOut.flush();
                    adminMenu();
                }
                else if(reply.getOperation().equals("Exit secundary menu"))
                {
                    initialMenu();
                    return;
                }
                request = new Message();
                request.setOperation("secundary menu");
                request.setMessage(ini);
                objOut.writeObject(request);
                objOut.flush();

                reply = (Message) objIn.readObject();
            }
        }

        public void tertiaryMenu() throws Exception
        {
            String ini = "\n\n1->Contribute to this project.\n\n2->Comment project.\n\n3.Exit.\n\nChoose an option:";
            Message reply = new Message();
            Message request;
            request = new Message();
            request.setOperation("tertiary menu");
            request.setMessage(ini);
            objOut.writeObject(request);
            objOut.flush();
            reply = (Message) objIn.readObject();
            while(reply.getOperation().equals("Exit tertiary menu") == false)
            {
                if(reply.getOperation().equals("pledge"))   //done
                {
                    String send;
                    try{

                        send = dataServerInterface.contributeToProject(reply.getIdProject(), reply.getUsername(), reply.getPledgeValue(), reply.getAlternativeChoosen());
                    }catch (RemoteException e){
                        restartRmi();
                        send = dataServerInterface.contributeToProject(reply.getIdProject(),reply.getUsername(),reply.getPledgeValue(),reply.getAlternativeChoosen());
                    }

                    request = new Message();
                    request.setOperation("pledge successfull");
                    request.setMessage(send);
                    objOut.writeObject(request);
                    objOut.flush();
                }
                else if(reply.getOperation().equals("show previous comments"))      //done
                {

                    String send;
                    try{

                        send = dataServerInterface.showCommentsProject(reply.getIdProject(), 0);
                    }catch (RemoteException e){
                        restartRmi();
                        send = dataServerInterface.showCommentsProject(reply.getIdProject(), 0);
                    }

                    request = new Message();
                    request.setOperation("show previous comments successfull");
                    request.setMessage(send);
                    objOut.writeObject(request);
                    objOut.flush();
                    ////////////////comment project/////////////////////
                    reply = (Message) objIn.readObject();
                    try{

                        send = dataServerInterface.commentProject(reply.getIdProject(), reply.getUsername(), reply.getComment());
                    }catch (RemoteException e){
                        restartRmi();
                        send = dataServerInterface.commentProject(reply.getIdProject(),reply.getUsername(),reply.getComment());
                    }

                    request = new Message();
                    request.setOperation("comment project successfull");
                    request.setMessage(send);
                    objOut.writeObject(request);
                    objOut.flush();
                }
                else if(reply.getOperation().equals("Exit tertiary menu"))
                {
                    secundaryMenu();
                    return;
                }
                request = new Message();
                request.setOperation("tertiary menu");
                request.setMessage(ini);
                objOut.writeObject(request);
                objOut.flush();
                reply = (Message) objIn.readObject();
            }
        }

        public void adminMenu() throws Exception
        {
            String ini = "\n\n1->Add rewards to a project.\n\n2->Remove rewards from a project.\n\n3->Cancel project.\n\n4->Reply to supporter's messages.\n\n5->Exit\n\nChoose an option:";
            Message reply = new Message();
            Message request;
            request = new Message();
            request.setOperation("admin menu");
            request.setMessage(ini);
            objOut.writeObject(request);
            objOut.flush();
            reply = (Message) objIn.readObject();
            while(reply.getOperation().equals("Exit admin menu") == false)
            {
                if(reply.getOperation().equals("add reward"))
                {
                    String send;
                    try{

                        send = dataServerInterface.addReward(reply.getIdProject(), reply.getRewards().get(0), reply.getUsername());
                    }catch (RemoteException e){
                        restartRmi();
                        send = dataServerInterface.addReward(reply.getIdProject(),reply.getRewards().get(0),reply.getUsername());
                    }
                    request = new Message();
                    request.setOperation("add reward successfull");
                    request.setMessage(send);
                    objOut.writeObject(request);
                    objOut.flush();
                }
                else if(reply.getOperation().equals("list rewards"))
                {
                    String send;
                    try{

                        send = dataServerInterface.listRewardsProject(reply.getIdProject());
                    }catch (RemoteException e){
                        restartRmi();
                        send = dataServerInterface.listRewardsProject(reply.getIdProject());
                    }

                    request = new Message();
                    request.setOperation("list rewards successfull");
                    request.setMessage(send);
                    objOut.writeObject(request);
                    objOut.flush();
                    /////////////////////////////////////////////////
                    reply = (Message) objIn.readObject();
                    try{

                        send = dataServerInterface.removeReward(reply.getIdProject(), reply.getIdReward(), reply.getUsername());
                    }catch (RemoteException e){
                        restartRmi();
                        send = dataServerInterface.removeReward(reply.getIdProject(), reply.getIdReward(), reply.getUsername());
                    }
                    request = new Message();
                    request.setOperation("remove reward successfull");
                    request.setMessage(send);
                    objOut.writeObject(request);
                    objOut.flush();
                }
                else if(reply.getOperation().equals("cancel project"))
                {
                    String send;
                    try{

                        send = dataServerInterface.cancelProject(reply.getIdProject(), reply.getUsername());
                    }catch (RemoteException e){
                        restartRmi();
                        send = dataServerInterface.cancelProject(reply.getIdProject(),reply.getUsername());
                    }
                    request = new Message();
                    request.setOperation("cancel project successfull");
                    request.setMessage(send);
                    objOut.writeObject(request);
                    objOut.flush();
                }
                else if(reply.getOperation().equals("show previous comments admin"))
                {
                    String send;
                    try{

                        send = dataServerInterface.showCommentsProject(reply.getIdProject(), 1);
                    }catch (RemoteException e){
                        restartRmi();
                        send = dataServerInterface.showCommentsProject(reply.getIdProject(), 1);
                    }
                    request = new Message();
                    request.setOperation("show comments project admin successfull");
                    request.setMessage(send);
                    objOut.writeObject(request);
                    objOut.flush();
                    /////////////////////////////////////////////////
                    reply = (Message) objIn.readObject();
                    try{

                        send = dataServerInterface.replyMessage(reply.getIdProject(), reply.getUsername(), reply.getIdMessage(), reply.getReply());
                    }catch (RemoteException e){
                        restartRmi();
                        send = dataServerInterface.replyMessage(reply.getIdProject(),reply.getUsername(),reply.getIdMessage() ,reply.getReply());
                    }

                    request = new Message();
                    request.setOperation("reply successfull");
                    request.setMessage(send);
                    objOut.writeObject(request);
                    objOut.flush();

                }
                else if(reply.getOperation().equals("Exit admin menu")) {
                    secundaryMenu();
                    return;
                }
                request = new Message();
                request.setOperation("admin menu");
                request.setMessage(ini);
                objOut.writeObject(request);
                objOut.flush();
                reply = (Message) objIn.readObject();
            }
        }
    }

}








