package server;

import java.awt.Dimension;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import javafx.event.ActionEvent;
import server.ServerCore.CoreListener;

public class Gui extends JFrame implements CoreListener{
    public static int ALTURA = 500;
    public static int ANCHO = 500;
    public static String TITULO = "Block-Chain";
    private JButton btnCargar;
    private JTextArea taPalabras;
    private JLabel jcomp3;
    private JLabel jcomp4;
    private JTextField tfCeros;
    private JButton btnCeros;
    private JLabel lblCeros;
    private JTextArea taKeys;
    private JButton btnMinar;
    private JLabel jcomp10;

    public Gui() {
        // setSize(ANCHO, ALTURA);
        setTitle(TITULO);
        // construct components
        btnCargar = new JButton("Cargar Palabras");
        taPalabras = new JTextArea(5, 5);
        jcomp3 = new JLabel("Palabras");
        jcomp4 = new JLabel("Nro de Ceros actuales:");
        tfCeros = new JTextField(5);
        btnCeros = new JButton("Fijar Nro de ceros");
        lblCeros = new JLabel("1");
        taKeys = new JTextArea(5, 5);
        btnMinar = new JButton("Minar");
        jcomp10 = new JLabel("Keys verificados");

        JScrollPane jsPalabras = new JScrollPane(taPalabras);
        JScrollPane jsKeys = new JScrollPane(taKeys);

        // adjust size and set layout
        setPreferredSize(new Dimension(613, 644));
        setLayout(null);

        // add components
        add(btnCargar);
        add(jsPalabras);
        add(jcomp3);
        add(jcomp4);
        add(tfCeros);
        add(btnCeros);
        add(lblCeros);
        add(jsKeys);
        add(btnMinar);
        add(jcomp10);

        // set component bounds (only needed by Absolute Positioning)
        btnCargar.setBounds(35, 25, 150, 25);
        jsPalabras.setBounds(40, 165, 265, 440);
        jcomp3.setBounds(125, 120, 100, 25);
        jcomp4.setBounds(35, 70, 145, 25);
        tfCeros.setBounds(300, 25, 100, 25);
        btnCeros.setBounds(415, 25, 135, 25);
        lblCeros.setBounds(220, 70, 100, 25);
        jsKeys.setBounds(330, 165, 245, 440);
        btnMinar.setBounds(440, 70, 100, 25);
        jcomp10.setBounds(395, 120, 115, 25);

        taPalabras.setEditable(false);
        taKeys.setEditable(false);

        btnCargar.addActionListener(e -> cargarPalabras());

        btnCeros.addActionListener(e -> fijarCeros());

        btnMinar.addActionListener(e -> minar());
    }

    private ServerCore core;

    public void iniciar(){
        core = new ServerCore(this);
        core.iniciarComunicaciones();
    }

    private void cargarPalabras(){
        JFileChooser fileChooser = new JFileChooser("./palabras/");
        int seleccion = fileChooser.showOpenDialog(this);
        if (seleccion == JFileChooser.APPROVE_OPTION){
            File fichero = fileChooser.getSelectedFile();
            //Guarda en core
            core.cargarPalabras(fichero);
            //Muestra en pantalla
            Scanner sc;
            try {
                sc = new Scanner(fichero);
                taPalabras.setText(null);
                while (sc.hasNextLine()){
                    taPalabras.append(sc.nextLine()+"\n");
                }
                sc.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void fijarCeros(){
        int numCeros = Integer.parseInt(tfCeros.getText());
        core.setNroCeros(numCeros);
        lblCeros.setText(""+numCeros);
    }

    private void minar(){
        btnMinar.setEnabled(false);
        btnCeros.setEnabled(false);
        btnCargar.setEnabled(false);
        core.minar();
    }

    @Override
    public void finalizaMinado() {
        taPalabras.setText(null);
        taKeys.setText(null);
        btnMinar.setEnabled(true);
        btnCargar.setEnabled(true);
        btnCeros.setEnabled(true);
        JOptionPane.showMessageDialog(this, "Minado finalizado", "Fin", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void muestraKey(String key) {
        taKeys.append(key+"\n");
    }
    
}