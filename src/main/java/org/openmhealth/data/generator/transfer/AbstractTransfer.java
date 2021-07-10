package org.openmhealth.data.generator.transfer;

import org.openmhealth.data.generator.domain.TimestampedValueGroup;
import org.openmhealth.data.generator.dto.MeasureDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: test-gradle
 * @author: LJ
 * @create: 2021-07-09 09:56
 * @description：
 **/
public abstract class AbstractTransfer<T extends MeasureDTO> implements Transfer<T>{

    public String getName(){
        return "some-user";
    };

    public Iterable<T> transferDatas(Iterable<TimestampedValueGroup> valueGroups) {

        List<T> measureDTOs = new ArrayList<>();

        for (TimestampedValueGroup timestampedValueGroup : valueGroups) {
            measureDTOs.add(newMeasureDTO(timestampedValueGroup));
        }

        return measureDTOs;
    }

    /**
     * @param timestampedValueGroup
     * @return needed MeasureDTO like HeartRateDTO
     */
    public abstract T newMeasureDTO(TimestampedValueGroup timestampedValueGroup);

}
