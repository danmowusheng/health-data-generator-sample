package org.openmhealth.data.generator.transfer;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.openmhealth.data.generator.constant.BodyWeight;
import org.openmhealth.data.generator.domain.TimestampedValueGroup;
import org.openmhealth.data.generator.dto.BodyWeightDTO;
import org.springframework.stereotype.Component;


/**
 * @program: test-gradle
 * @author: LJ
 * @create: 2021-07-09 10:22
 * @description：
 **/
@Component
public class BodyWeightDTOTransfer extends AbstractTransfer<BodyWeightDTO> {

    public static final String WEIGHT_KEY = "weight-in-kg";
    public static final BodyWeight bodyWeight = BodyWeight.BODY_WEIGHT;

    @Override
    public String getName() {
        return "body-weight";
    }

    @Override
    public BodyWeightDTO newMeasureDTO(TimestampedValueGroup timestampedValueGroup) {
        return new BodyWeightDTO.Builder(timestampedValueGroup.getValue(WEIGHT_KEY))
                .setTimestamp(timestampedValueGroup.getTimestamp())
                .setWeightType(1)
                .build();
    }

    public BodyWeightDTO newMeasureDTOMapper(String jsonString) throws JsonProcessingException {

        return objectMapper.readValue(jsonString, BodyWeightDTO.class);
    }


}
