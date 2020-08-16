package sockets;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import modelos.Respuesta;
import sockets.MinerThread.MineroListener;

public class ServerThread implements Runnable, MineroListener {

    private Map<Integer, MinerThread> mineros;

    private ExecutorService excsrv = Executors.newFixedThreadPool(8);
    private ServerSocket serverSocket;
    private boolean running = true;
    protected Thread runningThread = null;
    private int port;
    private ServerListener listener;

    public ServerThread(int port,ServerListener listener) {
        this.port = port;
        this.listener = listener;
        this.mineros = new HashMap<Integer, MinerThread>();
    }

    /**
     * Abre el serversocket en el puerto especificado
     */
    private void abrirServer() {
        try {
            this.serverSocket = new ServerSocket(port);
            System.out.println("Servidor abierto en: "+port);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        /*
         * synchronized(this){ this.runningThread = Thread.currentThread(); }
         */
        abrirServer();
        int contador = 0;
        while (isRunning()) {
            Socket mineroSock = null;
            try {
                mineroSock = this.serverSocket.accept(); // Acepta conexion
                System.out.println("Nueva conexion desde "+mineroSock.getRemoteSocketAddress().toString());
                MinerThread minero = new MinerThread(mineroSock, ++contador, this);
                this.mineros.put(minero.getId(), minero);
                this.excsrv.execute(minero); // Ejecuta nuevo hilo de minero
                //Thread thread = new Thread(minero);
                //thread.start();
            } catch (IOException e) {
                if (!isRunning()) {
                    System.out.println("Servidor detenido");
                    break;
                }
                System.err.println(e.getMessage());
                e.printStackTrace();
            }
            
        }
        this.excsrv.shutdown(); // Apaga el executor service
    }

    private synchronized boolean isRunning() {
        return this.running;
    }

    public synchronized void stop() {
        this.running = false;
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void atenderRespuesta(Integer idMinero, Respuesta mensaje) {
        this.listener.atenderRespuesta(idMinero, mensaje);
    }

    @Override
    public void eliminarMinero(Integer idMinero) {
        this.mineros.remove(idMinero);
    }

    public Map<Integer, MinerThread> getMineros() {
        return this.mineros;
    }

    public interface ServerListener {
        /**
         * Atiende respuesta del minero, puede ser cuando encontro
         * un key o cuando verifica uno.
         * @param idMinero Id del minero
         * @param respuesta Respuesta, depende del caso
         */
        void atenderRespuesta(Integer idMinero, Respuesta respuesta);
    }

}