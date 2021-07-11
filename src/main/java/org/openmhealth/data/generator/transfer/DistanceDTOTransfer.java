package org.openmhealth.data.generator.transfer;

import org.openmhealth.data.generator.domain.TimestampedValueGroup;
import org.openmhealth.data.generator.dto.DistanceDTO;
import org.springframework.stereotype.Component;

import java.time.temporal.ChronoField;

import static org.openmhealth.data.generator.service.DistanceDataPointGenerator.DISTANCE_PER_MINUTE_KEY;

/**
 * @program: test-gradle
 * @author: LJ
 * @create: 2021-07-09 15:53
 * @description：
 **/
@Component
public class DistanceDTOTransfer extends AbstractTransfer<DistanceDTO> {

    @Override
    public String getName(){
        return "distance";
    }

    @Override
    public DistanceDTO newMeasureDTO(TimestampedValueGroup timestampedValueGroup) {
        return new DistanceDTO.Builder(timestampedValueGroup.getValue(DISTANCE_PER_MINUTE_KEY))
                    .setTimestamp(timestampedValueGroup.getTimestamp())
                    .build();
    }
}
