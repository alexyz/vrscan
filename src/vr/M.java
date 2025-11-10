package vr;

public class M {

    public static M trans4(double x, double y, double z) {
        return new M(4,4).setId().set(0, 3, x).set(1, 3, y).set(2, 3, z);
    }

    public static M scale4(double x, double y, double z) {
        return new M(4,4).set(0, 0, x).set(1, 1, y).set(2, 2, z).set(3, 3, 1);
    }

    public static M hc(double x, double y, double z) {
        return new M(4,1).set(0, 0, x).set(1, 0, y).set(2, 0, z).set(3, 0, 1);
    }

    public static M rx(double t) {
        double ct = Math.cos(t), st = Math.sin(t);
        return new M(4, 4)
                .set(0, 0, 1)
                .set(1, 1, ct).set(1, 2, -st)
                .set(2, 1, st).set(2, 2, ct)
                .set(3, 3, 1);
    }

    public static M ry(double t) {
        double ct = Math.cos(t), st = Math.sin(t);
        return new M(4, 4)
                .set(0, 0, ct).set(0, 2, st)
                .set(1, 1, 1)
                .set(2, 0, -st).set(2, 2, ct)
                .set(3, 3, 1);
    }

    public static M rz(double t) {
        double ct = Math.cos(t), st = Math.sin(t);
        return new M(4, 4)
                .set(0, 0, ct).set(0, 1, -st)
                .set(1, 0, st).set(1, 1, ct)
                .set(2, 2, 1)
                .set(3, 3, 1);
    }

    private final double[] v;
    public final int nr, nc;
    private boolean ro;

    public M(int nr, int nc) {
        this.nr = nr;
        this.nc = nc;
        this.v = new double[nr * nc];
    }

    public P2 toP2() {
        return toP2(new P2());
    }

    public P2 toP2(P2 out) {
        if (nr == 4 && nc == 1) {
            double x = get(0, 0);
            double y = get(1, 0);
            if (Double.isFinite(x) && Double.isFinite(y)) {
                out.x = (int) Math.round(x);
                out.y = (int) Math.round(y);
                return out;
            } else {
                throw new RuntimeException();
            }
        } else {
            throw new RuntimeException();
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        for (int r = 0; r < nr; r++) {
            sb.append(r > 0 ? "; " : "").append("{");
            for (int c = 0; c < nc; c++) {
                sb.append(c > 0 ? ", " : "").append(get(r, c));
            }
            sb.append("}");
        }
        sb.append("}");
        return sb.toString();
    }

    // can create scale matrix
//    public M setDiag(double[] v) {
//        if (nr == nc) {
//            for (int i = 0; i < v.length; i++) {
//                set(i, i, v[i]);
//            }
//            return this;
//        } else {
//            throw new RuntimeException();
//        }
//    }


    public M setId() {
        if (nr == nc) {
            for (int r = 0; r < nr; r++) {
                for (int c = 0; c < nc; c++) {
                    set(r, c, r == c ? 1 : 0);
                }
            }
            return this;
        } else {
            throw new RuntimeException();
        }
    }

    public M setRow(int r, double... cv) {
        if (r >= 0 && r < nr && cv.length == nc) {
            for (int c = 0; c < cv.length; c++) {
                set(r, c, cv[c]);
            }
            return this;
        } else {
            throw new RuntimeException();
        }
    }

    public M setCol(int c, double... rv) {
        if (c >= 0 && c < nc && rv.length == nr) {
            for (int r = 0; r < rv.length; r++) {
                set(r, c, v[r]);
            }
            return this;
        } else {
            throw new RuntimeException();
        }
    }

//    public M setRows(double[][] v) {
//        if (v.length == nr) {
//            for (int r = 0; r < v.length; r++) {
//                if (v[r].length == nc) {
//                    for (int c = 0; c < v[r].length; c++) {
//                        set(r, c, v[r][c]);
//                    }
//                } else {
//                    throw new RuntimeException();
//                }
//            }
//            return this;
//        } else {
//            throw new RuntimeException();
//        }
//    }

    public M mul(M rm) {
        return mul(rm, new M(nr, rm.nc));
    }

    // the result matrix has the number of rows of the first and the number of columns of the second matrix.
    public M mul(M rm, M dm) {
        if (nc == rm.nr && dm.nr == nr && dm.nc == rm.nc) {
            //M dm = new M(nr, rm.nc);
            for (int dr = 0; dr < nr; dr++) { // the left row
                for (int dc = 0; dc < rm.nc; dc++) { // the right col
                    double v = 0;
                    for (int i = 0; i < nc; i++) { // each col in left row and each row in right col
                        v += get(dr, i) * rm.get(i, dc);
                    }
                    dm.set(dr, dc, v);
                }
            }
            return dm;
        } else {
            throw new RuntimeException();
        }
    }

    public M setRo() {
        this.ro = true;
        return this;
    }

//    public M setRow(int r, double... v) {
//        if (r >= 0 && r < nr && v.length <= nc) {
//            for (int c = 0; c < v.length; c++) {
//                set(r, c, v[c]);
//            }
//        } else {
//            throw new RuntimeException();
//        }
//        return this;
//    }
//
//    public M setCol(int c, double... v) {
//        if (c >= 0 && c < nc && v.length <= nr) {
//            for (int r = 0; r < v.length; r++) {
//                set(r, c, v[c]);
//            }
//        } else {
//            throw new RuntimeException();
//        }
//        return this;
//    }

    public M set(int r, int c, double v) {
        if (!ro && r >= 0 && r < nr && c >= 0 && c < nc) {
            this.v[(r * nc) + c] = v;
            return this;
        } else {
            throw new RuntimeException();
        }
    }

    public double get(int r, int c) {
        if (r >= 0 && r < nr && c >= 0 && c < nc) {
            return v[(r * nc) + c];
        } else {
            throw new RuntimeException();
        }
    }
}
