package bcserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Victor
 */
public class ServerThread extends Thread{
    private List<ServerMinero> mineros;
    
    
    public ServerThread(){
        mineros = new ArrayList<>();
    }

    @Override
    public void run() {
        try {
            ServerSocket server = new ServerSocket(5555);
            
            while(true){
                Socket socket = server.accept();
                System.out.println("Nueva conexion!");
                ServerMinero minero = new ServerMinero(socket);
                mineros.add(minero);
                minero.start();
            }
            
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
    }
    
    public void enviarUpdates(){
        Thread hilo = new Thread(new Runnable() {
            @Override
            public void run() {
                int total;
                while(true){
                    total = mineros.size();
                    
                    /*
                    System.out.println("total: "+total);
                    for(int i = 0; i < total; i++){
                        try{
                            mineros.get(i).enviar(prueba1);
                            CoreJuego(mineros.get(i));
                        }catch (IOException ex) {
                            //ex.printStackTrace();
                        }
                    }*/
                    
                    
                    
                    /*
                    for(int i = 0; i < total; i++){
                        try{
                            mineros.get(i).enviar(prueba1);
                        }catch (IOException ex) {
                            //ex.printStackTrace();
                        }
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {}
                    total = mineros.size();
                    System.out.println("total: "+total);
                    for(int i = 0; i < total; i++){
                        try {
                            mineros.get(i).enviar(prueba2);
                        } catch (IOException ex) {
                            //ex.printStackTrace();
                        }
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {}*/
                    
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {}
                    total = mineros.size();
                    System.out.println("total: "+total);
                    for(int i = 0; i < total; i++){
                        try {
                            mineros.get(i).enviar(transformarCadena(mapaCaracter));
                            Core(mineros.get(i));
                        } catch (IOException ex) {
                            //ex.printStackTrace();
                        }
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {}
                }
            }
        });
        hilo.start();
    }
    
    public void Core(ServerMinero miner){
        
    }
    
}
