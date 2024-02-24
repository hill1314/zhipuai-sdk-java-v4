package com.zhipu.oapi.hill;

import com.alibaba.fastjson.JSON;
import com.zhipu.oapi.Constants;
import com.zhipu.oapi.service.v4.model.ChatCompletionRequest;
import com.zhipu.oapi.service.v4.model.ChatMessage;
import com.zhipu.oapi.service.v4.model.ChatMessageRole;
import com.zhipu.oapi.service.v4.model.ModelApiResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 图像创建文本
 *
 * @Author huleilei9
 * @Date 2024/2/24
 **/
public class ImageCreateText {

    /**
     * 图生文
     */
    public static ModelApiResponse testImageToWord(String requestId, String imageUrl,String question) {
        //文本信息
        Map<String, Object> textMap = new HashMap<>();
        textMap.put("type", "text");
        textMap.put("text", question);

        //图片信息
        Map<String, Object> typeMap = new HashMap<>();
        typeMap.put("type", "image_url");
        Map<String, Object> urlMap = new HashMap<>();
        urlMap.put("url", imageUrl);
        typeMap.put("image_url", urlMap);

        List<Map<String, Object>> contentList = new ArrayList<>();
        contentList.add(textMap);
        contentList.add(typeMap);
        ChatMessage chatMessage = new ChatMessage(ChatMessageRole.USER.value(), contentList);

        List<ChatMessage> messages = new ArrayList<>();
        messages.add(chatMessage);

        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(Constants.ModelChatGLM4V)
                .stream(Boolean.FALSE)
                .invokeMethod(Constants.invokeMethod)
                .messages(messages)
                .requestId(requestId)
                .build();

        ModelApiResponse modelApiResponse = Tools.getClient().invokeModelApi(chatCompletionRequest);
        System.out.println("model output:" + JSON.toJSONString(modelApiResponse));

        return modelApiResponse;
    }
}
