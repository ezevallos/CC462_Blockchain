package sockets;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import modelos.Mensaje;
import modelos.Respuesta;

public class MinerThread implements Runnable {
    private Socket socket;
    private Integer id;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private boolean running = true;
    private MineroListener listener;

    public MinerThread(Socket socket, Integer id,MineroListener listener) {
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
            this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.listener.eliminarMinero(this.id);
    }

    @Override
    public void run() {
        inicializarStreams();
        while (running) {
            try {
                Respuesta respuesta = (Respuesta) this.in.readObject();
                this.listener.atenderRespuesta(this.id,respuesta);
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
        void atenderRespuesta(Integer idMinero, Respuesta mensaje);
        void eliminarMinero(Integer idMinero);
    }

    public Integer getId() {
        return id;
    }

}