package com.jeequan.jeepay.mgr.config;

import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;

@Configuration
public class RestTemplateConfig {

    private class CustomErrorHandler implements ResponseErrorHandler {

        Logger logger = LoggerFactory.getLogger(this.getClass());

        /**
         * 表示 response 是否存在任何错误。实现类通常会检查 response 的 HttpStatus。
         *
         * @param response
         * @return
         * @throws IOException
         */
        @Override
        public boolean hasError(ClientHttpResponse response) throws IOException {
            int rawStatusCode = response.getRawStatusCode();
            HttpStatus statusCode = HttpStatus.resolve(rawStatusCode);
            return (statusCode != null ? statusCode.isError() : hasError(rawStatusCode));
        }

        protected boolean hasError(int unknownStatusCode) {
            HttpStatus.Series series = HttpStatus.Series.resolve(unknownStatusCode);
            return (series == HttpStatus.Series.CLIENT_ERROR || series == HttpStatus.Series.SERVER_ERROR);
        }

        /**
         * 处理 response 中的错误, 当 hasError 返回 true 时才调用此方法。
         * 当返回异常信息时自己想要做的一些操作处理
         *
         * @param response
         * @throws IOException
         */
        @Override
        public void handleError(ClientHttpResponse response) throws IOException {
        }

        /**
         * 覆盖了上面的方法
         * 处理 response 中的错误, 当 hasError 返回 true 时才调用此方法。
         * 当返回异常信息时自己想要做的一些操作处理
         *
         * @param url
         * @param method
         * @param response
         * @throws IOException
         */
        @Override
        public void handleError(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
            logger.error("=======================ERROR============================");
            logger.error("HOST:{},URI：{}", url.getHost(), url.getPath());
            logger.error("Method Type：{}", method.name());
            logger.error("Exception：{}", response.getStatusCode());
            logger.error("========================================================");
        }
    }

    @Bean
    @ConditionalOnMissingBean(RestTemplate.class)
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory requestFactory =
                new SimpleClientHttpRequestFactory();
        // 设置连接超时，单位毫秒
        requestFactory.setConnectTimeout(30000);
        //设置读取超时
        requestFactory.setReadTimeout(30000);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new CustomErrorHandler());
        restTemplate.setRequestFactory(requestFactory);
        restTemplate.getMessageConverters().add(
                new StringHttpMessageConverter(StandardCharsets.UTF_8)
        );
        restTemplate.getMessageConverters().add(
                new FastJsonHttpMessageConverter()
        );
        return restTemplate;
    }

}
