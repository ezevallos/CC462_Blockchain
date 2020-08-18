package minero;

import modelos.Datos;
import modelos.Mensaje;
import modelos.Respuesta;
import modelos.RespuestaBuilder;
import sockets.ClientThread;
import sockets.ClientThread.ClienteListener;

import org.apache.commons.lang3.RandomStringUtils;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.ArrayList;
import java.util.List;

public class Minero implements ClienteListener {
    private ClientThread clientThread;  //Maneja la conexion con el server
    private static final int OP_MINAR = 1;
    private static final int OP_VERIFICAR = 2;
    private ExecutorService ex = Executors.newFixedThreadPool(4);

    public Minero(String host,int puerto){
        this.clientThread = new ClientThread(host, puerto,this);
    }

    public void conectar(){
        Thread thread = new Thread(clientThread);
        thread.start();
    }

    public static void main(String[] args) {

        String host = "localhost";//args[1];
        int puerto = 5555; //Integer.parseInt(args[2]);
        System.out.println("Host: " + host + "\nPuerto: " + puerto);
        Minero minero = new Minero(host, puerto);
        minero.conectar();
        
    }

    /**
     * Se crea un hilo para hacer la busquea del KEY
     * @param palabra Palabra que se concatenara
     * @param nroCeros cantidad de ceros al inicio del hash
     */
    public void minar(String palabra,int nroCeros){
        //ex.shutdownNow();
        for(int i = 0; i < 4; i++) {
            Runnable runnable = new MineroThread(palabra,nroCeros,clientThread,ex);
            ex.execute(runnable);
        }
        
    }

    /**
     * Se crea un hilo para que verfique una palabra con su key
     * @param palabra
     * @param key
     * @param nroCeros
     */
    public void verificar(String palabra,String key, int nroCeros){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
           SHAone digester = new SHAone();
           int sumz = 0;
           int zeros = nroCeros;
           String z = palabra + key;
           System.out.println("Message: z:" + z);
           byte[] dataBuffer = (z).getBytes();
           String thedigest = digester.Encript(dataBuffer);
           System.out.println("Verificando palabra="+palabra+" key="+key);
           // thedigest.charAt(0)=='0'&&thedigest.charAt(0)=='1'?":):):):):)":"--"));
           System.out.println("A verificar out:" + thedigest);
           //String thedigest = digester.Encript(dataBuffer);
                    // System.out.println("out"+i+":"+thedigest+(
                    // thedigest.charAt(0)=='0'&&thedigest.charAt(0)=='1'?":):):):):)":"--"));
                    //System.out.println("out" + i + ":" + thedigest);

                    // if (zeros == 1) {
                    // if (thedigest.charAt(0) == '0') {
                    // break;
                    // }
                    // } else if(zeros == 2){
                    // if (thedigest.charAt(0) == '0' && thedigest.charAt(1) == '0') {
                    // break;
                    // }
                    // } else {
                    // }
                    sumz = 0;
                    for (int j = 0; j < zeros; j++) {
                        if (thedigest.charAt(j) == '0') {
                            sumz = sumz + 1;
                        }
                    }
                    Datos datos = new Datos();
                    datos.setPalabra(palabra);
                    datos.setKey(key);
                    if (sumz == zeros) {
                        clientThread.enviarRespuesta(RespuestaBuilder.respVerficar(datos,true));
                    } else {
                        clientThread.enviarRespuesta(RespuestaBuilder.respVerficar(datos,false));                        
                    }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }


    @Override
    public void atenderMensaje(Mensaje mensaje) {
        switch(mensaje.getTipo()){
            case OP_MINAR:
                minar(mensaje.getPalabra(),mensaje.getNroCeros());
                break;
            case OP_VERIFICAR:
                verificar(mensaje.getPalabra(), mensaje.getKey(),mensaje.getNroCeros());
                break;
            default:
                System.out.println(mensaje.getPalabra());
                pong();
                break;
        }
    }

    public void pong(){
        Respuesta respuesta = new Respuesta();
        respuesta.setTipo(3);
        respuesta.setVerifica(true);
        clientThread.enviarRespuesta(respuesta);
    }
}
