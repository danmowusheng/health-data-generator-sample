package org.openmhealth.data.generator.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

/**
 * @program: test-gradle
 * @author: LJ
 * @create: 2021-06-23 19:09
 * @description：将offsetDateTime转变成便于文件命名的字符串
 **/
@Component
public class OffsetDateTime2String implements Converter<OffsetDateTime, String> {
    @Override
    public String convert(OffsetDateTime source){

        if (source == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(source.getYear());
        sb.append("-");
        if(source.getMonth().getValue()<10){
            sb.append(0);
        }
        sb.append(source.getMonth().getValue());
        sb.append("-");
        sb.append(source.getDayOfMonth());

        return sb.toString();
    }

}
