package TT2;

import TT2.Formasdos.Tetrominoes;

import javax.naming.ldap.SortKey;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;
import Gen.Visu;

public class Tablerodos extends JPanel implements ActionListener {


    final int BoardWidth = 20;
    int BoardHeight = 44;
    Frame f;
    Timer timer;
    boolean isFallingFinished = false;
    boolean isStarted = false;
    boolean isPaused = false;
    int numLinesRemoved = 0;
    JLabel statusbar;
    Formasdos curPiece;
    Tetrominoes[] board;
    int curX = 0;
    int curY = 0;
    String iD;
    DataInputStream din;
    DataOutputStream dout;
    DefaultListModel dlm;
    ArrayList list = new ArrayList();
    Socket sock;
    int can = 0;

    public Tablerodos(Tetrisdos parent, String a, Socket s) {
        sock=s;
        iD = a;
        f = parent;
        setBackground(Color.BLACK);
        timer = new Timer(250, this);
        setFocusable(true);
        curPiece = new Formasdos();
        statusbar = parent.getStatusBar();
        board = new Tetrominoes[BoardWidth * BoardHeight];
        addKeyListener(new TAdapter());
        clearBoard();
        JMenuBar menuBar = new JMenuBar();
        JMenu mnu = new JMenu("Menu");
        JMenuItem item = new JMenuItem("Ver Lista");
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pause();
                try {
                    dout.writeUTF(String.valueOf("pausa"));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                pers(list);
            }
        });
        JMenuItem item2 = new JMenuItem("Salir");
        item2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    dout.writeUTF(String.valueOf("sal-"+iD));
                    f.dispose();
                    can=Integer.MIN_VALUE;
                    try {
                        sock.close();
                        Visu vis =new Visu();
                    } catch (IOException exc) {
                        exc.printStackTrace();
                    }
                } catch (IOException ex) {
                }
            }
        });
        mnu.add(item);
        mnu.add(item2);
        menuBar.add(mnu);
        parent.setJMenuBar(menuBar);
        parent.pack();
        try {
            dlm = new DefaultListModel();
            din = new DataInputStream(s.getInputStream());
            dout = new DataOutputStream(s.getOutputStream());
            new Read().start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pers(ArrayList e) {
        JFrame jFrame = new JFrame();
        jFrame.setLocationRelativeTo(null);
        jFrame.setSize(200,300);
        JTextArea jpane = new JTextArea();
        jpane.append("                Lista de Usuarios\n");
        for (int i = 0; i < e.size(); i++) {
            jpane.append(e.get(i) + "\n");
        }
        jpane.setBounds(250, 150, 200, 300);
        JButton salir = new JButton("Exit");
        salir.setBounds(50, 250, 100, 50);
        salir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jFrame.dispose();
                pause();
            }
        });
        jFrame.add(salir);
        jpane.setEditable(false);
        jFrame.setUndecorated(true);
        jFrame.getRootPane();
        jFrame.add(jpane);
        jFrame.setVisible(true);

    }

    public void actionPerformed(ActionEvent e) {
        if (isFallingFinished) {
            isFallingFinished = false;
            newPiece();
        } else {
            oneLineDown();
        }
    }


    int squareWidth() {
        return (int) getSize().getWidth() / BoardWidth;
    }

    int squareHeight() {
        return (int) getSize().getHeight() / BoardHeight;
    }

    Tetrominoes shapeAt(int x, int y) {
        return board[(y * BoardWidth) + x];
    }


    public void start() {
        if (isPaused)
            return;
        isStarted = true;
        isFallingFinished = false;
        numLinesRemoved = 0;
        clearBoard();
        newPiece();
        statusbar.setText(String.valueOf(numLinesRemoved));
        timer.start();
    }

    private void pause() {
        if (!isStarted)
            return;

        isPaused = !isPaused;
        if (isPaused) {
            timer.stop();
            statusbar.setText("paused");
        } else {
            timer.start();
            statusbar.setText(String.valueOf(numLinesRemoved));
        }
        repaint();
    }

    public void paint(Graphics g) {
        super.paint(g);

        Dimension size = getSize();
        int boardTop = (int) size.getHeight() - BoardHeight * squareHeight();


        for (int i = 0; i < BoardHeight; ++i) {
            for (int j = 0; j < BoardWidth; ++j) {
                Tetrominoes shape = shapeAt(j, BoardHeight - i - 1);
                if (shape != Tetrominoes.NoShape)
                    drawSquare(g, 0 + j * squareWidth(),
                            boardTop + i * squareHeight(), shape);
            }
        }

        if (curPiece.getShape() != Tetrominoes.NoShape) {
            for (int i = 0; i < 4; ++i) {
                int x = curX + curPiece.x(i);
                int y = curY - curPiece.y(i);
                drawSquare(g, 0 + x * squareWidth(),
                        boardTop + (BoardHeight - y - 1) * squareHeight(),
                        curPiece.getShape());
            }
        }
    }

    private void dropDown() {
        int newY = curY;
        while (newY > 0) {
            if (!tryMove(curPiece, curX, newY - 1))
                break;
            --newY;
        }
        pieceDropped();
    }

    private void oneLineDown() {
        if (!tryMove(curPiece, curX, curY - 1))
            pieceDropped();
    }


    private void clearBoard() {
        for (int i = 0; i < BoardHeight * BoardWidth; ++i)
            board[i] = Tetrominoes.NoShape;
    }

    private void pieceDropped() {
        for (int i = 0; i < 4; ++i) {
            int x = curX + curPiece.x(i);
            int y = curY - curPiece.y(i);
            board[(y * BoardWidth) + x] = curPiece.getShape();
        }
        removeFullLines();
        if (!isFallingFinished)
            newPiece();
    }

    private void newPiece() {
        curPiece.setRandomShape();
        curX = BoardWidth / 2;
        curY = BoardHeight - 1 + curPiece.minY();

        if (!tryMove(curPiece, curX, curY)) {
            curPiece.setShape(Tetrominoes.NoShape);
            timer.stop();
            isStarted = false;
            statusbar.setText("game over");
            f.dispose();

            JOptionPane.showMessageDialog(getRootPane(), "Game over");
            can--;
            try {
                dout.writeUTF(String.valueOf("fin " + iD));
                try {
                    sock.close();
                    Visu vis =new Visu();
                } catch (IOException exc) {
                    exc.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean tryMove(Formasdos newPiece, int newX, int newY) {
        for (int i = 0; i < 4; ++i) {
            int x = newX + newPiece.x(i);
            int y = newY - newPiece.y(i);
            if (x < 0 || x >= BoardWidth || y < 0 || y >= BoardHeight)
                return false;
            if (shapeAt(x, y) != Tetrominoes.NoShape)
                return false;
        }

        curPiece = newPiece;
        curX = newX;
        curY = newY;
        repaint();
        return true;
    }

    private void removeFullLines() {
        int numFullLines = 0;

        for (int i = BoardHeight - 1; i >= 0; --i) {
            boolean lineIsFull = true;

            for (int j = 0; j < BoardWidth; ++j) {
                if (shapeAt(j, i) == Tetrominoes.NoShape) {
                    lineIsFull = false;
                    break;
                }
            }

            if (lineIsFull) {
                ++numFullLines;
                for (int k = i; k < BoardHeight - 1; ++k) {
                    for (int j = 0; j < BoardWidth; ++j)
                        board[(k * BoardWidth) + j] = shapeAt(j, k + 1);
                }
            }
        }

        if (numFullLines > 0) {
            numLinesRemoved += numFullLines;
            statusbar.setText(String.valueOf(numLinesRemoved));
            try {
                dout.writeUTF(String.valueOf("menos"));
            } catch (IOException e) {
            }
            isFallingFinished = true;
            curPiece.setShape(Tetrominoes.NoShape);
            repaint();
        }
    }

    private void drawSquare(Graphics g, int x, int y, Tetrominoes shape) {
        Color colors[] = {new Color(0, 0, 0), new Color(204, 102, 102),
                new Color(102, 204, 102), new Color(102, 102, 204),
                new Color(204, 204, 102), new Color(204, 102, 204),
                new Color(102, 204, 204), new Color(218, 170, 0)
        };


        Color color = colors[shape.ordinal()];

        g.setColor(color);
        g.fillRect(x + 1, y + 1, squareWidth() - 2, squareHeight() - 2);

        g.setColor(color.brighter());
        g.drawLine(x, y + squareHeight() - 1, x, y);
        g.drawLine(x, y, x + squareWidth() - 1, y);

        g.setColor(color.darker());
        g.drawLine(x + 1, y + squareHeight() - 1,
                x + squareWidth() - 1, y + squareHeight() - 1);
        g.drawLine(x + squareWidth() - 1, y + squareHeight() - 1,
                x + squareWidth() - 1, y + 1);
    }

    class Read extends Thread {
        public void run() {
            while (true) {
                try {
                    String m = din.readUTF();
                    if (m.contains(":;.,/=")) {
                        m = m.substring(6);
                        dlm.clear();
                        StringTokenizer st = new StringTokenizer(m, ",");
                        while (st.hasMoreTokens()) {
                            String u = st.nextToken();
                            if (!iD.equals(u)) {
                                dlm.addElement(u);
                            }
                        }
                    }
                    System.out.println(m);
                    if (m.contains(" ")) {
                        String[] nomb = m.split(" ");
                        ArrayList li = new ArrayList();
                        for (int i = 0; i < list.size(); i++) {
                            if (list.get(i).equals(nomb[1])) {
                                li.add(nomb[1] + "            Game Over");
                                can--;
                                if ((!statusbar.getText().equals("game over")) && (can == 1)) {
                                    JOptionPane.showMessageDialog(getRootPane(), "Has ganado");
                                }
                            } else li.add(list.get(i));
                        }
                        list = li;
                    }
                    if (m.contains("-")) {
                        String[] nomb = m.split("-");
                        ArrayList li = new ArrayList();
                        for (int i = 0; i < list.size(); i++) {
                            if (list.get(i).equals(nomb[1])) {
                                li.add(nomb[1] + "            Salio");
                                can--;
                                if ((!statusbar.getText().equals("game over")) && (can == 1)) {
                                    JOptionPane.showMessageDialog(getRootPane(), "Has ganado");
                                }
                            } else li.add(list.get(i));
                        }
                        list = li;
                    }
                    if (m.equals("pausa")) {
                        if (!statusbar.getText().equals("game over")) {
                            pause();
                            pers(list);
                        }
                    }
                    if (m.contains(",")) {
                        String[] nomb = m.split(",");
                        if (list.isEmpty()) {
                            for (int i = 0; i < nomb.length; i++) {
                                if (nomb[i].equals(iD)) {
                                    nomb[i] = nomb[i] + "            usted";
                                }
                                list.add(nomb[i]);
                                can++;
                            }
                        } else {
                            list.add(nomb[nomb.length - 1]);
                            can++;
                        }
                        if(list.size()==3){
                            timer.start();
                        }else timer.stop();
                    }
                    if (m.equals("menos")) {
                        BoardHeight--;
                        m = "";
                    }
                } catch (Exception e) {
                    break;
                }
            }

        }
    }

    class TAdapter extends KeyAdapter {
        public void keyPressed(KeyEvent e) {
            if (!isStarted || curPiece.getShape() == Tetrominoes.NoShape) {
                return;
            }

            int keycode = e.getKeyCode();

            if (isPaused)
                return;

            switch (keycode) {
                case KeyEvent.VK_LEFT:
                    tryMove(curPiece, curX - 1, curY);
                    break;
                case KeyEvent.VK_RIGHT:
                    tryMove(curPiece, curX + 1, curY);
                    break;
                case KeyEvent.VK_DOWN:
                    tryMove(curPiece.rotateRight(), curX, curY);
                    break;
                case KeyEvent.VK_UP:
                    tryMove(curPiece.rotateLeft(), curX, curY);
                    break;
                case KeyEvent.VK_SPACE:
                    dropDown();
                    break;
                case KeyEvent.VK_ENTER:
                    oneLineDown();
                    break;
                default:
                    break;
            }

        }
    }
}