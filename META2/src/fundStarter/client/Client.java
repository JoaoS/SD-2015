package fundStarter.client;

/**
 * Created by joaosubtil on 07/12/15.
 */


import fundStarter.commons.Alternative;
import fundStarter.commons.Guide;
import fundStarter.commons.Message;
import fundStarter.commons.Reward;

import java.io.*;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

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
    public static Guide guide;

    public static   Message loginData=null;
    public static   int alreadyLogin=0;
    public static   int signalToTerminate=0;


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
        int dRecon=reconnection*2;
        int tries=dRecon;

        while(tries >=0){

            if (sock==null){
                if (DEBUG){
                    System.out.println("connecting to 1, tries:"+tries);
                }
                try {
                    sock = new Socket(firstIP,clientPort);
                    tries=dRecon;

                } catch (IOException e) {
                    tries-=2;
                }

            }
            if(sock==null){
                if (DEBUG){
                    System.out.println("connecting to 2, tries:"+tries);
                }
                try {
                    sock = new Socket(secondIP,clientPort);
                    tries=dRecon;

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

                        signalToTerminate=1;
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
        if(tries <= 0){
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


        /*if(DEBUG) {
            System.out.println("Aqui->Reader thread");
            System.out.println("alreadylogedin=" + alreadyLogin);

        }*/

        signalToTerminate=0;

        //reading from server
        while (true)
        {
            Message reply = null;
            try {
                reply = (Message )objIn.readObject();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            if (reply.getOperation().equalsIgnoreCase("EXIT")) {
                System.out.println("Cannot connect to server, shuting down");
                System.exit(1);
            }
            synchronized(guide)
            {
                guide.getOperations().add(reply.getOperation());
            }
            if (reply.getMessage()!=null){


                if(!(alreadyLogin==1 && reply.getOperation().equalsIgnoreCase("initial menu"))) {

                    if(!(alreadyLogin==1& reply.getOperation().equalsIgnoreCase("login successful")))
                        System.out.println("Server->" + reply.getMessage());
                }

            }
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
                int check = 0;

                //veifies if the connection failed
                if (Client.signalToTerminate==1)
                    break;


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

                        case "login unsuccessful":
                            initialMenu();
                            Client.guide.getOperations().poll();//to avoid duplicate "unsucessful and initial menu" in case of login error -server side
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



    public void initialMenu() throws Exception
    {

        //se os dados já foram validados posso saltar esta parte
        if (Client.alreadyLogin == 1) {
            login();
        }
        else {
            String ini="\n-------------------Initial MENU-----------------\n\n1->Login\n\n2->Sign up\n\nChoose an option : ";
            int op=0;
            int check = 0;

            while(check==0)
            {
                try {
                    op = Integer.parseInt(reader.readLine());
                }catch (NumberFormatException e){
                }
                //test if connection was lost
                if(Client.signalToTerminate==1)
                    break;

                if(op == 1 )
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
                    System.out.println(ini);
                    System.out.println("Please select number 1 or 2");
                    check=0;
                }

            }
        }
    }

    public void login() throws Exception        //Validation done
    {
        Message request = new Message();
        if (Client.alreadyLogin != 0) {
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

        if (Client.signalToTerminate==0){
            objOut.writeObject(request);
            objOut.flush();
        }

    }

    public void signUp() throws Exception     //validation done
    {
        int check = 0;
        Message request = new Message();
        System.out.println("Username : ");
        String username = reader.readLine();
        while (check==0 ){
            String ePattern = "^[a-zA-Z0-9]*.$";
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
            java.util.regex.Matcher m = p.matcher(username);
            if (m.matches()) {
                check = 1;
            } else {
                System.out.println("Invalid username.\n\nusername : ");
                username = reader.readLine();
            }
        }
        check=0;

        System.out.println("Password : ");
        String password = reader.readLine();
        while (check==0 ){
            String ePattern = "^[a-zA-Z0-9]*.$";
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
            java.util.regex.Matcher m = p.matcher(password);
            if (m.matches()) {
                check = 1;
            } else {
                System.out.println("Invalid  password.\n\npassword : ");
                password = reader.readLine();
            }
        }
        check=0;

        System.out.println("BI : ");
        String bi = reader.readLine();
        while (check==0 ){
            String ePattern = "^[0-9]*$";
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
            java.util.regex.Matcher m = p.matcher(bi);
            if (m.matches()) {
                check = 1;
            } else {
                System.out.println("Invalid  BI.\n\nBI : ");
                bi = reader.readLine();
            }
        }
        check=0;

        System.out.println("Age : ");
        String ageS = reader.readLine();
        while (check==0 ){

            String ePattern = "^[0-9]*.$";
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
            java.util.regex.Matcher m = p.matcher(ageS);
            if (m.matches()) {
                check = 1;
            }
            else {
                System.out.println("Invalid  Age.\n\nAge : ");
                ageS = reader.readLine();
            }
        }
        check=0;
        int age=Integer.parseInt(ageS);

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

        if (Client.signalToTerminate==0){
            objOut.writeObject(request);
            objOut.flush();
        }
    }

    public  void secundaryMenu()throws Exception  //validation done
    {
        int op = 0;
        String ini = "\n-------------------Secundary Menu-----------------\n\n1->List current projects.\n\n2->List old projects.\n\n3.View details of a project.\n\n4.Check account balance.\n\n5.Check my rewards.\n\n6.Create project.\n\n7.Administrator menu.\n\n8.Exit.\n\nChoose an option:";
        while(op != 8)
        {
            do{
                try {
                    op = Integer.parseInt(reader.readLine());
                }catch (NumberFormatException e){
                    System.out.println("Please select number between 1 and 8");

                }

                if(op <= 0 || op>8) {
                    System.out.println("Select a valid option.\n");
                    System.out.println(ini);
                }

            }while(op <= 0 || op>8);
            //test for lost connection
            if(Client.signalToTerminate==1)
                break;

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

    public void listCurrentProjects() throws Exception    //validation done
    {
        Message request = new Message();
        request.setOperation("list current projects");
        if (Client.signalToTerminate==0){
            objOut.writeObject(request);
            objOut.flush();
        }
    }

    public void listOldProjects() throws Exception    //validation done
    {
        Message request = new Message();
        request.setOperation("list old projects");
        if (Client.signalToTerminate==0){
            objOut.writeObject(request);
            objOut.flush();
        }

    }


    public void viewProject() throws Exception
    {
        listAllProjects();
        idProject = Long.parseLong(reader.readLine());
        Message request = new Message();
        request.setOperation("view project");
        request.setIdProject(idProject);
        System.out.println("ID project : " + request.getIdProject());
        if (Client.signalToTerminate==0){
            objOut.writeObject(request);
            objOut.flush();
        }
        tertiaryMenu();
    }

    public void listAllProjects() throws Exception    //validation done
    {
        Message request = new Message();
        request.setOperation("list all projects");
        if (Client.signalToTerminate==0){
            objOut.writeObject(request);
            objOut.flush();
        }
    }

    public void checkAccountBalance() throws Exception //validation done
    {
        Message request = new Message();
        request.setUsername(Client.loginData.getUsername());
        request.setOperation("check account balance");

        if (Client.signalToTerminate==0){
            objOut.writeObject(request);
            objOut.flush();
        }
    }

    public void checkUserRewards() throws Exception   //validation done
    {
        Message request = new Message();
        request.setUsername(Client.loginData.getUsername());
        request.setOperation("check rewards");
        if (Client.signalToTerminate==0){
            objOut.writeObject(request);
            objOut.flush();
        }
    }

    public void createProject() throws Exception      //validation done
    {
        int check=0;

        int checkData = 0;
        Message request = new Message();
        String name = "",description = "";
        while(check ==0)
        {
            System.out.println("Name : ");
            name = reader.readLine();
            if(name.trim().length()!=0 )
            {
                check =1;
            }
        }
        check=0;
        while(check ==0)
        {
            System.out.println("Description : ");
            description = reader.readLine();
            if(description.trim().length()!=0 )
            {
                check=1;
            }
        }
        check=0;
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH");
        formatter.setLenient(false);
        String limitDate = "";
        while(checkData ==0)
        {
            System.out.println("Limit Date (dd/MM/yyyy HH):");
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
        String targetValueS  = reader.readLine();

        while (check==0 ){
            String ePattern = "^[0-9]*.$";
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
            java.util.regex.Matcher m = p.matcher(targetValueS);
            if (m.matches()) {
                check = 1;
            } else {
                System.out.println("Invalid Target value.\n\nTarget value : ");
                targetValueS = reader.readLine();
            }
        }
        check=0;
        long targetValue  = Long.parseLong(targetValueS);


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

        if (Client.signalToTerminate==0){
            objOut.writeObject(request);
            objOut.flush();
        }
    }

    public void sendExitMessage() throws Exception    //validation done
    {
        Message request = new Message();
        request.setUsername(Client.loginData.getUsername());
        request.setOperation("Exit secundary menu");

        Client.loginData = null;
        Client.alreadyLogin=0;

        if (Client.signalToTerminate==0){
            objOut.writeObject(request);
            objOut.flush();
        }
    }

    void tertiaryMenu() throws Exception      //validation done
    {
        int op = 0;
        String ini = "\n\n1->Contribute to this project.\n\n2->Comment project.\n\n3.Exit.\n\nChoose an option:";
        while(op != 3)
        {
            do{
                try {
                    op = Integer.parseInt(reader.readLine());
                }catch (NumberFormatException e){

                }

                if(op <= 0 || op>3) {
                    System.out.println("Select a valid option.\n");
                    System.out.println(ini);
                }
                if(Client.signalToTerminate==1)
                    break;
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

    public void contributeToProject() throws Exception       //validaton done
    {
        int check=0;
        System.out.println("How much do you want to donate ?");
        String pledgeValueS = reader.readLine();
        while (check==0 ){
            String ePattern = "^[0-9]*.$";
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
            java.util.regex.Matcher m = p.matcher(pledgeValueS);
            if (m.matches()) {
                check = 1;
            } else {
                System.out.println("Invalid Value.\n\nValue : ");
                pledgeValueS = reader.readLine();
            }
        }
        check=0;
        float pledgeValue = Float.parseFloat(pledgeValueS);

        System.out.println("ID of the alternative that you want to vote(insert 0 if the project have no alternatives or you do not want to vote) : ");
        String idAlternativeS = reader.readLine();
        while (check==0 ){
            String ePattern = "^[0-9]*.$";
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
            java.util.regex.Matcher m = p.matcher(idAlternativeS);
            if (m.matches()) {
                check = 1;
            } else {
                System.out.println("Invalid option.\n\nID : ");
                idAlternativeS = reader.readLine();
            }
        }
        check=0;
        long idAlternative = Long.parseLong(idAlternativeS);

        Message request = new Message();
        request.setUsername(Client.loginData.getUsername());
        request.setOperation("pledge");
        request.setAlternativeChoosen(idAlternative);
        request.setPledgeValue(pledgeValue);
        request.setIdProject(idProject);
        if (Client.signalToTerminate==0){
            objOut.writeObject(request);
            objOut.flush();
        }

    }

    public void showPreviousMessages() throws  Exception      //validaton done
    {
        Message request = new Message();
        request.setUsername(Client.loginData.getUsername());
        request.setOperation("show previous comments");
        request.setIdProject(idProject);
        if (Client.signalToTerminate==0){
            objOut.writeObject(request);
            objOut.flush();
        }
    }

    public void commentProject() throws Exception     //validaton done
    {
        showPreviousMessages();
        String msg = Client.loginData.getUsername() + ": ";
        msg += reader.readLine();
        Message request = new Message();
        request.setUsername(Client.loginData.getUsername());
        request.setOperation("comment project");
        request.setIdProject(idProject);
        request.setComment(msg);
        if (Client.signalToTerminate==0){
            objOut.writeObject(request);
            objOut.flush();
        }
    }

    public void sendExitMessage2() throws Exception       //validaton done
    {
        Message request = new Message();
        request.setUsername(Client.loginData.getUsername());
        request.setOperation("Exit tertiary menu");
        if (Client.signalToTerminate==0){
            objOut.writeObject(request);
            objOut.flush();
        }
        Client.alreadyLogin=0;
    }



    void adminMenu() throws Exception   //validaton done
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
                try {
                    op = Integer.parseInt(reader.readLine());
                }catch (NumberFormatException e){
                    System.out.println("Please select number between 1 and 8");

                }
                if(op <= 0 || op>5) {
                    System.out.println("Select a valid option.\n");
                    System.out.println(ini);
                }
                if(Client.signalToTerminate==1)
                    break;
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
                case 4:
                    replyMessage();
                    break;
                case 5:
                    sendExitMessage3();
                    break;
                default:
                    break;
            }
        }
    }

    public void addReward() throws Exception      //validation done
    {
        int check=0;
        System.out.println("ID of the project that you want to add a reward:");
        String idS = reader.readLine();
        while (check==0 ){
            String ePattern = "^[0-9]*.$";
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
            java.util.regex.Matcher m = p.matcher(idS);
            if (m.matches()) {
                check = 1;
            } else {
                System.out.println("Invalid value.\n\nNew ID : ");
                idS = reader.readLine();
            }
        }
        check=0;
        long id = Long.parseLong(idS);


        System.out.println("Description: ");
        String rewardDescription = reader.readLine();
        System.out.println("Minimum value of the pledge :");
        String minValueS = reader.readLine();
        while (check==0 ){
            String ePattern = "^[0-9]*.$";
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
            java.util.regex.Matcher m = p.matcher(minValueS);
            if (m.matches()) {
                check = 1;
            } else {
                System.out.println("Invalid value.\n\nNew value : ");
                minValueS = reader.readLine();
            }
        }
        check=0;
        double minValue = Double.parseDouble(minValueS);


        Message request = new Message();
        request.setUsername(Client.loginData.getUsername());
        request.setOperation("add reward");
        request.getRewards().add(new Reward(rewardDescription, minValue));
        request.setIdProject(id);
        if (Client.signalToTerminate==0){
            objOut.writeObject(request);
            objOut.flush();
        }
    }

    public void listRewardsProject(long id) throws Exception      //validation done
    {
        Message request = new Message();
        request.setUsername(Client.loginData.getUsername());
        request.setOperation("list rewards");
        request.setIdProject(id);
        if (Client.signalToTerminate==0){
            objOut.writeObject(request);
            objOut.flush();
        }
    }

    public void removeReward() throws Exception       //validation done
    {
        int check=0;
        System.out.println("ID of the project that you want to remove a reward:");
        String idS = reader.readLine();
        while (check==0 ){
            String ePattern = "^[0-9]*.$";
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
            java.util.regex.Matcher m = p.matcher(idS);
            if (m.matches()) {
                check = 1;
            } else {
                System.out.println("Invalid value.\n\nNew ID : ");
                idS = reader.readLine();
            }
        }
        check=0;
        long id = Long.parseLong(idS);
        listRewardsProject(id);

        String rewardIdS = reader.readLine();
        while (check==0 ){
            String ePattern = "^[0-9]*.$";
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
            java.util.regex.Matcher m = p.matcher(rewardIdS);
            if (m.matches()) {
                check = 1;
            } else {
                System.out.println("Invalid value.\n\nNew ID : ");
                rewardIdS = reader.readLine();
            }
        }
        long rewardId = Long.parseLong(rewardIdS);

        Message request = new Message();
        request.setUsername(Client.loginData.getUsername());
        request.setOperation("remove reward");
        request.setIdProject(id);
        request.setIdReward(rewardId);

        if (Client.signalToTerminate==0){
            objOut.writeObject(request);
            objOut.flush();
        }
    }

    public void cancelProject() throws Exception      //validation done
    {
        int check=0;
        System.out.println("ID of the project that you want to cancel:");
        String idS = reader.readLine();
        while (check==0 ){
            String ePattern = "^[0-9]*.$";
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
            java.util.regex.Matcher m = p.matcher(idS);
            if (m.matches()) {
                check = 1;
            } else {
                System.out.println("Invalid value.\n\nNew ID : ");
                idS = reader.readLine();
            }
        }
        check=0;
        long id = Long.parseLong(idS);

        Message request = new Message();
        request.setUsername(Client.loginData.getUsername());
        request.setOperation("cancel project");
        request.setIdProject(id);
        if (Client.signalToTerminate==0){
            objOut.writeObject(request);
            objOut.flush();
        }
    }

    public void replyMessage() throws Exception       //validation done
    {
        int check=0;
        System.out.println("ID of the project where you want to reply to messages:");
        String idS = reader.readLine();
        while (check==0 ){
            String ePattern = "^[0-9]*.$";
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
            java.util.regex.Matcher m = p.matcher(idS);
            if (m.matches()) {
                check = 1;
            } else {
                System.out.println("Invalid ID, please insert correct id.\n\nNew ID : ");
                idS = reader.readLine();
            }
        }
        check=0;
        long id = Long.parseLong(idS);

        Message request = new Message();
        request.setUsername(Client.loginData.getUsername());
        request.setOperation("show previous comments admin");
        request.setIdProject(id);
        if (Client.signalToTerminate==0){
            objOut.writeObject(request);
            objOut.flush();
        }
        ///////////////////////////////////////////////////////
        String idMessageS = reader.readLine();
        while (check==0 ){
            String ePattern = "^[0-9]*.$";
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
            java.util.regex.Matcher m = p.matcher(idMessageS);
            if (m.matches()) {
                check = 1;
            } else {
                System.out.println("Invalid message value.\n\nNew message value : ");
                idMessageS = reader.readLine();
            }
        }
        check=0;
        long idMessage = Long.parseLong(idMessageS);

        request = new Message();
        System.out.println("Type a reply:");
        String reply = reader.readLine();
        request.setIdMessage(idMessage);
        request.setUsername(Client.loginData.getUsername());
        request.setOperation("reply message");
        request.setReply(reply);
        request.setIdProject(id);
        if (Client.signalToTerminate==0){
            objOut.writeObject(request);
            objOut.flush();
        }
    }

    public void sendExitMessage3() throws Exception       //validation done
    {
        Message request = new Message();
        request.setUsername(Client.loginData.getUsername());
        request.setOperation("Exit admin menu");
        if (Client.signalToTerminate==0){
            objOut.writeObject(request);
            objOut.flush();
        }
    }

}

