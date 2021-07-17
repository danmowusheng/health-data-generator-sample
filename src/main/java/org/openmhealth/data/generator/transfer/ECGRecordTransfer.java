package org.openmhealth.data.generator.transfer;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.openmhealth.data.generator.domain.TimestampedValueGroup;

import org.openmhealth.data.generator.dto.EcgRecordDTO;
import org.springframework.stereotype.Component;

import java.time.temporal.ChronoField;

import static org.openmhealth.data.generator.service.ECGRecordDataPointGenerator.ECG_KEY;
import static org.openmhealth.data.generator.service.ECGRecordDataPointGenerator.FREQUENCY_KEY;

/**
 * @program: test-gradle
 * @author: LJ
 * @create: 2021-07-11 10:46
 * @description：
 **/
@Component
public class ECGRecordTransfer extends AbstractTransfer<EcgRecordDTO> {

    public static final Integer ECG_ARRHYTHMIA_TYPE = 1;
    public static final Integer ECG_TYPE = 1;
    public static final Integer USER_SYMPTOM = 1;
    public static final String AVG_HEART_RATE_KEY = "heart-rate-avg";

    @Override
    public String getName(){
        return "ECG-record";
    }

    @Override
    public EcgRecordDTO newMeasureDTO(TimestampedValueGroup timestampedValueGroup) {
        return new EcgRecordDTO.Builder(timestampedValueGroup.getValue(AVG_HEART_RATE_KEY).intValue())
                    .setSamplingFrequency(FREQUENCY_KEY)
                    .setEcgArrhythmiaType(ECG_ARRHYTHMIA_TYPE)
                    .setEcgLead(ECG_TYPE)
                    .setUserSymptom(USER_SYMPTOM)
                    .setIdentifier(timestampedValueGroup.getTimestamp().getLong(ChronoField.INSTANT_SECONDS))    //一个identifier对应一个Record
                    .setTimestamp(timestampedValueGroup.getTimestamp())
                    .build();
    }

    @Override
    public EcgRecordDTO newMeasureDTOMapper(String jsonString) throws JsonProcessingException {
        return objectMapper.readValue(jsonString,EcgRecordDTO.class);
    }
}
