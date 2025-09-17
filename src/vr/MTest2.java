package vr;

public class MTest2 {

    public static void main(String[] args) {

        M l = new M(2, 3).setRows(new double[][]{{1, 2, 3}, {4, 5, 6}});
        M r = new M(3, 2).setRows(new double[][]{{7, 8}, {9, 10}, {11, 12}});
        System.out.println("l=" + l);
        System.out.println("r=" + r);
        M p = l.mul(r);
        System.out.println("p=" + p);
        M i = new M(2, 2).setId();
        System.out.println("i=" + i);
        M p2 = p.mul(i);
        System.out.println("p2=" + p2);
    }
}

