package org.openmhealth.data.generator.transfer;

import org.openmhealth.data.generator.constant.BodyWeight;
import org.openmhealth.data.generator.domain.TimestampedValueGroup;
import org.openmhealth.data.generator.dto.BodyWeightDTO;
import org.springframework.stereotype.Component;

import java.time.temporal.ChronoField;

/**
 * @program: test-gradle
 * @author: LJ
 * @create: 2021-07-09 10:22
 * @description：
 **/
@Component
public class BodyWeightDTOtransfer extends AbstractTransfer<BodyWeightDTO> {

    public static final String WEIGHT_KEY = "weight-in-kg";
    public static final BodyWeight bodyWeight = BodyWeight.BODY_WEIGHT;

    @Override
    public String getName() {
        return "body-weight";
    }

    @Override
    public BodyWeightDTO newMeasureDTO(TimestampedValueGroup timestampedValueGroup) {
        return new BodyWeightDTO.Builder().setBodyWeight(timestampedValueGroup.getValue(WEIGHT_KEY))
                .setTimestamp(timestampedValueGroup.getTimestamp().getLong(ChronoField.INSTANT_SECONDS))
                .setmField(bodyWeight)
                .setBodyWeight(timestampedValueGroup.getValue(WEIGHT_KEY))
                .build();
    }


}
