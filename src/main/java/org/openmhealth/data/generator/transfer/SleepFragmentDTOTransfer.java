package org.openmhealth.data.generator.transfer;

import org.openmhealth.data.generator.domain.TimestampedValueGroup;
import org.openmhealth.data.generator.dto.SleepFragmentDTO;
import org.springframework.stereotype.Component;

import java.time.temporal.ChronoField;

/**
 * @program: test-gradle
 * @author: LJ
 * @create: 2021-07-09 16:01
 * @description：
 **/
@Component
public class SleepFragmentDTOTransfer extends AbstractTransfer<SleepFragmentDTO> {
    private static final String  SLEEP_FRAGMENT = "sleep-hours";
    private static final String  M_FIELD = "sleep-status";

    @Override
    public String getName(){
        return "sleep-duration";
    }

    @Override
    public SleepFragmentDTO newMeasureDTO(TimestampedValueGroup timestampedValueGroup) {
        return new SleepFragmentDTO.Builder().setTimestamp(timestampedValueGroup.getTimestamp().getLong(ChronoField.INSTANT_SECONDS))
                    .setmField(timestampedValueGroup.getValue(M_FIELD).intValue())
                    .setSleepFragment(timestampedValueGroup.getValue(SLEEP_FRAGMENT))
                    .build();
    }
}
