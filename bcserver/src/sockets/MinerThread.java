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
            this.out = new ObjectOutputStream(socket.getOutputStream());
            this.in = new ObjectInputStream(socket.getInputStream());
            System.out.println("Minero-"+id.toString()+" conectado!");
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void enviarMensaje(Mensaje mensaje) throws Exception{
        try {
            this.out.reset();
            this.out.writeObject(mensaje);
        } catch (IOException e) {
            stop();
            throw new Exception("Hubo una desconeccion");
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