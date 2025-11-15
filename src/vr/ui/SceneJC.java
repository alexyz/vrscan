package vr.ui;

import vr.*;
import vr.m.*;

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
    private float zoom = 1;
    private boolean num;
    private final Set<Integer> numFilter = new TreeSet<>();
    private final Set<Integer> dlFilter = new TreeSet<>();
    private float xr, yr, zr;

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
//        g.setColor(Color.white);
//        g.drawString("c=" + r + " w=" + w + " h=" + h, 12, 48);
//        g.drawString("tx=" + tx + " temptx=" + temptx + " zf=" + zoom, 12, 60);
        if (scene != null) {
            Render.Opts o = new Render.Opts();
            o.trans = tx.add(temptx);
            o.scale = zoom;
            o.xRot = xr;
            o.yRot = yr;
            o.zRot = zr;
            o.dispNum = num;
            o.numFilter = numFilter;
            o.dlFilter = dlFilter;
            //Render.drawImage2(scene, (Graphics2D) g, o);
            //Render2.drawImage2(scene, (Graphics2D) g, o);
            Render3.drawImage2(scene, (Graphics2D) g, o);
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

    public void setRot(float xr, float yr, float zr) {
        this.xr = xr;
        this.yr = yr;
        this.zr = zr;
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
            zoom = (float) (zoom * Math.pow(1.125, -d));
            repaint();
        }
    }
}
