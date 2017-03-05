package net.zhuoweizhang.mcpelauncher.texture;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.zhuoweizhang.mcpelauncher.TexturePack;

public class TextureUtils {
    public static List<String> getAllFilesFilter(List<TexturePack> packs, String filter) throws IOException {
        List<String> allFiles = new ArrayList();
        for (TexturePack p : packs) {
            List<String> fileList = p.listFiles();
            List<String> temp = new ArrayList();
            for (String s : fileList) {
                if (s.contains(filter)) {
                    temp.add(s);
                }
            }
            Collections.sort(temp);
            allFiles.addAll(temp);
        }
        return allFiles;
    }

    public static String removeExtraDotsFromPath(String inStr) {
        int endDot = inStr.lastIndexOf(46);
        return (endDot == -1 || inStr.indexOf(46) == endDot) ? inStr : inStr.substring(0, endDot).replace('.', '_') + inStr.substring(endDot);
    }
}
