package org.openmhealth.data.generator.service;

import com.google.common.collect.Sets;
import org.openmhealth.data.generator.domain.TimestampedValueGroup;
import org.openmhealth.schema.domain.omh.*;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * @program: test-gradle
 * @author: LJ
 * @create: 2021-07-10 10:19
 * @description：
 **/
@Component
public class LocationDataPointGenerator extends AbstractDataPointGeneratorImpl<Geoposition> {
    public static final String LATITUDE_KEY = "latitude";
    public static final String LONGITUDE_KEY = "longitude";
    public static final String EVALUATION_KEY = "precision";

    /**
     * 这里仅设置了经纬度及精度信息，其他的直接忽略
     * @param valueGroup a group of values
     * @return
     */
    @Override
    public Geoposition newMeasure(TimestampedValueGroup valueGroup) {
        return new Geoposition.Builder(new PlaneAngleUnitValue(PlaneAngleUnit.DEGREE_OF_ARC,valueGroup.getValue(LATITUDE_KEY)),
                new PlaneAngleUnitValue(PlaneAngleUnit.DEGREE_OF_ARC, valueGroup.getValue(LONGITUDE_KEY)), valueGroup.getTimestamp())
                .setElevation(new LengthUnitValue(LengthUnit.METER, 50))
                .build();
    }

    @Override
    public String getName() {
        return "geo-position";
    }

    @Override
    public Set<String> getRequiredValueGroupKeys() {
        return Sets.newHashSet(LATITUDE_KEY, LONGITUDE_KEY);
    }

    @Override
    public Set<String> getSupportedValueGroupKeys() {
        return Sets.newHashSet(LATITUDE_KEY, LONGITUDE_KEY, EVALUATION_KEY);
    }
}
