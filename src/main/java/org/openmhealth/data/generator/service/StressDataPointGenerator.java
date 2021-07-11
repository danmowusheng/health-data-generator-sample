package org.openmhealth.data.generator.service;

import com.google.common.collect.Sets;
import org.openmhealth.data.generator.additionalMeasure.Stress;
import org.openmhealth.data.generator.constant.StressGrade;
import org.openmhealth.data.generator.constant.StressMeasureType;
import org.openmhealth.data.generator.domain.TimestampedValueGroup;
import org.springframework.stereotype.Component;


import java.util.Set;

import static java.util.Collections.singleton;

/**
 * @program: test-gradle
 * @author: LJ
 * @create: 2021-07-10 10:51
 * @description：
 **/
@Component
public class StressDataPointGenerator extends AbstractDataPointGeneratorImpl<Stress> {
    public static final String STRESS_KEY = "stress-value";
    public static final String STRESS_GRADE_KEY = "stress-grade";
    public static final String MEASURE_TYPE_KEY = "measure-type";
    @Override
    public Stress newMeasure(TimestampedValueGroup valueGroup) {
        return new Stress.Builder().setEffectiveTimeFrame(valueGroup.getTimestamp())
                    .setStress(valueGroup.getValue(STRESS_KEY).intValue())
                    .setGrade(StressGrade.MEDIUM.ordinal())
                    .setStressMeasureType(StressMeasureType.PASSIVE)
                    .build();
    }

    @Override
    public String getName() {
        return "stress-detail";
    }

    @Override
    public Set<String> getRequiredValueGroupKeys() {
        return singleton(STRESS_KEY);
    }

    @Override
    public Set<String> getSupportedValueGroupKeys() {
        return Sets.newHashSet(STRESS_KEY, STRESS_GRADE_KEY, MEASURE_TYPE_KEY);
    }
}
