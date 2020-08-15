package modelos;

import java.io.Serializable;

public class Mensaje implements Serializable{
    private static final long serialVersionUID = -3958426687533929032L;
    
    private int tipo;
    private String body;
    private int nroCeros;

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int op) {
        this.tipo = op;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getNroCeros() {
        return nroCeros;
    }

    public void setNroCeros(int nroCeros) {
        this.nroCeros = nroCeros;
    }
}