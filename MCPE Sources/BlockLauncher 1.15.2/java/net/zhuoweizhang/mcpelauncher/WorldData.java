package net.zhuoweizhang.mcpelauncher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import net.hockeyapp.android.utils.HttpURLConnectionBuilder;
import org.json.JSONException;
import org.json.JSONObject;

public class WorldData {
    boolean dirty = false;
    JSONObject mData;
    File mDir;
    File mFile;

    public WorldData(File dir) throws IOException {
        this.mDir = dir;
        this.mFile = new File(dir, "blocklauncher_data.json");
        load();
    }

    protected void load() throws IOException {
        Throwable th;
        if (!this.mFile.exists() || this.mFile.length() == 0) {
            loadDefaults();
            return;
        }
        byte[] buf = new byte[((int) this.mFile.length())];
        FileInputStream fis = null;
        try {
            FileInputStream fis2 = new FileInputStream(this.mFile);
            try {
                fis2.read(buf);
                if (fis2 != null) {
                    try {
                        fis2.close();
                    } catch (IOException ie) {
                        ie.printStackTrace();
                    }
                }
                if (true) {
                    try {
                        this.mData = new JSONObject(new String(buf, HttpURLConnectionBuilder.DEFAULT_CHARSET));
                        return;
                    } catch (JSONException je) {
                        je.printStackTrace();
                        loadDefaults();
                        return;
                    }
                }
                loadDefaults();
            } catch (Throwable th2) {
                th = th2;
                fis = fis2;
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException ie2) {
                        ie2.printStackTrace();
                    }
                }
                throw th;
            }
        } catch (Throwable th3) {
            th = th3;
            if (fis != null) {
                fis.close();
            }
            throw th;
        }
    }

    protected void loadDefaults() {
        try {
            this.mData = new JSONObject();
            this.mData.put("entities", new JSONObject());
        } catch (JSONException je) {
            throw new RuntimeException(je);
        }
    }

    public void setEntityData(long entityId, String key, String value) {
        if (key.indexOf(".") == -1) {
            throw new RuntimeException("Entity data keys must be in format of author.modname.keyname; for example, coolmcpemodder.sz.favoritecolor");
        }
        try {
            JSONObject obj = this.mData.getJSONObject("entities");
            JSONObject entityData = this.mData.optJSONObject(Long.toString(entityId));
            if (entityData == null) {
                entityData = new JSONObject();
                obj.put(Long.toString(entityId), entityData);
            }
            entityData.put(key, value);
            this.dirty = true;
        } catch (JSONException je) {
            throw new RuntimeException(je);
        }
    }

    public String getEntityData(long entityId, String key) {
        try {
            JSONObject entityData = this.mData.getJSONObject("entities").optJSONObject(Long.toString(entityId));
            if (entityData == null) {
                return null;
            }
            return entityData.optString(key);
        } catch (JSONException je) {
            throw new RuntimeException(je);
        }
    }

    public void clearEntityData(long entityId) {
        try {
            this.dirty = this.mData.getJSONObject("entities").remove(Long.toString(entityId)) != null;
            if (!this.dirty) {
            }
        } catch (JSONException je) {
            throw new RuntimeException(je);
        }
    }

    public void save() throws IOException {
        Throwable th;
        if (this.dirty) {
            FileOutputStream fis = null;
            try {
                FileOutputStream fis2 = new FileOutputStream(this.mFile);
                try {
                    fis2.write(this.mData.toString().getBytes(HttpURLConnectionBuilder.DEFAULT_CHARSET));
                    this.dirty = false;
                    if (fis2 != null) {
                        try {
                            fis2.close();
                        } catch (IOException ie) {
                            ie.printStackTrace();
                        }
                    }
                } catch (Throwable th2) {
                    th = th2;
                    fis = fis2;
                    if (fis != null) {
                        try {
                            fis.close();
                        } catch (IOException ie2) {
                            ie2.printStackTrace();
                        }
                    }
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                if (fis != null) {
                    fis.close();
                }
                throw th;
            }
        }
    }
}
