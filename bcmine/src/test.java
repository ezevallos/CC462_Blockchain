import minero.Minero;

public class test {
    public static void main(String[] args) {
        Minero minero = new Minero("localhost", 5555);
        minero.conectar();
        //Scanner sc = new Scanner(System.in);
        //sc.nextLine();
    }
}