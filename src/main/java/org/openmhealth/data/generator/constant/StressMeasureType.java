package org.openmhealth.data.generator.constant;

/**
 * @EnumName StressMeasureType
 * @Description 压力测量类型
 * @Author zws
 * @Date 2021/7/2 12:18
 * @Version 1.0
 */
public enum StressMeasureType {

    ACTIVE("active",1),//主动
    PASSIVE("passive",2);//被动

    private String name;
    private Integer index;

    // 构造方法
    private StressMeasureType(String name, int index) {
        this.name = name;
        this.index = index;
    }
}
