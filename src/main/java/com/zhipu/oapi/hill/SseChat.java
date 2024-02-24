package com.zhipu.oapi.hill;

import cn.hutool.core.collection.CollectionUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhipu.oapi.ClientV4;
import com.zhipu.oapi.Constants;
import com.zhipu.oapi.service.v4.model.*;
import com.zhipu.oapi.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * sse聊天
 *
 * @Author huleilei9
 * @Date 2024/2/24
 **/
public class SseChat {

    /**
     * Mapper
     */
    private static ObjectMapper mapper = Tools.defaultObjectMapper();

    /**
     * sse-invoke调用模型，使用标准Listener，直接返回结果
     *
     * @param requestId 请求id
     * @param question  问题
     * @param paramMap
     * @return {@link ModelApiResponse}
     */
    public static ModelApiResponse execSseInvoke(String requestId, String question, Map<String, String> paramMap) {
        List<ChatMessage> messages = new ArrayList<>();
        ChatMessage chatMessage = new ChatMessage(ChatMessageRole.USER.value(), question);
        messages.add(chatMessage);

        // 函数调用参数构建部分
        List<ChatTool> chatToolList = new ArrayList<>();
        ChatTool chatTool = new ChatTool();
        chatTool.setType(ChatToolType.FUNCTION.value());
        ChatFunctionParameters chatFunctionParameters = new ChatFunctionParameters();
        chatFunctionParameters.setType("object");
        List<String> required = new ArrayList<>();

        if (CollectionUtil.isNotEmpty(paramMap)) {
            Map<String, Object> properties = new HashMap<>(paramMap.size());
            paramMap.keySet().stream()
                    .forEach(key -> {
                        if (StringUtils.isNotEmpty(paramMap.get(key))) {
                            properties.put(key, new HashMap<String, Object>() {{
                                put("type", "string");
                                put("description", paramMap.get(key));
                            }});
                            chatFunctionParameters.setProperties(properties);

                            required.add(key);
                        }
                    });
        }

        ChatFunction chatFunction = ChatFunction.builder()
                .name("query_train_info")
                .description("根据用户提供的信息，查询对应的车次")
                .parameters(chatFunctionParameters)
                .required(required)
                .build();
        chatTool.setFunction(chatFunction);

        chatToolList.add(chatTool);

        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(Constants.ModelChatGLM4)
                .stream(Boolean.TRUE)
                .messages(messages)
                .requestId(requestId)
                .tools(chatToolList)
                .toolChoice("auto")
                .build();

        //
        ModelApiResponse sseModelApiResp = Tools.getClient().invokeModelApi(chatCompletionRequest);
        if (sseModelApiResp.isSuccess()) {
            AtomicBoolean isFirst = new AtomicBoolean(true);
            ChatMessageAccumulator chatMessageAccumulator = Tools.mapStreamToAccumulator(sseModelApiResp.getFlowable())
                    .doOnNext(accumulator -> {
                        {
                            if (isFirst.getAndSet(false)) {
                                System.out.print("Response: ");
                            }
                            if (accumulator.getDelta() != null && accumulator.getDelta().getTool_calls() != null) {
                                String jsonString = mapper.writeValueAsString(accumulator.getDelta().getTool_calls());
                                System.out.println("tool_calls: " + jsonString);
                            }
                            if (accumulator.getDelta() != null && accumulator.getDelta().getContent() != null) {
                                System.out.print(accumulator.getDelta().getContent());
                            }
                        }
                    })
                    .doOnComplete(System.out::println)
                    .lastElement()
                    .blockingGet();

            Choice choice = new Choice(chatMessageAccumulator.getChoice().getFinishReason(), 0L, chatMessageAccumulator.getDelta());
            List<Choice> choices = new ArrayList<>();
            choices.add(choice);

            ModelData data = new ModelData();
            data.setChoices(choices);
            data.setUsage(chatMessageAccumulator.getUsage());
            data.setId(chatMessageAccumulator.getId());
            data.setCreated(chatMessageAccumulator.getCreated());
            data.setRequestId(chatCompletionRequest.getRequestId());

            sseModelApiResp.setFlowable(null);
            sseModelApiResp.setData(data);
        }

        return sseModelApiResp;
    }


    /**
     * 闲聊
     *
     * @param requestId  请求id
     * @param question   问题
     * @param properties 特性
     * @return {@link ModelApiResponse}
     */
    public static ModelApiResponse chat(String requestId, String question, Map<String, Object> properties) {
        List<ChatMessage> messages = new ArrayList<>();
        ChatMessage chatMessage = new ChatMessage(ChatMessageRole.USER.value(), question);
        messages.add(chatMessage);

        // 函数调用参数构建部分  官网资料：https://open.bigmodel.cn/dev/howuse/functioncall
        ChatFunctionParameters chatFunctionParameters = new ChatFunctionParameters();
        chatFunctionParameters.setType("object");
        chatFunctionParameters.setProperties(properties);

        //函数
        ChatFunction chatFunction = ChatFunction.builder()
                .name("get_weather")
                .description("Get the current weather of a location")
                .parameters(chatFunctionParameters)
                .build();

        ChatTool chatTool = new ChatTool();
        chatTool.setType(ChatToolType.FUNCTION.value());
        chatTool.setFunction(chatFunction);

        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(Constants.ModelChatGLM4)
                .stream(Boolean.FALSE)
                .invokeMethod(Constants.invokeMethod)
                .messages(messages)
                .requestId(requestId)
                //可供模型调用的函数工具列表
                .tools(CollectionUtil.newArrayList(chatTool))
                .toolChoice("auto")
                .build();

        ModelApiResponse invokeModelApiResp = Tools.getClient().invokeModelApi(chatCompletionRequest);

        return invokeModelApiResp;
    }

    /**
     * sse-invoke调用模型，使用标准Listener，直接返回结果
     *
     * @param requestId  请求id
     * @param question   问题
     * @param properties 特性
     * @return {@link ModelApiResponse}
     */
    public static ModelApiResponse chatOnListener(String requestId, String question, Map<String, Object> properties) {
        ModelApiResponse sseModelApiResp = chat(requestId, question, properties);

        if (sseModelApiResp.isSuccess()) {
            AtomicBoolean isFirst = new AtomicBoolean(true);
            ChatMessageAccumulator chatMessageAccumulator = Tools.mapStreamToAccumulator(sseModelApiResp.getFlowable())
                    .doOnNext(accumulator -> {
                        {
                            if (isFirst.getAndSet(false)) {
                                System.out.print("Response: ");
                            }
                            if (accumulator.getDelta() != null && accumulator.getDelta().getTool_calls() != null) {
                                String jsonString = mapper.writeValueAsString(accumulator.getDelta().getTool_calls());
                                System.out.println("tool_calls: " + jsonString);
                            }
                            if (accumulator.getDelta() != null && accumulator.getDelta().getContent() != null) {
                                System.out.print(accumulator.getDelta().getContent());
                            }
                        }
                    })
                    .doOnComplete(System.out::println)
                    .lastElement()
                    .blockingGet();

            Choice choice = new Choice(chatMessageAccumulator.getChoice().getFinishReason(), 0L, chatMessageAccumulator.getDelta());
            List<Choice> choices = new ArrayList<>();
            choices.add(choice);

            ModelData data = new ModelData();
            data.setChoices(choices);
            data.setUsage(chatMessageAccumulator.getUsage());
            data.setId(chatMessageAccumulator.getId());
            data.setCreated(chatMessageAccumulator.getCreated());
            data.setRequestId(requestId);

            sseModelApiResp.setFlowable(null);
            sseModelApiResp.setData(data);
        }

        return sseModelApiResp;
    }

}
