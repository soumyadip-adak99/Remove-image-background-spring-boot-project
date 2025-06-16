package org.removeBG.utility;

import org.springframework.core.io.InputStreamResource;

import java.io.InputStream;

public class MultipartInputStreamFileResource extends InputStreamResource {

    private final String FILE_NAME;

    public MultipartInputStreamFileResource(InputStream inputStream, String fileName) {
        super(inputStream);
        this.FILE_NAME = fileName;
    }

    @Override
    public String getFilename() {
        return this.FILE_NAME;
    }

    @Override
    public long contentLength() {
        return -1;
    }
}
