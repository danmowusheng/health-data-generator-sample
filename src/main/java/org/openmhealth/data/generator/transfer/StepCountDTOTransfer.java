package org.openmhealth.data.generator.transfer;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.openmhealth.data.generator.domain.TimestampedValueGroup;
import org.openmhealth.data.generator.dto.StepsDTO;
import org.springframework.stereotype.Component;

import java.time.temporal.ChronoField;

/**
 * @program: test-gradle
 * @author: LJ
 * @create: 2021-07-09 10:38
 * @description：
 **/
@Component
public class StepCountDTOTransfer extends AbstractTransfer<StepsDTO> {
    public static final String STEPS_PER_MINUTE_KEY = "steps-per-minute";

    @Override
    public String getName() {
        return "step-count";
    }

    @Override
    public StepsDTO newMeasureDTO(TimestampedValueGroup timestampedValueGroup) {
        return new StepsDTO.Builder(timestampedValueGroup.getValue(STEPS_PER_MINUTE_KEY).intValue())
                    .setTimestamp(timestampedValueGroup.getTimestamp())
                    .build();
    }

    @Override
    public StepsDTO newMeasureDTOMapper(String jsonString) throws JsonProcessingException {
        return objectMapper.readValue(jsonString,StepsDTO.class);
    }
}
