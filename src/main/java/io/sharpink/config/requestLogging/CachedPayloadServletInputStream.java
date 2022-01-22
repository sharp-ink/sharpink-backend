package io.sharpink.config.requestLogging;

import lombok.SneakyThrows;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class CachedPayloadServletInputStream extends ServletInputStream {

    private final InputStream cachedPayloadInputStream;

    public CachedPayloadServletInputStream(byte[] cachedPayload) {
        this.cachedPayloadInputStream = new ByteArrayInputStream(cachedPayload);
    }

    @Override
    public int read() throws IOException {
        return cachedPayloadInputStream.read();
    }

    @Override
    public boolean isFinished() {
        try {
            return cachedPayloadInputStream.available() == 0;
        } catch (IOException e) {
            e.printStackTrace();
            return true;
        }
    }

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public void setReadListener(ReadListener listener) {

    }
}
