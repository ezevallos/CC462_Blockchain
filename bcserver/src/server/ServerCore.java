package server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;

import modelos.Datos;
import modelos.Mensaje;
import modelos.MensajeBuilder;
import modelos.Respuesta;
import sockets.MinerThread;
import sockets.ServerThread;
import sockets.ServerThread.ServerListener;

public class ServerCore implements ServerListener {
    private static final int RESP_MINAR = 1;
    private static final int RESP_VERIFICAR = 2;
    private Queue<String> palabras; // Contiene las palabras que se les buscara el key en el minado
    private Map<Integer, MinerThread> mineros;
    private Queue<Datos> colaVerificacion; // Contiene datos a verificar como el key
    private ServerThread serverThread; // Maneja la comunicacion socket
    private int nroCeros = 1; // Cantidad de ceros al principio del hash resultado
    private String palabraActual; // Palabra actual que se esta minando
    private String archivoSalida; // Nombre de archivo de salida donde se guarda los datos verificados
    //private static final String DIR_PALABRAS = "./palabras/";
    private int numMinando, numVerifican, numConfirman, numRespVer;
    private CoreListener listener;
    private Datos datoVerificar;

    public ServerCore(CoreListener listener) {
        serverThread = new ServerThread(5555, this);
        mineros = serverThread.getMineros();
        palabras = new LinkedList<>();
        colaVerificacion = new LinkedList<>();
        this.listener = listener;
    }

    public void runCore() {
        iniciarComunicaciones();
    }

    public void iniciarComunicaciones() {
        Thread thread = new Thread(this.serverThread);
        thread.start();
    }

    /**
     * Fija los numeros de ceros al inicio del hash resultante Ejemplo: Sha1(palabra
     * + key) = 000xxxxxxx
     * 
     * @param nroCeros Cantidad de ceros delante
     */
    public void setNroCeros(int nroCeros) {
        this.nroCeros = nroCeros;
        System.out.println("Número de ceros: " + this.nroCeros);
    }

    /**
     * Se carga la lista de palabras que luego se enviaran para encontrar los keys
     * en Sha1(palabra+key).
     * 
     * @param path Ruta del archivo de texto
     */
    public void cargarPalabras(File file) {
        palabras.clear();
        Scanner sc;
        try {
            sc = new Scanner(file);
            while (sc.hasNextLine())
                palabras.offer(sc.nextLine());
            sc.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println("Se cargó " + palabras.size() + " palabras");
    }

    /**
     * Crea un archivo de texto donde se guardara posteriormente los datos
     */
    public void crearArchivoSalida() {
        archivoSalida = "salida" + System.currentTimeMillis() + ".txt";
        try {
            writeLine(archivoSalida, Datos.HEADERS);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Se envia una palabra de la cola para que todos los mineros busquen un key en:
     * Sha1(palabra + key)
     */
    public void minar() {
        palabraActual = palabras.poll();
        colaVerificacion.clear();
        datoVerificar = null;
        if (palabraActual != null && nroCeros > 0) {
            numMinando = 0;
            Mensaje mensaje = MensajeBuilder.msjMinar(palabraActual, nroCeros);
            Map<Integer, MinerThread> mineros_c = new HashMap<>(mineros);
            for (Integer id : mineros_c.keySet()) {
                // Broadcasting.
                MinerThread minero = mineros_c.get(id);
                try {
                    minero.enviarMensaje(mensaje);
                    // System.out.println("Se envia "+palabraActual+" con "+nroCeros+" ceros a
                    // minero-"+minero.getId().toString());
                    numMinando++;
                } catch (Exception e) {
                    // e.printStackTrace();
                }
            }
            System.out.println("----------------------------------------------------------------------------------------------");
            System.out.println("Se envió palabra=" + palabraActual + " con " + nroCeros + " ceros a " + numMinando + " mineros");
        } else {
            listener.finalizaMinado();
            System.out.println("Nada que minar");
        }
    }

    /**
     * Se envia el key a los mineros para que lo verfiquen
     * 
     * @param idMiner Id del minero que encontro el key
     * @param key     El key que encontro
     */
    public void verificar(Integer idMiner, Datos datos) {
        if (datos.getPalabra().equals(palabraActual)) {
            datoVerificar = datos;
            numConfirman = 0;
            numVerifican = 0;
            numRespVer = 0;
            Mensaje mensaje = MensajeBuilder.msjVerificarKey(datoVerificar.getPalabra(), datoVerificar.getKey(), nroCeros);
            Map<Integer, MinerThread> mineros_c = new HashMap<>(mineros);
            for (Integer id : mineros_c.keySet()) {
                // Envia a todos menos al que lo encontro.
                if (!id.equals(idMiner)) {
                    MinerThread minero = mineros_c.get(id);
                    try {
                        minero.enviarMensaje(mensaje);
                        numVerifican++;
                    } catch (Exception e) {
                        // e.printStackTrace();
                    }
                }
            }
            System.out.println("Se mando verificar key="+datos.getKey()+" a "+numVerifican+" mineros");
        }
    }

    /**
     * Guarda los datos de respuesta que fueron confirmados en el archivo de salida
     * 
     * @param datos Contiene la palabra, key, nroCeros, nroIter, tiempoMS
     */
    public void guardarBloque(Datos datos) {
        if (datos != null) {
            try {
                writeLine(archivoSalida, datos.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Escribe una linea al final del archivo especificado
     * 
     * @param fileName nombre de archivo
     * @param line     Linea a escribir
     * @throws IOException
     */
    public void writeLine(String fileName, String line) throws IOException {
        Writer output = new BufferedWriter(new FileWriter(fileName, true));
        output.append(line+"\n");
        output.close();
    }

    /**
     * Se atiende la respuesta del minero cuando encuentra una Key
     * 
     * @param idMinero
     * @param respuesta
     */
    public synchronized void respMinar(Integer idMinero, Respuesta respuesta) {
        Datos datos = respuesta.getDatos();
        datos.setIdMinero(idMinero);
        if (palabraActual!=null && palabraActual.equals(datos.getPalabra())) { // Si no es se descarta
            System.out.println("Minero-"+idMinero.toString()+" encontro el key="+datos.getKey());
            colaVerificacion.offer(datos); // Encola
            if (colaVerificacion.size() == 1) { // Si esta en la cabeza
                verificar(idMinero, datos); // Envia a verificar a los demas
            }
        }
    }

    /**
     * Se atiende la respuesta del minero cuando verifica una key
     * 
     * @param idMinero
     * @param respuesta
     */
    public synchronized void respVerificar(Integer idMinero, Respuesta respuesta) {
        Datos datos = respuesta.getDatos();
        numRespVer++;
        if (datoVerificar!=null && datoVerificar.getPalabra().equals(datos.getPalabra()) 
            && datoVerificar.getKey().equals(datos.getKey()) ) { // Si no es se descarta
            if(respuesta.isVerifica()){
                numConfirman++;
                System.out.println("Minero-"+idMinero.toString()+" confirma el key="+datoVerificar.getKey());
            }else{
                //Falla verificacion
                System.out.println("Minero-"+idMinero.toString()+" NO Confirma el key="+datoVerificar.getKey());
                System.out.println("Key="+datoVerificar.getKey()+" No se pudo confirmar!");
                colaVerificacion.poll();
                Datos datos2 = colaVerificacion.peek(); //Pasa al siguiente verificar
                if(datos2!=null)
                    verificar(datos2.getIdMinero(), datos2);
                return;
            }
            if(numConfirman == numVerifican){   //ASUMO QUE NINGUNO SE DESCONECTA
                //Exito
                System.out.println("Key="+datoVerificar.getKey()+" Confirmado!");
                listener.muestraKey(datoVerificar.getKey());
                guardarBloque(datoVerificar);   //Guarda
                minar();    //Pasa a la siguiente palabra
            }
        }
    }

    public void mostrar(Integer idMinero, Respuesta respuesta) {
        System.out.println("Minero-" + idMinero.toString() + ": " + respuesta.isVerifica());
    }

    @Override
    public void atenderRespuesta(Integer idMinero, Respuesta respuesta) {
        switch (respuesta.getTipo()) {
            case RESP_MINAR:
                respMinar(idMinero, respuesta);
                break;
            case RESP_VERIFICAR:
                respVerificar(idMinero, respuesta);
                break;
            default:
                mostrar(idMinero, respuesta);
                break;
        }
    }

    /**
     * Solo de prueba
     */
    public void ping() {
        Mensaje mensaje = new Mensaje();
        mensaje.setTipo(3);
        mensaje.setPalabra("Esto es una prueba");
        Map<Integer, MinerThread> mineros_c = new HashMap<>(mineros);
        for (Integer id : mineros_c.keySet()) {
            // Envia a todos menos al que lo encontro.
            MinerThread minero = mineros_c.get(id);
            try {
                minero.enviarMensaje(mensaje);
                System.out.println("Envia ping a minero-"+minero.getId().toString());
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public interface CoreListener{
        void finalizaMinado();
        void muestraKey(String key);
    }
}