package io.guangsoft.media.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import io.guangsoft.media.interceptor.EncodingInterceptor;
import io.guangsoft.media.utils.SpringContextHolder;
import io.guangsoft.media.utils.fastjson.FastjsonUnXssFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
@Configuration
public class WebMVCConfig implements WebMvcConfigurer {

    @Bean
    public SpringContextHolder springContextHolder() {
        return new SpringContextHolder();
    }

    /**
     * 编码拦截器
     */
    @Bean
    public HandlerInterceptor encodingInterceptor() {
        EncodingInterceptor encodingInterceptor = new EncodingInterceptor();
        return encodingInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //编码拦截器
        registry.addInterceptor(encodingInterceptor()).addPathPatterns("/**").excludePathPatterns("/files/**", "/static/**");
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setSerializerFeatures(SerializerFeature.PrettyFormat);
        FastjsonUnXssFilter jsonUnFilter = new FastjsonUnXssFilter();
        fastJsonConfig.setSerializeFilters(jsonUnFilter);
        //处理中文乱码问题
        List<MediaType> fastMediaTypes = new ArrayList<MediaType>();
        fastMediaTypes.add(MediaType.APPLICATION_JSON);
        converter.setSupportedMediaTypes(fastMediaTypes);
        converter.setFastJsonConfig(fastJsonConfig);
        converters.add(converter);
    }

    /**
     * 添加静态资源文件，外部可以直接访问地址
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //第一个方法设置访问路径前缀，第二个方法设置资源路径
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
        //添加对上传文件的直接访问
        Environment env = SpringContextHolder.getBean(Environment.class);
        String filePath = env.getProperty("file-path");
        registry.addResourceHandler("/files/**").addResourceLocations("file:" + filePath);
        registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/manage/**").addResourceLocations("classpath:/static/manage/");
    }


}