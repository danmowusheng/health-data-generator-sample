/*
 * Copyright 2016 Open mHealth
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openmhealth.data.generator.service;

import org.openmhealth.data.generator.domain.TimestampedValueGroup;
import org.openmhealth.schema.domain.omh.AmbientTemperature;
import org.openmhealth.schema.domain.omh.TemperatureUnitValue;
import org.springframework.stereotype.Component;

import java.util.Set;

import static java.util.Collections.singleton;
import static org.openmhealth.schema.domain.omh.TemperatureUnit.CELSIUS;


/**
 * @author Emerson Farrugia
 */
@Component
public class AmbientTemperatureDataPointGenerator extends AbstractDataPointGeneratorImpl<AmbientTemperature> {

    public static final String TEMPERATURE_KEY = "temperature-in-c";

    @Override
    public String getName() {
        return "ambient-temperature";
    }

    @Override
    public Set<String> getRequiredValueGroupKeys() {
        return singleton(TEMPERATURE_KEY);
    }

    @Override
    public Set<String> getSupportedValueGroupKeys() {
        return singleton(TEMPERATURE_KEY);
    }

    @Override
    public AmbientTemperature newMeasure(TimestampedValueGroup valueGroup) {
        //build一个指标的数据需要一个测量标准如摄氏度，另外需要一组同一时段的真实数据
        return new AmbientTemperature.Builder(new TemperatureUnitValue(CELSIUS, valueGroup.getValue(TEMPERATURE_KEY)))
                .setEffectiveTimeFrame(valueGroup.getTimestamp())
                .build();
    }
}
