package vr;

import vr.ui.ScanJF;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class Main2627 {

    public static void main(String[] args) throws Exception {
        File romDir = new File(args[0]);
        File outDir = new File(System.getProperty("user.dir"));

        //String[] romFiles = new String[]{"mpr-14890.26", "mpr-14891.27"};
        //byte[] roms = Roms.loadRom(romDir, romFiles);
        Roms roms = new Roms(romDir);
        int[] romWords = roms.loadSwap(Roms.RB.polygons);

        List<DL> lists = Polygons.loadDisplayLists(romWords);


        System.out.println("paralists.size=" + lists.size());


        Scene bf = new Scene(Polygons.T1_START);
        bf.dls.addAll(lists.stream().filter(l -> l.offset >= Polygons.T1_START && l.offset < Polygons.T1_END).toList());
        Scene ap = new Scene(Polygons.T2_START);
        ap.dls.addAll(lists.stream().filter(l -> l.offset >= Polygons.T2_START && l.offset < Polygons.T2_END).toList());
        Scene bb = new Scene(Polygons.T3_START);
        bb.dls.addAll(lists.stream().filter(l -> l.offset >= Polygons.T3_START && l.offset < Polygons.T3_END).toList());


        for (Scene s : new Scene[]{bf, bb, ap}) {
            {
                BufferedImage im = new BufferedImage(1024, 1024, BufferedImage.TYPE_3BYTE_BGR);
                Graphics2D g = (Graphics2D) im.getGraphics();
                g.setClip(0, 0, im.getWidth(), im.getHeight());
                drawImage2(s, g, new P2(), 1, false, Collections.emptySet(), Collections.emptySet());
                String file = String.format("scenes\\m2scene%x.png", s.minPos());
                System.out.println("writing " + file);
                ImageIO.write(im, "png", new File(outDir, file));
            }
        }


    }

    // draw independent of scene size
    public static void drawImage2(Scene s, Graphics2D g, P2 t, double sf, boolean num, Set<Integer> numFilter, Set<Integer> dlFilter) {
        Rectangle win = g.getClipBounds();
        int w = win.width;
        int h = win.height;
        //System.out.println("drawImage2 s=" + s + " w=" + win);

        // normalise 3d point to bounding box of scene
        M norm2 = s.normalisationMatrix2();
        //System.out.println("norm2=" + norm2);

        // ortho projection of xz
        M proj2 = new M(4, 4).setRows(new double[][]{{1, 0, 0, 0}, {0, 0, 1, 0}, {0, 0, 0, 0}, {0, 0, 0, 1}});

        // scale to window size preserving aspect
        double sf2 = (sf * Math.min(w, h)) / 2.0;
        M winsc2 = M.scale4(sf2, sf2, 0);

        // translate to 0,0
        M wintr2 = M.trans4((w / 2.0) + t.x, (h / 2.0) + t.y, 0);

        M total2 = wintr2.mul(winsc2.mul(proj2.mul(norm2))).setRo();
        //System.out.println("total2=" + total2);

        // to get the width and height, we need the difference between the 2d projections of the extreme points
        //System.out.println("draw scene " + s);

        //Graphics2D g = (Graphics2D) im.getGraphics();
        //Font bigf = new Font(Font.MONOSPACED, Font.PLAIN, 12);

        Color lightBlue = new Color(128,128,255);
        Color lightGreen = new Color(128,255,128);

        g.setFont(ScanJF.MONO);
        g.setColor(Color.white);
        g.drawString(String.format("%x,%d", s.position, s.dls.size()), 16, 16);

        for (float x = -1; x <= 1; x += 0.5f) {
            for (float z = -1; z <= 1; z += 0.5f) {
                F3 f = new F3().set(x, 0, z);
                P2 p1 = wintr2.mul(winsc2.mul(proj2)).mul(f.toM()).toP2();
                g.setColor(Color.red);
                g.setFont(ScanJF.MONO);
                g.drawOval(p1.x - 2, p1.y - 2, 4, 4);
                g.drawString(f.toString(), p1.x, p1.y + ScanJF.MONO.getSize());
            }
        }

        for (DL dl : s.dls) {
            boolean in;
            {
                // 2d projection = x,y,z => x,-z
                // scale = window size / 2 (max of w,h to preserve aspect?)
                // trans  = windows size / 2 (to move origin to TL)
                // then draw each point if it's (the PL) within the g ctx

                P2 p1 = total2.mul(dl.min().toM()).toP2();
                P2 p2 = total2.mul(dl.max().toM()).toP2();
                //System.out.println("p1=" + p1 + " p2=" + p2);
                P2 min = p1.min(p2);
                P2 max = p1.max(p2);

                in = min.x < w && min.y < h && max.x >= 0 && max.y >= 0;

                if (in) {
                    g.setColor(Color.darkGray);
                    g.drawRect(min.x, min.y, max.x - min.x, max.y - min.y);
                    g.setColor(Color.lightGray);
                    g.setFont(ScanJF.MONO);
                    g.drawString(String.format("%x,%d", dl.offset, dl.polys.size()), min.x, min.y + 12);
                }
            }

            if (in && (dlFilter.size() == 0 || dlFilter.contains(dl.offset))) {
                P2 r2p = null, r3p = null;

                for (int n = 0; n < dl.polys.size(); n++) {
                    Para p = dl.polys.get(n);
                    P2 s2p = total2.mul(p.s2.toM()).toP2();
                    P2 s3p = total2.mul(p.s3.toM()).toP2();

                    if ((p.word & 0xff00000) != 0) {
                        if ((p.word & 0xf) == 1) {
                            // draw Q
                            g.setColor(lightBlue);
                            g.drawLine(s2p.x, s2p.y, s3p.x, s3p.y);
                            if (r2p != null && r3p != null) {
                                g.drawLine(s2p.x, s2p.y, r2p.x, r2p.y);
                                g.drawLine(s3p.x, s3p.y, r3p.x, r3p.y);
                            } else {
                                System.out.println(String.format("invalid Q %x:%d", dl.offset, n));
                            }
                        } else if ((p.word & 0xf) == 2) {
                            // draw T
                            g.setColor(lightGreen);
                            if (r2p != null && r3p != null) {
                                g.drawLine(s2p.x, s2p.y, r2p.x, r2p.y);
                                g.drawLine(s3p.x, s3p.y, r3p.x, r3p.y);
                            } else {
                                System.out.println(String.format("invalid T %x:%d", dl.offset, n));
                            }
                        } else {
                            throw new RuntimeException();
                        }
                    } else {
                        // draw line only
                        g.setColor(Color.lightGray);
                        g.drawLine(s2p.x, s2p.y, s3p.x, s3p.y);
                    }

                    boolean eq = s3p.x == s2p.x && s2p.y == s3p.y;

                    //g.setColor(Color.white);
                    //g.drawOval(s2p.x - 1, s2p.y -1, 2, 2);
                    if (num && (numFilter.size() == 0 || numFilter.contains(n))) {
                        g.setColor(Color.red);
                        g.drawString(eq ? n + "-" : n + ":2", s2p.x - 6, s2p.y - 6); // upper
                    }

                    if (!eq) {
                        //g.setColor(Color.white);
                        //g.drawOval(s3p.x - 1, s3p.y -1, 2, 2);
                        if (num && (numFilter.size() == 0 || numFilter.contains(n))) {
                            g.setColor(Color.red);
                            g.drawString(n + ":3", s3p.x + 6, s3p.y + 6); // lower
                        }
                    }

                    // register update
//                    switch (p.word & 0xf00) {
//                        case 0x400:
//                        case 0x800:
//                        case 0xa00: r2p = s2p; r3p = s3p; break;
//                        case 0x900: r3p = s2p; break;
//                        case 0xb00: r2p = s3p; break;
//                        case 0x600: break; // no update?
//                        default: System.out.println(String.format("invalid update %x:%d", dl.offset, n));
//                    }

                    int link = (p.word >> 8) & 3;
                    switch (link) {
                        case 0:
                        case 2:
                            r2p = s2p; r3p = s3p; break;
                        case 1:
                            r3p = s2p; break;
                        case 3:
                            r2p = s3p; break;
                    }
                }
            }
        }
        //return im;
    }


}

