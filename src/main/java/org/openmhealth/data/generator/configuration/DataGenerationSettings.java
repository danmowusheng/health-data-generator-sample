/*
 * Copyright 2015 Open mHealth
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

package org.openmhealth.data.generator.configuration;

import org.openmhealth.data.generator.domain.MeasureGenerationRequest;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;


/**
 * @author Emerson Farrugia
 */
@Component
@ConfigurationProperties("data")
public class DataGenerationSettings {
    private String userID = "some-user";
    //开始时间
    private OffsetDateTime startDateTime = OffsetDateTime.of(2014, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
    //结束时间
    private OffsetDateTime endDateTime = OffsetDateTime.of(2015, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
    //时间间隔
    private Duration meanInterPointDuration = Duration.ofHours(24);
    //是否检测夜间
    private Boolean suppressNightTimeMeasures = false;
    //指标产生器请求?包括产生器名称以及这个字段以上的一些字段，如开始时间等
    private List<MeasureGenerationRequest> measureGenerationRequests = new ArrayList<>();

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public OffsetDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(OffsetDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public OffsetDateTime getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(OffsetDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }

    public Duration getMeanInterPointDuration() {
        return meanInterPointDuration;
    }

    public void setMeanInterPointDuration(Duration meanInterPointDuration) {
        this.meanInterPointDuration = meanInterPointDuration;
    }

    public Boolean isSuppressNightTimeMeasures() {
        return suppressNightTimeMeasures;
    }

    public void setSuppressNightTimeMeasures(Boolean suppressNightTimeMeasures) {
        this.suppressNightTimeMeasures = suppressNightTimeMeasures;
    }

    public List<MeasureGenerationRequest> getMeasureGenerationRequests() {
        return measureGenerationRequests;
    }

    public void setMeasureGenerationRequests(List<MeasureGenerationRequest> measureGenerationRequests) {
        this.measureGenerationRequests = measureGenerationRequests;
    }
}
