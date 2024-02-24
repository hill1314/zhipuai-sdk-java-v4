package com.zhipu.oapi.hill;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.zhipu.oapi.ClientV4;
import com.zhipu.oapi.service.v4.image.ImageApiResponse;
import com.zhipu.oapi.service.v4.model.*;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Hll-测试
 *
 * @Author huleilei9
 * @Date 2024/2/24
 **/
public class MyTest {

    /**
     * 应用程序编程接口Key
     */
    private static final String API_KEY = "4df3a7cccf69d2f10426294db9a4cfae";

    /**
     * api秘钥
     */
    private static final String API_SECRET = "zLhlfyYTaI1nitc7";

    /**
     * 请自定义自己的业务id
     */
    private static final String REQUEST_ID_TEMPLATE = "hll-%d";

    /**
     * client
     */
    private static final ClientV4 client = new ClientV4.Builder(API_KEY, API_SECRET).build();


    /**
     * 前置调用
     */
    @Before
    public void before() {
        System.setProperty("org.slf4j.simpleLogger.logFile", "System.out");
    }


    /**
     * sse-invoke调用模型，使用标准Listener，直接返回结果
     */
    @Test
    public void testSseInvoke() {
        String requestId = String.format(REQUEST_ID_TEMPLATE, System.currentTimeMillis());
        String question = "你能帮我查询2024年2月25日从北京南站到上海的火车票吗？";
        Map<String, String> properties = new HashMap<>(8);
        properties.put("departure", "出发城市或车站");
        properties.put("destination", "目的地城市或车站");
        properties.put("date", "要查询的车次日期");

        ModelApiResponse response = SseChat.execSseInvoke(requestId, question, properties);

        System.out.println("model output =>" + JSON.toJSONString(response));
    }

    /**
     * 同步调用
     */
    @Test
    public void testInvoke() {
        String question = "ChatGLM和你哪个更强大";
        String requestId = String.format(REQUEST_ID_TEMPLATE, System.currentTimeMillis());

        Map<String, Object> properties = new HashMap<>(8);
        properties.put("location", new HashMap<String, Object>() {{
            put("type", "string");
            put("description", "城市，如：北京");
        }});
        properties.put("unit", new HashMap<String, Object>() {{
            put("type", "string");
            put("enum", new ArrayList<String>() {{
                add("celsius");
                add("fahrenheit");
            }});
        }});

        ModelApiResponse invokeModelApiResp = SseChat.chat(requestId, question, properties);

        try {
            System.out.println("model output:" + Tools.defaultObjectMapper().writeValueAsString(invokeModelApiResp));
            //model output:{"code":200,"msg":"调用成功","success":true,
            // "data":{"usage":{"prompt_tokens":142,"completion_tokens":63,"total_tokens":205},"created":1708761922,"model":"GLM-4","id":"8415029917331099644","choices":[{"finish_reason":"stop","index":0,"message":{"role":"assistant","content":"ChatGLM 和我都是语言模型，但我们的设计目的和应用场景不同。ChatGLM 是为聊天场景设计的，能够和人类进行流畅的对话，而我则是为各种应用场景设计的，可以用于生成文本、代码、图像等。因此，我们在不同的方面有各自的优势。"}}]}}
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }


    /**
     * 文生图
     */
    @Test
    public void testCreateImage() {
        ImageApiResponse imageApiResponse = TextCreateImage.createImage("画一个温顺可爱的小狗");
        System.out.println("imageApiResponse:" + JSON.toJSONString(imageApiResponse));
        //imageApiResponse:{"code":200,"data":{"created":1708758404,"data":[{"url":"https://sfile.chatglm.cn/testpath/954c900c-c539-519d-b0c6-dae12bfd96fd_0.png"}]},"msg":"调用成功","success":true}
    }

    /**
     * 图生文
     */
    @Test
    public void testImageToWord() {
        String question = "图里有什么";
        String imageUrl = "https://cdn.bigmodel.cn/enterpriseAc/3f328152-e15c-420c-803d-6684a9f551df.jpeg?attname=24.jpeg";
        String requestId = String.format(REQUEST_ID_TEMPLATE, System.currentTimeMillis());

        ModelApiResponse modelApiResponse = ImageCreateText.testImageToWord(requestId, imageUrl, question);
        System.out.println("model output:" + JSON.toJSONString(modelApiResponse));

    }

}
