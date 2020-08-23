package org.holmesrm8.PG.service;

import org.holmesrm8.PG.model.CsvUpload;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;

public interface GarageService {
    BigDecimal saveAllUploads(List<CsvUpload> csvUploads) throws ParseException, IOException;
}
