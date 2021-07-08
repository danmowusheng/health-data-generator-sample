package org.openmhealth.data.generator.constant;

/**
 * @EnumName BodyWeight
 * @Description 体重数据必需label
 * @Author zws
 * @Date 2021/7/7 10:53
 * @Version 1.0
 */
public enum BodyWeight {

    BODY_WEIGHT("body_weight",1),//体重
    BMI("bmi",2),//体重指数
    BODY_FAT("body_fat",3),//体脂
    BODY_FAT_RATE("body_fat_rate",4),//体脂率
    MUSCLE_MASS("muscle_mass",5),//肌肉重量
    BASAL_METABOLISM("basal_metabolism",6),//基础代谢
    MOISTURE("moisture",7),//水分
    MOISTURE_RATE("moisture_rate",8),//含水率
    VISCERAL_FAT_LEVEL("visceral_fat_level",9),//内脏脂肪水平
    BONE_SALT("bone_salt",10),//骨盐
    PROTEIN_RATE("protein_rate",11),//蛋白质转化率
    BODY_AGE("muscle_mass",12),//年龄
    BODY_SCORE("body_score",13),//身体得分
    SKELETAL_MUSCLE_MASS("skeletal_muscle_mass",14),//骨骼肌块
    IMPEDANCE("impedance",15);//阻抗

    private String name;
    private Integer index;

    // 构造方法
    private BodyWeight(String name, int index) {
        this.name = name;
        this.index = index;
    }

}
