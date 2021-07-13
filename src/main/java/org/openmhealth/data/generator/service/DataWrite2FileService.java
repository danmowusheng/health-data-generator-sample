package org.openmhealth.data.generator.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.openmhealth.data.generator.dto.MeasureDTO;
import org.openmhealth.schema.domain.omh.DataPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @program: test-gradle
 * @author: LJ
 * @create: 2021-07-11 22:17
 * @description：
 **/
@Service
public class DataWrite2FileService <T extends MeasureDTO> {

    private String filename;

    @Value("${output.file.append:true}")
    private Boolean append;

    @Autowired
    private ObjectMapper objectMapper;

    @PostConstruct
    public void clearFile() throws IOException {

        if (!append) {
            Files.deleteIfExists(Paths.get(filename));
        }
    }

    public long writeDatas(Iterable<? extends MeasureDTO> measureDTOS) throws IOException {

        long written = 0;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, append))) {

            for (MeasureDTO measureDTO:measureDTOS) {
                // this simplifies direct imports into MongoDB

                String valueAsString = objectMapper.writeValueAsString(measureDTO);
                writer.write(valueAsString);
                writer.write("\n");
                written++;
            }
        }

        return written;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }

    public void setAppend(Boolean append) {
        this.append = append;
    }
}
