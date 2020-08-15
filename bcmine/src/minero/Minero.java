package minero;

import modelos.Mensaje;
import sockets.ClientThread;
import sockets.ClientThread.ClienteListener;

public class Minero implements ClienteListener {
    private ClientThread socketThread;  //Maneja la conexion con el server

    public Minero(String host,int puerto){
        this.socketThread = new ClientThread(host, puerto,this);
    }

    public static void main(String[] args) {

        String host = "xx";
        int puerto = 5555;
        System.out.println("Host: " + host + "\nPuerto: " + puerto);

        
        //socketThread.start();
        

        SHAone digester = new SHAone();
        int sumz = 0;
        int zeros = 4;
        for (int i = 0; i < 100000; i++) {
            try {
                // String z = "13 > 34 100 " + String.valueOf(i);

                String z = "13 > 34 100 " + String.valueOf((int) (Math.random() * 10000000));
                System.out.println("Message: z:" + z);
                byte[] dataBuffer = (z).getBytes();
                String thedigest = digester.Encript(dataBuffer);
                // System.out.println("out"+i+":"+thedigest+(
                // thedigest.charAt(0)=='0'&&thedigest.charAt(0)=='1'?":):):):):)":"--"));
                System.out.println("out" + i + ":" + thedigest);

                // if (zeros == 1) {
                // if (thedigest.charAt(0) == '0') {
                // break;
                // }
                // } else if(zeros == 2){
                // if (thedigest.charAt(0) == '0' && thedigest.charAt(1) == '0') {
                // break;
                // }
                // } else {
                // }
                sumz = 0;
                for (int j = 0; j < zeros; j++) {
                    if (thedigest.charAt(j) == '0') {
                        sumz = sumz + 1;
                    }
                }
                if (sumz == zeros) {
                    break;
                }

            } catch (Exception ex) {
            }

        }
    }

    @Override
    public void atenderMensaje(Mensaje mensaje) {
        // TODO Auto-generated method stub

    }
}
