package org.fmod;

import android.media.MediaCodec.BufferInfo;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.os.Build.VERSION;
import android.util.Log;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.ByteBuffer;
import net.zhuoweizhang.mcpelauncher.api.modpe.EnchantType;

public class MediaCodec implements InvocationHandler {
    private int mChannelCount = 0;
    private long mCodecPtr = 0;
    private int mCurrentOutputBufferIndex = -1;
    private Object mDataSourceProxy = null;
    private android.media.MediaCodec mDecoder = null;
    private MediaExtractor mExtractor = null;
    private ByteBuffer[] mInputBuffers = null;
    private boolean mInputFinished = false;
    private long mLength = 0;
    private ByteBuffer[] mOutputBuffers = null;
    private boolean mOutputFinished = false;
    private int mSampleRate = 0;

    private static native long fmodGetSize(long j);

    private static native int fmodReadAt(long j, long j2, byte[] bArr, int i);

    public long getLength() {
        return this.mLength;
    }

    public int getSampleRate() {
        return this.mSampleRate;
    }

    public int getChannelCount() {
        return this.mChannelCount;
    }

    public Object invoke(Object obj, Method method, Object[] objArr) {
        if (method.getName().equals("readAt")) {
            return Integer.valueOf(fmodReadAt(this.mCodecPtr, ((Long) objArr[0]).longValue(), (byte[]) objArr[1], ((Integer) objArr[2]).intValue()));
        }
        if (method.getName().equals("getSize")) {
            return Long.valueOf(fmodGetSize(this.mCodecPtr));
        }
        if (method.getName().equals("close")) {
            return null;
        }
        Log.w("fmod", "MediaCodec::invoke : Unrecognised method found: " + method.getName());
        return null;
    }

    public boolean init(long j) {
        int i = 0;
        if (VERSION.SDK_INT < 17) {
            Log.w("fmod", "MediaCodec::init : MediaCodec unavailable, ensure device is running at least 4.2 (JellyBean).\n");
            return false;
        }
        this.mCodecPtr = j;
        this.mExtractor = new MediaExtractor();
        try {
            Method method = Class.forName("android.media.MediaExtractor").getMethod("setDataSource", new Class[]{Class.forName("android.media.DataSource")});
            this.mDataSourceProxy = Proxy.newProxyInstance(r0.getClassLoader(), new Class[]{r0}, this);
            method.invoke(this.mExtractor, new Object[]{this.mDataSourceProxy});
            int trackCount = this.mExtractor.getTrackCount();
            for (int i2 = 0; i2 < trackCount; i2++) {
                MediaFormat trackFormat = this.mExtractor.getTrackFormat(i2);
                String string = trackFormat.getString("mime");
                Log.d("fmod", "MediaCodec::init : Format " + i2 + " / " + trackCount + " -- " + trackFormat);
                if (string.equals("audio/mp4a-latm")) {
                    try {
                        this.mDecoder = android.media.MediaCodec.createDecoderByType(string);
                        this.mExtractor.selectTrack(i2);
                        this.mDecoder.configure(trackFormat, null, null, 0);
                        this.mDecoder.start();
                        this.mInputBuffers = this.mDecoder.getInputBuffers();
                        this.mOutputBuffers = this.mDecoder.getOutputBuffers();
                        if (trackFormat.containsKey("encoder-delay")) {
                            i2 = trackFormat.getInteger("encoder-delay");
                        } else {
                            i2 = 0;
                        }
                        if (trackFormat.containsKey("encoder-padding")) {
                            i = trackFormat.getInteger("encoder-padding");
                        }
                        long j2 = trackFormat.getLong("durationUs");
                        this.mChannelCount = trackFormat.getInteger("channel-count");
                        this.mSampleRate = trackFormat.getInteger("sample-rate");
                        this.mLength = (long) ((((int) (((j2 * ((long) this.mSampleRate)) + (1000000 - 1)) / 1000000)) - i2) - i);
                        return true;
                    } catch (IOException e) {
                        Log.e("fmod", "MediaCodec::init : " + e.toString());
                        return false;
                    }
                }
            }
            return false;
        } catch (ClassNotFoundException e2) {
            Log.w("fmod", "MediaCodec::init : " + e2.toString());
            return false;
        } catch (NoSuchMethodException e3) {
            Log.w("fmod", "MediaCodec::init : " + e3.toString());
            return false;
        } catch (IllegalAccessException e4) {
            Log.e("fmod", "MediaCodec::init : " + e4.toString());
            return false;
        } catch (InvocationTargetException e5) {
            Log.e("fmod", "MediaCodec::init : " + e5.toString());
            return false;
        }
    }

    public void close() {
        if (this.mDecoder != null) {
            this.mDecoder.stop();
            this.mDecoder.release();
            this.mDecoder = null;
        }
        if (this.mExtractor != null) {
            this.mExtractor.release();
            this.mExtractor = null;
        }
    }

    public int read(byte[] bArr, int i) {
        int i2;
        if (this.mInputFinished && this.mOutputFinished && this.mCurrentOutputBufferIndex == -1) {
            i2 = -1;
        } else {
            i2 = 0;
        }
        while (!this.mInputFinished) {
            int dequeueInputBuffer = this.mDecoder.dequeueInputBuffer(0);
            if (dequeueInputBuffer < 0) {
                break;
            }
            int readSampleData = this.mExtractor.readSampleData(this.mInputBuffers[dequeueInputBuffer], 0);
            if (readSampleData >= 0) {
                this.mDecoder.queueInputBuffer(dequeueInputBuffer, 0, readSampleData, this.mExtractor.getSampleTime(), 0);
                this.mExtractor.advance();
            } else {
                this.mDecoder.queueInputBuffer(dequeueInputBuffer, 0, 0, 0, 4);
                this.mInputFinished = true;
            }
        }
        if (!this.mOutputFinished && this.mCurrentOutputBufferIndex == -1) {
            BufferInfo bufferInfo = new BufferInfo();
            dequeueInputBuffer = this.mDecoder.dequeueOutputBuffer(bufferInfo, 10000);
            if (dequeueInputBuffer >= 0) {
                this.mCurrentOutputBufferIndex = dequeueInputBuffer;
                this.mOutputBuffers[dequeueInputBuffer].limit(bufferInfo.size);
                this.mOutputBuffers[dequeueInputBuffer].position(bufferInfo.offset);
            } else if (dequeueInputBuffer == -3) {
                this.mOutputBuffers = this.mDecoder.getOutputBuffers();
            } else if (dequeueInputBuffer == -2) {
                Log.d("fmod", "MediaCodec::read : MediaCodec::dequeueOutputBuffer returned MediaCodec.INFO_OUTPUT_FORMAT_CHANGED " + this.mDecoder.getOutputFormat());
            } else if (dequeueInputBuffer == -1) {
                Log.d("fmod", "MediaCodec::read : MediaCodec::dequeueOutputBuffer returned MediaCodec.INFO_TRY_AGAIN_LATER.");
            } else {
                Log.w("fmod", "MediaCodec::read : MediaCodec::dequeueOutputBuffer returned " + dequeueInputBuffer);
            }
            if ((bufferInfo.flags & 4) != 0) {
                this.mOutputFinished = true;
            }
        }
        if (this.mCurrentOutputBufferIndex != -1) {
            ByteBuffer byteBuffer = this.mOutputBuffers[this.mCurrentOutputBufferIndex];
            i2 = Math.min(byteBuffer.remaining(), i);
            byteBuffer.get(bArr, 0, i2);
            if (!byteBuffer.hasRemaining()) {
                byteBuffer.clear();
                this.mDecoder.releaseOutputBuffer(this.mCurrentOutputBufferIndex, false);
                this.mCurrentOutputBufferIndex = -1;
            }
        }
        return i2;
    }

    public void seek(int i) {
        if (this.mCurrentOutputBufferIndex != -1) {
            this.mOutputBuffers[this.mCurrentOutputBufferIndex].clear();
            this.mCurrentOutputBufferIndex = -1;
        }
        this.mInputFinished = false;
        this.mOutputFinished = false;
        this.mDecoder.flush();
        this.mExtractor.seekTo((((long) i) * 1000000) / ((long) this.mSampleRate), 0);
        long sampleTime = ((this.mExtractor.getSampleTime() * ((long) this.mSampleRate)) + (1000000 - 1)) / 1000000;
        int i2 = (int) (((((long) i) - sampleTime) * ((long) this.mChannelCount)) * 2);
        if (i2 < 0) {
            Log.w("fmod", "MediaCodec::seek : Seek to " + i + " resulted in position " + sampleTime);
            return;
        }
        byte[] bArr = new byte[EnchantType.pickaxe];
        while (i2 > 0) {
            i2 -= read(bArr, Math.min(bArr.length, i2));
        }
    }
}
