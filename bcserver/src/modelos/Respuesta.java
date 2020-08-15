package modelos;

import java.io.Serializable;

public class Respuesta implements Serializable{
    private static final long serialVersionUID = 1L;

    private int tipo;
    private String key;
    private long numIter;
    private long tiempoMs;
    private boolean verifica;

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getNumIter() {
        return numIter;
    }

    public void setNumIter(long numIter) {
        this.numIter = numIter;
    }

    public long getTiempoMs() {
        return tiempoMs;
    }

    public void setTiempoMs(long tiempoMs) {
        this.tiempoMs = tiempoMs;
    }

    public boolean isVerifica() {
        return verifica;
    }

    public void setVerifica(boolean verifica) {
        this.verifica = verifica;
    }
}