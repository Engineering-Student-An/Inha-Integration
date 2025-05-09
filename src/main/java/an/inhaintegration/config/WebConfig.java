package an.inhaintegration.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new MobileInterceptor());
    }

    @Bean
    // Spring MVC에서는 <form>에서 POST만 지원하므로 PUT, PATCH, DELETE 요청을 하려면 HiddenHttpMethodFilter가 필수
    // _method 파라미터를 읽어 실제 HTTP 메서드를 바꿔줌
    public HiddenHttpMethodFilter hiddenHttpMethodFilter() {
        return new HiddenHttpMethodFilter();
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://ec2-13-209-198-107.ap-northeast-2.compute.amazonaws.com:8082") // 프론트엔드가 실행 중인 도메인
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
    }
}
