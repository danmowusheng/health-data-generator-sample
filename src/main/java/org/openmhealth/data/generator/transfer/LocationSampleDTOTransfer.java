package org.openmhealth.data.generator.transfer;

import org.openmhealth.data.generator.domain.TimestampedValueGroup;
import org.openmhealth.data.generator.dto.LocationSampleDTO;
import org.springframework.stereotype.Component;

import java.time.temporal.ChronoField;

/**
 * @program: test-gradle
 * @author: LJ
 * @create: 2021-07-09 15:56
 * @description：
 **/
@Component
public class LocationSampleDTOTransfer extends AbstractTransfer<LocationSampleDTO> {
    private static final String LOCATION = "a-location";
    private static final String M_FIELD = "latitude";

    @Override
    public String getName(){
        return "geo-position";
    }

    @Override
    public LocationSampleDTO newMeasureDTO(TimestampedValueGroup timestampedValueGroup) {
        return new LocationSampleDTO.Builder().setTimestamp(timestampedValueGroup.getTimestamp().getLong(ChronoField.INSTANT_SECONDS))
                    .setLocationSample(timestampedValueGroup.getValue(LOCATION))
                    .setmField((timestampedValueGroup.getValue(M_FIELD)).intValue())
                    .build();
    }
}
