package net.zhuoweizhang.mcpelauncher;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import net.hockeyapp.android.utils.HttpURLConnectionBuilder;
import org.json.JSONException;
import org.json.JSONObject;

public class MpepInfo {
    public String authorName;
    public List<String> modFiles;
    public String modName;
    public String modNote;
    public String modVersion;
    public String scrambleCode;

    public static MpepInfo fromZip(ZipFile zip) throws IOException, JSONException {
        ZipEntry entry = zip.getEntry("metadata/metadata.json");
        if (entry == null) {
            return null;
        }
        InputStream is = zip.getInputStream(entry);
        byte[] buf = new byte[((int) entry.getSize())];
        is.read(buf);
        is.close();
        return fromJSON(new JSONObject(new String(buf, Charset.forName(HttpURLConnectionBuilder.DEFAULT_CHARSET))));
    }

    public static MpepInfo fromJSON(JSONObject json) throws JSONException {
        MpepInfo info = new MpepInfo();
        JSONObject m = json.getJSONObject("mod");
        info.modName = m.optString("mod_name");
        info.authorName = m.optString("author_name");
        info.modVersion = m.optString("mod_version");
        info.modNote = m.optString("mod_note");
        info.scrambleCode = m.optString("scramble_code");
        return info;
    }
}
