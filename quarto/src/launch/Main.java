package launch;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JFrame;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import javax.swing.JPanel;

public class Main {

    public static final class DRAW extends JPanel implements MouseListener, MouseMotionListener {

        static int ScreenX = 300, ScreenY = 300;
        int MR = 1, MC = 1;
        boolean MO;
        private boolean MouseDown;
        int turnState;
        byte[][] pieces = new byte[4][4];
        byte queue = 0b00010000;

        public void reset() {
            MR = 0;
            MC = 0;
            MouseDown = false;
            turnState = 1;
            queue = 0b00010000;
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    pieces[i][j] = 0b00010000;
                    pieces[i][j] += ((i + 1) % 2) * 8;
                    pieces[i][j] += ((j + 1) % 2) * 4;
                    pieces[i][j] += ((i + 2) % 4 < 2 ? 1 : 0) * 2;
                    pieces[i][j] += ((j + 2) % 4 < 2 ? 1 : 0) * 1;
                }
            }
        }
//abcd a=color1BLACK b=shape1SQUARE c=hasHole1YES d=tall1YES if its nine then not played

        public DRAW() {
            super();
            reset();
            setBounds(0, 0, ScreenX, ScreenY);
            addMouseListener(this);
            addMouseMotionListener(this);
        }

        public void drawPiece(Graphics2D g, byte b, int x, int y, boolean q) {
            if (q) {
                if (b >= 16) {
                    return;
                }
            } else if ((turnState == 1 || turnState == 3) ? b < 16 : b >= 16) {
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
                    g.drawOval((ScreenX / 5 * i) + 25, (ScreenY / 5 * j) + 75, 50, 50);
                    drawPiece(g, pieces[i][j], (ScreenX / 5 * i) + 25, (ScreenY / 5 * j) + 75, false);
                }
            }
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (MO) {System.out.println("E");
                if (turnState == 1 || turnState == 3) {
                    queue=pieces[MR][MC];
                    queue-=0b00010000;
                }else{
                    pieces[MR][MC]=queue;
                    queue=0b00010000;
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
            MO = false;
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    if (new Rectangle((ScreenX / 5 * i) + 25, (ScreenY / 5 * j) + 75, 50, 50).intersects(new Rectangle(e.getX(), e.getY(), 1, 1))) {
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
        JFrame f = new JFrame("Quarto!");
        f.setSize(300, 350);
        f.setResizable(false);
        f.setDefaultCloseOperation(EXIT_ON_CLOSE);
        f.getContentPane().add(new DRAW());
        f.setVisible(true);
    }

}
