package org.openmhealth.data.generator.transfer;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.openmhealth.data.generator.domain.TimestampedValueGroup;
import org.openmhealth.data.generator.dto.SleepDurationDTO;
import org.springframework.stereotype.Component;

import static org.openmhealth.data.generator.service.SleepDurationDataPointGenerator.DURATION_KEY;

/**
 * @program: test-gradle
 * @author: LJ
 * @create: 2021-07-09 16:01
 * @description：
 **/
@Component
public class SleepFragmentDTOTransfer extends AbstractTransfer<SleepDurationDTO> {
    private static final Integer SLEEP_TYPE = 1;
    @Override
    public String getName(){
        return "sleep-duration";
    }

    @Override
    public SleepDurationDTO newMeasureDTO(TimestampedValueGroup timestampedValueGroup) {
        return new SleepDurationDTO.Builder(timestampedValueGroup.getValue(DURATION_KEY))
                    .setTimestamp(timestampedValueGroup.getTimestamp())
                    .setSleepType(SLEEP_TYPE)
                    .build();
    }

    @Override
    public SleepDurationDTO newMeasureDTOMapper(String jsonString) throws JsonProcessingException {
        return objectMapper.readValue(jsonString, SleepDurationDTO.class);
    }
}
