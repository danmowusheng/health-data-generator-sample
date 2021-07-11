package org.openmhealth.data.generator.transfer;

import org.openmhealth.data.generator.domain.TimestampedValueGroup;
import org.openmhealth.data.generator.dto.CaloriesDTO;
import org.springframework.stereotype.Component;



/**
 * @program: test-gradle
 * @author: LJ
 * @create: 2021-07-09 15:50
 * @description：
 **/
@Component
public class CaloriesDTOTransfer extends AbstractTransfer<CaloriesDTO> {
    private static final String CALORIE_KEY = "calorie-burned";

    @Override
    public String getName(){
        return "calorie-count";
    }

    @Override
    public CaloriesDTO newMeasureDTO(TimestampedValueGroup timestampedValueGroup) {
        return new CaloriesDTO.Builder(timestampedValueGroup.getValue(CALORIE_KEY))
                    .setTimestamp(timestampedValueGroup.getTimestamp())
                    .build();
    }
}
