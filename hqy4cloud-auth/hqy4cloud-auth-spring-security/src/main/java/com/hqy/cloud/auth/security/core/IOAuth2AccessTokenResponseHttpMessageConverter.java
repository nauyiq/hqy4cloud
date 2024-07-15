package com.hqy.cloud.auth.security.core;

import com.hqy.cloud.common.bind.R;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.GenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.security.oauth2.core.endpoint.DefaultMapOAuth2AccessTokenResponseConverter;
import org.springframework.security.oauth2.core.endpoint.DefaultOAuth2AccessTokenResponseMapConverter;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.util.Assert;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 自定义 /oauth2/token响应的消息转换器
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/7/15
 */
public class IOAuth2AccessTokenResponseHttpMessageConverter extends AbstractHttpMessageConverter<R<OAuth2AccessTokenResponse>> {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    private static final ParameterizedTypeReference<Map<String, Object>> STRING_OBJECT_MAP = new ParameterizedTypeReference<Map<String, Object>>() {
    };

    private GenericHttpMessageConverter<Object> jsonMessageConverter = HttpMessageConverters.getJsonMessageConverter();

    private Converter<Map<String, Object>, OAuth2AccessTokenResponse> accessTokenResponseConverter = new DefaultMapOAuth2AccessTokenResponseConverter();

    private Converter<OAuth2AccessTokenResponse, Map<String, Object>> accessTokenResponseParametersConverter = new DefaultOAuth2AccessTokenResponseMapConverter();

    public IOAuth2AccessTokenResponseHttpMessageConverter() {
        super(DEFAULT_CHARSET, MediaType.APPLICATION_JSON, new MediaType("application", "*+json"));
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return true;
    }

    @Override
    protected R<OAuth2AccessTokenResponse> readInternal(Class<? extends R<OAuth2AccessTokenResponse>> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        try {
            Map<String, Object> tokenResponseParameters = (Map<String, Object>) this.jsonMessageConverter
                    .read(STRING_OBJECT_MAP.getType(), null, inputMessage);
            OAuth2AccessTokenResponse response = this.accessTokenResponseConverter.convert(tokenResponseParameters);
            return R.ok(response);
        }
        catch (Exception ex) {
            throw new HttpMessageNotReadableException(
                    "An error occurred reading the OAuth 2.0 Access Token Response: " + ex.getMessage(), ex,
                    inputMessage);
        }
    }


    @Override
    protected void writeInternal(R<OAuth2AccessTokenResponse> tokenResponse, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        try {
            Map<String, Object> tokenResponseParameters = this.accessTokenResponseParametersConverter
                    .convert(tokenResponse.getData());
            this.jsonMessageConverter.write(R.ok(tokenResponseParameters), STRING_OBJECT_MAP.getType(),
                    MediaType.APPLICATION_JSON, outputMessage);
        }
        catch (Exception ex) {
            throw new HttpMessageNotWritableException(
                    "An error occurred writing the OAuth 2.0 Access Token Response: " + ex.getMessage(), ex);
        }
    }


    /**
     * Sets the {@link Converter} used for converting the OAuth 2.0 Access Token Response
     * parameters to an {@link OAuth2AccessTokenResponse}.
     * @param accessTokenResponseConverter the {@link Converter} used for converting to an
     * {@link OAuth2AccessTokenResponse}
     * @since 5.6
     */
    public final void setAccessTokenResponseConverter(
            Converter<Map<String, Object>, OAuth2AccessTokenResponse> accessTokenResponseConverter) {
        Assert.notNull(accessTokenResponseConverter, "accessTokenResponseConverter cannot be null");
        this.accessTokenResponseConverter = accessTokenResponseConverter;
    }

    /**
     * Sets the {@link Converter} used for converting the
     * {@link OAuth2AccessTokenResponse} to a {@code Map} representation of the OAuth 2.0
     * Access Token Response parameters.
     * @param accessTokenResponseParametersConverter the {@link Converter} used for
     * converting to a {@code Map} representation of the Access Token Response parameters
     * @since 5.6
     */
    public final void setAccessTokenResponseParametersConverter(
            Converter<OAuth2AccessTokenResponse, Map<String, Object>> accessTokenResponseParametersConverter) {
        Assert.notNull(accessTokenResponseParametersConverter, "accessTokenResponseParametersConverter cannot be null");
        this.accessTokenResponseParametersConverter = accessTokenResponseParametersConverter;
    }

}
