package com.zhipu.oapi;


import com.alibaba.fastjson.JSON;
import com.zhipu.oapi.service.v4.embedding.EmbeddingApiResponse;
import com.zhipu.oapi.service.v4.embedding.EmbeddingRequest;
import com.zhipu.oapi.service.v4.file.FileApiResponse;
import com.zhipu.oapi.service.v4.fine_turning.*;
import com.zhipu.oapi.service.v4.image.CreateImageRequest;
import com.zhipu.oapi.service.v4.image.ImageApiResponse;
import com.zhipu.oapi.service.v4.model.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class V4Test {

    private static final ClientV4 client = new ClientV4.Builder(Constants.onlineKeyV3, Constants.onlineSecretV3).build();

    // 请自定义自己的业务id
    private static final String requestIdTemplate = "mycompany-%d";


    /**
     * sse-V4：function调用
     */
    @Test
    public void testFunctionSSE() {
        List<ChatMessage> messages = new ArrayList<>();
        ChatMessage chatMessage = new ChatMessage(ChatMessageRole.USER.value(), "ChatGPT和你哪个更强大");
        messages.add(chatMessage);
        String requestId = String.format(requestIdTemplate, System.currentTimeMillis());
        // 函数调用参数构建部分
        List<ChatTool> chatToolList = new ArrayList<>();
        ChatTool chatTool = new ChatTool();
        chatTool.setType(ChatToolType.FUNCTION.value());
        ChatFunctionParameters chatFunctionParameters = new ChatFunctionParameters();
        chatFunctionParameters.setType("object");
        Map<String,Object> properties = new HashMap<>();
        properties.put("location",new HashMap<String,Object>(){{
            put("type","string");
            put("description","城市，如：北京");
        }});
        properties.put("unit",new HashMap<String,Object>(){{
            put("type","string");
            put("enum",new ArrayList<String>(){{add("celsius");add("fahrenheit");}});
        }});
        chatFunctionParameters.setProperties(properties);
        ChatFunction chatFunction = ChatFunction.builder()
                .name("get_weather")
                .description("Get the current weather of a location")
                .parameters(chatFunctionParameters)
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
        ModelApiResponse sseModelApiResp = client.invokeModelApi(chatCompletionRequest);
        System.out.println("model output:"+ JSON.toJSONString(sseModelApiResp));
    }


    /**
     * sse-V4：非function调用
     */
    @Test
    public void testNonFunctionSSE() {
        List<ChatMessage> messages = new ArrayList<>();
        ChatMessage chatMessage = new ChatMessage(ChatMessageRole.USER.value(), "ChatGPT和你哪个更强大");
        messages.add(chatMessage);
        String requestId = String.format(requestIdTemplate, System.currentTimeMillis());
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(Constants.ModelChatGLM4)
                .stream(Boolean.TRUE)
                .messages(messages)
                .requestId(requestId)
                .build();
        ModelApiResponse sseModelApiResp = client.invokeModelApi(chatCompletionRequest);
        System.out.println("model output:"+ JSON.toJSONString(sseModelApiResp));
    }


    /**
     * V4-同步function调用
     */
    @Test
    public void testFunctionInvoke(){
        List<ChatMessage> messages = new ArrayList<>();
        ChatMessage chatMessage = new ChatMessage(ChatMessageRole.USER.value(), "ChatGPT和你哪个更强大");
        messages.add(chatMessage);
        String requestId = String.format(requestIdTemplate, System.currentTimeMillis());
        // 函数调用参数构建部分
        List<ChatTool> chatToolList = new ArrayList<>();
        ChatTool chatTool = new ChatTool();
        chatTool.setType(ChatToolType.FUNCTION.value());
        ChatFunctionParameters chatFunctionParameters = new ChatFunctionParameters();
        chatFunctionParameters.setType("object");
        Map<String,Object> properties = new HashMap<>();
        properties.put("location",new HashMap<String,Object>(){{
            put("type","string");
            put("description","城市，如：北京");
        }});
        properties.put("unit",new HashMap<String,Object>(){{
            put("type","string");
            put("enum",new ArrayList<String>(){{add("celsius");add("fahrenheit");}});
        }});
        chatFunctionParameters.setProperties(properties);
        ChatFunction chatFunction = ChatFunction.builder()
                .name("get_weather")
                .description("Get the current weather of a location")
                .parameters(chatFunctionParameters)
                .build();
        chatTool.setFunction(chatFunction);
        chatToolList.add(chatTool);
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(Constants.ModelChatGLM4)
                .stream(Boolean.FALSE)
                .invokeMethod(Constants.invokeMethod)
                .messages(messages)
                .requestId(requestId)
                .tools(chatToolList)
                .toolChoice("auto")
                .build();
        ModelApiResponse invokeModelApiResp = client.invokeModelApi(chatCompletionRequest);
        System.out.println("model output:"+ JSON.toJSONString(invokeModelApiResp));
    }



    /**
     * V4-同步非function调用
     */
    @Test
    public void testNonFunctionInvoke(){
        List<ChatMessage> messages = new ArrayList<>();
        ChatMessage chatMessage = new ChatMessage(ChatMessageRole.USER.value(), "ChatGPT和你哪个更强大");
        messages.add(chatMessage);
        String requestId = String.format(requestIdTemplate, System.currentTimeMillis());
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(Constants.ModelChatGLM4)
                .stream(Boolean.FALSE)
                .invokeMethod(Constants.invokeMethod)
                .messages(messages)
                .requestId(requestId)
                .build();
        ModelApiResponse invokeModelApiResp = client.invokeModelApi(chatCompletionRequest);
        System.out.println("model output:"+ JSON.toJSONString(invokeModelApiResp));
    }

    /**
     * V4异步调用
     */
    @Test
    public void testAsyncInvoke(){
        String taskId = getAsyncTaskId();
        testQueryResult(taskId);
    }


    /**
     * 文生图
     */
    @Test
    public void testCreateImage() {
        CreateImageRequest createImageRequest = new CreateImageRequest();
        createImageRequest.setModel(Constants.ModelCogView);
        createImageRequest.setPrompt("画一个温顺可爱的小狗");
        ImageApiResponse imageApiResponse = client.createImage(createImageRequest);
        System.out.println("imageApiResponse:"+JSON.toJSONString(imageApiResponse));
    }


    /**
     * 图生文
     */
    @Test
    public void testImageToWord(){
        List<ChatMessage> messages = new ArrayList<>();
        List<Map<String,Object>> contentList = new ArrayList<>();
        Map<String,Object> textMap = new HashMap<>();
        textMap.put("type","text");
        textMap.put("text","图里有什么");
        Map<String,Object> typeMap = new HashMap<>();
        typeMap.put("type","image_url");
        Map<String,Object> urlMap = new HashMap<>();
        urlMap.put("url","https://cdn.bigmodel.cn/enterpriseAc/3f328152-e15c-420c-803d-6684a9f551df.jpeg?attname=24.jpeg");
        typeMap.put("image_url",urlMap);
        contentList.add(textMap);
        contentList.add(typeMap);
        ChatMessage chatMessage = new ChatMessage(ChatMessageRole.USER.value(), contentList);
        messages.add(chatMessage);
        String requestId = String.format(requestIdTemplate, System.currentTimeMillis());


        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(Constants.ModelChatGLM4V)
                .stream(Boolean.FALSE)
                .invokeMethod(Constants.invokeMethod)
                .messages(messages)
                .requestId(requestId)
                .build();
        ModelApiResponse modelApiResponse = client.invokeModelApi(chatCompletionRequest);
        System.out.println("model output:"+ JSON.toJSONString(modelApiResponse));
    }


    /**
     * 向量模型V4
     */
    @Test
    public void testEmbeddings(){
        EmbeddingRequest embeddingRequest = new EmbeddingRequest();
        embeddingRequest.setInput("hello world");
        embeddingRequest.setModel(Constants.ModelEmbedding2);
        EmbeddingApiResponse apiResponse = client.invokeEmbeddingsApi(embeddingRequest);
        System.out.println("model output:"+JSON.toJSONString(apiResponse));
    }


    /**
     * V4微调上传数据集
     */
    @Test
    public void testUploadFile(){
        String filePath = "/Users/wujianguo/Downloads/transaction-data.jsonl";
        String purpose = "fine-tune";
        FileApiResponse fileApiResponse = client.invokeUploadFileApi(purpose,filePath);
        System.out.println("model output:"+JSON.toJSONString(fileApiResponse));
    }


    /**
     * 微调V4-查询上传文件列表
     */
    @Test
    public void testQueryUploadFileList(){
        String filePath = "/Users/wujianguo/Downloads/transaction-data.jsonl";
        String purpose = "fine-tune";
        FileApiResponse fileApiResponse = client.invokeUploadFileApi(purpose,filePath);
        System.out.println("model output:"+JSON.toJSONString(fileApiResponse));
    }


    /**
     * 微调V4-创建微调任务
     */
    @Test
    public void testCreateFineTuningJob(){
        FineTuningJobRequest request = new FineTuningJobRequest();
        String requestId = String.format(requestIdTemplate, System.currentTimeMillis());
        request.setRequestId(requestId);
        request.setModel("chatglm3-6b");
        request.setTraining_file("file-20240118082608327-kp8qr");
        CreateFineTuningJobApiResponse createFineTuningJobApiResponse = client.createFineTuningJob(request);
        System.out.println("model output:" + JSON.toJSONString(createFineTuningJobApiResponse));
    }


    /**
     * 微调V4-查询微调任务
     */
    @Test
    public void testRetrieveFineTuningJobs(){
        String fineTuningJobId  = "ftjob-20240119114544390-zkgjb";
        QueryFineTuningJobApiResponse queryFineTuningJobApiResponse = client.retrieveFineTuningJobs(fineTuningJobId);
        System.out.println("model output:"+JSON.toJSONString(queryFineTuningJobApiResponse));
    }


    /**
     * testQueryPersonalFineTuningJobs V4-查询个人微调任务
     */
     @Test
     public void testQueryPersonalFineTuningJobs(){
         QueryPersonalFineTuningJobRequest queryPersonalFineTuningJobRequest = new QueryPersonalFineTuningJobRequest();
         queryPersonalFineTuningJobRequest.setLimit(1);
         QueryPersonalFineTuningJobApiResponse queryPersonalFineTuningJobApiResponse = client.queryPersonalFineTuningJobs(queryPersonalFineTuningJobRequest);
         System.out.println("model output:"+JSON.toJSONString(queryPersonalFineTuningJobApiResponse));
     }


    private static String getAsyncTaskId() {
        List<ChatMessage> messages = new ArrayList<>();
        ChatMessage chatMessage = new ChatMessage(ChatMessageRole.USER.value(), "ChatGPT和你哪个更强大");
        messages.add(chatMessage);
        String requestId = String.format(requestIdTemplate, System.currentTimeMillis());
        // 函数调用参数构建部分
        List<ChatTool> chatToolList = new ArrayList<>();
        ChatTool chatTool = new ChatTool();
        chatTool.setType(ChatToolType.FUNCTION.value());
        ChatFunctionParameters chatFunctionParameters = new ChatFunctionParameters();
        chatFunctionParameters.setType("object");
        Map<String,Object> properties = new HashMap<>();
        properties.put("location",new HashMap<String,Object>(){{
            put("type","string");
            put("description","城市，如：北京");
        }});
        properties.put("unit",new HashMap<String,Object>(){{
            put("type","string");
            put("enum",new ArrayList<String>(){{add("celsius");add("fahrenheit");}});
        }});
        chatFunctionParameters.setProperties(properties);
        ChatFunction chatFunction = ChatFunction.builder()
                .name("get_weather")
                .description("Get the current weather of a location")
                .parameters(chatFunctionParameters)
                .build();
        chatTool.setFunction(chatFunction);
        chatToolList.add(chatTool);
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(Constants.ModelChatGLM4)
                .stream(Boolean.FALSE)
                .invokeMethod(Constants.invokeMethodAsync)
                .messages(messages)
                .requestId(requestId)
                .tools(chatToolList)
                .toolChoice("auto")
                .build();
        ModelApiResponse invokeModelApiResp = client.invokeModelApi(chatCompletionRequest);
        System.out.println("model output:"+ JSON.toJSONString(invokeModelApiResp));
        return invokeModelApiResp.getData().getTaskId();
    }


    private static void testQueryResult(String taskId) {
        QueryModelResultRequest request = new QueryModelResultRequest();
        request.setTaskId(taskId);
        QueryModelResultResponse queryResultResp = client.queryModelResult(request);
        System.out.println("model output:"+JSON.toJSONString(queryResultResp));
    }

}