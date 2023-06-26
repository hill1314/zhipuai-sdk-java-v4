package com.zhipu.modelapi.demo;

import com.zhipu.modelapi.ClientV3;
import com.zhipu.modelapi.Constants;
import com.zhipu.modelapi.service.v3.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class V3OkHttpClientTest {

    private static ClientV3 client = new ClientV3.Builder(TestConstants.testKeyV3, TestConstants.testSecretV3)
            .devMode(true)
            .build();
    // 请自定义自己的业务id
    private static final String requestIdTemplate = "mycompany-%d";

    public static void main(String[] args) throws Exception {

        // 1. async-invoke调用模型，再调用查询结果接口
        // 1.1 invoke model async
        //String taskId = testAsyncInvoke();
        // 1.2 query model result
        // 根据模型不同，异步调用可能需要等待约30s
        //testQueryResult(taskId, 30 * 1000);

        // 2. sse-invoke调用模型，使用标准Listener，等待finish事件完成之后直接返回结果，效果等同于同步调用（method = invoke）
        // 如果有自定义listener的需求，例如搭建sse server，在自己的客户端上实现流式展示，
        // 请自行实现com.zhipu.modelapi.service.v3.ModelEventSourceListener
        testSseInvoke();

        // 3. parallel sse invoke
        //parallelSseInvoke();

        // 4. sse-invoke english
        // testSseEnglishInvoke();

        // 5. sync invoke
        //testSyncInvoke();
    }

    private static void testSyncInvoke() {
        ModelApiRequest modelApiRequest = syncRequest();
        ModelApiResponse modelApiResp = client.invokeModelApi(modelApiRequest);
        System.out.println(String.format("call model api finished, method: %s", modelApiRequest.getInvokeMethod()));
        System.out.println(String.format("invoke api code: %d", modelApiResp.getCode()));
        String taskId = modelApiResp.getData().getTaskId();
        System.out.println(String.format("taskId: %s", taskId));
    }

    private static void parallelSseInvoke() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(5);

        for (int i = 0; i < 5; i++) {
            Thread t = new Thread(new Runnable(){
                @Override
                public void run() {
                    testSseInvoke();
                    latch.countDown();
                }
            });
            t.run();
        }

        latch.await();
        System.out.println("parallel sse invoke finished");
    }

     private static String testAsyncInvoke() {
         ModelApiRequest modelApiRequest = asyncRequest();
         ModelApiResponse modelApiResp = client.invokeModelApi(modelApiRequest);
         System.out.println(String.format("call model api finished, method: %s", modelApiRequest.getInvokeMethod()));
         System.out.println(String.format("invoke api code: %d", modelApiResp.getCode()));
         String taskId = modelApiResp.getData().getTaskId();
         System.out.println(String.format("taskId: %s", taskId));
         return taskId;
     }

     private static void testQueryResult(String taskId, long waitMillis) throws Exception{
        if (waitMillis > 0) {
            Thread.sleep(waitMillis);
        }
         QueryModelResultRequest request = queryRequest(taskId);
         QueryModelResultResponse queryResultResp = client.queryModelResult(request);
         System.out.println(String.format("call query result finished, code: %d", queryResultResp.getCode()));
         System.out.println("model output:");
         System.out.println(queryResultResp.getData().getChoices().get(0).getContent());
     }

    private static void testQueryResult() {
        String taskId = "75931252186628016897638021336447918085";
        QueryModelResultRequest request = queryRequest(taskId);
        QueryModelResultResponse queryResultResp = client.queryModelResult(request);
        System.out.println(String.format("call query result finished, code: %d", queryResultResp.getCode()));
        System.out.println("model output:");
        System.out.println(queryResultResp.getData().getChoices().get(0).getContent());
    }

     private static void testSseInvoke() {
         ModelApiRequest sseModelApiRequest = sseRequest();
         ModelApiResponse sseModelApiResp = client.invokeModelApi(sseModelApiRequest);
         System.out.println(String.format("call model api finished, method: %s", sseModelApiRequest.getInvokeMethod()));
         System.out.println(String.format("invoke api code: %d", sseModelApiResp.getCode()));
         System.out.println("model output:");
         System.out.println(sseModelApiResp.getData().getChoices().get(0).getContent());
     }

    private static void testSseEnglishInvoke() {
        ModelApiRequest sseModelApiRequest = sseEnglishRequest();
        ModelApiResponse sseModelApiResp = client.invokeModelApi(sseModelApiRequest);
        System.out.println(String.format("call model api finished, method: %s", sseModelApiRequest.getInvokeMethod()));
        System.out.println(String.format("invoke api code: %d", sseModelApiResp.getCode()));
        System.out.println("model output:");
        System.out.println(sseModelApiResp.getData().getChoices().get(0).getContent());
    }

    private static ModelApiRequest asyncRequest() {
        ModelApiRequest modelApiRequest = new ModelApiRequest();
        modelApiRequest.setModelId(Constants.ModelChatGLM6B);
        modelApiRequest.setInvokeMethod(Constants.invokeMethodAsync);
        ModelApiRequest.Prompt prompt = new ModelApiRequest.Prompt(ModelConstants.roleUser, "ChatGPT和你哪个更强大");
        List<ModelApiRequest.Prompt> prompts = new ArrayList<>();
        prompts.add(prompt);
        modelApiRequest.setPrompt(prompts);
        // 自定义业务id，需保证唯一性
        String requestId = String.format(requestIdTemplate, System.currentTimeMillis());
        modelApiRequest.setRequestId(requestId);
        return modelApiRequest;
    }

    private static ModelApiRequest syncRequest() {
        ModelApiRequest modelApiRequest = new ModelApiRequest();
        modelApiRequest.setModelId(Constants.ModelChatGLM6B);
        modelApiRequest.setInvokeMethod(Constants.invokeMethodSync);
        ModelApiRequest.Prompt prompt = new ModelApiRequest.Prompt(ModelConstants.roleUser, "ChatGPT和你哪个更强大");
        List<ModelApiRequest.Prompt> prompts = new ArrayList<>();
        prompts.add(prompt);
        modelApiRequest.setPrompt(prompts);
        // 自定义业务id，需保证唯一性
        String requestId = String.format(requestIdTemplate, System.currentTimeMillis());
        modelApiRequest.setRequestId(requestId);
        return modelApiRequest;
    }

    private static ModelApiRequest sseEnglishRequest() {
        ModelApiRequest modelApiRequest = new ModelApiRequest();
        modelApiRequest.setModelId(Constants.ModelChatGLM6B);
        modelApiRequest.setInvokeMethod(Constants.invokeMethodSse);

        // 可自定义sse listener
        StandardEventSourceListener listener = new StandardEventSourceListener();
        listener.setIncremental(false);
        modelApiRequest.setSseListener(listener);
        modelApiRequest.setIncremental(false);

        ModelApiRequest.Prompt prompt = new ModelApiRequest.Prompt(ModelConstants.roleUser, "tell me something about C Ronaldo in English");
        List<ModelApiRequest.Prompt> prompts = new ArrayList<>();
        prompts.add(prompt);
        modelApiRequest.setPrompt(prompts);
        String requestId = String.format(requestIdTemplate, System.currentTimeMillis());
        modelApiRequest.setRequestId(requestId);
        return modelApiRequest;
    }
    private static QueryModelResultRequest queryRequest(String taskId) {
        QueryModelResultRequest queryModelResultRequest = new QueryModelResultRequest();
        queryModelResultRequest.setTaskId(taskId);
        return queryModelResultRequest;
    }

    private static ModelApiRequest sseRequest() {
        ModelApiRequest modelApiRequest = new ModelApiRequest();
        modelApiRequest.setModelId(Constants.ModelChatGLM6B);
        modelApiRequest.setInvokeMethod(Constants.invokeMethodSse);
        //modelApiRequest.setIncremental(false);
        // 可自定义sse listener
        StandardEventSourceListener listener = new StandardEventSourceListener();
        modelApiRequest.setIncremental(true);
        listener.setIncremental(true);
        modelApiRequest.setSseListener(listener);
        //String content = "ChatGPT和你哪个更强大";
        String content = "tell me something about C Ronaldo";
        ModelApiRequest.Prompt prompt = new ModelApiRequest.Prompt(ModelConstants.roleUser, content);
        List<ModelApiRequest.Prompt> prompts = new ArrayList<>();
        prompts.add(prompt);
        modelApiRequest.setPrompt(prompts);
        String requestId = String.format(requestIdTemplate, System.currentTimeMillis());
        modelApiRequest.setRequestId(requestId);
        System.out.println("request id: " + requestId);
        return modelApiRequest;
    }
}
