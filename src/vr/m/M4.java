package vr.m;

/**
 * 4x4 matrix
 */
public class M4 extends M {

    public static M4 id() {
        M4 m = new M4();
        for (int i = 0; i < 4; i++) {
            m.set(i, i, 1);
        }
        return m;
    }

    public static M4 trans(float x, float y, float z) {
        M4 m = id();
        m.set(0, 3, x);
        m.set(1, 3, y);
        m.set(2, 3, z);
        return m;
    }

    public static M4 scale(float x, float y, float z) {
        M4 m = id();
        m.set(0, 0, x);
        m.set(1, 1, y);
        m.set(2, 2, z);
        return m;
    }

    public static M4 rx(float t) {
        float ct = (float) Math.cos(t), st = (float) Math.sin(t);
        M4 m = id();
        m.set(1, 1, ct);
        m.set(1, 2, -st);
        m.set(2, 1, st);
        m.set(2, 2, ct);
        return m;
    }

    public static M4 ry(float t) {
        float ct = (float) Math.cos(t), st = (float) Math.sin(t);
        M4 m = id();
        m.set(0, 0, ct);
        m.set(0, 2, st);
        m.set(2, 0, -st);
        m.set(2, 2, ct);
        return m;
    }

    public static M4 rz(float t) {
        float ct = (float) Math.cos(t), st = (float) Math.sin(t);
        M4 m = id();
        m.set(0, 0, ct);
        m.set(0, 1, -st);
        m.set(1, 0, st);
        m.set(1, 1, ct);
        return m;
    }

    public M4() {
        super(new float[16]);
    }

    @Override
    public int nc() {
        return 4;
    }

    @Override
    public int nr() {
        return 4;
    }

    public int index(int r, int c) {
        if (r >= 0 && r < 4 && c >= 0 && c < 4) {
            return r * 4 + c;
        } else {
            return -1;
        }
    }

    public M mul(M bm) {
        if (bm instanceof M1 bm1) {
            return mul1(bm1, new M1());
        } else if (bm instanceof M4 bm4) {
            return mul4(bm4, new M4());
        } else {
            throw new RuntimeException();
        }
    }

    public M1 mul1(M1 bm, M1 cm) {
        float[] b = bm.a;
        float[] c = cm.a;
        for (int i = 0; i < 4; i++) {
            int ai = i << 2;
            c[i] = a[ai] * b[0] + a[ai + 1] * b[1] + a[ai + 2] * b[2] + a[ai + 3] * b[3];
        }
        return cm;
    }

    // the result matrix has the number of rows of the first and the number of columns of the second matrix.
    public M4 mul4(M4 bm, M4 cm) {
        float[] b = bm.a;
        float[] c = cm.a;
        for (int i = 0; i < 16; i++) {
            int ai = i & 0xc;
            int bi = i & 0x3;
            c[i] = a[ai] * b[bi] + a[ai + 1] * b[bi + 4] + a[ai + 2] * b[bi + 8] + a[ai + 3] * b[bi + 12];
        }
        return cm;
    }

}
