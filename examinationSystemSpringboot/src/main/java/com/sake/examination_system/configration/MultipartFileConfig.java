package com.sake.examination_system.configration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;
import javax.servlet.MultipartConfigElement;

@Configuration
public class MultipartFileConfig {
    @Value("${config.multifile.maxFileSize}")
    private Long maxFileSize;

    @Value("${config.multifile.maxRequestSize}")
    private Long maxRequestSize;

    @Bean
    public MultipartConfigElement multipartConfigElement(){
        MultipartConfigFactory factory = new MultipartConfigFactory();
        /**
         *
         * 单个数据大小
         *
         */
        factory.setMaxFileSize(DataSize.ofMegabytes(maxFileSize));
        factory.setMaxRequestSize(DataSize.ofMegabytes(maxRequestSize));
        return factory.createMultipartConfig();
    }

}
