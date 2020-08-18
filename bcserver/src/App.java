import server.Gui;

public class App {
    public static void main(String[] args) throws Exception {
        
        Gui gui = new Gui();
        gui.setDefaultCloseOperation (Gui.EXIT_ON_CLOSE);
        //gui.getContentPane().add (new MyPanel());
        gui.pack();
        gui.setLocationRelativeTo(null);
        gui.setResizable(false);
        gui.iniciar();
        gui.setVisible (true);

    }

    
}
