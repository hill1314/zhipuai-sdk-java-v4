package com.zhipu.oapi.service.v4.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 函数功能参数
 *
 * @author huleilei9
 * @date 2024/02/24
 */

/*
 * "properties": {
 *                     "flight_number": {
 *                         "description": "航班号",
 *                         "type": "string"
 *                     },
 *                     "date": {
 *                         "description": "日期",
 *                         "type": "string",
 *                     }
 *                 }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatFunctionParameters {


    /**
     * 定义 JSON 数据的数据类型约束
     */
    private String type;

    /**
     * 特性
     * 其中的每个属性代表要定义的 JSON 数据中的一个键
     */
    private Object properties;

    /**
     * 指定哪些属性在数据中必须被包含
     */
    private List<String> required;

}
