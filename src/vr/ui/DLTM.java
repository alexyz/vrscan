package vr.ui;

import vr.DL;
import vr.Poly;

import javax.swing.table.AbstractTableModel;

public class DLTM extends AbstractTableModel {

    private static final String[] cols = {
            "N", "Addr", "Word", "Type",
            "Ln", "Zo", "Ta", "Mo",
            "BfC", "Lm", "S1", "S2",
            "S3", "Ta", "Tex", "Col",
            "Col"
    };

    private final DL dl;

    public DLTM(DL dl) {
        this.dl = dl;
    }

    @Override
    public int getRowCount() {
        return dl.polys.size();
    }

    @Override
    public int getColumnCount() {
        return cols.length;
    }

    @Override
    public String getColumnName(int c) {
        return cols[c];
    }

    @Override
    public Object getValueAt(int r, int c) {
        Poly p = dl.polys.get(r);
        int w = p.word;
        switch (c) {
            case 0:
                return r;
            case 1:
                return dl.offset + r * 40;
            case 2:
                return w;
            case 3:
                return Poly.typeStr(w);
            case 4:
                return Poly.link(w);
            case 5:
                return Poly.zorder(w);
            case 6:
                return Poly.texadr(w);
            case 7:
                return Poly.moire(w);
            case 8:
                return Poly.bfcull(w);
            case 9:
                return Poly.lightmode(w);
            case 10:
                return p.s1.isZeroHc() ? null : p.s1; // if 0, then show empty string
            case 11:
                return p.s2;
            case 12:
                return Poly.type(w) == Poly.TYPE_Q ? p.s3 : null; // if T then show empty string
            case 13:
                return p.texAddr;
            case 14:
                return p.tex;
            case 15:
                return p.col;
            case 16:
                return p.colObj;
            default:
                return -1;
        }
    }

}
