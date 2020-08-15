package modelos;

public class MensajeBuilder {
    public static final int OP_MINAR = 1;
    public static final int OP_VERIFICAR = 2;

    /**
     * Mensaje que ordena minar a partir de una palabra
     * @param palabra la palabra que se usara
     * @param nroCeros Cantidad de ceros al inicio del hash resultante
     * @return mensaje
     */
    public static Mensaje msjMinar(String palabra,int nroCeros){
        Mensaje mensaje = new Mensaje();
        mensaje.setTipo(OP_MINAR);
        mensaje.setPalabra(palabra);
        mensaje.setNroCeros(nroCeros);
        return mensaje;
    }

    /**
     * Mensaje que ordena verficar una key con la palabra
     * que estaba minandose
     * @param key Key encontrado en el minado
     * @param nroCeros Cantidad de ceros al inicio del hash resultante
     * @return  mensaje
     */
    public static Mensaje msjVerificarKey(String palabra,String key,int nroCeros){
        Mensaje mensaje = new Mensaje();
        mensaje.setTipo(OP_VERIFICAR);
        mensaje.setPalabra(palabra);
        mensaje.setKey(key);
        mensaje.setNroCeros(nroCeros);
        return mensaje;
    }
}