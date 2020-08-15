package modelos;

public class Datos {
    public static final String HEADERS= "palabra key nroCeros nroIter tiempoMs";
    private Integer idMinero;
    private String palabra;
    private String key;
    private int nroCeros;
    private long nroIter;
    private long tiempoMs;

    public Datos() {}

    public Datos(Integer idMinero, String palabra, String key, int nroCeros, long nroIter, long tiempoMs) {
        this.idMinero = idMinero;
        this.palabra = palabra;
        this.key = key;
        this.nroCeros = nroCeros;
        this.nroIter = nroIter;
        this.tiempoMs = tiempoMs;
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

    public long getNroIter() {
        return nroIter;
    }

    public void setNroIter(long nroIter) {
        this.nroIter = nroIter;
    }

    public long getTiempoMs() {
        return tiempoMs;
    }

    public void setTiempoMs(long tiempoMs) {
        this.tiempoMs = tiempoMs;
    }

    @Override
    public String toString() {
        return palabra + " " + key + " " + nroCeros + " " + nroIter + " " + tiempoMs;
    }

    public Integer getIdMinero() {
        return idMinero;
    }

    public void setIdMinero(Integer idMinero) {
        this.idMinero = idMinero;
    }

    
}