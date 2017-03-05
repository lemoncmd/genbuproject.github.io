package net.zhuoweizhang.mcpelauncher;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface TexturePack extends Closeable {
    InputStream getInputStream(String str) throws IOException;

    long getSize(String str) throws IOException;

    List<String> listFiles() throws IOException;
}
