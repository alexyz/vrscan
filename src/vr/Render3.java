package vr;

import vr.m.M;
import vr.m.M4;
import vr.m.P2;
import vr.ui.ScanJF;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

public class Render3 {

    private static class QT implements Comparable<QT> {
        public final int[] xp, yp;
        public final float z;
        public final Color col;

        private QT(int[] xp, int[] yp, float z, Color col) {
            this.xp = xp;
            this.yp = yp;
            this.z = z;
            this.col = col;
        }

        @Override
        public int compareTo(QT o) {
            return (int) Math.signum(z - o.z);
        }
    }

    // draw independent of scene size
    public static void drawImage2(Scene s, Graphics2D g, Render.Opts o) {
        Rectangle win = g.getClipBounds();
        float w = win.width;
        float h = win.height;

        // normalise 3d point to bounding box of scene
        M norm2 = s.normalisationMatrix2();

        // rotation matrix
        M rot = M4.rx(o.xRot).mul(M4.ry(o.yRot)).mul(M4.rz(o.zRot));

        // ortho projection of x,-z
        M proj2 = new M4().set(0, 0, 1).set(1, 2, -1).set(3, 3, 1);

        // scale to window size preserving aspect
        float sf2 = (o.scale * Math.min(w, h)) / 2f;
        M winsc2 = M4.scale(sf2, sf2, 0);

        // translate to 0,0
        M wintr2 = M4.trans((w / 2) + o.trans.x, (h / 2) + o.trans.y, 0);

        M total2 = wintr2.mul(winsc2.mul(proj2.mul(norm2.mul(rot))));

        ArrayList<QT> qts = new ArrayList<>(s.countP());

        for (DL dl : s.dls) {
            boolean in;
            {
                P2 p1 = total2.mul(dl.min().toM1()).toP2();
                P2 p2 = total2.mul(dl.max().toM1()).toP2();
                P2 min = p1.min(p2);
                P2 max = p1.max(p2);
                in = min.x < w && min.y < h && max.x >= 0 && max.y >= 0;
            }

            if (in) {

                P2 p2 = null, p3 = null;

                for (int n = 0; n < dl.polys.size(); n++) {
                    Poly p = dl.polys.get(n);
                    Poly op = n > 0 ? dl.polys.get(n-1) : p;

                    P2 p0 = total2.mul(p.s2.toM1()).toP2();
                    P2 p1 = total2.mul(p.s3.toM1()).toP2();

                    int link = Poly.link(p.word);
                    if (link > 0) {
                        int type = Poly.type(p.word);
                        //float ord = Math.max(Math.max(p.s2.y, p.s3.y), Math.max(op.s2.y, op.s3.y)); // mostly works for vr
                        float ord = (dl.ordinal << 16) + n; // works for vf dl0, but not for dl1...

                        if (type == Poly.TYPE_Q) {
                            // draw Q
                            int[] xp = new int[]{p0.x, p1.x, p3.x, p2.x};
                            int[] yp = new int[]{p0.y, p1.y, p3.y, p2.y};
                            qts.add(new QT(xp, yp, ord, p.colObj));
                        } else if (type == Poly.TYPE_T) {
                            // draw T
                            int[] xp = new int[]{p0.x, p2.x, p3.x};
                            int[] yp = new int[]{p0.y, p2.y, p3.y};
                            qts.add(new QT(xp, yp, ord, p.colObj));
                        }
                    }

                    switch (link) {
                        case 0:
                        case 2:
                            p2 = p0;
                            p3 = p1;
                            break;
                        case 1:
                            p3 = p0;
                            break;
                        case 3:
                            p2 = p1;
                            break;
                    }
                }
            }
        }

        // the z ordering is basically wrong, so shuffle first to emphasize it
        Collections.shuffle(qts);
        Collections.sort(qts);

        for (QT qt : qts) {
            g.setColor(qt.col);
            g.fillPolygon(qt.xp, qt.yp, qt.xp.length);
        }

        g.setFont(ScanJF.MONO);
        g.setColor(Color.white);
        g.drawString(String.format("scene=%s", s), 16, 16);
        g.drawString(String.format("opts=%s", o), 16, 32);
    }


}

