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
    public static class DRAW extends JPanel implements MouseListener,MouseMotionListener{
        static int ScreenX,ScreenY;
        int MR=1,MC=1;
        boolean MO;
        static{
            ScreenX=Toolkit.getDefaultToolkit().getScreenSize().width;
            ScreenY=Toolkit.getDefaultToolkit().getScreenSize().height;
        }
        public DRAW(){
            
            setBounds(0,0,ScreenX,ScreenY);
            addMouseListener(this);
            addMouseMotionListener(this);
        }
        @Override
        public void paintComponent(Graphics g2){
            Graphics2D g = (Graphics2D)g2;
            g.setColor(Color.DARK_GRAY);
            g.fillRect(0,0,ScreenX,ScreenY);
            g.setColor(Color.YELLOW);
            for(int i = 0; i < 8; i++){
                for(int j = 0; j < 4; j++){
                    g.setColor(MO&&i==MR&&j==MC?Color.GREEN:Color.YELLOW);
                    g.drawOval(ScreenX/8*i+40,ScreenY/4*j+100,80,80);
                }
            }
        }
        @Override
        public void mouseClicked(MouseEvent e) {
            System.exit(0);
        }
        @Override
        public void mousePressed(MouseEvent e) {
        }
        @Override
        public void mouseReleased(MouseEvent e) {
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
            for(int i = 0; i < 8; i++){
                for(int j = 0; j < 4; j++){
                    if(new Rectangle(ScreenX/8*i+40,ScreenY/4*j+100,80,80).intersects(new Rectangle(e.getX(),e.getY(),1,1))){
                        MR=i;System.out.println("SDF");
                        MC=j;
                        MO=true;
                    }
                }
            }
            repaint();
        }
        
    }
    public static void main(String[] args) {
        JFrame f = new JFrame("quarto");
        f.setDefaultCloseOperation(EXIT_ON_CLOSE);
        f.setUndecorated(true);
        f.setExtendedState(f.getExtendedState() | JFrame.MAXIMIZED_BOTH);
        f.getContentPane().add(new DRAW());
        java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().setFullScreenWindow(f);
        f.setVisible(true);
    }
    
}
