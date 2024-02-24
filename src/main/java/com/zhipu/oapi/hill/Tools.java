package com.zhipu.oapi.hill;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.zhipu.oapi.ClientV4;
import com.zhipu.oapi.service.v4.model.*;
import io.reactivex.Flowable;

/**
 * 工具
 *
 * @Author huleilei9
 * @Date 2024/2/24
 **/
public class Tools {

    /**
     * 应用程序编程接口Key
     */
    private static final String API_KEY = "4df3a7cccf69d2f10426294db9a4cfae";

    /**
     * api秘钥
     */
    private static final String API_SECRET = "zLhlfyYTaI1nitc7";

    /**
     * 将流映射到累加器
     *
     * @param flowable 可流动
     * @return {@link Flowable}<{@link ChatMessageAccumulator}>
     */
    public static Flowable<ChatMessageAccumulator> mapStreamToAccumulator(Flowable<ModelData> flowable) {
        return flowable.map(chunk -> {
            return new ChatMessageAccumulator(chunk.getChoices().get(0).getDelta(), null, chunk.getChoices().get(0), chunk.getUsage(), chunk.getCreated(), chunk.getId());
        });
    }


    /**
     * 对象Mapper
     *
     * @return {@link ObjectMapper}
     */
    public static ObjectMapper defaultObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        mapper.addMixIn(ChatFunction.class, ChatFunctionMixIn.class);
        mapper.addMixIn(ChatCompletionRequest.class, ChatCompletionRequestMixIn.class);
        mapper.addMixIn(ChatFunctionCall.class, ChatFunctionCallMixIn.class);
        return mapper;
    }


    /**
     * 获取Client
     *
     * @return {@link ClientV4}
     */
    public static ClientV4 getClient() {
        return new ClientV4.Builder(API_KEY, API_SECRET).build();
    }
}
