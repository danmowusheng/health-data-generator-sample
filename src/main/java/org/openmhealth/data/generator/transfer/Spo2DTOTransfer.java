package org.openmhealth.data.generator.transfer;

import org.openmhealth.data.generator.domain.TimestampedValueGroup;
import org.openmhealth.data.generator.dto.Spo2DTO;
import org.springframework.stereotype.Component;

import java.time.temporal.ChronoField;

/**
 * @program: test-gradle
 * @author: LJ
 * @create: 2021-07-09 16:05
 * @description：
 **/
@Component
public class Spo2DTOTransfer extends AbstractTransfer<Spo2DTO> {
    private static final String  SPO2 = "percentage";

    @Override
    public String getName(){
        return "spo2-percentage";
    }

    public enum SupplementalOxygenAdministrationMode{
        NASAL_CANNULA;
    }

    public enum MeasurementSystem{
        PERIPHERAL_CAPILLARY;
    }

    public enum MeasurementMethod{
        PULSE_OXIMETRY;
    }

    @Override
    public Spo2DTO newMeasureDTO(TimestampedValueGroup timestampedValueGroup) {
        return new Spo2DTO.Builder().setTimestamp(timestampedValueGroup.getTimestamp().getLong(ChronoField.INSTANT_SECONDS))
                        .setmField(MeasurementSystem.PERIPHERAL_CAPILLARY.name())
                        .setOxygenTherapy(false)
                        .setSpo2(timestampedValueGroup.getValue(SPO2))
                        .setSpo2Measurement(MeasurementMethod.PULSE_OXIMETRY.name())
                        .build();
    }
}
