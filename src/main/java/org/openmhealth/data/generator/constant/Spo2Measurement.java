package org.openmhealth.data.generator.constant;

/**
 * @EnumName Spo2Measurement
 * @Description spo2测量
 * @Author zws
 * @Date 2021/7/2 11:29
 * @Version 1.0
 */
public enum Spo2Measurement {

    NEITHER("neither",1),//从未
    MECHANISM("mechanism",2),//机械
    APPROACH("approach",3);//方法

    private String name;
    private Integer index;

    // 构造方法
    private Spo2Measurement(String name, int index) {
        this.name = name;
        this.index = index;
    }
}
