package bcmine;

import java.io.Serializable;

public class Mensaje implements Serializable
    
private int op;
private String body;

public int getOp() {
    return op;
    }

public void setOp(int op) {
    this.op = op;
    }

public String getBody() {
    return body;
    }

public void setBody(String body) {
    this.body = body;
    }

    
}