package vr;

import vr.m.F3;
import vr.m.M;
import vr.m.M4;
import vr.m.P2;
import vr.ui.ScanJF;

import java.awt.*;
import java.util.Set;

public class Render2 {

    // draw independent of scene size
    public static void drawImage2(Scene s, Graphics2D g, Render.Opts o) {
        Rectangle win = g.getClipBounds();
        float w = win.width;
        float h = win.height;

        // normalise 3d point to bounding box of scene
        M norm2 = s.normalisationMatrix2();
        //System.out.println("norm2=" + norm2);

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


        for (DL dl : s.dls) {
            boolean in;
            {
                P2 p1 = total2.mul(dl.min().toM1()).toP2();
                P2 p2 = total2.mul(dl.max().toM1()).toP2();
                P2 min = p1.min(p2);
                P2 max = p1.max(p2);

                in = min.x < w && min.y < h && max.x >= 0 && max.y >= 0;

                if (in) {
//                    g.setColor(Color.darkGray);
//                    g.drawRect(min.x, min.y, max.x - min.x, max.y - min.y);
//                    g.setColor(Color.lightGray);
//                    g.setFont(ScanJF.MONO);
//                    g.drawString(String.format("%x", dl.offset), min.x, min.y + 12);
                }
            }

            int[] xp = new int[4], yp = new int[4];
            Color[] cols = new Color[0x400];

            if (in) {

                P2 p2 = null, p3 = null;

                for (int n = 0; n < dl.polys.size(); n++) {
                    Poly p = dl.polys.get(n);
                    P2 p0 = total2.mul(p.s2.toM1()).toP2();
                    P2 p1 = total2.mul(p.s3.toM1()).toP2();
                    Color col = cols[p.tex];
                    if (col == null) {
                        cols[p.tex] = col = new Color(Poly.red(p.col), Poly.green(p.col), Poly.blue(p.col));
                    }

                    int link = Poly.link(p.word);
                    if (link > 0) {


                        int type = Poly.type(p.word);
                        if (type == 1) {
                            // draw Q
                            xp[0] = p0.x; xp[1] = p1.x; xp[2] = p3.x; xp[3] = p2.x;
                            yp[0] = p0.y; yp[1] = p1.y; yp[2] = p3.y; yp[3] = p2.y;
                            g.setColor(col);
                            g.fillPolygon(xp, yp, 4);

//                            g.setColor(Color.lightGray);
//                            g.drawLine(p0.x, p0.y, p1.x, p1.y);
//                            g.drawLine(p2.x, p2.y, p3.x, p3.y);
//                            g.drawLine(p0.x, p0.y, p2.x, p2.y);
//                            g.drawLine(p1.x, p1.y, p3.x, p3.y);

                        } else if (type == 2) {
                            // draw T
                            xp[0] = p0.x; xp[1] = p2.x; xp[2] = p3.x;
                            yp[0] = p0.y; yp[1] = p2.y; yp[2] = p3.y;
                            g.setColor(col);
                            g.fillPolygon(xp, yp, 3);

//                            g.setColor(Color.lightGray);
//                            g.drawLine(p2.x, p2.y, p3.x, p3.y);
//                            g.drawLine(p0.x, p0.y, p2.x, p2.y);
//                            g.drawLine(p0.x, p0.y, p3.x, p3.y);
                        }
                    } else {
                        // draw line only
//                        g.setColor(Color.lightGray);
//                        g.drawLine(p0.x, p0.y, p1.x, p1.y);
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

        g.setFont(ScanJF.MONO);
        g.setColor(Color.white);
        g.drawString(String.format("scene=%s", s), 16, 16);
        g.drawString(String.format("opts=%s", o), 16, 32);
    }


}

