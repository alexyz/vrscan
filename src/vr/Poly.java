package vr;

import vr.m.*;

import java.awt.*;

public class Poly {
    /*
     * 0000 000L | LNNI III0 | 0BMT ZZLL | 0000 00TT
     * T = type
     * L = link, link2
     * I = lightmode
     * N = netmerc
     * U = unknown
     */

    /***
     * note the only flag that must be non-zero is type
     */
    public static boolean isWord(int w) {
        int t = type(w);
        return unknown(w) == 0 && (t == 1 || t == 2) && link(w) == link2(w);
    }

    public static final int TYPE_Q = 1, TYPE_T = 2;

    public static int type(int w) {
        return w & 3;
    }

    /** always 0 */
    public static int unknown(int w) {
        return w & 0xfe0180fc;
    }

    public static int link(int w) {
        return (w >>> 8) & 3;
    }

    public static int zorder(int w) {
        return (w >>> 10) & 3;
    }

    public static int texadr(int w) {
        return (w >>> 12) & 1;
    }

    public static int moire(int w) {
        return (w >>> 13) & 1;
    }

    public static int bfcull(int w) {
        return (w >>> 14) & 1;
    }

    public static int lightmode(int w) {
        return (w >>> 17) & 15;
    }

    public static int netmerc(int w) {
        return (w >>> 21) & 3;
    }

    public static int link2(int w) {
        return (w >>> 23) & 3;
    }

    public static int red(int col) {
        return (col & 0x1f) * 8;
    }

    public static int green(int col) {
        return ((col >>> 5) & 0x1f) * 8;
    }

    public static int blue(int col) {
        return ((col >>> 10) & 0x1f) * 8;
    }

    public static String typeStr(int w) {
        switch (type(w)) {
            case TYPE_Q: return "Q";
            case TYPE_T: return "T";
            default: return w + "?";
        }
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

    private void app(StringBuilder sb, String k, int v) {
        if (v != 0) {
            sb.append(" ").append(k).append("=").append(Integer.toHexString(v));
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("t=").append(typeStr(word));
        app(sb,"ln", link(word));
        app(sb,"zo", zorder(word));
        app(sb,"ta", texadr(word));
        app(sb,"mo", moire(word));
        app(sb,"bf", bfcull(word));
        app(sb,"lm", lightmode(word));
        app(sb,"nm", netmerc(word));
        app(sb,"un", unknown(word));

        String ws = sb.toString();
//        String ws = String.format("t=%s ln=%x zo=%x ta=%x mo=%x bf=%x lm=%x",
//                typeStr(word), link(word), zorder(word), texadr(word), moire(word), bfcull(word), lightmode(word));

        String cs = String.format("ta=%x tex=%x col=%x", texAddr, tex, col);

        if (s2.equals(s3)) {
            return String.format("P[%8x [%s] [%s], %s, %s, -]", word, ws, cs, s1.toHcStr(), s2.toHcStr());
        } else {
            return String.format("P[%8x [%s] [%s], %s, %s, %s]", word, ws, cs, s1.toHcStr(), s2.toHcStr(), s3.toHcStr());
        }
    }
}
