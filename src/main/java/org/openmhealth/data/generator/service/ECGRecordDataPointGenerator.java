package org.openmhealth.data.generator.service;

import com.google.common.collect.Sets;
import org.openmhealth.data.generator.domain.TimestampedValueGroup;
import org.openmhealth.schema.domain.omh.HeartRate;
import org.openmhealth.schema.domain.omh.Measure;

import java.util.Set;

/**
 * @program: test-gradle
 * @author: LJ
 * @create: 2021-07-11 10:25
 * @description：
 **/
public class ECGRecordDataPointGenerator extends AbstractDataPointGeneratorImpl<HeartRate>{

    public static final String  ECG_KEY = "ecg-value";
    public static final Integer  FREQUENCY_KEY = 30;

    @Override
    public HeartRate newMeasure(TimestampedValueGroup valueGroup) {
        return null;
    }

    @Override
    public String getName() {
        return "ECG-record";
    }

    @Override
    public Set<String> getRequiredValueGroupKeys() {
        return Sets.newHashSet(ECG_KEY);
    }

    @Override
    public Set<String> getSupportedValueGroupKeys() {
        return Sets.newHashSet(ECG_KEY);
    }
}
