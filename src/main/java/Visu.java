import TT.Tetris;
import TT2.Tetrisdos;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Locale;

public class Visu extends JFrame implements ActionListener {
    private JPanel jpanel;
    private JButton uno = new JButton("Single Player");
    private JButton dos = new JButton("Multiplayer ");
    private JLabel panel;
    private JTextField usuario = new JTextField("Nick name");

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
        usuario.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent e){
                usuario.setText("");
            }
        });
        jpanel.add(usuario);
        this.getContentPane().add(jpanel);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton clic = (JButton) e.getSource();
        if (usuario.getText().equals("Nick name")||usuario.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "Cambie nombre de usuario");
        } else {
            if (clic.equals(uno)) {
                String d[] = {"facil", "normal", "dificil"};
                int x = JOptionPane.showOptionDialog(null, "Escoja la dificultad", "Dificultad",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, new ImageIcon("src/main/java/Imagen/dif.png"), d
                        , "Option");
                this.dispose();
                Tetris game = new Tetris(x);
                game.setLocationRelativeTo(null);
                game.setVisible(true);
            }
            if (clic.equals(dos)) {
                setVisible(false);
                conectar(usuario.getText());
                this.dispose();
            }
        }
    }

    public void conectar(String a) {
        try {
            String id = a;

            Socket s = new Socket("localhost", 2089);
            DataOutputStream dout = new DataOutputStream(s.getOutputStream());
            dout.writeUTF(id);
            String i = new DataInputStream(s.getInputStream()).readUTF();
            if (i.equals("Ya estas registrado")) {
                JOptionPane.showMessageDialog(this, "YA ESTA REGISTRADO\n");
            } else {
                Tetrisdos tet = new Tetrisdos(id,s);
                tet.setLocationRelativeTo(null);
                tet.setVisible(true);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Visu();

    }
}