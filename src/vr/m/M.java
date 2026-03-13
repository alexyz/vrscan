package vr.m;

import java.util.Arrays;

/**
 * abstract matrix
 */
public abstract class M {

    protected final float[] a;

    protected M(float[] a) {
        this.a = a;
    }

    public abstract int nc();

    public abstract int nr();

    public abstract M mul(M m);

    public abstract int index(int r, int c);

    public P2 toP2() { throw new RuntimeException(); }

    public M set(int r, int c, float v) {
        a[index(r, c)] = v;
        return this;
    }

    public float get(int r, int c) {
        return a[index(r, c)];
    }

    public M setAll(float... v) {
        if (a.length == v.length) {
            System.arraycopy(v, 0, a, 0, v.length);
            return this;
        } else {
            throw new RuntimeException();
        }
    }

    @Override
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

    @Override
    public boolean equals(Object obj) {
        return obj instanceof M m && Arrays.equals(a, m.a);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(a);
    }
}
