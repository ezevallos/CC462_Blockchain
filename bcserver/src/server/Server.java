package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import server.Minero.MineroListener;

public class Server implements Runnable, MineroListener {

    private Map<Integer,Minero> mineros;
    private ExecutorService excsrv = Executors.newFixedThreadPool(8);
    private ServerSocket serverSocket;
    private boolean running = true;
    protected Thread runningThread = null;
    private int port;

    public Server(int port) {
        this.port = port;
        this.mineros = new HashMap<Integer,Minero>();
    }

    private void abrirServer() {
        try {
            this.serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void run() {
        /*synchronized(this){
            this.runningThread = Thread.currentThread();
        }*/
        abrirServer();
        int contador = 0;
        while(isRunning()){
            Socket mineroSock = null;
            try{
                mineroSock = this.serverSocket.accept(); //Acepta conexion
            }catch(IOException e){
                if(!isRunning()){
                    System.out.println("Servidor detenido");
                    break;
                }
                System.err.println(e.getMessage());
            }
            Minero minero = new Minero(mineroSock, ++contador,this);
            this.mineros.put(minero.getId(), minero);
            this.excsrv.execute(minero); //Ejecuta nuevo hilo de minero
        }
        this.excsrv.shutdown(); //Apaga el executor service
    }

    private synchronized boolean isRunning(){
        return this.running;
    }

    public synchronized void stop(){
        this.running = false;
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void leerMinero(Integer id, Mensaje mensaje) {
        // TODO Auto-generated method stub

    }

    public void cargarLista(String path){
        
    }
    
}