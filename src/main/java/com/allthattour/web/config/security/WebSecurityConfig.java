package com.allthattour.web.config.security;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.CompositeSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.ConcurrentSessionControlAuthenticationStrategy;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionFixationProtectionStrategy;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.allthattour.web.config.security.model.ActiveUserStore;
import com.allthattour.web.handler.CustomLoginSuccessHandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// @Configuration
@Slf4j
@EnableWebSecurity
@RequiredArgsConstructor
// public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
@Configuration
public class WebSecurityConfig {
    private final UserDetailsService userDetailsService;

    // 정적 자원에 대해서는 Security 설정을 적용하지 않음.
    // @Override
    // public void configure(WebSecurity web) {
    // web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    // }

    // @Override
    // protected void configure(HttpSecurity http) throws Exception {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable().authorizeRequests()
                .antMatchers("/cart", "/api/service/**").hasAnyRole("USER")
                .anyRequest().permitAll()
                .and()
                .formLogin(login -> login.loginPage("/login")
                        .successForwardUrl("/index")
                        .failureForwardUrl("/login")
                        .defaultSuccessUrl("/index", false)
                        .permitAll())
                .addFilterBefore(customAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                // .sessionManagement(s -> s
                // .sessionFixation(SessionManagementConfigurer.SessionFixationConfigurer::changeSessionId)
                // .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                // .maximumSessions(1)
                // .maxSessionsPreventsLogin(false)
                // .expiredUrl("/index"))
                .authenticationProvider(customAuthenticationProvider());
        http.logout()
                .logoutUrl("/logout") // 로그아웃 처리 URL (= form action url)
                .logoutSuccessUrl("/index")
                .addLogoutHandler((request, response, authentication) -> {
                    HttpSession session = request.getSession();
                    if (session != null) {
                        session.invalidate();
                    }
                })
                .logoutSuccessHandler((request, response, authentication) -> {
                    response.sendRedirect("/index");
                })
                .deleteCookies("remember-me");
        return http.build();
    }

    @Bean
    public CompositeSessionAuthenticationStrategy concurrentSession() {
        ConcurrentSessionControlAuthenticationStrategy concurrentAuthenticationStrategy = new ConcurrentSessionControlAuthenticationStrategy(
                sessionRegistry());
        List<SessionAuthenticationStrategy> delegateStrategies = new ArrayList<SessionAuthenticationStrategy>();
        delegateStrategies.add(concurrentAuthenticationStrategy);
        delegateStrategies.add(new SessionFixationProtectionStrategy());
        delegateStrategies.add(new RegisterSessionAuthenticationStrategy(sessionRegistry()));

        return new CompositeSessionAuthenticationStrategy(delegateStrategies);
    }

    @Bean
    SessionInformationExpiredStrategy sessionInformationExpiredStrategy() {
        return new CustomSessionInformationExpiredStrategy("/index");
    }

    @Bean
    public static ServletListenerRegistrationBean httpSessionEventPublisher() {
        return new ServletListenerRegistrationBean(new HttpSessionEventPublisher());
    }

    @Bean
    public CommonsMultipartResolver multipartResolver() {
        CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver();
        commonsMultipartResolver.setDefaultEncoding("UTF-8");
        commonsMultipartResolver.setMaxUploadSize(50 * 1024 * 1024);
        return commonsMultipartResolver;
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl() {
            @Override
            public void registerNewSession(String sessionId, Object principal) {
                // TODO Auto-generated method stub
                super.registerNewSession(sessionId, principal);
                System.out.println("session created: " + sessionId);
            }
        };
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    // public AuthenticationManager
    // authenticationManager(AuthenticationConfiguration authConfig) throws
    // Exception {
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(customAuthenticationProvider());
    }

    @Bean
    public CustomAuthenticationFilter customAuthenticationFilter() throws Exception {
        CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(authenticationManager());
        customAuthenticationFilter.setFilterProcessesUrl("/user/login");
        customAuthenticationFilter.setAuthenticationSuccessHandler(customLoginSuccessHandler());
        customAuthenticationFilter.setAuthenticationFailureHandler(
                new SimpleUrlAuthenticationFailureHandler("/login?error"));
        customAuthenticationFilter.afterPropertiesSet();
        return customAuthenticationFilter;
    }

    @Bean
    public CustomLoginSuccessHandler customLoginSuccessHandler() {
        return new CustomLoginSuccessHandler();
    }

    @Bean
    public CustomAuthenticationProvider customAuthenticationProvider() {
        return new CustomAuthenticationProvider(userDetailsService, bCryptPasswordEncoder());
    }

    @Bean
    public ActiveUserStore activeUserStore() {
        return new ActiveUserStore();
    }

    // @Override
    // public void configure(AuthenticationManagerBuilder
    // authenticationManagerBuilder) {
    // authenticationManagerBuilder.authenticationProvider(customAuthenticationProvider());
    // }

}