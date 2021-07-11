package org.openmhealth.data.generator.transfer;

import org.openmhealth.data.generator.domain.TimestampedValueGroup;
import org.openmhealth.data.generator.dto.EcgDetailDTO;
import org.springframework.stereotype.Component;



import static org.openmhealth.data.generator.service.ECGDetailDTODataPointGenerator.VOLTAGE_KEY;

/**
 * @program: test-gradle
 * @author: LJ
 * @create: 2021-07-11 10:30
 * @description：
 **/
@Component
public class ECGDetailDTOTransfer extends AbstractTransfer<EcgDetailDTO>{
    public static final Integer ECG_TYPE = 1;

    @Override
    public String getName(){
        return "ECG-detail";
    }

    @Override
    public EcgDetailDTO newMeasureDTO(TimestampedValueGroup timestampedValueGroup) {
        return new EcgDetailDTO.Builder(timestampedValueGroup.getValue(VOLTAGE_KEY))
                    .setECGType(ECG_TYPE)
                    .setTimestamp(timestampedValueGroup.getTimestamp())
                    .build();
    }
}
