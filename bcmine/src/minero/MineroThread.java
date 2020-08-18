package minero;

import modelos.Datos;
import modelos.RespuestaBuilder;
import sockets.ClientThread;

import org.apache.commons.lang3.RandomStringUtils;

public class MineroThread implements Runnable {
    private String palabra;
    private int nroCeros;
    private ClientThread clientThread;
    private boolean isRunning;
    private MineroItf itf;
    
    public MineroThread (String palabra, int nroCeros, ClientThread clientThread, MineroItf itf) {
        this.palabra = palabra;
        this.nroCeros = nroCeros;
        this.clientThread = clientThread;
        this.isRunning = true;
        this.itf = itf;
    }
    
    @Override
    public void run() {
        long threadID = Thread.currentThread().getId();
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
                System.out.println("Thread-"+threadID+" Message: z:" + z);
                byte[] dataBuffer = (z).getBytes();
                String thedigest = digester.Encript(dataBuffer);
                System.out.println("Thread-"+threadID+" out" + i + ":" + thedigest);

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
                    datos.setNroCeros(nroCeros);
                    datos.setTiempoMs(timeEnd-timeStart);
                    datos.setNroIter(i);
                    clientThread.enviarRespuesta(RespuestaBuilder.respMinar(datos));
                    System.out.println("Thread-"+threadID+" Key="+key+" encontrado en "+datos.getTiempoMs()+" ms");
                    //ex.shutdownNow();
                    break;
                }                       
                } catch (Exception ex) {}
            i++; 
        }
    }
    
    public void setRunning() {
        isRunning = false;
    }
    
    public interface MineroItf {
        void detieneThreads();
    }
}