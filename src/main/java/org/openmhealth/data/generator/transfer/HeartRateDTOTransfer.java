package org.openmhealth.data.generator.transfer;

import org.openmhealth.data.generator.constant.HeartRate;
import org.openmhealth.data.generator.domain.TimestampedValueGroup;
import org.openmhealth.data.generator.dto.HeartRateDTO;
import org.springframework.stereotype.Component;

import java.time.temporal.ChronoField;

/**
 * @program: test-gradle
 * @author: LJ
 * @create: 2021-07-09 10:03
 * @description：
 **/
@Component
public class HeartRateDTOTransfer extends AbstractTransfer<HeartRateDTO> {

    public static final String RATE_KEY = "rate-in-beats-per-minute";
    public static final HeartRate type = HeartRate.RESTING;

    @Override
    public String getName() {
        return "heart-rate";
    }

    @Override
    public HeartRateDTO newMeasureDTO(TimestampedValueGroup valueGroup) {
        return new HeartRateDTO.Builder((valueGroup.getValue(RATE_KEY)).intValue())
                .setTimestamp(valueGroup.getTimestamp())
                .setmField(1)
                .build();
    }
}
