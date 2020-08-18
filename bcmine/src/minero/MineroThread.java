package minero;

import modelos.Datos;
import modelos.Mensaje;
import modelos.Respuesta;
import modelos.RespuestaBuilder;
import sockets.ClientThread;
import sockets.ClientThread.ClienteListener;

import org.apache.commons.lang3.RandomStringUtils;
import java.util.concurrent.ExecutorService;

public class MineroThread implements Runnable {
    private String palabra;
    private int nroCeros;
    private ClientThread clientThread;
    private ExecutorService ex;
    
    public MineroThread (String palabra, int nroCeros, ClientThread clientThread, ExecutorService ex) {
        this.palabra = palabra;
        this.nroCeros = nroCeros;
        this.clientThread = clientThread;
        this.ex = ex;
    }
    
    @Override
    public void run() {
        SHAone digester = new SHAone();
        int sumz = 0;
        int zeros = nroCeros;
        for (int i = 0; i < 100000; i++) {
            try {
                // String z = "13 > 34 100 " + String.valueOf(i);
                String key = randomAlphanumeric(10,11);
                String z = palabra + key;
                System.out.println("Message: z:" + z);
                byte[] dataBuffer = (z).getBytes();
                String thedigest = digester.Encript(dataBuffer);
                // System.out.println("out"+i+":"+thedigest+(
                // thedigest.charAt(0)=='0'&&thedigest.charAt(0)=='1'?":):):):):)":"--"));
                System.out.println("out" + i + ":" + thedigest);

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
                clientThread.enviarRespuesta(RespuestaBuilder.respMinar(datos));
                ex.shutdownNow();
                break;
            }                       
            } catch (Exception ex) {
            }

        }
    }
    
}