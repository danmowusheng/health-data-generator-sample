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

package org.openmhealth.data.generator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;



/**
 * This application loads a data generation fixture from application.yml and creates data points according to that
 * fixture. The data points are then either written to the console or to a file, as configured.
 *
 * @author Emerson Farrugia
 */
/**
* @Description: data-generator模拟的是端（手表）产生的数据？但是这里需要快速的得到有用的数据
* @author: LJ
* @Date: 2021/6/23
**/
@SpringBootApplication
public class Application {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class);
    }

}