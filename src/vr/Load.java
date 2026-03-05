package vr;

public class Load {
    public final String[] fileNames;
    public final LoadType type;

    public Load(LoadType type, String[] fileNames) {
        this.type = type;
        this.fileNames = fileNames;
    }
}