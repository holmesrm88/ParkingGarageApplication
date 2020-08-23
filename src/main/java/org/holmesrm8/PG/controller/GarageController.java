package org.holmesrm8.PG.controller;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.extern.java.Log;
import org.holmesrm8.PG.model.CsvUpload;
import org.holmesrm8.PG.service.GarageService;
import org.holmesrm8.PG.util.ParkingGarageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;

import java.io.Reader;
import java.util.List;


@ComponentScan
@RestController
@Log
public class GarageController {
    @Autowired
    GarageService garageService;


    @PostMapping("/upload-csv-file")
    public String uploadCSVFile(@RequestParam("file") MultipartFile file, Model model) {

        // validate file
        if (file.isEmpty()) {
            model.addAttribute("message", "Please select a CSV file to upload.");
            model.addAttribute("status", false);
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "CSV File is empty. Please provide file with data");
        } else {

            // parse CSV file to create a list of `Vehicle` objects
            try  {
                Reader reader = ParkingGarageUtils.getReader(file);
                // create csv bean reader
                CsvToBean<CsvUpload> csvToBean = new CsvToBeanBuilder(reader)
                        .withType(CsvUpload.class)
                        .withIgnoreLeadingWhiteSpace(true)
                        .build();

                // convert `CsvToBean` object to list of csvUploads
                List<CsvUpload> csvUploads = csvToBean.parse();

                garageService.saveAllUploads(csvUploads);

                // save csvUploads list on model
                model.addAttribute("csvToBeans", csvToBean);
                model.addAttribute("status", true);

            } catch(RuntimeException ex){
                model.addAttribute("message", "An error occurred while processing the CSV file.");
                model.addAttribute("status", false);
                log.severe("Runtime Exception occurred: "+ex);
                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, ex.getCause().toString());
            }
            catch (Exception ex) {
                model.addAttribute("message", "An error occurred while processing the CSV file.");
                model.addAttribute("status", false);
                throw new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getCause().toString());
            }
        }

        return "File upload complete. Results printed in directory under ParkingGarageMonthlyOutput.txt";
    }
}
