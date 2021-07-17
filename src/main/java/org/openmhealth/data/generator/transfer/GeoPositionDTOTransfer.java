package org.openmhealth.data.generator.transfer;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.openmhealth.data.generator.domain.TimestampedValueGroup;
import org.openmhealth.data.generator.dto.GeoPositionDTO;
import org.springframework.stereotype.Component;

/**
 * @program: test-gradle
 * @author: LJ
 * @create: 2021-07-16 14:31
 * @description：
 **/
public class GeoPositionDTOTransfer extends AbstractTransfer<GeoPositionDTO> {
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";

    @Override
    public String getName(){
        return "geo-position";
    }

    @Override
    public GeoPositionDTO newMeasureDTO(TimestampedValueGroup timestampedValueGroup) {
        return new GeoPositionDTO.Builder(timestampedValueGroup.getValue(LATITUDE), timestampedValueGroup.getValue(LONGITUDE))
                                .setPrecision(5)
                                .build();
    }

    @Override
    public GeoPositionDTO newMeasureDTOMapper(String jsonString) throws JsonProcessingException {
        return null;
    }
}
