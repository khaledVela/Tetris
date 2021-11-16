package TT;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;


public class Tetris extends JFrame {

    JLabel statusbar;


    public Tetris(int dif) {
        statusbar = new JLabel(" 0");
        add(statusbar, BorderLayout.SOUTH);
        Tablero board = new Tablero(this,dif);
        add(board);
        board.start();
        setSize(400, 800);
        setTitle("Tetris");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        JMenuBar bar = new JMenuBar();
        JMenu menu = new JMenu("Menu");
        JMenuItem item1 = new JMenuItem("Volver a empezar");
        item1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                board.start();
            }
        });
        menu.add(item1);
        bar.add(menu);
        this.setJMenuBar(bar);
    }

    public JLabel getStatusBar() {
        return statusbar;
    }
}