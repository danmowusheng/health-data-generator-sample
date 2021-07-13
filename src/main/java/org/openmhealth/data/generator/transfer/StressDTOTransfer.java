package org.openmhealth.data.generator.transfer;

import org.openmhealth.data.generator.constant.StressGrade;
import org.openmhealth.data.generator.constant.StressMeasureType;
import org.openmhealth.data.generator.domain.TimestampedValueGroup;
import org.openmhealth.data.generator.dto.StressDTO;
import org.springframework.stereotype.Component;

import java.time.temporal.ChronoField;

import static org.openmhealth.data.generator.service.StressDataPointGenerator.STRESS_GRADE_KEY;
import static org.openmhealth.data.generator.service.StressDataPointGenerator.STRESS_KEY;

/**
 * @program: test-gradle
 * @author: LJ
 * @create: 2021-07-09 16:15
 * @description：
 **/
@Component
public class StressDTOTransfer extends AbstractTransfer<StressDTO> {
    //StressMeasureType.PASSIVE.ordinal()
    @Override
    public String getName(){
        return "stress-detail";
    }

    @Override
    public StressDTO newMeasureDTO(TimestampedValueGroup timestampedValueGroup) {
        return new StressDTO.Builder(timestampedValueGroup.getValue(STRESS_KEY).intValue()).setTimestamp(timestampedValueGroup.getTimestamp())
                    .setGrade(StressGrade.NORMAL.ordinal())
                    .setMeasureType(1)
                    .build();
    }
}
