package org.openmhealth.data.generator.service;


import org.openmhealth.data.generator.domain.TimestampedValueGroup;
import org.openmhealth.schema.domain.omh.HeartRate;
import org.openmhealth.schema.domain.omh.Measure;

import java.util.Collections;
import java.util.Set;

/**
 * @program: test-gradle
 * @author: LJ
 * @create: 2021-07-11 10:21
 * @description：
 **/
public class ECGDetailDTODataPointGenerator extends AbstractDataPointGeneratorImpl<HeartRate>{
    public static final String  VOLTAGE_KEY = "voltage-value";
    @Override
    public HeartRate newMeasure(TimestampedValueGroup valueGroup) {
        return null;
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
