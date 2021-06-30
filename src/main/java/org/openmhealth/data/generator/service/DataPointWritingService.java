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

import org.openmhealth.schema.domain.omh.DataPoint;


/**
 * @author Emerson Farrugia
 */
public interface DataPointWritingService {

    /**
     * @param dataPoints the data points to write
     * @return the number of data points that have been written
     * @throws Exception if an error occurred while writing data points
     */
    long writeDataPoints(Iterable<? extends DataPoint<?>> dataPoints) throws Exception;
}
