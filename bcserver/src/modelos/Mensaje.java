package modelos;

import java.io.Serializable;

public class Mensaje implements Serializable{
    private static final long serialVersionUID = -3958426687533929032L;

    private int tipo;
    private String palabra;
    private String key;
    private int nroCeros;

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int op) {
        this.tipo = op;
    }

    public String getPalabra() {
        return palabra;
    }

    public void setPalabra(String palabra) {
        this.palabra = palabra;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getNroCeros() {
        return nroCeros;
    }

    public void setNroCeros(int nroCeros) {
        this.nroCeros = nroCeros;
    }
}