package com.hqy.gateway.server.support;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import com.hqy.gateway.server.AbstractCodeServer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Optional;

/**
 * 验证码生成逻辑
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/6 18:38
 */
@Slf4j
@RequiredArgsConstructor
public class ImageCodeServer extends AbstractCodeServer implements HandlerFunction<ServerResponse> {

    private static final Integer DEFAULT_IMAGE_WIDTH = 100;
    private static final Integer DEFAULT_IMAGE_HEIGHT = 40;

    @Override
    public Mono<ServerResponse> handle(ServerRequest request) {
        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(DEFAULT_IMAGE_WIDTH, DEFAULT_IMAGE_HEIGHT);
        String code = lineCaptcha.getCode();

        //save code.
        Optional<String> randomStr = request.queryParam(RANDOM_KEY);
        randomStr.ifPresent(s -> saveCode(s, code));

        return ServerResponse.status(HttpStatus.OK).contentType(MediaType.IMAGE_JPEG)
                .body(BodyInserters.fromResource(new ByteArrayResource(lineCaptcha.getImageBytes())));
    }
}
