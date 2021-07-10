package org.openmhealth.data.generator.transfer;

import org.openmhealth.data.generator.domain.TimestampedValueGroup;
import org.openmhealth.data.generator.dto.MeasureDTO;

public interface Transfer<T extends MeasureDTO> {

    String getName();

    /**
     * @param valueGroups a list of value groups, where each value group corresponds to a data point
     * @return transfer these data to measureDTO
     */
    Iterable<T> transferDatas(Iterable<TimestampedValueGroup> valueGroups);
}
