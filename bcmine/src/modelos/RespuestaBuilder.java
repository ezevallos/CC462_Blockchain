package modelos;

public class RespuestaBuilder {
    public static final int RESP_MINAR = 1;
    public static final int RESP_VERIFICAR = 2;

    public static Respuesta respMinar(Datos datos){
        Respuesta respuesta = new Respuesta();
        respuesta.setTipo(RESP_MINAR);
        respuesta.setDatos(datos);
        return respuesta;
    }

    public static Respuesta respVerficar(Datos datos,boolean confirma){
        Respuesta respuesta = new Respuesta();
        respuesta.setTipo(RESP_VERIFICAR);
        respuesta.setDatos(datos);
        respuesta.setVerifica(confirma);
        return respuesta;
    }
}