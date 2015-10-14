/**
 * Created by joaosubtil on 09/10/15.
 */

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.Properties;

public class Client {

    public static boolean DEBUG=true;

    public static   int clientPort;
    public static 	int reconnection;
    public static   int threadWait =1000;
    public static 	String firstIP;
    public static 	String secondIP;
    public static   SendToServer th;
    public static   DataInputStream in;
    public static   DataOutputStream out;

    public static int teste=0;

    //public static   ObjectInputStream  objIn;


    public static void main(String args[]){
        
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

        /*/


        try {
            System.out.println("ligado");
            s = new Socket(firstIP,clientPort);
        } catch (IOException e) {
            tries--;
        }
    /*/
        int tries=reconnection*2;
        while(tries !=0){

            System.out.println("tries="+tries);
            Socket sock=null;


                if (sock==null){
                    try {
                        System.out.println("connecting to 1");
                        sock = new Socket(firstIP,clientPort);
                        tries=reconnection;
                    } catch (IOException e) {
                        tries--;
                    }

                }
                /*
                else if(sock==null){
                    try {
                        sock = new Socket("192.168.1.254",clientPort);
                        tries=reconnection;
                    } catch (IOException e) {
                        tries--;
                    }
                }
                */
                if (sock!=null){
                    try {
                        System.out.println("channels");
                        createChannels(sock);

                    }catch (IOException e){

                        System.out.println("ligação perdida");
                        try {
                            th.interrupt();
                            th.join();
                            System.out.println("thread killed");

                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                        //fechar o socket actual
                        if(sock!=null){
                            try {
                                sock.close();
                            } catch (Exception e2){
                                e2.printStackTrace();
                                System.out.println("cannot close socket");
                            }
                        }
                        try {
                            Thread.sleep(threadWait);
                        }catch(InterruptedException threadex) {
                            System.out.println("Program error(thread), restart please");
                        }
                        tries--;
                    }

                }
                else{
                    try {
                        Thread.sleep(threadWait);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
        }
        if(tries == 0){
            System.out.println("Server not found, try again later");
            System.exit(0);

        }

/**/

    }

    public static void createChannels(Socket sock) throws IOException {

        teste++;
        System.out.println("->>>>>>>teste="+teste+" sock"+sock);

        DataOutputStream out = new DataOutputStream(sock.getOutputStream());
        DataInputStream in = new DataInputStream(sock.getInputStream());
        ObjectOutputStream objOut = new ObjectOutputStream(out);
        objOut.flush();
        ObjectInputStream objIn = new ObjectInputStream(in);

        System.out.println("yoyoyo");

        th=null;
        th=new SendToServer();
        th.setObjOut(objOut);
        th.start();

        while (true){
            System.out.println("estou a ler do socket");
            String data = objIn.readUTF();
            System.out.println("->"+data);

        }
    }
}

class SendToServer extends Thread{

    public static ObjectOutputStream objOut=null;


    SendToServer(){}
    public static void setObjOut(ObjectOutputStream objOut) {
        SendToServer.objOut = objOut;
    }

    public void run(){

        InputStreamReader input = null;
        BufferedReader reader = null;

        while(!Thread.interrupted()){

            //isto não está a apanhar as excepções
            try {
                input = new InputStreamReader(System.in);
                reader = new BufferedReader(input);
                String texto=reader.readLine();
                objOut.writeUTF(texto);

            } catch (IOException e){
                e.printStackTrace();
                try {
                    reader.close();
                    input.close();
                } catch (Exception e1) {
                    e1.printStackTrace();
                    System.out.println("erro ao fechar o socket");
                }

            }
        }
        try {
            if (reader!=null && input!= null){
                reader.close();
                input.close();
            }
        } catch (IOException e1) {
            e1.printStackTrace();
            System.out.println("erro ao fechar o socket");
        }

        System.out.println("Stoping send thread");

    }







}
