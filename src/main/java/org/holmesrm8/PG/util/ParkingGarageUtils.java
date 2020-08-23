package org.holmesrm8.PG.util;

import org.apache.commons.io.ByteOrderMark;
import org.apache.commons.io.input.BOMInputStream;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.io.Reader;

public class ParkingGarageUtils {

    public static Reader getReader(MultipartFile file) throws Exception {
        BOMInputStream inputStream = new BOMInputStream(file.getInputStream(), ByteOrderMark.UTF_8,
                ByteOrderMark.UTF_16BE, ByteOrderMark.UTF_16LE);

        return new InputStreamReader(inputStream);
    }
}
