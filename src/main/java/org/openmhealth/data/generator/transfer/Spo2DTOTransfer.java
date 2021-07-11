package org.openmhealth.data.generator.transfer;

import org.openmhealth.data.generator.domain.TimestampedValueGroup;
import org.openmhealth.data.generator.dto.Spo2DTO;
import org.springframework.stereotype.Component;

import java.time.temporal.ChronoField;

import static org.openmhealth.data.generator.service.OxygenSaturationDataPointGenerator.SATURATION_KEY;

/**
 * @program: test-gradle
 * @author: LJ
 * @create: 2021-07-09 16:05
 * @description：
 **/
@Component
public class Spo2DTOTransfer extends AbstractTransfer<Spo2DTO> {

    @Override
    public String getName(){
        return "oxygen-saturation";
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
        return new Spo2DTO.Builder(timestampedValueGroup.getValue(SATURATION_KEY))
                        .setTimestamp(timestampedValueGroup.getTimestamp())
                        .setmField(MeasurementSystem.PERIPHERAL_CAPILLARY.name())
                        .setOxygenTherapy(false)
                        .setSpo2Measurement(MeasurementMethod.PULSE_OXIMETRY.name())
                        .build();
    }
}
