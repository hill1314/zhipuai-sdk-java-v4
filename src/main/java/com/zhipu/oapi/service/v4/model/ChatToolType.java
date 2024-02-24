package com.zhipu.oapi.service.v4.model;

/**
 * chat工具类型
 *
 * @author huleilei9
 * @date 2024/02/24
 */
public enum ChatToolType {

    /**
     * 网络搜索
     */
    WEB_SEARCH("web_search"),

    /**
     * 检索
     * Retrieval 可以通过访问外部的知识来增强大模型的推理能力
     * https://open.bigmodel.cn/dev/howuse/retrieval
     */
    RETRIEVAL("retrieval"),

    /**
     * 外部函数
     * https://open.bigmodel.cn/dev/howuse/functioncall
     */
    FUNCTION("function");

    private final String value;

    ChatToolType(final String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }


}
