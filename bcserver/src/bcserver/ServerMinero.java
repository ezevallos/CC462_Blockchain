package bcserver;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Victor
 */
public class ServerMinero extends Thread{
    Socket socket;
    DataInputStream in;
    DataOutputStream out;
    
    //Codigo agregado para obtener el estado de los
    boolean state_jugador;
    // atributos
    
    public void setState_jugador(boolean state_jugador) {
        this.state_jugador = state_jugador;
    }
    
    public boolean isState_jugador() {
        return state_jugador;
    }
    
    public ServerMinero(Socket socket){
        this.socket = socket;
    }
    
    @Override
    public void run(){
        try {
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            
            while(true){
                char key = in.readChar();
                System.out.println(key);
                // readObject()
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    public void enviar(String msg) throws IOException{
        out.writeUTF(msg);
    }
}
