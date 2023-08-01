package com.hqy.cloud.chatgpt.core.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hqy.cloud.chatgpt.common.dto.UnofficialApiChatChunk;
import com.theokanning.openai.OpenAiError;
import com.theokanning.openai.OpenAiHttpException;
import com.theokanning.openai.service.OpenAiService;
import io.reactivex.FlowableEmitter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ResponseBody;
import org.jetbrains.annotations.NotNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/7/27 16:12
 */
@Slf4j
public class UnofficialApiResponseBodyCallback implements Callback<ResponseBody> {
    private static final ObjectMapper mapper = OpenAiService.defaultObjectMapper();
    private final FlowableEmitter<UnofficialApiChatChunk> emitter;
    private boolean done;

    public UnofficialApiResponseBodyCallback(FlowableEmitter<UnofficialApiChatChunk> emitter, boolean done) {
        this.emitter = emitter;
        this.done = done;
    }

    @Override
    public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
        BufferedReader reader = null;
        try {
            if (!response.isSuccessful()) {
                HttpException e = new HttpException(response);
                ResponseBody errorBody = response.errorBody();
                if (errorBody == null) {
                    throw e;
                } else {
                    OpenAiError error = mapper.readValue(
                            errorBody.string(),
                            OpenAiError.class
                    );
                    throw new OpenAiHttpException(error, e, e.code());
                }
            }

            ResponseBody body = response.body();
            if (body != null) {
                InputStream inputStream = body.byteStream();
                reader = new BufferedReader(new InputStreamReader(inputStream));

            }


        } catch (Throwable cause) {

        } finally {

        }



    }

    @Override
    public void onFailure(Call<ResponseBody> call, Throwable t) {

    }
}
