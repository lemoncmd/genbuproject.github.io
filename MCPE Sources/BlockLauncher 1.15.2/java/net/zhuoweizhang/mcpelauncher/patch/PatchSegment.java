package net.zhuoweizhang.mcpelauncher.patch;

import java.util.Arrays;

public class PatchSegment {
    public byte[] data;
    public int offset;

    public PatchSegment(int offset, byte[] data) {
        this.offset = offset;
        this.data = data;
    }

    public String toString() {
        return super.toString() + " offset=" + Integer.toString(this.offset, 16) + " data=" + Arrays.toString(this.data);
    }
}
