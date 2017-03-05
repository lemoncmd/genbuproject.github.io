package net.zhuoweizhang.mcpelauncher.patch;

import java.util.ArrayList;
import java.util.List;

public abstract class Patch {
    protected List<PatchSegment> segments = new ArrayList();

    public List<PatchSegment> getSegments() {
        return this.segments;
    }
}
