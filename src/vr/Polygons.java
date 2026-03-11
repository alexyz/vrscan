package vr;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * https://github.com/alexyz/mame/pull/1/changes
 */
public class Polygons {
    public List<DL> displayLists;
    public List<PA> polyAddrs;
    public short[] textures;
    /**
     * 15 bit color
     */
    public short[] palette;
    public Color[] colors;

    public Polygons load(Roms roms, Game g) {
        this.polyAddrs = loadPolyAddresses(roms, g);
        this.textures = loadTextures(roms, g);
        this.palette = loadPalette(roms, g);
        this.colors = initColors();

        // load display lists...
        this.displayLists = loadDisplayLists(roms.loadWords(g, Bank.polygons), g);

        // copy pa into dl
        for (DL dl : displayLists) {
            int exPa = (dl.offset + 16) / 4;
            dl.pa = polyAddrs.stream().filter(pa -> pa.polyAddr == exPa).findFirst().orElse(null);

            if (dl.pa != null) {
                int ta = dl.pa.texAddr;
                for (int n = 1; n < dl.polys.size(); n++) {
                    Poly p = dl.polys.get(n);
                    ta += Poly.texadr(p.word);
                    if (Poly.link(p.word) > 0) {
                        p.texAddr = ta;
                        p.tex = textures[ta - 0x40000] & 0xffff;
                        p.col = palette[p.tex & 0x3ff] & 0xffff;
                        p.colObj = colors[p.tex & 0x3ff];
                    }
                }
            }
        }

        return this;
    }

    private Color[] initColors() {
        Color[] colors = new Color[palette.length];
        for (int n = 0; n < palette.length; n++) {
            int c = palette[n] & 0xffff;
            colors[n] = new Color(Poly.red(c), Poly.green(c), Poly.blue(c));
        }
        return colors;
    }

    private short[] loadPalette(Roms roms, Game g) {
        short[] a = new short[0x400];
        if (g == Game.vr) {
            short[] data = roms.loadHalfWords(g, Bank.mainCpu1);
            int p = 0xEC980 / 2;
            System.arraycopy(data, p, a, 0, a.length);

        } else if (g == Game.vf) {
            short[] data = roms.loadHalfWords(g, Bank.mainCpu1);
            int p = 0xD963E / 2;
            System.arraycopy(data, p, a, 0, a.length);

        } else {
            System.out.println("no palette for " + g);
            for (int n = 0; n < a.length; n++) {
                int c = (n * 32) / a.length;
                a[n] = (short) ((c << 10) | (c << 5) | c);
            }
        }
        return a;
    }

    private short[] loadTextures(Roms roms, Game g) {
        short[] a = new short[0x60000];
        if (g == Game.vr) {
            short[] data = roms.loadHalfWords(g, Bank.mainCpu1);
            System.arraycopy(data, 0, a, 0, a.length);

        } else if (g == Game.vf) {
            short[] data = roms.loadHalfWords(g, Bank.mainCpu3);
            System.arraycopy(data, (0x300000 / 2), a, 0, a.length);

        } else {
            System.out.println("no textures for " + g);
            for (int n = 0; n < a.length; n++) {
                a[n] = (short) ((n * 0x3ff) / a.length);
            }
        }
        return a;
    }

    /**
     * <p>a big array somewhere in rom that lists every polygon address with its texture.</p>
     * <p>these are the arguments to <i>void model1_state::push_object(uint32_t tex_adr, uint32_t poly_adr, uint32_t size)</i></p>
     * <p>mame invokes this from the render list which is output by the game.</p>
     */
    private List<PA> loadPolyAddresses(Roms roms, Game g) {
        List<PA> list = new ArrayList<>();

        if (g == Game.vr) {
            int[] words = roms.loadWords(g, Bank.mainCpu1);
            int s = 0xe0000 / 4, e = 0xec980 / 4; // convert byte address to word address
            for (int n = s; n < e; n += 4) {
                PA pa = new PA();
                pa.polyAddr = words[n];
                pa.texAddr = words[n + 1];
                pa.extra1 = words[n + 2];
                pa.extra2 = words[n + 3];
                list.add(pa);
            }

        } else if (g == Game.vf) {
            int[] words = roms.loadWords(g, Bank.mainCpu1);
            int s = 0xd0000 / 4, e = 0xD9638 / 4;
            for (int n = s; n < e; n += 3) {
                PA pa = new PA();
                pa.extra1 = words[n];
                pa.texAddr = words[n + 1];
                pa.polyAddr = words[n + 2];
                list.add(pa);
            }
        }

        return list;
    }

    /**
     * look through the polygon roms to find things that look like display lists.
     * a poly starts with a control word, though a DL (sometimes) starts with 6 words then list of polys
     */
    private List<DL> loadDisplayLists(int[] romWords, Game g) {
        List<DL> lists = new ArrayList<>();
        boolean newlist = true;
        int ord = 0;

        int sp;
        switch (g) {
            case Game.wingwar: sp = 0x124; break;
            case Game.vr: sp = 0xb58; break;
            default: sp = 0; break;
        }

        for (int n = sp >>> 2; n < romWords.length; ) {
            int w = romWords[n];
            int wp = n << 2;

            if (w == 0 || w == -1) {
                newlist = true;

                // swa has a lot of orphaned 6 word values
                // void model1_state::push_object(uint32_t tex_adr, uint32_t poly_adr, uint32_t size)
                //   poly_adr += 6; ...
                if ((g == Game.swa || g == Game.wingwar) && n+7 < romWords.length && Poly.isWord(romWords[n+7])) {
                    System.out.println(String.format("skip %s offset %x word %x", g, wp, w));
                    n += 7;
                } else {
                    n++;
                }

            } else if (Poly.isWord(w)) {
                Poly p = Poly.readPara(romWords, n);
                if (isDummy(p)) {
                    //System.out.println(String.format("dummy p %x", wp * 4));
                    newlist = true;

                } else {
                    if (w == 1) {
                        newlist = true; // todo netmerc, but doesn't work well. without this can see entire map
                    }
                    if (newlist) {
                        lists.add(new DL(ord, n * 4));
                        newlist = false;
                        ord++;
                    }
                    lists.get(lists.size() - 1).polys.add(p);
                }
                n += 10;

            } else if (g == Game.wingwar && Poly.isWord(romWords[n+1])) {
                // good old wingwar
                n++;

            } else {
                String msg = String.format("unknown %s offset %x word %x t=%x l=%x z=%x ta=%x m=%x bf=%x lm=%x l2=%x u=%x",
                        g, wp, w,
                        Poly.type(w), Poly.link(w), Poly.zorder(w), Poly.texadr(w),
                        Poly.moire(w), Poly.bfcull(w), Poly.lightmode(w), Poly.link2(w),
                        Poly.unknown(w));
                System.out.println(msg);
                throw new RuntimeException(msg);
            }
        }
        return lists;
    }

    public static boolean isDummy(Poly p) {
        // 00021401 00000000 00000000 00000000 3F800000 C0000000 3F800000 3F800000 C0000000 BF800000
        // 00021401 00000000 3F800000 00000000 BF800000 C0000000 3F800000 BF800000 C0000000 BF800000
        if (p.word == 0x00021401) {
            if (p.s1.equalsHc(0, 0, 0) && p.s2.equalsHc(1, -2, 1) && p.s3.equalsHc(1, -2, -1)) {
                return true;
            } else if (p.s1.equalsHc(0, 1, 0) && p.s2.equalsHc(-1, -2, 1) && p.s3.equalsHc(-1, -2, -1)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("%s[dls=%d pas=%d tex=%d pal=%d]",
                getClass().getSimpleName(),
                displayLists.size(),
                polyAddrs.size(),
                (textures != null ? textures.length : null),
                (palette != null ? palette.length : null));
    }

}
