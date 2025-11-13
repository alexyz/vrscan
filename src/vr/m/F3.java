package vr.m;

/**
 * 3d point
 */
public class F3 {
    public float x,y,z;

    public F3() { }

    /** F3 with maximum values */
    public F3 setMax() {
        return set(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);
    }

    public F3 set(float x, float y, float z) {
        this.x = x; this.y = y; this.z = z;
        return this;
    }

    /** F3 with minimum values */
    public F3 setMin() {
        return set(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY);
    }

    public float get(int d) {
        switch (d) {
            case 0: return x;
            case 1: return y;
            case 2: return z;
            default: throw new RuntimeException();
        }
    }

    public void set(int d, float v) {
        switch (d) {
            case 0: x = v; break;
            case 1: y = v; break;
            case 2: z = v; break;
            default: throw new RuntimeException();
        }
    }

    public boolean equals3(float x, float y, float z) {
        return this.x == x && this.y == y && this.z == z;
    }

    public M1 toM1() {
        return M1.hc(x,y,z);
    }

    @Override
    public int hashCode() {
        return Float.hashCode(x)
                ^ Integer.rotateLeft(Float.hashCode(y), 10)
                ^ Integer.rotateLeft(Float.hashCode(z), 20);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof F3 f) {
            return x == f.x && y == f.y && z == f.z;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return String.format("[%+1.1f,%+1.1f,%+1.1f]",x,y,z);
    }
}
