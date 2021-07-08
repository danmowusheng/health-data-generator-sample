package org.openmhealth.data.generator.constant;

/**
 * @EnumName StressGrade
 * @Description 压力等级
 * @Author zws
 * @Date 2021/7/2 12:14
 * @Version 1.0
 */
public enum StressGrade {

    RELAX("relax",1),//放松
    NORMAL("normal",2),//正常
    MEDIUM("medium",3),//中等
    HIGH("high",4);//偏高

    private String name;
    private Integer index;

    // 构造方法
    private StressGrade(String name, int index) {
        this.name = name;
        this.index = index;
    }
}
