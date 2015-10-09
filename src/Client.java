/**
 * Created by joaosubtil on 09/10/15.
 */

import java.net.*;
import java.io.*;
import java.util.*;

public class Client {

    public static   int clientPort;
    public static 	int reconnection;
    public static 	String firstIP;
    public static 	String secondIP;
    public static 	Socket s = null;
    public static   ReadFromServer th;


    public static void main(String args[]) {
        
        Client client= new Client();
        //read properties file
        Properties prop = new Properties();
        InputStream input = null;
        try {
            input = new FileInputStream("tcpProp.properties");
            prop.load(input);
            client.firstIP=prop.getProperty("firstIP");
            client.secondIP=prop.getProperty("secondIP");
            client.reconnection =Integer.parseInt(prop.getProperty("reconnection"));
            client.clientPort=Integer.parseInt(prop.getProperty("clientPort"));
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("Error readin properties file Client.java");
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
        while(reconnection !=0){

            try{
                
                if(reconnection>=tries/2){
                    client.createChannels(new Socket(firstIP, clientPort), client);

                }
                else{
                    client.createChannels(new Socket(secondIP, clientPort), client);

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


                reconnection--;
            }
        }

        if(reconnection == 0){
            System.out.println("Server not found, try again later");
            System.exit(0);
        }


    }
    public void createChannels(Socket s, Client client) throws Exception {

        DataInputStream in = new DataInputStream(s.getInputStream());
        client.th=new ReadFromServer();
        client.th.setSocket(s);
        client.th.start();
        
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

    public Socket s;
    
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
