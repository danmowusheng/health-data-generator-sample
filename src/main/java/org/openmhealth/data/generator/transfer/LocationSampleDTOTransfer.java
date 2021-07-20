package org.openmhealth.data.generator.transfer;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.openmhealth.data.generator.domain.TimestampedValueGroup;
import org.openmhealth.data.generator.dto.LocationSampleDTO;
import org.springframework.stereotype.Component;

import static org.openmhealth.data.generator.service.LocationDataPointGenerator.LATITUDE_KEY;

/**
 * @program: test-gradle
 * @author: LJ
 * @create: 2021-07-09 15:56
 * @description：
 **/
@Component
public class LocationSampleDTOTransfer extends AbstractTransfer<LocationSampleDTO> {
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";

    @Override
    public String getName(){
        return "geo-detail";
    }

    @Override
    public LocationSampleDTO newMeasureDTO(TimestampedValueGroup timestampedValueGroup) {
        return new LocationSampleDTO.Builder(timestampedValueGroup.getValue(LATITUDE_KEY))
                    .setTimestamp(timestampedValueGroup.getTimestamp())
                    .setGpsType(1)
                    .build();
    }

    @Override
    public LocationSampleDTO newMeasureDTOMapper(String jsonString) throws JsonProcessingException {
        return objectMapper.readValue(jsonString,LocationSampleDTO.class);
    }
}
