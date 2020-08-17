import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import javax.swing.JFrame;

import server.Gui;
import server.ServerCore;

public class App {
    public static void main(String[] args) throws Exception {
        
        Gui gui = new Gui();
        gui.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
        //gui.getContentPane().add (new MyPanel());
        gui.pack();
        gui.setLocationRelativeTo(null);
        gui.setResizable(false);
        gui.iniciar();
        gui.setVisible (true);

    }

    
}
