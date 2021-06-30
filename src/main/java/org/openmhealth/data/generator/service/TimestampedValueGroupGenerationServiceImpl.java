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

import org.apache.commons.math3.distribution.ExponentialDistribution;
import org.openmhealth.data.generator.domain.BoundedRandomVariableTrend;
import org.openmhealth.data.generator.domain.MeasureGenerationRequest;
import org.openmhealth.data.generator.domain.TimestampedValueGroup;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.time.temporal.ChronoUnit.SECONDS;


/**
 * @author Emerson Farrugia
 */
@Service
public class TimestampedValueGroupGenerationServiceImpl implements TimestampedValueGroupGenerationService {

    public static final int NIGHT_TIME_START_HOUR = 23;
    public static final int NIGHT_TIME_END_HOUR = 6;


    @Override
    public Iterable<TimestampedValueGroup> generateValueGroups(MeasureGenerationRequest request) {
        //获取平均间隔？
        ExponentialDistribution interPointDurationDistribution =
                new ExponentialDistribution(request.getMeanInterPointDuration().getSeconds());
        //获取总时长
        long totalDurationInS = Duration.between(request.getStartDateTime(), request.getEndDateTime()).getSeconds();

        OffsetDateTime effectiveDateTime = request.getStartDateTime();
        //初始化一组带时间戳的数据
        List<TimestampedValueGroup> timestampedValueGroups = new ArrayList<>();

        do {
            effectiveDateTime = effectiveDateTime.plus((long) interPointDurationDistribution.sample(), SECONDS);

            if (!effectiveDateTime.isBefore(request.getEndDateTime())) {
                break;
            }

            if (request.isSuppressNightTimeMeasures() != null && request.isSuppressNightTimeMeasures() &&
                    (effectiveDateTime.getHour() >= NIGHT_TIME_START_HOUR ||
                            effectiveDateTime.getHour() < NIGHT_TIME_END_HOUR)) {
                continue;
            }

            TimestampedValueGroup valueGroup = new TimestampedValueGroup();
            valueGroup.setTimestamp(effectiveDateTime);
            //当前时间点所处在开始和结束之间的位置
            //用于下一步制造新的数值
            double trendProgressFraction = (double)
                    Duration.between(request.getStartDateTime(), effectiveDateTime).getSeconds() / totalDurationInS;
            //对每一种值value加上合适的变量
            for (Map.Entry<String, BoundedRandomVariableTrend> trendEntry : request.getTrends().entrySet()) {

                String key = trendEntry.getKey();
                BoundedRandomVariableTrend trend = trendEntry.getValue();

                double value = trend.nextValue(trendProgressFraction);
                valueGroup.setValue(key, value);
            }
            //增加一个带时间戳的数据
            timestampedValueGroups.add(valueGroup);
        }
        while (true);

        return timestampedValueGroups;
    }
}
