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
    public static   ReadFromServer th;

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




        System.out.println("Possible connections:"+reconnection);
        int tries=reconnection;
        while(tries !=0){

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

                tries--;
            }

        }

        if(tries == 0){
            System.out.println("Server not found, try again later");
            System.exit(0);
        }


    }
    public static void createChannels(Socket s) throws Exception {

        DataInputStream in = new DataInputStream(s.getInputStream());
        objIn = new ObjectInputStream(in);

        th=new ReadFromServer();
        th.setSocket(s);
        th.start();
        
        while (true) {
            String data = in.readUTF();

            if(data.equals("EXIT")){
                System.out.println("Connection issues, try again later");
                System.exit(-1);
            }
            System.out.println("->"+data);

        }
    }
}

class ReadFromServer extends Thread{

    public static ObjectOutputStream objOut=null;
    public static Socket s;



    ReadFromServer(){}
    public void setSocket(Socket s) { this.s = s; }
    public void closeSocket() throws IOException { this.s.close();  }

    public void run()
    {

        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);
        DataOutputStream out = null;


        try {
            out = new DataOutputStream(s.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }


        while(true){

            String texto;

            try {
                texto = reader.readLine();
                out.writeUTF(texto);
                System.out.println("texto lido:"+texto);

            }catch(Exception e)	{
                //assumir que o server foi abaixo e matar esta thread
                //comer a excepção

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







}
