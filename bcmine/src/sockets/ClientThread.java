package sockets;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

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

    private void conectarSocket() throws UnknownHostException, IOException {
        socket = new Socket(address, port);
        in = new ObjectInputStream(socket.getInputStream());
        out = new ObjectOutputStream(socket.getOutputStream());
   }

   @Override
   public void run() {
        try {
            conectarSocket();
            while(running){
                Mensaje mensaje = (Mensaje)in.readObject();
                this.listener.atenderMensaje(mensaje);
            }
        } catch (UnknownHostException e) {
            System.err.println("Imposible conectar a host");
        } catch (IOException | ClassNotFoundException e) {
            System.err.println(e.getMessage());
        } finally{
            stopSocket();
        }
        
   }
   
   public void stopSocket(){
       running = false;
       try {
           socket.close();
       } catch (IOException e) {
            System.err.println(e.getMessage());
       }
   }

   public boolean isStoped(){
       return !running;
   }
   
   public void enviarRespuesta(Respuesta respuesta){
       try {
           out.writeObject(respuesta);
       } catch (IOException ex) {
           System.err.println(ex.getMessage());
       }
   }

   public interface ClienteListener{
        void atenderMensaje(Mensaje mensaje);
   }
}
