package org.openmhealth.data.generator.constant;

/**
 * @EnumName HeartRate
 * @Description 心率信息必需label
 * @Author zws
 * @Date 2021/7/7 10:19
 * @Version 1.0
 */
public enum HeartRate {

    RESTING("resting",1),//静息心率
    EXERCISE("exercise",2);//运动心率

    private String name;
    private Integer index;

    // 构造方法
    private HeartRate(String name, int index) {
        this.name = name;
        this.index = index;
    }
}
