package org.openmhealth.data.generator.transfer;

import org.openmhealth.data.generator.domain.TimestampedValueGroup;
import org.openmhealth.data.generator.dto.DistanceDTO;
import org.springframework.stereotype.Component;

import java.time.temporal.ChronoField;

/**
 * @program: test-gradle
 * @author: LJ
 * @create: 2021-07-09 15:53
 * @description：
 **/
@Component
public class DistanceDTOTransfer extends AbstractTransfer<DistanceDTO> {
    private static final String  DISTANCE = "miles-per-minute";

    @Override
    public String getName(){
        return "distance";
    }

    @Override
    public DistanceDTO newMeasureDTO(TimestampedValueGroup timestampedValueGroup) {
        return new DistanceDTO.Builder().setTimestamp(timestampedValueGroup.getTimestamp().getLong(ChronoField.INSTANT_SECONDS))
                    .setDistanceCount(timestampedValueGroup.getValue(DISTANCE))
                    .build();
    }
}
