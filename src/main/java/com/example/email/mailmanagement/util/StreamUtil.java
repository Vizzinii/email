package com.example.email.mailmanagement.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class StreamUtil {

    public static byte[] inputStreamToByteArray(InputStream inputStream) {
        try (ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
            int nRead;
            byte[] data = new byte[1024];
            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();
            return buffer.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert InputStream to byte array", e);
        }
    }

    public static InputStream byteArrayToInputStream(byte[] byteArray) {
        return new ByteArrayInputStream(byteArray);
    }
}