package com.zhipu.oapi.hill;

import com.alibaba.fastjson.JSON;
import com.zhipu.oapi.ClientV4;
import com.zhipu.oapi.service.v4.file.FileApiResponse;
import com.zhipu.oapi.service.v4.file.QueryFileApiResponse;
import com.zhipu.oapi.service.v4.file.QueryFilesRequest;
import com.zhipu.oapi.service.v4.fine_turning.*;

/**
 * 微调作业SDK
 *
 * @Author huleilei9
 * @Date 2024/2/24
 **/
public class FineTuningJobs {
    /**
     * client
     */
    public static ClientV4 client = Tools.getClient();

    /**
     * 微调上传数据集
     */
    public static FileApiResponse uploadFile(String filePath) {
//        String filePath = "/Users/wujianguo/Downloads/transaction-data.jsonl";
        String purpose = "fine-tune";
        FileApiResponse fileApiResponse = client.invokeUploadFileApi(purpose, filePath);
        System.out.println("model output:" + JSON.toJSONString(fileApiResponse));
        return fileApiResponse;
    }

    /**
     * 微调文件上传列表查询
     */
    public static QueryFileApiResponse queryUploadFileList() {
        QueryFilesRequest queryFilesRequest = new QueryFilesRequest();
        QueryFileApiResponse queryFileApiResponse = client.queryFilesApi(queryFilesRequest);
        System.out.println("model output:" + JSON.toJSONString(queryFileApiResponse));
        return queryFileApiResponse;
    }

    /**
     * 创建微调任务
     */
    public static CreateFineTuningJobApiResponse createFineTuningJob(String requestId) {
        FineTuningJobRequest request = new FineTuningJobRequest();
        request.setRequestId(requestId);
        request.setModel("chatglm3-6b");
        request.setTraining_file("file-20240118082608327-kp8qr");
        CreateFineTuningJobApiResponse createFineTuningJobApiResponse = client.createFineTuningJob(request);
        System.out.println("model output:" + JSON.toJSONString(createFineTuningJobApiResponse));
        return createFineTuningJobApiResponse;
    }

    /**
     * 查询个人微调作业
     */
    public static QueryPersonalFineTuningJobApiResponse queryPersonalFineTuningJobs() {
        QueryPersonalFineTuningJobRequest queryPersonalFineTuningJobRequest = new QueryPersonalFineTuningJobRequest();
        queryPersonalFineTuningJobRequest.setLimit(1);
        QueryPersonalFineTuningJobApiResponse queryPersonalFineTuningJobApiResponse = client.queryPersonalFineTuningJobs(queryPersonalFineTuningJobRequest);
        System.out.println("model output:" + JSON.toJSONString(queryPersonalFineTuningJobApiResponse));
        return queryPersonalFineTuningJobApiResponse;
    }

    /**
     * 微调-查询微调任务事件
     */
    public static QueryFineTuningEventApiResponse queryFineTuningJobsEvents(String jobId, Integer limit) {
        QueryFineTuningJobRequest queryFineTuningJobRequest = new QueryFineTuningJobRequest();
        queryFineTuningJobRequest.setJobId(jobId);
        queryFineTuningJobRequest.setLimit(limit);
//        queryFineTuningJobRequest.setAfter("1");
        QueryFineTuningEventApiResponse queryFineTuningEventApiResponse = client.queryFineTuningJobsEvents(queryFineTuningJobRequest);
        System.out.println("model output:" + JSON.toJSONString(queryFineTuningEventApiResponse));
        return queryFineTuningEventApiResponse;
    }

    /**
     * 查询微调任务
     */
    public static QueryFineTuningJobApiResponse retrieveFineTuningJobs(String jobId, Integer limit) {
        QueryFineTuningJobRequest queryFineTuningJobRequest = new QueryFineTuningJobRequest();
        queryFineTuningJobRequest.setJobId(jobId);
        queryFineTuningJobRequest.setLimit(limit);
//        queryFineTuningJobRequest.setAfter("1");
        QueryFineTuningJobApiResponse queryFineTuningJobApiResponse = client.retrieveFineTuningJobs(queryFineTuningJobRequest);
        System.out.println("model output:" + JSON.toJSONString(queryFineTuningJobApiResponse));
        return queryFineTuningJobApiResponse;
    }


}
