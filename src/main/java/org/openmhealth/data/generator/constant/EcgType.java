package org.openmhealth.data.generator.constant;

/**
 * @EnumName EcgType
 * @Description ecg类型
 * @Author zws
 * @Date 2021/7/2 12:54
 * @Version 1.0
 */
public enum EcgType {

    ONE("relax",1),//单导
    SIX("normal",6),//6导
    TWELVE("medium",12),//12导
    EIGHTEEN("high",18);//18导

    private String name;
    private Integer index;

    // 构造方法
    private EcgType(String name, int index) {
        this.name = name;
        this.index = index;
    }
}
