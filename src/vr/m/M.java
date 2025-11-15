package vr.m;

/**
 * abstract matrix
 */
public abstract class M {

    public abstract M set(int r, int c, float v);

    public abstract float get(int r, int c);

    public abstract int nc();

    public abstract int nr();

    public abstract M mul(M m);

    public P2 toP2() { throw new RuntimeException(); }

    public M setAll(float... v) {
        if (v.length == nr() * nc()) {
            for (int r = 0; r < nr(); r++) {
                for (int c = 0; c < nc(); c++) {
                    set(r, c, v[r * nc() + c]);
                }
            }
        } else {
            throw new RuntimeException();
        }
        return this;
    }

    public boolean equalValue(M m) {
        if (nc() == m.nc() && nr() == m.nr()) {
            for (int r = 0; r < nr(); r++) {
                for (int c = 0; c < nc(); c++) {
                    if (get(r, c) != m.get(r, c)) {
                        return false;
                    }
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        for (int r = 0; r < nr(); r++) {
            sb.append(r > 0 ? "; " : "").append("{");
            for (int c = 0; c < nc(); c++) {
                sb.append(c > 0 ? ", " : "").append(get(r, c));
            }
            sb.append("}");
        }
        sb.append("}");
        return sb.toString();
    }
}
