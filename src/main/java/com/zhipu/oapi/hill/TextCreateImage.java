package com.zhipu.oapi.hill;

import com.zhipu.oapi.Constants;
import com.zhipu.oapi.service.v4.image.CreateImageRequest;
import com.zhipu.oapi.service.v4.image.ImageApiResponse;

/**
 * 文本创建图像
 *
 * @Author huleilei9
 * @Date 2024/2/24
 **/
public class TextCreateImage {

    /**
     * 创建图像
     *
     * @param prompt 提示
     * @return {@link ImageApiResponse}
     */
    public static ImageApiResponse createImage(String prompt) {
        CreateImageRequest createImageRequest = new CreateImageRequest();
        createImageRequest.setModel(Constants.ModelCogView);
        createImageRequest.setPrompt(prompt);
        ImageApiResponse imageApiResponse = Tools.getClient().createImage(createImageRequest);
        return imageApiResponse;
    }
}
