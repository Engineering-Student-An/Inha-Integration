package an.inhaintegration.config;

import an.inhaintegration.domain.StudentRole;
import an.inhaintegration.service.CustomOauth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOauth2UserService customOAuth2UserService;
    private final CustomOAuth2AuthenticationSuccessHandler customOAuth2AuthenticationSuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/rental/**", "/board/new", "/board/*/like", "/board/*/reply/new",
                                "/reply/*/like", "/proposal/**", "/changeInfo", "/board/myList", "/item/request/**").authenticated()
                        .requestMatchers("/admin/**").hasRole(StudentRole.ADMIN.name())
                        .anyRequest().permitAll()
                );

        // 폼 로그인 설정
        http.formLogin((auth) -> auth.loginPage("/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("loginId")
                        .passwordParameter("password")
                        .permitAll()
                        .successHandler(new CustomAuthenticationSuccessHandler())
                        .failureHandler(new CustomAuthenticationFailureHandler()) // 커스텀 실패 핸들러 설정

                );

        // Oauth2 로그인 설정
        http.oauth2Login(oauth -> oauth
                        // 커스텀 로그인 페이지 경로 설정
                        .loginPage("/login")
                        // OAuth2 로그인 요청(인가 요청)을 저장할 위치 설정 (세션 기반)
                        .authorizationEndpoint(authorizationEndpoint ->
                                authorizationEndpoint.authorizationRequestRepository(
                                        new HttpSessionOAuth2AuthorizationRequestRepository()
                                )
                        )
                        // 서버가 인증 리다이렉트 결과를 받을 엔드포인트 설정
                        .redirectionEndpoint(redirectionEndpoint ->
                                        redirectionEndpoint.baseUri("/login/oauth2/code/*"))
                        // 로그인 성공 핸들러
                        .successHandler(customOAuth2AuthenticationSuccessHandler)
                        // UserInfo 엔드포인트 설정 ( = "AT을 이용해 유저 프로필을 가져오고, 우리 서비스에 맞게 변환하는 곳)
                        .userInfoEndpoint(userInfoEndpoint ->
                                userInfoEndpoint.userService(customOAuth2UserService))

                );

        // Remember-Me : 자동로그인
        http.rememberMe(rememberMe -> rememberMe
                        .key("anchangmin")
                        .rememberMeParameter("remember-me")
                        .tokenValiditySeconds(60*60*24*30)
                        .authenticationSuccessHandler(new CustomAuthenticationSuccessHandler())
                );

        // 로그아웃 설정
        http.logout((auth) -> auth
                .logoutUrl("/oauth-login/logout")
                .logoutSuccessUrl("/login?logout") // 리다이렉트
//                .deleteCookies("JSESSIONID", "remember-me") // 쿠키 삭제
                .invalidateHttpSession(true) // 세션 무효화
                .clearAuthentication(true) // 인증정보 삭제
        );

        // csrf 설정
        http.csrf((auth) -> auth.disable());

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){

        return new BCryptPasswordEncoder();
    }
}
