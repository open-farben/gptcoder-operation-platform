package cn.com.farben.commons.web.io;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class FbServletInputStream extends ServletInputStream {
    private ByteArrayInputStream bis;

    @Override
    public boolean isFinished() {
        return true;
    }

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public void setReadListener(ReadListener readListener) {

    }

    @Override
    public int read() throws IOException {
        return bis.read();
    }

    public FbServletInputStream(ByteArrayInputStream bis) {
        this.bis = bis;
    }
}
