package modelos;

import java.io.Serializable;

public class Respuesta implements Serializable{
    private static final long serialVersionUID = -53487644710116311L;

    private int tipo;
    private Datos datos;
    private boolean verifica;

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public boolean isVerifica() {
        return verifica;
    }

    public void setVerifica(boolean verifica) {
        this.verifica = verifica;
    }

    public Datos getDatos() {
        return datos;
    }

    public void setDatos(Datos datos) {
        this.datos = datos;
    }

    
}