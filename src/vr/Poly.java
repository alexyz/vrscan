package vr;

import vr.m.*;

import java.awt.*;

public class Poly {
    // 00 82 29 01
    //  1 54 06 01 (nm)
    //  1 f8 07 02 (nm)
    /**
     * 0000 000U | UUUL LLL0 | 0BMT ZZLL | 0000 00TT
     * U = unknown
     */
    public static int W_MASK = 0b0000_0001_1111_1110_0111_1111_0000_0011;

    public static boolean isWord(int w) {
        return (w & W_MASK) != 0 && (w & ~W_MASK) == 0;
    }

    public static final int TYPE_Q = 1, TYPE_T = 2;

    public static int type(int w) {
        return w & 3;
    }

    public static int link(int w) {
        return (w >> 8) & 3;
    }

    public static int zorder(int w) {
        return (w >> 10) & 3;
    }

    public static int texadr(int w) {
        return (w >> 12) & 1;
    }

    public static int moire(int w) {
        return (w >> 13) & 1;
    }

    public static int backface(int w) {
        return (w >> 14) & 1;
    }

    public static int lightmode(int w) {
        return (w >> 17) & 15;
    }

    public static int red(int col) {
        return (col & 0x1f) * 8;
    }

    public static int green(int col) {
        return ((col >> 5) & 0x1f) * 8;
    }

    public static int blue(int col) {
        return ((col >> 10) & 0x1f) * 8;
    }

    public static Poly readPara(int[] words, int o) {
        Poly p = new Poly();
        p.word = words[o];
        readSeg(words, o + 1, p.s1);
        readSeg(words, o + 4, p.s2);
        readSeg(words, o + 7, p.s3);
        return p;
    }

    private static void readSeg(int[] w, int o, M1 f) {
        f.setHc(Float.intBitsToFloat(w[o]), Float.intBitsToFloat(w[o + 1]), Float.intBitsToFloat(w[o + 2]));
    }

    public int word;
    public int texAddr, tex, col;
    public Color colObj;
    public final M1 s1 = new M1(), s2 = new M1(), s3 = new M1();

    @Override
    public boolean equals(Object o) {
        if (o instanceof Poly p) {
            return word == p.word && s1.equals(p.s1) && s2.equals(p.s2) && s3.equals(p.s3);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        String ws = String.format("t=%s l=%d z=%d ta=%d m=%d bf=%d lm=%d",
                type(word), link(word), zorder(word), texadr(word), moire(word), backface(word), lightmode(word));

        String cs = String.format("ta=%x tex=%x col=%x", texAddr, tex, col);

        if (s2.equals(s3)) {
            return String.format("P[%8x [%s] [%s], %s, %s, -]", word, ws, cs, s1, s2);
        } else {
            return String.format("P[%8x [%s] [%s], %s, %s, %s]", word, ws, cs, s1, s2, s3);
        }
    }
}
