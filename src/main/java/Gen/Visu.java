package Gen;

import TT.Tetris;
import TT2.Servidor;
import TT2.Tetrisdos;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Visu extends JFrame implements ActionListener {
    private JPanel jpanel;
    private JButton uno = new JButton("Single Player");
    private JButton dos = new JButton("Multiplayer ");
    private JLabel panel;
    private JTextField usuario = new JTextField("Nick name");
    private int puerto;

    public Visu() {
        setSize(700, 500);
        setTitle("TI");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(700, 500));
        setLocationRelativeTo(null);
        Panel();
        setVisible(true);
    }

    private void Panel() {
        jpanel = new JPanel();
        jpanel.setLayout(null);
        jpanel.setBackground(new Color(255, 188, 88));
        panel = new JLabel(new ImageIcon("src/main/java/Imagen/fon.png"));
        panel.setBounds(-125, -250, 960, 958);
        jpanel.add(panel);
        uno.setBounds(110, 200, 150, 50);
        uno.addActionListener(this::actionPerformed);
        jpanel.add(uno);
        dos.setBounds(110, 260, 150, 50);
        dos.addActionListener(this::actionPerformed);
        jpanel.add(dos);
        usuario.setOpaque(false);
        Border rounded = new LineBorder(new Color(5, 12, 178), 1, true);
        usuario.setBorder(rounded);
        usuario.setHorizontalAlignment((int) CENTER_ALIGNMENT);
        usuario.setBounds(110, 320, 150, 50);
        usuario.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                usuario.setText("");
            }
        });
        jpanel.add(usuario);
        this.getContentPane().add(jpanel);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton clic = (JButton) e.getSource();
        if (usuario.getText().equals("Nick name") || usuario.getText().equals("") || usuario.getText().length() == 5) {
            JOptionPane.showMessageDialog(this, "Cambie nombre de usuario\no que tenga hasta 4 caracteres");
        } else {
            if (clic.equals(uno)) {
                String d[] = {"facil", "normal", "dificil"};
                int x = JOptionPane.showOptionDialog(null, "Escoja la dificultad", "Dificultad",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, new ImageIcon("src/main/java/Imagen/dif.png"), d
                        , "Option");
                this.dispose();
                Tetris game = new Tetris(x, usuario.getText());
                game.setLocationRelativeTo(null);
                game.setVisible(true);
            }
            if (clic.equals(dos)) {
                setVisible(false);
                conectar(usuario.getText());
            }
        }
    }

    public void conectar(String a) {
        try {
            String id = a;
            puerto();
            Socket s = new Socket("localhost", puerto);
            DataOutputStream dout = new DataOutputStream(s.getOutputStream());
            dout.writeUTF(id);
            String i = new DataInputStream(s.getInputStream()).readUTF();
            if (i.equals("Ya estas registrado")) {
                JOptionPane.showMessageDialog(this, "YA ESTA REGISTRADO\n");
            } else {
                Tetrisdos tet = new Tetrisdos(id, s);
                tet.setLocationRelativeTo(null);
                tet.setVisible(true);
                this.dispose();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void puerto() throws IOException {
        FileReader in = new FileReader("src/main/java/Imagen/puertos");
        Scanner sc = new Scanner(in);
        int num = sc.nextInt();
        System.out.println(num);
        puerto = sc.nextInt();
        num++;
        if (num == 4) {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("src/main/java/Imagen/puertos")));
            int x=(int) Math.floor(Math.random() * (4000 - 1024 + 1) + 1024);
            out.println(1);
            out.println(x);
            out.close();
            new Servidor(x);
        } else {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("src/main/java/Imagen/puertos")));
            out.println(num);
            out.println(puerto);
            System.out.println(num + "-" + puerto);
            out.close();

        }



    }

    public static void main(String[] args) {
        new Visu();

    }
}