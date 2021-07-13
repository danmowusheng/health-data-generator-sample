package org.openmhealth.data.generator.transfer;

import org.openmhealth.data.generator.constant.SleepFragment;
import org.openmhealth.data.generator.domain.TimestampedValueGroup;
import org.openmhealth.data.generator.dto.SleepFragmentDTO;
import org.springframework.stereotype.Component;

import java.time.temporal.ChronoField;

import static org.openmhealth.data.generator.service.SleepDurationDataPointGenerator.DURATION_KEY;

/**
 * @program: test-gradle
 * @author: LJ
 * @create: 2021-07-09 16:01
 * @description：
 **/
@Component
public class SleepFragmentDTOTransfer extends AbstractTransfer<SleepFragmentDTO> {
    private static final SleepFragment M_FIELD = SleepFragment.DREAM_SLEEP;
    private static final Integer SLEEP_FRAGMENT = 1;
    @Override
    public String getName(){
        return "sleep-duration";
    }

    @Override
    public SleepFragmentDTO newMeasureDTO(TimestampedValueGroup timestampedValueGroup) {
        return new SleepFragmentDTO.Builder(timestampedValueGroup.getValue(DURATION_KEY))
                    .setTimestamp(timestampedValueGroup.getTimestamp())
                    .setmField(1)
                    .setSleepFragment(SLEEP_FRAGMENT)
                    .build();
    }
}
