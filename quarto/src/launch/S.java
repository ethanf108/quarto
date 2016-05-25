package launch;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.JFrame;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class S {

    public static class DRAW extends JPanel implements MouseListener, MouseMotionListener {

        static int ScreenX = 300, ScreenY = 300;
        int MR = 1, MC = 1;
        boolean MO;
        private boolean MouseDown;
        int turnState;
        byte[][] NUPieces = new byte[4][4];
        byte[][] Pieces = new byte[4][4];
        byte queue = 0b00010000;
        boolean WON = false;
        Socket sender = null;
        BufferedWriter dataSender = null;
        boolean server;
        private boolean ready = false;
        private BufferedReader dataReader = null;

        public void UPLOAD() {
            try {
                String s = null;
                if (!ready) {
                    return;
                }
                for (byte b[] : Pieces) {
                    for (byte c : b) {
                        s += String.format("%8s", Integer.toBinaryString(0b00010001)).replace(' ', '0');
                    }
                }
                for (byte b[] : NUPieces) {
                    for (byte c : b) {
                        s += String.format("%8s", Integer.toBinaryString(0b00010001)).replace(' ', '0');
                    }
                }
                s+="\n";
                dataSender.write(s);
                dataSender.flush();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        public void DOWNLOAD() {
            String h = null;
            try {
                h+=dataReader.readLine();
            for (int i = 1; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    String p = h.substring(((8*j)*i)-1, ((8*j)*i)+7);
                    System.out.println("p");
                }
            }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        public void reset() {
            MR = 0;
            MC = 0;
            MouseDown = false;
            turnState = 1;
            MO = false;
            WON = false;
            queue = 0b00010000;
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    NUPieces[i][j] = 0b00010000;
                    NUPieces[i][j] += ((i + 1) % 2) * 8;
                    NUPieces[i][j] += ((j + 1) % 2) * 4;
                    NUPieces[i][j] += ((i + 2) % 4 < 2 ? 1 : 0) * 2;
                    NUPieces[i][j] += ((j + 2) % 4 < 2 ? 1 : 0) * 1;
                }
            }
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    Pieces[i][j] = 0b00010000;
                }
            }
        }
//abcd a=color1BLACK b=shape1SQUARE c=hasHole1YES d=tall1YES if its nine then not played

        public DRAW() {
            super();
            SwingUtilities.invokeLater(() -> {
                setBounds(0, 0, ScreenX, ScreenY);
                addMouseListener(this);
                addMouseMotionListener(this);
            });
            reset();
        }

        boolean CH(int i, int b, boolean f) {
            if (f) {
                return ((Pieces[i][0] | Pieces[i][1] | Pieces[i][2] | Pieces[i][3]) < 16)
                        && CB(i, 0, b) == CB(i, 1, b)
                        && CB(i, 1, b) == CB(i, 2, b)
                        && CB(i, 2, b) == CB(i, 3, b)
                        && CB(i, 3, b) == CB(i, 1, b);
            } else {
                return ((Pieces[0][i] | Pieces[1][i] | Pieces[2][i] | Pieces[3][i]) < 16)
                        && CB(0, i, b) == CB(1, i, b)
                        && CB(1, i, b) == CB(2, i, b)
                        && CB(2, i, b) == CB(3, i, b)
                        && CB(3, i, b) == CB(0, i, b);
            }
        }

        int CB(int i, int j, int k) {
            return (Pieces[i][j] & (byte) Math.pow(2, k));
        }

        public void init() {
            if (server) {
                try {
                    ServerSocket serv = new ServerSocket(1680);
                    while (!serv.isBound()) {
                    }
                    sender = serv.accept();
                    dataSender = new BufferedWriter(new OutputStreamWriter(sender.getOutputStream()));
                    dataReader = new BufferedReader(new InputStreamReader(sender.getInputStream()));
                    ready = true;
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }else{
                try {
                    sender = new Socket("localhost",1680);
                    while(!sender.isBound()){}
                    dataSender = new BufferedWriter(new OutputStreamWriter(sender.getOutputStream()));
                    dataReader = new BufferedReader(new InputStreamReader(sender.getInputStream()));
                    ready = true;
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
                new Thread(() -> {
                    UPLOAD();
                    DOWNLOAD();
                    repaint();
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }).start();
        }

        boolean checkWin() {
            boolean returnT = false;
            for (int i = 0; i < 4; i++) {
                if (CH(i, 0, true) || CH(i, 1, true) || CH(i, 2, true) || CH(i, 3, true)) {
                    Pieces[i][0] += 32;
                    Pieces[i][1] += 32;
                    Pieces[i][2] += 32;
                    Pieces[i][3] += 32;
                    returnT = true;
                    WON = true;
                }
            }
            for (int i = 0; i < 4; i++) {
                if (CH(i, 0, false) || CH(i, 1, false) || CH(i, 2, false) || CH(i, 3, false)) {
                    Pieces[0][i] += 32;
                    Pieces[1][i] += 32;
                    Pieces[2][i] += 32;
                    Pieces[3][i] += 32;
                    returnT = true;
                    WON = true;
                }
            }
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    for (int k = 0; k < 4; k++) {
                        if (((Pieces[i][j] | Pieces[i][j + 1] | Pieces[i + 1][j] | Pieces[i + 1][j + 1]) < 16)
                                && (CB(i, j, k) == CB(i, j + 1, k)
                                && CB(i, j + 1, k) == CB(i + 1, j, k)
                                && CB(i + 1, j, k) == CB(i + 1, j + 1, k)
                                && CB(i + 1, j + 1, k) == CB(i, j, k))) {
                            Pieces[i][j] += 32;
                            Pieces[i][j + 1] += 32;
                            Pieces[i + 1][j] += 32;
                            Pieces[i + 1][j + 1] += 32;
                            returnT = true;
                            WON = true;
                        }
                    }
                }
            }
            return returnT;
        }

        public void drawPiece(Graphics2D g, byte b, int x, int y, boolean q) {
            if (!ready) {
                return;
            }
            if (q) {
                if (b >= 16) {
                    return;
                }
            } else if ((turnState == 1 || turnState == 3) ? b < 16 : (b >= 16 && (b & 32) != 32)) {
                return;
            }
            boolean fill = !((b & 2) == 2);
            int size = ((b & 1) == 1) ? 30 : 20;
            if ((b & 8) == 8) {
                g.setColor(Color.BLACK);
            } else {
                g.setColor(Color.WHITE);
            }
            if (((b & 4) == 4)) {
                if (fill) {
                    g.fillRect(x + (size / 2) + (size == 20 ? 5 : -5), y + (size / 2) + (size == 20 ? 5 : -5), size, size);
                } else {
                    g.drawRect(x + (size / 2) + (size == 20 ? 5 : -5), y + (size / 2) + (size == 20 ? 5 : -5), size, size);
                }
            } else if (fill) {
                g.fillOval(x + (size / 2) + (size == 20 ? 5 : -5), y + (size / 2) + (size == 20 ? 5 : -5), size, size);
            } else {
                g.drawOval(x + (size / 2) + (size == 20 ? 5 : -5), y + (size / 2) + (size == 20 ? 5 : -5), size, size);
            }
        }

        @Override
        public void paintComponent(Graphics g2) {
            Graphics2D g = (Graphics2D) g2;
            g.setColor(Color.GRAY);
            g.fillRect(0, 0, ScreenX, ScreenY + 100);
            g.setColor(Color.YELLOW);
            g.drawOval(10, 10, 50, 50);
            drawPiece(g, queue, 10, 10, true);
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    g.setColor(MO && i == MR && j == MC ? (MouseDown ? Color.RED : Color.GREEN) : Color.YELLOW);
                    if ((Pieces[i][j] & 32) == 32) {
                        g.setColor(Color.BLUE);
                    }
                    g.drawOval((ScreenX / 5 * i) + 25, (ScreenY / 5 * j) + 75, 50, 50);
                    if (turnState == 1 || turnState == 3) {
                        drawPiece(g, NUPieces[i][j], (ScreenX / 5 * i) + 25, (ScreenY / 5 * j) + 75, false);
                    } else {
                        drawPiece(g, Pieces[i][j], (ScreenX / 5 * i) + 25, (ScreenY / 5 * j) + 75, false);
                    }
                }
            }
            String tmp;
            if (WON) {
                tmp = (turnState > 2 ? "Player One Won!" : "Player Two Won!") + " Play again?";
            } else {
                switch (turnState) {
                    case 1:
                        tmp = "Player One, Give Player Two a Piece";
                        break;
                    case 2:
                        tmp = "Player Two, Play the Piece";
                        break;
                    case 3:
                        tmp = "Player Two, Give Player One a Piece";
                        break;
                    case 4:
                        tmp = "Player One, Play the Piece";
                        break;
                    default:
                        tmp = "You done fucked up.";
                }
            }
            g.setColor(Color.WHITE);
            g.drawString(tmp, 75, 40);
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (!ready) {
                return;
            }
            if (WON) {
                reset();
            } else if (MO) {
                if (turnState == 1 || turnState == 3) {
                    if ((NUPieces[MR][MC] & 16) == 16) {
                        queue = NUPieces[MR][MC];
                        queue -= 0b00010000;
                        NUPieces[MR][MC] -= 0b00010000;
                        turnState = (turnState % 4) + 1;
                    }
                } else if ((Pieces[MR][MC] & 16) == 16) {
                    Pieces[MR][MC] = queue;
                    queue = 0b00010000;
                    if (checkWin()) {
                    } else {
                        turnState = (turnState % 4) + 1;
                    }
                }
            }
            repaint();
        }

        @Override
        public void mousePressed(MouseEvent e) {
            MouseDown = true;
            repaint();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            MouseDown = false;
            repaint();
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            mouseMoved(e);
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            if (!ready) {
                return;
            }
            MO = false;
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    if (Math.abs(Math.sqrt(Math.pow((ScreenX / 5 * i) + 50 - e.getX(), 2) + Math.pow((ScreenY / 5 * j) + 100 - e.getY(), 2))) < 26.0) {
                        MR = i;
                        MC = j;
                        MO = true;
                    }
                }
            }
            repaint();
        }

    }

    public static void main(String[] args) {
        final boolean t;
        try {
            t = (char) System.in.read() == 'y';
            JFrame f = new JFrame("Quarto!");
            f.setSize(300, 350);
            f.setResizable(false);
            f.setDefaultCloseOperation(EXIT_ON_CLOSE);
            DRAW s = new DRAW();
            s.server = t;
            s.init();
            f.getContentPane().add(s);
            f.setVisible(true);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
