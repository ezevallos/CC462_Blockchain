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
    private int nroCeros; // Cantidad de ceros al principio del hash resultado
    private String palabraActual; // Palabra actual que se esta minando
    private String archivoSalida; // Nombre de archivo de salida donde se guarda los datos verificados

    public ServerCore() {
        serverThread = new ServerThread(5555, this);
        mineros = serverThread.getMineros();
    }

    public void iniciarComunicaciones(){
        Thread thread = new Thread(this.serverThread);
        thread.start();
    }

    /**
     * Fija los numeros de ceros al inicio del hash resultante 
     * Ejemplo: Sha1(palabra + key) = 000xxxxxxx
     * @param nroCeros Cantidad de ceros delante
     */
    public void setNroCeros(int nroCeros) {
        this.nroCeros = nroCeros;
    }

    /**
     * Se carga la lista de palabras que luego se enviaran para encontrar los keys
     * en Sha1(palabra+key).
     * 
     * @param path Ruta del archivo de texto
     */
    public void cargarPalabras(String path) {
        if (palabras == null)
            palabras = new LinkedList<>();
        File file = new File(path);
        Scanner sc;
        try {
            sc = new Scanner(file);
            while (sc.hasNextLine())
                palabras.offer(sc.nextLine());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    /**
     * Crea un archivo de texto donde se guardara posteriormente los datos
     */
    public void crearArchivoSalida() {
        archivoSalida = "salida" + System.currentTimeMillis()+".txt";
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
        if (palabraActual != null && nroCeros > 0) {
            Mensaje mensaje = MensajeBuilder.msjMinar(palabraActual, nroCeros);
            Map<Integer, MinerThread> mineros_c = new HashMap<>(mineros);
            for (Integer id : mineros_c.keySet()) {
                // Broadcasting.
                MinerThread minero = mineros_c.get(id);
                minero.enviarMensaje(mensaje);
            }
        }
    }

    /**
     * Se envia el key a los mineros para que lo verfiquen
     * @param idMiner Id del minero que encontro el key
     * @param key     El key que encontro
     */
    public void verificar(Integer idMiner, Datos datos) {
        if(datos.getPalabra().equals(palabraActual)){
            Mensaje mensaje = MensajeBuilder.msjVerificarKey(datos.getPalabra(),datos.getKey(), nroCeros);
            Map<Integer, MinerThread> mineros_c = new HashMap<>(mineros);
            for (Integer id : mineros_c.keySet()) {
                // Envia a todos menos al que lo encontro.
                if (!id.equals(idMiner)) {
                    MinerThread minero = mineros_c.get(id);
                    minero.enviarMensaje(mensaje);
                }
            }
        }
    }

    /**
     * Guarda los datos de respuesta que fueron confirmados
     * en el archivo de salida
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
     * @param fileName nombre de archivo
     * @param line Linea a escribir
     * @throws IOException
     */
    public void writeLine(String fileName, String line) throws IOException {
        Writer output = new BufferedWriter(new FileWriter(fileName,true));
        output.append(line);
        output.close();
    }

    /**
     * Se atiende la respuesta del minero cuando encuentra una Key
     * @param idMinero
     * @param respuesta
     */
    public void respMinar(Integer idMinero, Respuesta respuesta){
        Datos datos = respuesta.getDatos();
        datos.setIdMinero(idMinero);
        if(palabraActual.equals(datos.getPalabra())){   //Si no es se descarta
            colaVerificacion.offer(datos);  //Encola
            if(colaVerificacion.size()==1){ //Si esta en la cabeza
                verificar(idMinero, datos); //Envia a verificar a los demas
            }
        }
    }

    /**
     * Se atiende la respuesta del minero cuando verifica una key
     * @param idMinero
     * @param respuesta
     */
    public void respVerificar(Integer idMinero, Respuesta respuesta){
        
    }

    public void mostrar(Integer idMinero, Respuesta respuesta){
        System.out.println("Minero-"+idMinero.toString()+": "+respuesta.isVerifica());
    }

    @Override
    public void atenderRespuesta(Integer idMinero, Respuesta respuesta) {
        switch(respuesta.getTipo()){
            case RESP_MINAR:
                respMinar(idMinero, respuesta);
                break;
            case RESP_VERIFICAR:
                respVerificar(idMinero, respuesta);
                break;
            default:
                mostrar(idMinero,respuesta);
                break;
        }
    }


    public void ping(){
        Mensaje mensaje = new Mensaje();
        mensaje.setTipo(3);
        mensaje.setPalabra("Esto es una prueba");
        Map<Integer, MinerThread> mineros_c = new HashMap<>(mineros);
        for (Integer id : mineros_c.keySet()) {
            // Envia a todos menos al que lo encontro.
            MinerThread minero = mineros_c.get(id);
            minero.enviarMensaje(mensaje);
            System.out.println("Envia ping a minero-"+minero.getId().toString());
        }
    }
}