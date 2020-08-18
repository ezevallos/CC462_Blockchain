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
    private boolean isRunning;
    private MineroItf itf;
    
    public MineroThread (String palabra, int nroCeros, ClientThread clientThread, ExecutorService ex, MineroItf itf) {
        this.palabra = palabra;
        this.nroCeros = nroCeros;
        this.clientThread = clientThread;
        this.ex = ex;
        this.isRunning = true;
        this.itf = itf;
    }
    
    @Override
    public void run() {
        SHAone digester = new SHAone();
        int sumz = 0;
        int zeros = nroCeros;
        Datos datos = new Datos();
        datos.setPalabra(palabra);
        long timeStart = System.currentTimeMillis();
        int i = 0; 
        while (isRunning && i < 100000) {
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
                    itf.detieneThreads();
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
    
    public void setRunning() {
        isRunning = false;
    }
    
    public interface MineroItf {
        void detieneThreads();
    }
}