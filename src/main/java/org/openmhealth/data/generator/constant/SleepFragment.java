package org.openmhealth.data.generator.constant;

/**
 * @EnumName SleepFragment
 * @Description 睡眠状态
 * @Author zws
 * @Date 2021/7/2 9:46
 * @Version 1.0
 */
public enum SleepFragment {

    LIGHT_SLEEP("light_sleep",1),//浅睡
    DREAM_SLEEP("dream_sleep",2),//梦睡
    DEEP_SLEEP("deep_sleep",3),//深睡
    AWAKE("awake",4),//清醒
    NAP("nap",5);//午睡

    private String name;
    private Integer index;

    // 构造方法
    private SleepFragment(String name, int index) {
        this.name = name;
        this.index = index;
    }
}
