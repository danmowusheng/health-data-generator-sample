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

package org.openmhealth.data.generator.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//将一个对象映射为一个json字符串
/**
 * @author Emerson Farrugia
 */
@Configuration
public class JacksonConfiguration {

    /**
     * @return an {@link ObjectMapper} that matches schema conventions
     */
    @Bean
    public ObjectMapper objectMapper() {
        return org.openmhealth.schema.configuration.JacksonConfiguration.newObjectMapper();
    }
}
