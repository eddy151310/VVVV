package com.ahdi.lib.utils.security;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class BytesUtils {
    public static void writeWithLength(byte[] bs, OutputStream out)
            throws IOException {
        if (bs == null) {
            writeInt(-1, out);
        } else {
            writeInt(bs.length, out);
            if (bs.length != 0) {
                out.write(bs);
            }
        }
    }

    public static byte[] readWithLength(InputStream in) throws IOException {
        int len = readInt(in);
        if (len == -1) {
            return null;
        } else {
            byte[] b = new byte[len];
            if (len == 0) {
                return b;
            }
            if (len != in.read(b, 0, len)) {
                throw new EOFException();
            }
            return b;
        }
    }

    public static int intBytesLength() {
        return 4;
    }

    public static byte[] toBytes(int v) {
        byte[] bs = new byte[4];
        bs[0] = (byte) ((v >>> 24) & 0xFF);
        bs[1] = (byte) ((v >>> 16) & 0xFF);
        bs[2] = (byte) ((v >>> 8) & 0xFF);
        bs[3] = (byte) ((v >>> 0) & 0xFF);
        return bs;
    }

    public static void writeInt(int v, OutputStream out) throws IOException {
        out.write((v >>> 24) & 0xFF);
        out.write((v >>> 16) & 0xFF);
        out.write((v >>> 8) & 0xFF);
        out.write((v >>> 0) & 0xFF);
    }

    public static int readInt(InputStream in) throws IOException {
        int ch1 = in.read();
        int ch2 = in.read();
        int ch3 = in.read();
        int ch4 = in.read();
        if ((ch1 | ch2 | ch3 | ch4) < 0)
            throw new EOFException();
        return ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0));
    }
}
