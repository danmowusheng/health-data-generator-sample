package org.openmhealth.data.generator.service;

import org.openmhealth.data.generator.domain.TimestampedValueGroup;
import org.openmhealth.schema.domain.omh.CaloriesBurned1;
import org.openmhealth.schema.domain.omh.KcalUnit;
import org.openmhealth.schema.domain.omh.KcalUnitValue;
import org.springframework.stereotype.Component;

import static java.util.Collections.singleton;
import java.util.Set;

/**
 * @program: test-gradle
 * @author: LJ
 * @create: 2021-07-10 09:53
 * @description：
 **/
@Component
public class CaloriesBurned1DataPointGenerator extends AbstractDataPointGeneratorImpl<CaloriesBurned1> {

    public static final String CALORIE_KEY = "calorie-burned";
    public static final String ACTIVE_NAME = "run";

    @Override
    public CaloriesBurned1 newMeasure(TimestampedValueGroup valueGroup) {
        return new CaloriesBurned1.Builder(new KcalUnitValue(KcalUnit.KILOCALORIE, valueGroup.getValue(CALORIE_KEY)))
                    .setEffectiveTimeFrame(valueGroup.getTimestamp())
                    .setActivityName(ACTIVE_NAME)
                    .build();
    }

    @Override
    public String getName() {
        return "calorie-count";
    }

    @Override
    public Set<String> getRequiredValueGroupKeys() {
        return singleton(CALORIE_KEY);
    }

    @Override
    public Set<String> getSupportedValueGroupKeys() {
        return singleton(CALORIE_KEY);
    }
}
