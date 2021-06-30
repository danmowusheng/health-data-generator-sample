package org.openmhealth.data.generator.domain;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * @program: test-gradle
 * @author: LJ
 * @create: 2021-06-22 22:33
 * @description：a request to get  measure dataPoints
 **/
public class MeasureRequest {

    private OffsetDateTime startTime;
    private OffsetDateTime endTime;
    private String healthdata;

    public OffsetDateTime getStartTime() {
        return startTime;
    }

    public OffsetDateTime getEndTime() {
        return endTime;
    }

    public String getHealthdata() {
        return healthdata;
    }

    public void setStartTime(OffsetDateTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(OffsetDateTime endTime) {
        this.endTime = endTime;
    }
}
