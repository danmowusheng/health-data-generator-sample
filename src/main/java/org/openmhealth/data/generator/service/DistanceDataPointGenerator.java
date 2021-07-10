package org.openmhealth.data.generator.service;

import com.google.common.collect.Sets;
import org.openmhealth.data.generator.domain.TimestampedValueGroup;
import org.openmhealth.schema.domain.omh.DurationUnitValue;
import org.openmhealth.schema.domain.omh.StepCount1;
import org.openmhealth.schema.domain.omh.StepCount2;
import org.springframework.stereotype.Component;

import java.util.Set;

import static org.openmhealth.schema.domain.omh.DurationUnit.SECOND;
import static org.openmhealth.schema.domain.omh.TimeInterval.ofStartDateTimeAndDuration;

/**
 * @program: test-gradle
 * @author: LJ
 * @create: 2021-07-10 10:09
 * @description：
 **/
@Component
public class DistanceDataPointGenerator extends AbstractDataPointGeneratorImpl<StepCount1> {
    public static final String DISTANCE_PER_MINUTE_KEY = "miles-per-minute";
    public static final String DURATION_KEY = "duration-in-seconds";

    @Override
    public StepCount1 newMeasure(TimestampedValueGroup valueGroup) {
        DurationUnitValue duration = new DurationUnitValue(SECOND, valueGroup.getValue(DURATION_KEY));
        double milesPerMin = valueGroup.getValue(DISTANCE_PER_MINUTE_KEY);

        Double miles = milesPerMin * duration.getValue().doubleValue() / 60.0;

        return new StepCount1.Builder(miles.longValue())
                .setEffectiveTimeFrame(ofStartDateTimeAndDuration(valueGroup.getTimestamp(), duration))
                .build();
    }

    @Override
    public String getName() {
        return "distance";
    }

    @Override
    public Set<String> getRequiredValueGroupKeys() {
        return Sets.newHashSet(DISTANCE_PER_MINUTE_KEY, DURATION_KEY);
    }

    @Override
    public Set<String> getSupportedValueGroupKeys() {
        return Sets.newHashSet(DISTANCE_PER_MINUTE_KEY, DURATION_KEY);
    }
}
