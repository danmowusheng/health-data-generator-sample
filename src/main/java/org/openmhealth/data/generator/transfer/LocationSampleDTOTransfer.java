package org.openmhealth.data.generator.transfer;

import org.openmhealth.data.generator.domain.TimestampedValueGroup;
import org.openmhealth.data.generator.dto.LocationSampleDTO;
import org.springframework.stereotype.Component;

import java.time.temporal.ChronoField;

import static org.openmhealth.data.generator.service.LocationDataPointGenerator.LATITUDE_KEY;

/**
 * @program: test-gradle
 * @author: LJ
 * @create: 2021-07-09 15:56
 * @description：
 **/
@Component
public class LocationSampleDTOTransfer extends AbstractTransfer<LocationSampleDTO> {
    private static final String M_FIELD = "latitude";

    @Override
    public String getName(){
        return "geo-position";
    }

    @Override
    public LocationSampleDTO newMeasureDTO(TimestampedValueGroup timestampedValueGroup) {
        return new LocationSampleDTO.Builder(timestampedValueGroup.getValue(LATITUDE_KEY))
                    .setTimestamp(timestampedValueGroup.getTimestamp())
                    .setmField(2)
                    .build();
    }
}
