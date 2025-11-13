package vr.m;

/**
 * 4x1 vector
 */
public class M1 extends M {

    public static M1 hc(double x, double y, double z) {
        M1 m = new M1();
        m.r0 = x;
        m.r1 = y;
        m.r2 = z;
        m.r3 = 1;
        return m;
    }

    public double r0, r1, r2, r3;

    @Override
    public M set(int r, int c, double v) {
        if (r >= 0 && r < 4 && c == 0) {
            switch (r) {
                case 0: r0 = v; break;
                case 1: r1 = v; break;
                case 2: r2 = v; break;
                case 3: r3 = v; break;
            }
            return this;
        }
        throw new RuntimeException();
    }

    @Override
    public double get(int r, int c) {
        if (r >= 0 && r < 4 && c == 0) {
            switch (r) {
                case 0: return r0;
                case 1: return r1;
                case 2: return r2;
                case 3: return r3;
            }
        }
        throw new RuntimeException();
    }

    @Override
    public int nc() {
        return 1;
    }

    @Override
    public int nr() {
        return 4;
    }

    @Override
    public M mul(M m) {
        throw new RuntimeException();
    }

    public P2 toP2() {
        return toP2(new P2());
    }

    public P2 toP2(P2 out) {
            double x = r0;
            double y = r1;
            if (Double.isFinite(x) && Double.isFinite(y)) {
                out.x = (int) Math.round(x);
                out.y = (int) Math.round(y);
                return out;
            } else {
                throw new RuntimeException();
            }
    }

}
