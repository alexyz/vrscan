package vr;

import java.io.IOException;
import java.util.List;

public class World {
    public final Roms roms;
    public List<DL> totalDls;
    public List<PA> pas;

    public World(Roms roms) throws IOException {
        this.roms = roms;
    }

    public void init() throws IOException {
        this.totalDls = Polygons.loadDisplayLists(roms.loadSwap(Roms.RB.polygons));
        this.pas = Polygons.loadPolyAddrs(roms.loadSwap(Roms.RB.mainCpu1));
        for (DL dl : totalDls) {
            int exPa = (dl.offset + 16) / 4;
            dl.pa = pas.stream().filter(pa -> pa.polyAddr == exPa).findFirst().orElse(null);
        }
    }

    public Scene bf() {
        Scene bf = new Scene(Polygons.T1_START);
        bf.dls.addAll(totalDls.stream().filter(l -> l.offset >= Polygons.T1_START && l.offset < Polygons.T1_END).toList());
        return bf;
    }

    public Scene ap() {
        Scene ap = new Scene(Polygons.T2_START);
        ap.dls.addAll(totalDls.stream().filter(l -> l.offset >= Polygons.T2_START && l.offset < Polygons.T2_END).toList());
        return ap;
    }

    public Scene bb() {
        Scene bb = new Scene(Polygons.T3_START);
        bb.dls.addAll(totalDls.stream().filter(l -> l.offset >= Polygons.T3_START && l.offset < Polygons.T3_END).toList());
        return bb;
    }
}
