package org.openmhealth.data.generator.constant;

/**
 * @EnumName LocationSample
 * @Description 位置信息必需label
 * @Author zws
 * @Date 2021/7/7 10:14
 * @Version 1.0
 */
public enum LocationSample {

    LONGITUDE("longitude",1),//经度
    LATITUDE("latitude",2),//纬度
    ALTITUDE("altitude",3);//海拔

    private String name;
    private Integer index;

    // 构造方法
    private LocationSample(String name, int index) {
        this.name = name;
        this.index = index;
    }
}
