package net.hockeyapp.android.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import net.zhuoweizhang.mcpelauncher.api.modpe.EnchantType;

public class SimpleMultipartEntity {
    private static final char[] BOUNDARY_CHARS = "-_1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    private String mBoundary;
    private boolean mIsSetFirst = false;
    private boolean mIsSetLast = false;
    private ByteArrayOutputStream mOut = new ByteArrayOutputStream();

    public SimpleMultipartEntity() {
        int i = 0;
        StringBuffer stringBuffer = new StringBuffer();
        Random random = new Random();
        while (i < 30) {
            stringBuffer.append(BOUNDARY_CHARS[random.nextInt(BOUNDARY_CHARS.length)]);
            i++;
        }
        this.mBoundary = stringBuffer.toString();
    }

    public void addPart(String str, File file, boolean z) throws IOException {
        addPart(str, file.getName(), new FileInputStream(file), z);
    }

    public void addPart(String str, String str2) throws IOException {
        writeFirstBoundaryIfNeeds();
        this.mOut.write(("Content-Disposition: form-data; name=\"" + str + "\"\r\n").getBytes());
        this.mOut.write("Content-Type: text/plain; charset=UTF-8\r\n".getBytes());
        this.mOut.write("Content-Transfer-Encoding: 8bit\r\n\r\n".getBytes());
        this.mOut.write(str2.getBytes());
        this.mOut.write(("\r\n--" + this.mBoundary + "\r\n").getBytes());
    }

    public void addPart(String str, String str2, InputStream inputStream, String str3, boolean z) throws IOException {
        writeFirstBoundaryIfNeeds();
        try {
            String str4 = "Content-Type: " + str3 + "\r\n";
            this.mOut.write(("Content-Disposition: form-data; name=\"" + str + "\"; filename=\"" + str2 + "\"\r\n").getBytes());
            this.mOut.write(str4.getBytes());
            this.mOut.write("Content-Transfer-Encoding: binary\r\n\r\n".getBytes());
            byte[] bArr = new byte[EnchantType.fishingRod];
            while (true) {
                int read = inputStream.read(bArr);
                if (read == -1) {
                    break;
                }
                this.mOut.write(bArr, 0, read);
            }
            this.mOut.flush();
            if (z) {
                writeLastBoundaryIfNeeds();
            } else {
                this.mOut.write(("\r\n--" + this.mBoundary + "\r\n").getBytes());
            }
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void addPart(String str, String str2, InputStream inputStream, boolean z) throws IOException {
        addPart(str, str2, inputStream, "application/octet-stream", z);
    }

    public String getBoundary() {
        return this.mBoundary;
    }

    public long getContentLength() {
        writeLastBoundaryIfNeeds();
        return (long) this.mOut.toByteArray().length;
    }

    public String getContentType() {
        return "multipart/form-data; boundary=" + getBoundary();
    }

    public ByteArrayOutputStream getOutputStream() {
        writeLastBoundaryIfNeeds();
        return this.mOut;
    }

    public void writeFirstBoundaryIfNeeds() throws IOException {
        if (!this.mIsSetFirst) {
            this.mOut.write(("--" + this.mBoundary + "\r\n").getBytes());
        }
        this.mIsSetFirst = true;
    }

    public void writeLastBoundaryIfNeeds() {
        if (!this.mIsSetLast) {
            try {
                this.mOut.write(("\r\n--" + this.mBoundary + "--\r\n").getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.mIsSetLast = true;
        }
    }
}
