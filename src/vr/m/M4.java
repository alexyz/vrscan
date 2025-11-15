package vr.m;

/**
 * 4x4 matrix
 */
public class M4 extends M {

    public static void main(String[] args) {
        M a = new M4().setAll(1, 2, 3, 3.5f, 4, 5, 6, 6.5f, 7, 8, 9, 9.5f, 10, 11, 12, 13);
        M b = new M4().setAll(9, 8, 7, 7.5f, 6, 5, 4, 4.5f, 3, 2, 1, 1.5f, -1, -2, -3, -4);
        M ab = a.mul(b);
        M exAb = new M4().setAll(26.5f, 17f, 7.5f, 7f, 77.5f, 56, 34.5f, 35.5f, 128.5f, 95, 61.5f, 64, 179, 133, 87, 90.5f);
        System.out.println("ab=" + ab);
        if (!ab.equalValue(exAb)) {
            throw new RuntimeException();
        }

        M d = new M1().setAll(9, 8, 7, 7.5f);
        M ad = a.mul(d);
        System.out.println("ad=" + ad);
        M exAd = new M1().setAll(72.25f, 166.75f, 261.25f, 359.5f);
        if (!ad.equalValue(exAd)) {
            throw new RuntimeException();
        }
    }

    public static M4 id() {
        M4 m = new M4();
        m.r0c0 = m.r1c1 = m.r2c2 = m.r3c3 = 1;
        return m;
    }

    public static M4 trans(float x, float y, float z) {
        M4 m = id();
        m.r0c3 = x;
        m.r1c3 = y;
        m.r2c3 = z;
        return m;
    }

    public static M4 scale(float x, float y, float z) {
        M4 m = id();
        m.r0c0 = x;
        m.r1c1 = y;
        m.r2c2 = z;
        return m;
    }

    public static M4 rx(float t) {
        float ct = (float) Math.cos(t), st = (float) Math.sin(t);
        M4 m = id();
        m.r1c1 = ct;
        m.r1c2 = -st;
        m.r2c1 = st;
        m.r2c2 = ct;
        return m;
    }

    public static M4 ry(float t) {
        float ct = (float) Math.cos(t), st = (float) Math.sin(t);
        M4 m = id();
        m.r0c0 = ct;
        m.r0c2 = st;
        m.r2c0 = -st;
        m.r2c2 = ct;
        return m;
    }

    public static M4 rz(float t) {
        float ct = (float) Math.cos(t), st = (float) Math.sin(t);
        M4 m = id();
        m.r0c0 = ct;
        m.r0c1 = -st;
        m.r1c0 = st;
        m.r1c1 = ct;
        return m;
    }

    public float r0c0, r0c1, r0c2, r0c3;
    public float r1c0, r1c1, r1c2, r1c3;
    public float r2c0, r2c1, r2c2, r2c3;
    public float r3c0, r3c1, r3c2, r3c3;

    @Override
    public int nc() {
        return 4;
    }

    @Override
    public int nr() {
        return 4;
    }

    public M4 set(int r, int c, float v) {
        if (r >= 0 && r < 4 && c >= 0 && c < 4) {
            switch (r * 4 + c) {
                case 0:
                    r0c0 = v;
                    break;
                case 1:
                    r0c1 = v;
                    break;
                case 2:
                    r0c2 = v;
                    break;
                case 3:
                    r0c3 = v;
                    break;
                case 4:
                    r1c0 = v;
                    break;
                case 5:
                    r1c1 = v;
                    break;
                case 6:
                    r1c2 = v;
                    break;
                case 7:
                    r1c3 = v;
                    break;
                case 8:
                    r2c0 = v;
                    break;
                case 9:
                    r2c1 = v;
                    break;
                case 10:
                    r2c2 = v;
                    break;
                case 11:
                    r2c3 = v;
                    break;
                case 12:
                    r3c0 = v;
                    break;
                case 13:
                    r3c1 = v;
                    break;
                case 14:
                    r3c2 = v;
                    break;
                case 15:
                    r3c3 = v;
                    break;
            }
            return this;
        }
        throw new RuntimeException();
    }

    public float get(int r, int c) {
        if (r >= 0 && r < 4 && c >= 0 && c < 4) {
            switch (r * 4 + c) {
                case 0:
                    return r0c0;
                case 1:
                    return r0c1;
                case 2:
                    return r0c2;
                case 3:
                    return r0c3;
                case 4:
                    return r1c0;
                case 5:
                    return r1c1;
                case 6:
                    return r1c2;
                case 7:
                    return r1c3;
                case 8:
                    return r2c0;
                case 9:
                    return r2c1;
                case 10:
                    return r2c2;
                case 11:
                    return r2c3;
                case 12:
                    return r3c0;
                case 13:
                    return r3c1;
                case 14:
                    return r3c2;
                case 15:
                    return r3c3;
            }
        }
        throw new RuntimeException();
    }

    public M mul(M rm) {
        if (rm instanceof M1 rm1) {
            return mul1(rm1, new M1());
        } else if (rm instanceof M4 rm4) {
            return mul4(rm4, new M4());
        } else {
            throw new RuntimeException();
        }
    }

    public M1 mul1(M1 rm, M1 dm) {
        dm.r0 = r0c0 * rm.r0 + r0c1 * rm.r1 + r0c2 * rm.r2 + r0c3 * rm.r3;
        dm.r1 = r1c0 * rm.r0 + r1c1 * rm.r1 + r1c2 * rm.r2 + r1c3 * rm.r3;
        dm.r2 = r2c0 * rm.r0 + r2c1 * rm.r1 + r2c2 * rm.r2 + r2c3 * rm.r3;
        dm.r3 = r3c0 * rm.r0 + r3c1 * rm.r1 + r3c2 * rm.r2 + r3c3 * rm.r3;
        return dm;
    }

    // the result matrix has the number of rows of the first and the number of columns of the second matrix.
    public M4 mul4(M4 rm, M4 dm) {
        dm.r0c0 = r0c0 * rm.r0c0 + r0c1 * rm.r1c0 + r0c2 * rm.r2c0 + r0c3 * rm.r3c0;
        dm.r0c1 = r0c0 * rm.r0c1 + r0c1 * rm.r1c1 + r0c2 * rm.r2c1 + r0c3 * rm.r3c1;
        dm.r0c2 = r0c0 * rm.r0c2 + r0c1 * rm.r1c2 + r0c2 * rm.r2c2 + r0c3 * rm.r3c2;
        dm.r0c3 = r0c0 * rm.r0c3 + r0c1 * rm.r1c3 + r0c2 * rm.r2c3 + r0c3 * rm.r3c3;
        dm.r1c0 = r1c0 * rm.r0c0 + r1c1 * rm.r1c0 + r1c2 * rm.r2c0 + r1c3 * rm.r3c0;
        dm.r1c1 = r1c0 * rm.r0c1 + r1c1 * rm.r1c1 + r1c2 * rm.r2c1 + r1c3 * rm.r3c1;
        dm.r1c2 = r1c0 * rm.r0c2 + r1c1 * rm.r1c2 + r1c2 * rm.r2c2 + r1c3 * rm.r3c2;
        dm.r1c3 = r1c0 * rm.r0c3 + r1c1 * rm.r1c3 + r1c2 * rm.r2c3 + r1c3 * rm.r3c3;
        dm.r2c0 = r2c0 * rm.r0c0 + r2c1 * rm.r1c0 + r2c2 * rm.r2c0 + r2c3 * rm.r3c0;
        dm.r2c1 = r2c0 * rm.r0c1 + r2c1 * rm.r1c1 + r2c2 * rm.r2c1 + r2c3 * rm.r3c1;
        dm.r2c2 = r2c0 * rm.r0c2 + r2c1 * rm.r1c2 + r2c2 * rm.r2c2 + r2c3 * rm.r3c2;
        dm.r2c3 = r2c0 * rm.r0c3 + r2c1 * rm.r1c3 + r2c2 * rm.r2c3 + r2c3 * rm.r3c3;
        dm.r3c0 = r3c0 * rm.r0c0 + r3c1 * rm.r1c0 + r3c2 * rm.r2c0 + r3c3 * rm.r3c0;
        dm.r3c1 = r3c0 * rm.r0c1 + r3c1 * rm.r1c1 + r3c2 * rm.r2c1 + r3c3 * rm.r3c1;
        dm.r3c2 = r3c0 * rm.r0c2 + r3c1 * rm.r1c2 + r3c2 * rm.r2c2 + r3c3 * rm.r3c2;
        dm.r3c3 = r3c0 * rm.r0c3 + r3c1 * rm.r1c3 + r3c2 * rm.r2c3 + r3c3 * rm.r3c3;
        return dm;
    }

}
