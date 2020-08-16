import server.ServerCore;

public class App {
    public static void main(String[] args) throws Exception {
        ServerCore core = new ServerCore();
        core.iniciarComunicaciones();
        
        while(true){
            Thread.sleep(10000);
            System.out.println("Envia ping");
            core.ping();
        }
    }
}
