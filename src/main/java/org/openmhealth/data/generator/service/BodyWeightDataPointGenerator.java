/*
 * Copyright 2014 Open mHealth
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
import org.openmhealth.schema.domain.omh.BodyWeight;
import org.openmhealth.schema.domain.omh.MassUnitValue;
import org.springframework.stereotype.Component;

import java.util.Set;

import static java.util.Collections.singleton;
import static org.openmhealth.schema.domain.omh.MassUnit.KILOGRAM;


/**
 * @author Emerson Farrugia
 */
@Component
public class BodyWeightDataPointGenerator extends AbstractDataPointGeneratorImpl<BodyWeight> {

    public static final String WEIGHT_KEY = "weight-in-kg";

    @Override
    public String getName() {
        return "body-weight";
    }

    @Override
    public Set<String> getRequiredValueGroupKeys() {
        return singleton(WEIGHT_KEY);
    }

    @Override
    public Set<String> getSupportedValueGroupKeys() {
        return singleton(WEIGHT_KEY);
    }

    @Override
    public BodyWeight newMeasure(TimestampedValueGroup valueGroup) {

        return new BodyWeight.Builder(new MassUnitValue(KILOGRAM, valueGroup.getValue(WEIGHT_KEY)))
                .setEffectiveTimeFrame(valueGroup.getTimestamp())
                .build();
    }
}
