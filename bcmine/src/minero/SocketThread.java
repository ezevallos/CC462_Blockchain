package minero;/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
*
* @author Victor
*/
public class SocketThread extends Thread{
   private Socket socket;
   private DataInputStream in;
   private DataOutputStream out;
   private boolean running = true;
   
   public SocketThread(String addres,int port) throws IOException{
       socket = new Socket(addres, port);
       in = new DataInputStream(socket.getInputStream());
       out = new DataOutputStream(socket.getOutputStream());
       
       //Inicia juego
       out.writeChar('l');
   }

   @Override
   public void run() {
       try {
           while(running){
               String msg = in.readUTF(); //Lee lo que el server le envia
               leer(msg);
           }
       } catch (IOException ex) {
           System.out.println("Se perdió la conexión");
           System.out.println(ex.getMessage());
       }finally{
           try {
               socket.close();
           } catch (IOException ex) {
               System.out.println(ex.getMessage());
           }
       }
   }
   
   //Verifica si perdio o repinta el nuevo mapa que manda el server
   private void leer(String msg){
       if("pierdes".equals(msg)){
           stopSocket();
           return;
       }
   }
   
   public void stopSocket(){
       running = false;
   }
   
   //Envia letras: w, a, s, d, x
   public void enviarOrden(char key){
       try {
           out.writeChar(key);
       } catch (IOException ex) {
           System.out.println(ex.getMessage());
       }
   }
}
