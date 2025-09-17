package vr.ui;

import vr.Main2627;
import vr.P2;
import vr.Scene;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.Set;
import java.util.TreeSet;

public class SceneJC extends JComponent {
    private final P2 tx = new P2();
    private final P2 temptx = new P2();

    private Scene scene;
    private double zoom = 1;
    private boolean num;
    private final Set<Integer> numFilter = new TreeSet<>();
    private final Set<Integer> dlFilter = new TreeSet<>();

    public SceneJC() {
        SceneMA ma = new SceneMA();
        addMouseListener(ma);
        addMouseMotionListener(ma);
        addMouseWheelListener(ma);
    }

    public void setScene(Scene s) {
        this.scene = s;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Rectangle r = g.getClipBounds();
        int w = getWidth();
        int h = getHeight();
        g.setColor(Color.black);
        g.fillRect(0, 0, w, h);
        g.setColor(Color.white);
        g.drawString("c=" + r + " w=" + w + " h=" + h, 12, 24);
        g.drawString("tx=" + tx + " temptx=" + temptx + " zf=" + zoom, 12, 48);
        if (scene != null) {
            Main2627.drawImage2(scene, (Graphics2D) g, tx.add(temptx), zoom, num, numFilter, dlFilter);
        }
    }

    public void setNumbers(boolean num) {
        this.num = num;
        repaint();
    }

    public void setNumberFilter(Set<Integer> set) {
        numFilter.clear();
        numFilter.addAll(set);
        repaint();
    }

    public void setDlFilter(Set<Integer> set) {
        dlFilter.clear();
        dlFilter.addAll(set);
        repaint();
    }

    private class SceneMA extends MouseAdapter {

        private P2 pressed;

        @Override
        public void mouseDragged(MouseEvent e) {
            if (pressed != null) {
                temptx.set(e.getX() - pressed.x, e.getY() - pressed.y);
                repaint();
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            pressed = new P2(e.getX(), e.getY());
            //System.out.println("pressed " + pressed);
            repaint();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            pressed = null;
            tx.set(tx.add(temptx));
            temptx.set(0, 0);
            repaint();
            //System.out.println("released " + tx);
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            //System.out.println("clicked p=" + e.getPoint());
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            //System.out.println("wheel " + e);
            int d = e.getWheelRotation();
            zoom = zoom * Math.pow(1.125, -d);
            repaint();
        }
    }
}
