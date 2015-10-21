/**
 * Created by joaosubtil on 09/10/15.
 */

import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import sun.text.IntHashtable;

import java.net.*;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Client {

    public static boolean DEBUG=true;


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
    public static   int alreadyLogin=0;


    public static int signalToTerminate=0;



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
        while(tries >0){
            sock=null;
            signalToTerminate=0;

            if (sock==null){
                try {
                    sock = new Socket(firstIP,clientPort);
                    tries=reconnection*2;
                    if (DEBUG)
                        System.out.println("connecting to first");
                } catch (IOException e) {
                    tries--;
                    if (DEBUG)
                        System.out.println("Remaining tries="+tries+"->First server");
                }

            }
            if (sock==null){
                try {
                    sock = new Socket(secondIP,clientPort);
                    System.out.println("secondIP="+secondIP);
                    tries=reconnection*2;
                    if (DEBUG)
                        System.out.println("connecting to second");
                } catch (IOException e) {
                    tries--;
                    if (DEBUG)
                        System.out.println("Remaining tries="+tries+"->Second server");
                }

            }

            if (sock!=null){
                try {
                    if(DEBUG){
                        System.out.println("Socket status="+sock);
                    }
                    createChannels(sock);

                }catch (IOException e){

                    if (DEBUG){
                        System.out.println("Connection lost");
                    }

                    if(th!=null){
                        try {
                            if(DEBUG){
                                System.out.println("Wait for sender thread to die");
                            }
                            signalToTerminate=1;
                            th.join();
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
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
        while (true)
        {
            Message reply = null;

            try {

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //todo verificar se as classes message estão em sintonia
                reply = (Message)objIn.readObject();
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
    long idProject;
   
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
                    if (Client.signalToTerminate==1)
                        break;

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
                            case "login successful":
                                Client.alreadyLogin=1;
                                secundaryMenu();
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
            System.out.println("thread is dead");
        }

    
    
    public void initialMenu() throws IOException
    {
            String ini="\n-------------------Initial MENU-----------------\n\n1->Login\n\n2->Sign up\n\nChoose an option : ";
            int op = Integer.parseInt(reader.readLine());
            int check = 0;
            while(check == 0)
            {
                if(op == 1 || Client.alreadyLogin==1) //todo testar aqui se a sessao já foi iniciada
                {
                    check = 1;
                    login();
                }
                else if(op ==2)
                {
                    check = 1;
                    signUp();
                }
                else
                {

                    System.out.println("Select a valid option." + ini);
                    op = Integer.parseInt(reader.readLine());
                }
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
        if(Client.signalToTerminate!=1){
            objOut.writeObject(request);
            objOut.flush();
        }
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


        public  void secundaryMenu()throws IOException
        {
            int op = 0;
            String ini = "\n-------------------Secundary Menu-----------------\n\n1->List current projects.\n\n2->List old projects.\n\n3.View details of a project.\n\n4.Check account balance.\n\n5.Check my rewards.\n\n6.Create project.\n\n7.Administrator menu.\n\n8.Exit.\n\nChoose an option:";
            while(op != 8)
            {
                do{
                    op =Integer.parseInt(reader.readLine());
                    if(op <= 0 || op>8) {
                        System.out.println("Select a valid option.\n");
                        System.out.println(ini);
                    }
                }while(op <= 0 || op>8);
                switch(op)
                {
                    case 1:
                        listCurrentProjects();
                        break;
                    case 2:
                        listOldProjects();
                        break;
                    case 3:
                        viewProject();
                        break;
                    case 4:
                        checkAccountBalance();
                        break;
                    case 5:
                        checkUserRewards();
                        break;
                    case 6:
                        createProject();
                        break;
                    case 7:
                        adminMenu();
                        break;
                    case 8:
                        sendExitMessage();
                        break;
                default:
                    break;
            }
        }
    }


    public void listCurrentProjects() throws IOException
    {
        Message request = new Message();
        request.setOperation("list current projects");
        objOut.writeObject(request);
        objOut.flush();
    }

    public void listOldProjects() throws IOException
    {
        Message request = new Message();
        request.setOperation("list old projects");
        objOut.writeObject(request);
        objOut.flush();
    }


    public void viewProject() throws IOException            //todo validação do id inserido
    {
        listAllProjects();
        idProject = Long.parseLong(reader.readLine());
        Message request = new Message();
        request.setOperation("view project");
        request.setIdProject(idProject);
        System.out.println("ID project : " + request.getIdProject());
        objOut.writeObject(request);
        objOut.flush();
        tertiaryMenu();
    }

    public void listAllProjects() throws IOException
    {
        Message request = new Message();
        request.setOperation("list all projects");
        objOut.writeObject(request);
        objOut.flush();
    }

    public void checkAccountBalance() throws IOException
    {
        Message request = new Message();
        request.setUsername(Client.loginData.getUsername());
        request.setOperation("check account balance");
        objOut.writeObject(request);
        objOut.flush();
    }


    public void checkUserRewards() throws IOException
    {
        Message request = new Message();
        request.setUsername(Client.loginData.getUsername());
        request.setOperation("check rewards");
        objOut.writeObject(request);
        objOut.flush();
    }

    public void createProject() throws IOException
    {
        int checkData = 0;
        Message request = new Message();
        System.out.println("Name : ");
        String name = reader.readLine();
        System.out.println("Description : ");
        String description = reader.readLine();
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        formatter.setLenient(false);
        String limitDate = "";
        while(checkData ==0)
        {
            System.out.println("Limit Date (dd/MM/yyyy HH:mm):");
            limitDate = reader.readLine();
            try
            {
                date = formatter.parse(limitDate);
                checkData = 1;
            }catch(ParseException e)
            {
                checkData =0;
                System.out.println("\nInvalid date.\n");
            }
        }
        System.out.println("Target value: ");
        long targetValue  = Long.parseLong(reader.readLine());
        System.out.println("Enterprise(just press enter if this is project is individual) : ");
        String enterprise= reader.readLine();
        //////////////////////////////////////////////////////
        System.out.println("Add rewards to the project(insert 0 in description to stop) :");
        while(true)
        {

                System.out.println("Description: ");
                String rewardDescription = reader.readLine();
                if(rewardDescription.equals("0")== true)
                {
                    break;
                }
                System.out.println("Minimum value of the pledge :");
                double minValue = Double.parseDouble(reader.readLine());
                request.getRewards().add(new Reward(rewardDescription, minValue));
        }
        System.out.println("Add alternatives to the project(insert 0 in description to stop) :");
        while(true)
        {

            System.out.println("Description: ");
            String alternativeDescription = reader.readLine();
            if(alternativeDescription.equals("0")== true)
            {
                break;
            }
            System.out.println("Divisor(Nr votes of pledge = ceil(pledge value/(divisor * minimum value of pledge))) : ");
            float divisor = Float.parseFloat(reader.readLine());
            request.getAlternatives().add(new Alternative(alternativeDescription, divisor));
        }

        //////////////////////////////////////////////////////
        request.setUsername(Client.loginData.getUsername());
        request.setProjectName(name);
        request.setProjectDescription(description);
        request.setProjectLimitDate(limitDate);
        request.setProjectTargetValue(targetValue);
        request.setProjectEnterprise(enterprise);
        request.setOperation("create project");
        objOut.writeObject(request);
        objOut.flush();
    }

    public void sendExitMessage() throws IOException
    {
        Message request = new Message();
        request.setUsername(Client.loginData.getUsername());
        request.setOperation("Exit secundary menu");
        Client.loginData = null;
        objOut.writeObject(request);
        objOut.flush();
    }

    void tertiaryMenu() throws IOException
    {
        int op = 0;
        String ini = "\n\n1->Contribute to this project.\n\n2->Comment project.\n\n3.Exit.\n\nChoose an option:";
        while(op != 3)
        {
            do{
                op =Integer.parseInt(reader.readLine());
                if(op <= 0 || op>3) {
                    System.out.println("Select a valid option.\n");
                    System.out.println(ini);
                }
            }while(op <= 0 || op>3);
            switch(op)
            {
                case 1:
                    contributeToProject();
                    break;
                case 2:
                    commentProject();
                    break;
                case 3:
                    sendExitMessage2();
                    break;
                default:
                    break;
            }
        }
    }


    public void contributeToProject() throws IOException
    {
        System.out.println("How much do you want to donate ?");
        float pledgeValue = Float.parseFloat(reader.readLine());
        System.out.println("ID of the alternative that you want to vote : ");
        long idAlternative = Long.parseLong(reader.readLine());
        Message request = new Message();
        request.setUsername(Client.loginData.getUsername());
        request.setOperation("pledge");
        request.setAlternativeChoosen(idAlternative);
        request.setPledgeValue(pledgeValue);
        request.setIdProject(idProject);
        objOut.writeObject(request);
        objOut.flush();

    }

    public void showPreviousMessages() throws  IOException
    {
        Message request = new Message();
        request.setUsername(Client.loginData.getUsername());
        request.setOperation("show previous comments");
        request.setIdProject(idProject);
        objOut.writeObject(request);
        objOut.flush();
    }

    public void commentProject() throws IOException
    {
        showPreviousMessages();
        String msg = Client.loginData.getUsername() + ": ";
        msg += reader.readLine();
        Message request = new Message();
        request.setUsername(Client.loginData.getUsername());
        request.setOperation("comment project");
        request.setIdProject(idProject);
        request.setComment(msg);
        objOut.writeObject(request);
        objOut.flush();
    }

    public void sendExitMessage2() throws IOException
    {
        Message request = new Message();
        request.setUsername(Client.loginData.getUsername());
        request.setOperation("Exit tertiary menu");
        objOut.writeObject(request);
        objOut.flush();
    }

    void adminMenu() throws IOException
    {

        Message request = new Message();
        request.setUsername(Client.loginData.getUsername());
        request.setOperation("admin menu");
        objOut.writeObject(request);
        objOut.flush();
        ////////////////////////////////////////////////////////
        int op = 0;
        String ini = "\n\n1->Add rewards to a project.\n\n2->Remove rewards from a project.\n\n3->Cancel project.\n\n4->Reply to supporter's messages.\n\n5->Exit\n\nChoose an option:";
        while(op != 5)
        {
            do{
                op =Integer.parseInt(reader.readLine());
                if(op <= 0 || op>5) {
                    System.out.println("Select a valid option.\n");
                    System.out.println(ini);
                }
            }while(op <= 0 || op>5);
            switch(op)
            {
                case 1:
                    addReward();
                    break;
                case 2:
                    removeReward();
                    break;
                case 3:
                    cancelProject();
                    break;
                case 4:                                     //todo replyMessages
                    replyMessage();
                    break;
                case 5:                                     //todo send exit message in admin menu
                    sendExitMessage3();
                    break;
                default:
                    break;
            }
        }
    }

    public void addReward() throws IOException                  //todo validação de escolha de projecto em que o user é admin
    {
        System.out.println("ID of the project that you want to add a reward:");
        long id = Long.parseLong(reader.readLine());
        System.out.println("Description: ");
        String rewardDescription = reader.readLine();
        System.out.println("Minimum value of the pledge :");
        double minValue = Double.parseDouble(reader.readLine());
        Message request = new Message();
        request.setUsername(Client.loginData.getUsername());
        request.setOperation("add reward");
        request.getRewards().add(new Reward(rewardDescription, minValue));
        request.setIdProject(id);
        objOut.writeObject(request);
        objOut.flush();
    }

    public void listRewardsProject(long id) throws IOException
    {
        Message request = new Message();
        request.setUsername(Client.loginData.getUsername());
        request.setOperation("list rewards");
        request.setIdProject(id);
        objOut.writeObject(request);
        objOut.flush();
    }

    public void removeReward() throws IOException                  //todo validação de escolha de projecto em que o user é admin.
    {                                                               //todo validação do reward escolhido
        System.out.println("ID of the project that you want to remove a reward:");
        long id = Long.parseLong(reader.readLine());
        listRewardsProject(id);
        long rewardId = Long.parseLong(reader.readLine());
        Message request = new Message();
        request.setUsername(Client.loginData.getUsername());
        request.setOperation("remove reward");
        request.setIdProject(id);
        request.setIdReward(rewardId);
        objOut.writeObject(request);
        objOut.flush();
    }

    public void cancelProject() throws IOException              //todo validação de escolha de projecto em que o user é admin
    {
        System.out.println("ID of the project that you want to cancel:");
        long id = Long.parseLong(reader.readLine());
        Message request = new Message();
        request.setUsername(Client.loginData.getUsername());
        request.setOperation("cancel project");
        request.setIdProject(id);
        objOut.writeObject(request);
        objOut.flush();
    }

    public void replyMessage() throws IOException
    {
        System.out.println("ID of the project where you want to reply to messages:");
        long id = Long.parseLong(reader.readLine());
        Message request = new Message();
        request.setUsername(Client.loginData.getUsername());
        request.setOperation("show previous comments admin");
        request.setIdProject(id);
        objOut.writeObject(request);
        objOut.flush();
        ///////////////////////////////////////////////////////
        long idMessage = Long.parseLong(reader.readLine());
        request = new Message();
        System.out.println("Type a reply:");
        String reply = "\t\t\t\t" + reader.readLine();
        request.setIdMessage(idMessage);
        request.setUsername(Client.loginData.getUsername());
        request.setOperation("reply message");
        request.setReply(reply);
        request.setIdProject(id);
        objOut.writeObject(request);
        objOut.flush();
    }

    public void sendExitMessage3() throws IOException
    {
        Message request = new Message();
        request.setUsername(Client.loginData.getUsername());
        request.setOperation("Exit admin menu");
        objOut.writeObject(request);
        objOut.flush();
    }

    //todo ----------------------------> fim do projecto
}

