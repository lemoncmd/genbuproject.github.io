package eu.chainfire.libsuperuser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class StreamGobbler extends Thread {
    private OnLineListener listener = null;
    private BufferedReader reader = null;
    private String shell = null;
    private List<String> writer = null;

    public interface OnLineListener {
        void onLine(String str);
    }

    public StreamGobbler(String shell, InputStream inputStream, List<String> outputList) {
        this.shell = shell;
        this.reader = new BufferedReader(new InputStreamReader(inputStream));
        this.writer = outputList;
    }

    public StreamGobbler(String shell, InputStream inputStream, OnLineListener onLineListener) {
        this.shell = shell;
        this.reader = new BufferedReader(new InputStreamReader(inputStream));
        this.listener = onLineListener;
    }

    public void run() {
        while (true) {
            try {
                String line = this.reader.readLine();
                if (line != null) {
                    if (this.writer != null) {
                        this.writer.add(line);
                    }
                    if (this.listener != null) {
                        this.listener.onLine(line);
                    }
                }
            } catch (IOException e) {
            }
            try {
                break;
            } catch (IOException e2) {
                return;
            }
        }
        this.reader.close();
    }
}
