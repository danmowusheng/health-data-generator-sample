package org.openmhealth.data.generator.service;


import org.openmhealth.data.generator.additionalMeasure.ECGRecord;
import org.openmhealth.data.generator.domain.TimestampedValueGroup;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;

import static java.util.Collections.singleton;
import static org.openmhealth.data.generator.transfer.ECGDetailDTOTransfer.ECG_TYPE;
import static org.openmhealth.data.generator.transfer.ECGRecordTransfer.ECG_ARRHYTHMIA_TYPE;
import static org.openmhealth.data.generator.transfer.ECGRecordTransfer.USER_SYMPTOM;

/**
 * @program: test-gradle
 * @author: LJ
 * @create: 2021-07-11 10:25
 * @description：
 **/
@Component
public class ECGRecordDataPointGenerator extends AbstractDataPointGeneratorImpl<ECGRecord>{

    public static final String  ECG_KEY = "ecg-value";
    public static final Integer  FREQUENCY_KEY = 30;

    @Override
    public ECGRecord newMeasure(TimestampedValueGroup valueGroup) {
        return new ECGRecord.Builder(valueGroup.getValue(ECG_KEY), FREQUENCY_KEY)
                    .setEffectiveTimeFrame(valueGroup.getTimestamp())
                    .setEcgArrhythmiaType(ECG_ARRHYTHMIA_TYPE)
                    .setEcgType(ECG_TYPE)
                    .setUserSymptom(USER_SYMPTOM)
                    .build();
    }

    @Override
    public String getName() {
        return "ECG-record";
    }

    @Override
    public Set<String> getRequiredValueGroupKeys() {
        return singleton(ECG_KEY);
    }

    @Override
    public Set<String> getSupportedValueGroupKeys() {
        return singleton(ECG_KEY);
    }
}
