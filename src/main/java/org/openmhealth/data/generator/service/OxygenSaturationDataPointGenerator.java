package org.openmhealth.data.generator.service;

import com.google.common.collect.Sets;
import org.openmhealth.data.generator.domain.TimestampedValueGroup;
import org.openmhealth.schema.domain.omh.OxygenSaturation;
import org.openmhealth.schema.domain.omh.TypedUnitValue;
import org.springframework.stereotype.Component;


import java.util.Set;

import static java.util.Collections.singleton;
import static org.openmhealth.schema.domain.omh.PercentUnit.PERCENT;
import static org.openmhealth.schema.domain.omh.OxygenFlowRateUnit.LITERS_PER_MINUTE;
import static org.openmhealth.schema.domain.omh.OxygenSaturation.MeasurementMethod.PULSE_OXIMETRY;
import static org.openmhealth.schema.domain.omh.OxygenSaturation.MeasurementSystem.PERIPHERAL_CAPILLARY;
import static org.openmhealth.schema.domain.omh.OxygenSaturation.SupplementalOxygenAdministrationMode.NASAL_CANNULA;

//@Component:把普通pojo实例化到spring容器中，相当于配置文件中的 <bean id="" class=""/>

/**
 * @program: test-gradle
 * @author: LJ
 * @create: 2021-06-29 16:49
 * @description：generate OxygenSaturation dataPoint,新建立的，还有一些地方不是很了解
 **/
@Component
public class OxygenSaturationDataPointGenerator extends AbstractDataPointGeneratorImpl<OxygenSaturation> {
    public static final String SATURATION_KEY = "percentage";
    public static final String OXYGEN_FLOW_RATE_KEY = "L-per-minute";
    @Override
    public String getName() {
        return "oxygen-saturation";
    }

    @Override
    public Set<String> getRequiredValueGroupKeys() {
        return Sets.newHashSet(SATURATION_KEY, OXYGEN_FLOW_RATE_KEY);
    }

    @Override
    public Set<String> getSupportedValueGroupKeys() {
        return Sets.newHashSet(SATURATION_KEY, OXYGEN_FLOW_RATE_KEY);
    }

    /**
    * @Description: 构造一份血氧饱和的数据需要3个常量确定测量方式以及一份血氧流速的值，以及一份血氧饱和本身的值
    * @Param:
    * @author: LJ
    * @Date: 2021/6/30
    **/
    @Override
    public OxygenSaturation newMeasure(TimestampedValueGroup valueGroup) {

        return new OxygenSaturation.Builder(new TypedUnitValue<>(PERCENT, valueGroup.getValue(SATURATION_KEY)))
                .setSupplementalOxygenAdministrationMode(NASAL_CANNULA)
                .setMeasurementSystem(PERIPHERAL_CAPILLARY)
                .setMeasurementMethod(PULSE_OXIMETRY)
                .setSupplementalOxygenFlowRate(new TypedUnitValue<>(LITERS_PER_MINUTE,valueGroup.getValue(OXYGEN_FLOW_RATE_KEY)))
                .setEffectiveTimeFrame(valueGroup.getTimestamp())
                .build();
    }
}
