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
        Datos datos = new Datos();
        datos.setPalabra(palabra);
        long timeStart = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            try {
                
                String key = RandomStringUtils.randomAlphanumeric(10,11);
                String z = palabra + key;
                System.out.println("Message: z:" + z);
                byte[] dataBuffer = (z).getBytes();
                String thedigest = digester.Encript(dataBuffer);
                System.out.println("out" + i + ":" + thedigest);

                sumz = 0;
                for (int j = 0; j < zeros; j++) {
                    if (thedigest.charAt(j) == '0') {
                        sumz = sumz + 1;
                    }
                }
                
                if (sumz == zeros) {
                    long timeEnd = System.currentTimeMillis();
                    datos.setKey(key);
                    datos.setTiempoMs(timeEnd-timeStart);
                    datos.setNroIter(i);
                    clientThread.enviarRespuesta(RespuestaBuilder.respMinar(datos));
                    //ex.shutdownNow();
                    break;
                }                       
                } catch (Exception ex) {
                }

        }
    }
    
}