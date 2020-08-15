package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Minero implements Runnable {
    private Socket socket;
    private Integer id;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private boolean running = true;
    private MineroListener listener;

    public Minero(Socket socket, Integer id,MineroListener listener) {
        this.socket = socket;
        this.id = id;
        this.listener = listener;
    }

    private void inicializarStreams() {
        try {
            this.in = new ObjectInputStream(socket.getInputStream());
            this.out = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
            stop();
        }
    }

    private void stop() {
        this.running = false; // Se cancela
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        inicializarStreams();
        while (running) {
            try {
                Mensaje mensaje = (Mensaje) this.in.readObject();
                this.listener.leerMinero(this.id,mensaje);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void enviarMensaje(Mensaje mensaje){
        try {
            this.out.writeObject(mensaje);
        } catch (IOException e) {
            e.printStackTrace();    //Hubo alguna desconexion
            stop();
        }
    }

    public boolean isStoped(){
        return !this.running;
    }
    
    public interface MineroListener{
        void leerMinero(Integer id, Mensaje mensaje);
    }

    public Integer getId() {
        return id;
    }

}