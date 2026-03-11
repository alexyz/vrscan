package vr.m;

/**
 * 4x1 vector
 */
public class M1 extends M {

    public M1() {
        //
    }

    public final float[] a = new float[4];

    @Override
    public int index(int r, int c) {
        if (c == 0) {
            return r;
        } else {
            return -1;
        }
    }

    @Override
    public float[] values() {
        return a;
    }

    @Override
    public int nc() {
        return 1;
    }

    @Override
    public int nr() {
        return 4;
    }

    public M1 sub(M1 bm) {
        return sub(bm, new M1());
    }

    public M1 sub(M1 bm, M1 cm) {
        float[] b = bm.a;
        float[] c = cm.a;
        for (int i = 0; i < 4; i++) {
            c[i] = a[i] - b[i];
        }
        return cm;
    }

//    public boolean lte(M1 bm) {
//        float[] b = bm.a;
//        for (int i = 0; i < 4; i++) {
//            if (a[i] > b[i]) {
//                return false;
//            }
//        }
//        return true;
//    }
//
//    public M1 min(M1 bm) {
//        if (lte(bm)) {
//            return this;
//        } else if (bm.lte(this)) {
//            return bm;
//        } else {
//            M1 cm = new M1();
//            for (int i = 0; i < 4; i++) {
//                cm.a[i] = Math.min(a[i], bm.a[i]);
//            }
//            return cm;
//        }
//    }

    public M1 setMin(M1 om1) {
        for (int i = 0; i < 4; i++) {
            if (om1.a[i] < a[i]) {
                a[i] = om1.a[i];
            }
        }
        return this;
    }

    public M1 setMax(M1 om1) {
        for (int i = 0; i < 4; i++) {
            if (om1.a[i] > a[i]) {
                a[i] = om1.a[i];
            }
        }
        return this;
    }

    @Override
    public M mul(M m) {
        throw new RuntimeException();
    }

    /** F3 with maximum values */
    public M1 setMaxHc() {
        return setHc(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);
    }

    /** F3 with minimum values */
    public M1 setMinHc() {
        return setHc(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY);
    }

    public M1 setHc(float x, float y, float z) {
        a[0] = x;
        a[1] = y;
        a[2] = z;
        a[3] = 1;
        return this;
    }

    public boolean equalsHc(float x, float y, float z) {
        if (a[3] == 1) {
            return x() == x && y() == y && z() == z;
        } else {
            throw new RuntimeException();
        }
    }

    public M1 toHc() {
        if (a[3] == 1) {
            return this;
        } else {
            throw new RuntimeException();
        }
    }

    public float x() {
        return a[0];
    }

    public float y() {
        return a[1];
    }

    public float z() {
        return a[2];
    }

    public P2 toP2() {
        return toP2(new P2());
    }

    public P2 toP2(P2 out) {
        float x = x(), y = y();
        if (Double.isFinite(x) && Double.isFinite(y)) {
            out.set(Math.round(x), Math.round(y));
            return out;
        } else {
            throw new RuntimeException("cannot convert to P2: " + this);
        }
    }

    public String toHcStr() {
        if (a[3] == 1) {
            return String.format("[%.2f, %.2f, %.2f]", a[0], a[1], a[2]);
        } else {
            return super.toString();
        }

    }

    public boolean isZeroHc() {
        return a[0] == 0 && a[1] == 0 && a[2] == 0 && a[3] == 1;
    }
}
