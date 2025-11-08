package vr.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

public class BinJC extends JComponent {
    public static class Opt {
        public enum Format {
            SB(1), RGBA5551(2), RGB565(2), EX(2);
            int nb;

            Format(int nb) {
                this.nb = nb;
            }
        }

        public Format format = Format.SB;
        public int size, offset;
        public boolean swap;

        @Override
        public String toString() {
            return String.format("%s[size=%s off=%s format=%s swap=%s]", getClass().getSimpleName(), size, offset, format, swap);
        }
    }

    private byte[] data;
    private BufferedImage im;
    private Opt opt = new Opt();
    private boolean dirty;

    public void setData(byte[] data) {
        this.data = data;
        dirty = true;
        repaint();
    }


    public void setParams(Opt opt) {
        this.opt = opt;
        dirty = true;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        int w = getWidth();
        int h = getHeight();
        if (data != null && opt.size > 0) {
            if (im == null || im.getWidth() != w || im.getHeight() != h) {
                im = new BufferedImage(w, h, BufferedImage.TYPE_3BYTE_BGR);
                dirty = true;
            }
            if (dirty) {
                render(im, data, opt);
                dirty = false;
            }
            g.drawImage(im, 0, 0, null);
        } else {
            g.drawString("src/vr", 16, 16);
        }
    }

    private static void render(BufferedImage im, byte[] data, Opt opt) {
        WritableRaster raster = im.getRaster();
        int width = im.getWidth();
        int height = im.getHeight();
        int cols = width / opt.size;
        int rows = height / opt.size;
        System.out.println("render im=" + width + "," + height + " data=" + data.length + " opt=" + opt + " cols=" + cols + " rows=" + rows);

        Graphics2D g = (Graphics2D) im.getGraphics();
        g.setColor(Color.black);
        g.fillRect(0, 0, width, height);
        if (cols <= 0) {
            g.setColor(Color.white);
            g.drawString("too small", 16, 16);
            return;
        }

        int size = opt.size;
        int off = opt.offset;
        // rgb
        int[] p = new int[3];
        int nb = opt.format.nb;

        for (int c = 0; c < cols; c++) {
            for (int r = 0; r < rows; r++) {
                int baseIndex = (c * size * size * rows) + (r * size * size);
                for (int x = 0; x < size; x++) {
                    for (int y = 0; y < size; y++) {
                        int index = (baseIndex + ((size * y) + x)) * nb + off;
                        if (index < data.length) {
                            int w;
                            if (nb == 1) {
                                w = data[index] & 0xff;
                            } else if (nb == 2) {
                                int b1 = data[index] & 0xff, b2 = data[index + 1] & 0xff;
                                w = opt.swap ? (b2 << 8) | b1 : (b1 << 8) | b2;
                            } else {
                                throw new RuntimeException();
                            }
                            formatPixel(opt, p, w);
                        } else {
                            p[0] = 0;
                            p[1] = 0;
                            p[2] = 0x7f;
                        }
                        raster.setPixel((c * size) + x, (r * size) + y, p);
                    }
                }
            }
        }

    }

    private static void formatPixel(Opt opt, int[] p, int w) {
        switch (opt.format) {
            case SB:
                p[0] = p[1] = p[2] = w & 0xff;
                break;
            case EX:
                p[0] = (w & 0x1f) << 3;
                p[1] = ((w >>> 0x5) & 0x1f) << 3;
                p[2] = ((w >>> 0xA) & 0x1f) << 3;
                break;
            case RGB565:
                // rrrr rggg gggb bbbb
                // 1111 1000 0000 0000
                //      0111 1110 0000
                //              1 1111
                p[0] = (w & 0xf800) >>> 8;
                p[1] = (w & 0x7e0) >>> 2;
                p[2] = (w & 0x1f) << 3;
                break;
            case RGBA5551:
                // rrrr rggg ggbb bbba
                // 1111 1000 0000 0000
                //      0111 1100 0000
                //             11 1110
                p[0] = (w & 0xf800) >>> 8;
                p[1] = (w & 0x7c0) >>> 3;
                p[2] = (w & 0x3e) << 2;
                break;
            default:
                throw new RuntimeException();
        }
    }

}
