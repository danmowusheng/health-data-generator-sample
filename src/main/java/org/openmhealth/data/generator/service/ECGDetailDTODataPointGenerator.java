package org.openmhealth.data.generator.service;


import org.openmhealth.data.generator.additionalMeasure.ECGDetail;
import org.openmhealth.data.generator.domain.TimestampedValueGroup;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;

/**
 * @program: test-gradle
 * @author: LJ
 * @create: 2021-07-11 10:21
 * @description：
 **/
public class ECGDetailDTODataPointGenerator extends AbstractDataPointGeneratorImpl<ECGDetail>{
    public static final String  VOLTAGE_KEY = "voltage-value";
    @Override
    public ECGDetail newMeasure(TimestampedValueGroup valueGroup) {
        return new ECGDetail.Builder(valueGroup.getValue(VOLTAGE_KEY))
                    .setEffectiveTimeFrame(valueGroup.getTimestamp())
                    .build();
    }

    @Override
    public String getName() {
        return "ECG-detail";
    }

    @Override
    public Set<String> getRequiredValueGroupKeys() {
        return Collections.singleton(VOLTAGE_KEY);
    }

    @Override
    public Set<String> getSupportedValueGroupKeys() {
        return Collections.singleton(VOLTAGE_KEY);
    }
}
