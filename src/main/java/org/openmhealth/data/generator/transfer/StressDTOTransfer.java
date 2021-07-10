package org.openmhealth.data.generator.transfer;

import org.openmhealth.data.generator.constant.StressMeasureType;
import org.openmhealth.data.generator.domain.TimestampedValueGroup;
import org.openmhealth.data.generator.dto.StressDTO;
import org.springframework.stereotype.Component;

import java.time.temporal.ChronoField;

/**
 * @program: test-gradle
 * @author: LJ
 * @create: 2021-07-09 16:15
 * @description：
 **/
@Component
public class StressDTOTransfer extends AbstractTransfer<StressDTO> {
    private static final String STRESS = "stress";
    private static final String GRADE = "grade";

    @Override
    public String getName(){
        return "stress-detail";
    }

    @Override
    public StressDTO newMeasureDTO(TimestampedValueGroup timestampedValueGroup) {
        return new StressDTO.Builder().setTimestamp(timestampedValueGroup.getTimestamp().getLong(ChronoField.INSTANT_SECONDS))
                    .setGrade(timestampedValueGroup.getValue(GRADE).intValue())
                    .setMeasureType(1)
                    .setStress(timestampedValueGroup.getValue(STRESS).intValue())
                    .build();
    }
}
