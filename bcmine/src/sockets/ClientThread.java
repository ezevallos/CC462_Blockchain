package sockets;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import modelos.Mensaje;
import modelos.Respuesta;

/**
 * Hilo de conexion al server
 * @author Victor
 */
public class ClientThread implements Runnable {
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private boolean running = true;
    private ClienteListener listener;
    private String address;
    private int port;

    public ClientThread(String address, int port, ClienteListener listener){
        this.address = address;
        this.port = port;
        this.listener = listener;
    }

    private void conectarSocket() {
        System.out.println("Conectando a "+address+":"+port);
        try {
            this.socket = new Socket(address, port);
            this.out = new ObjectOutputStream(socket.getOutputStream());
            this.in = new ObjectInputStream(socket.getInputStream());
            System.out.println("Conectado!");
        } catch (Exception e) {
            System.out.println("No se pudo conectar");
            e.printStackTrace();
        }
   }

   @Override
   public void run() {
        try {
            conectarSocket();
            while(running){
                Mensaje mensaje = (Mensaje)in.readObject();
                this.listener.atenderMensaje(mensaje);
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally{
            stopSocket();
        }
        
   }
   
   public void stopSocket(){
       running = false;
       try {
           socket.close();
       } catch (Exception e) {
            e.printStackTrace();
            //System.err.println(e.getMessage());
       }
   }

   public boolean isStoped(){
       return !running;
   }
   
   public void enviarRespuesta(Respuesta respuesta){
       try {
           out.writeObject(respuesta);
       } catch (IOException ex) {
           ex.printStackTrace();
           //System.err.println(ex.getMessage());
       }
   }

   public interface ClienteListener{
        void atenderMensaje(Mensaje mensaje);
   }
}
