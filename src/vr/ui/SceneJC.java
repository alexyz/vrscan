package vr.ui;

import vr.*;
import vr.m.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

public class SceneJC extends JComponent {
    private final P2 tx = new P2();
    private final P2 temptx = new P2();
    private float rx, ry, rz, tempRx, tempRy, tempRz;

    private Scene scene;
    private float zoom = 1;
//    private boolean num;
//    private final Set<Integer> numFilter = new TreeSet<>();
//    private final Set<Integer> dlFilter = new TreeSet<>();
//    private float xr, yr, zr;
    private Render.Opts opts;

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
        //Rectangle r = g.getClipBounds();
        int w = getWidth();
        int h = getHeight();
        g.setColor(Color.black);
        g.fillRect(0, 0, w, h);
        g.setColor(Color.white);
        g.drawString("w=" + w + " h=" + h, 12, 48);
        g.drawString("tx=" + tx + " temptx=" + temptx + " zf=" + zoom, 12, 60);
        g.drawString(String.format("r=%.2f,%.2f,%.2f tr=%.2f,%.2f,%.2f", rx, ry, rz, tempRx, tempRy, tempRz), 12, 72);

        if (scene != null && opts != null) {
//            Render.Opts o = new Render.Opts();
//            o.trans = tx.add(temptx);
//            o.scale = zoom;
//            o.xRot = xr;
//            o.yRot = yr;
//            o.zRot = zr;
//            o.dispNum = num;
//            o.numFilter = numFilter;
//            o.dlFilter = dlFilter;
            opts.trans = tx.add(temptx);
            opts.scale = zoom;
            opts.xRot = rx + tempRx;
            opts.yRot = ry + tempRy;
            opts.zRot = rz + tempRz;

            try {
                switch (opts.render) {
                    case R1:
                        Render.drawImage2(scene, (Graphics2D) g, opts);
                        break;
                    case R3:
                        Render3.drawImage2(scene, (Graphics2D) g, opts);
                        break;
                    default:
                        throw new RuntimeException();
                }
            } catch (Exception e) {
                e.printStackTrace();
                g.setColor(Color.white);
                g.drawString(e.toString(), 16,16);
            }
            //Render.drawImage2(scene, (Graphics2D) g, o);
            //Render2.drawImage2(scene, (Graphics2D) g, o);
            //Render3.drawImage2(scene, (Graphics2D) g, o);
        }
    }

    public void setOpts(Render.Opts opts) {
        this.opts = opts;
        repaint();
    }

    private class SceneMA extends MouseAdapter {

        private P2 pressed;
        private int button;

        @Override
        public void mouseDragged(MouseEvent e) {
            if (pressed != null) {
                if (button == MouseEvent.BUTTON1) {
                    temptx.set(e.getX() - pressed.x, e.getY() - pressed.y);
                } else if (button == MouseEvent.BUTTON3) {
                    tempRx = (float) (Math.PI / getHeight()) * (e.getY() - pressed.y);
                    tempRy = (float) (Math.PI / getWidth()) * (e.getX() - pressed.x);
                    tempRz = 0;
                }
            }
            repaint();
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (pressed == null) {
                pressed = new P2(e.getX(), e.getY());
                button = e.getButton();
            }
            //System.out.println("pressed " + pressed);
            repaint();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (pressed != null) {
                if (button == MouseEvent.BUTTON1) {
                    tx.set(tx.add(temptx));
                    temptx.set(0, 0);
                } else if (button == MouseEvent.BUTTON3) {
                    rx = rx + tempRx;
                    ry = ry + tempRy;
                    rz = rz + tempRz;
                    tempRx = 0;
                    tempRy = 0;
                    tempRz = 0;
                }
                pressed = null;
                button = MouseEvent.NOBUTTON;
            }
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
