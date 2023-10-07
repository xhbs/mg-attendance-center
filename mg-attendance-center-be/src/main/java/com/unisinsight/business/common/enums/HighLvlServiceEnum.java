package com.unisinsight.business.common.enums;


import java.util.Arrays;
import java.util.List;

/**
 * @author tanggang
 * @version 1.0
 * @description
 * @email tang.gang@inisinsight.com
 * @date 2021/8/17 10:12
 **/
public enum HighLvlServiceEnum {
    //整个云南省的数据不统计
//    LEVEL_1(1,"staticHighLvlServcieImpl1"),
    LEVEL_2(2,"staticHighLvlServcieImpl2"),
    LEVEL_3(3,"staticHighLvlServcieImpl3"),
    LEVEL_4(4,"staticHighLvlServcieImpl4"),
    LEVEL_5(5,"staticHighLvlServcieImpl5"),
    ;

    //level对应orgType
    private int level;
    private String service;

    HighLvlServiceEnum(int level,String service){
        this.level=level;
        this.service=service;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public static List<HighLvlServiceEnum> getStaticHighLvlServcieEnumDescList(){
        HighLvlServiceEnum[] values = HighLvlServiceEnum.values();
        List<HighLvlServiceEnum> highLvlServiceEnums = Arrays.asList(values);
        highLvlServiceEnums.sort((v1,v2)->{
            return v2.getLevel()-v1.getLevel();
        });
        return highLvlServiceEnums;
    }

   public static HighLvlServiceEnum getHighLvlServiceEnum(int level){
        HighLvlServiceEnum[] values = HighLvlServiceEnum.values();
        for (HighLvlServiceEnum value: values){
            if(level == value.getLevel()){
                return value;
            }
        }
        return null;
    }
    public static HighLvlServiceEnum getMaxHighLvlServiceEnum(){
        HighLvlServiceEnum[] values = HighLvlServiceEnum.values();
        List<HighLvlServiceEnum> highLvlServiceEnums = Arrays.asList(values);
        highLvlServiceEnums.sort((v1,v2) ->v2.getLevel()-v1.getLevel());
        return highLvlServiceEnums.get(0);
    }
}
