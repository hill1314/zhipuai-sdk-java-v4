package com.zhipu.oapi.service.v4.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.function.Function;

/**
 * 外部函数
 * https://open.bigmodel.cn/dev/howuse/functioncall
 *
 * @author huleilei9
 * @date 2024/02/24
 */

/*
 * tools = [
 *     {
 *         "type": "function",
 *         "function": {
 *             "name": "get_flight_number",
 *             "description": "根据始发地、目的地和日期，查询对应日期的航班号",
 *             "parameters": {
 *                 "type": "object",
 *                 "properties": {
 *                     "departure": {
 *                         "description": "出发地",
 *                         "type": "string"
 *                     },
 *                     "destination": {
 *                         "description": "目的地",
 *                         "type": "string"
 *                     },
 *                     "date": {
 *                         "description": "日期",
 *                         "type": "string",
 *                     }
 *                 },
 *                 "required": [ "departure", "destination", "date" ]
 *             },
 *         }
 *     },
 *     {
 *         "type": "function",
 *         "function": {
 *             "name": "get_ticket_price",
 *             "description": "查询某航班在某日的票价",
 *             "parameters": {
 *                 "type": "object",
 *                 "properties": {
 *                     "flight_number": {
 *                         "description": "航班号",
 *                         "type": "string"
 *                     },
 *                     "date": {
 *                         "description": "日期",
 *                         "type": "string",
 *                     }
 *                 },
 *                 "required": [ "flight_number", "date"]
 *             },
 *         }
 *     },
 * ]
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ChatFunction {

    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 参数
     * parameters字段需要传入一个 Json Schema 对象，以准确地定义函数所接受的参数。若调用函数时不需要传入参数，省略该参数即可
     */
    private ChatFunctionParameters parameters;


    private List<String> required;

    @JsonIgnore
    private Function<Object, Object> executor;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String name;
        private String description;


        private ChatFunctionParameters parameters;


        private List<String> required;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder parameters(ChatFunctionParameters parameters) {
            this.parameters = parameters;
            return this;
        }

        public Builder required(List<String> required) {
            this.required = required;
            return this;
        }



        public ChatFunction build() {
            ChatFunction chatFunction = new ChatFunction();
            chatFunction.setName(name);
            chatFunction.setDescription(description);
            chatFunction.setParameters(parameters);
            chatFunction.setRequired(required);
            return chatFunction;
        }
    }
}
