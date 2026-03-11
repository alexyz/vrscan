package vr.ui;

import vr.DL;

public class DLCI {
    public final DL dl;

    public DLCI(DL dl) {
        this.dl = dl;
    }

    @Override
    public String toString() {
        if (dl != null) {
            return String.format("DL %x (%d polys)", dl.offset, dl.polys.size());
        } else {
            return "*";
        }
    }
}
