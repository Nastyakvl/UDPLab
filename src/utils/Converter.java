package utils;

import java.nio.ByteBuffer;

/**
 * Created by 14Kavalerova on 05.05.2017.
 */
public class Converter {


    static public byte[] intToByte(int data) {

        return ByteBuffer.allocate(4).putInt(data).array();
    }

    static public int byteToInt(byte[] data, int offset, int length) {

        return ByteBuffer.wrap(data, offset, length).getInt();

    }

    static public byte[] longToByte(long data) {

        return ByteBuffer.allocate(8).putLong(data).array();
    }

    static public long byteToLong(byte[] data, int offset, int length) {

        return ByteBuffer.wrap(data, offset, length).getLong();

    }

    static public long byteToLong(byte[] data) {

        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.put(data);
        buffer.flip();//need flip
        return buffer.getLong();


    }
}
