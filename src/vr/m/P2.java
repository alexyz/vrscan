package vr.m;

/**
 * 2d screen point
 */
public class P2 {
    public int x, y;

    public P2() {
    }

    public P2(int x, int y) {
        set(x,y);
    }

    public void set(P2 p) {
        this.x = p.x;
        this.y = p.y;
    }

    public void set(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public P2 add(P2 p2) {
        return new P2(x+p2.x, y+p2.y);
    }

    public P2 min(P2 op) {
        if (x <= op.x && y <= op.y) {
            return this;
        } else if (op.x <= x && op.y <= y) {
            return op;
        } else {
            return new P2(Math.min(x, op.x), Math.min(y, op.y));
        }
    }

    public P2 max(P2 op) {
        if (x >= op.x && y >= op.y) {
            return this;
        } else if (op.x >= x && op.y >= y) {
            return op;
        } else {
            return new P2(Math.max(x, op.x), Math.max(y, op.y));
        }
    }

    /**
     * difference between points
     */
    public P2 diff(P2 p) {
        return new P2(Math.abs(x - p.x), Math.abs(y - p.y));
    }

    @Override
    public int hashCode() {
        return x ^ Integer.rotateLeft(y, 16);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof P2 p) {
            return x == p.x && y == p.y;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return String.format("P[%d,%d]", x, y);
    }
}
