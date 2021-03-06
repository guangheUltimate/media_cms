package io.guangsoft.media.config;

import io.guangsoft.media.shiro.cache.SpringCacheManagerWrapper;
import io.guangsoft.media.shiro.credential.RetryLimitHashedCredentialsMatcher;
import io.guangsoft.media.shiro.filter.ShiroFilterFactoryBean;
import io.guangsoft.media.shiro.filter.authc.FormAuthenticationFilter;
import io.guangsoft.media.shiro.filter.jcaptcha.JCaptchaValidateFilter;
import io.guangsoft.media.shiro.filter.online.OnlineSessionFilter;
import io.guangsoft.media.shiro.filter.user.SysUserFilter;
import io.guangsoft.media.shiro.realm.UserRealm;
import io.guangsoft.media.shiro.session.CacheSessionDAO;
import io.guangsoft.media.shiro.session.OnlineSessionFactory;
import io.guangsoft.media.shiro.session.SessionDAO;
import io.guangsoft.media.shiro.session.SessionManager;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.ExecutorServiceSessionValidationScheduler;
import org.apache.shiro.session.mgt.eis.JavaUuidSessionIdGenerator;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.web.filter.authc.LogoutFilter;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.DelegatingFilterProxy;

import javax.servlet.Filter;
import java.util.Map;

@Configuration
@EnableConfigurationProperties({ShiroConfigProperties.class})
public class ShiroConfig {

    @Autowired
    private ShiroConfigProperties shiroConfigProperties;

    @Bean
    public SpringCacheManagerWrapper shiroCacheManager(EhCacheCacheManager ehCacheCacheManager) {
        SpringCacheManagerWrapper shiroCacheManager = new SpringCacheManagerWrapper();
        shiroCacheManager.setCacheManager(ehCacheCacheManager);
        return shiroCacheManager;
    }

    @Bean
    public SessionDAO sessionDAO(JavaUuidSessionIdGenerator sessionIdGenerator) {
        CacheSessionDAO sessionDAO = new CacheSessionDAO();
        sessionDAO.setSessionIdGenerator(sessionIdGenerator);
        return sessionDAO;
    }

    @Bean
    public RetryLimitHashedCredentialsMatcher credentialsMatcher(CacheManager shiroCacheManager) {
        RetryLimitHashedCredentialsMatcher RetryLimitHashedCredentialsMatcher = new RetryLimitHashedCredentialsMatcher(shiroCacheManager);
        RetryLimitHashedCredentialsMatcher.setMaxRetryCount(shiroConfigProperties.getMaxRetryCount());
        RetryLimitHashedCredentialsMatcher.setShowCaptchaRetryCount(shiroConfigProperties.getShowCaptchaRetryCount());
        RetryLimitHashedCredentialsMatcher.setHashAlgorithmName(shiroConfigProperties.getCredentialsHashAlgorithmName());
        RetryLimitHashedCredentialsMatcher.setHashIterations(shiroConfigProperties.getCredentialsHashIterations());
        RetryLimitHashedCredentialsMatcher.setStoredCredentialsHexEncoded(shiroConfigProperties.getCredentialsStoredCredentialsHexEncoded());
        return RetryLimitHashedCredentialsMatcher;
    }

    @Bean
    public UserRealm userRealm(RetryLimitHashedCredentialsMatcher credentialsMatcher) {
        UserRealm userRealm = new UserRealm();
        userRealm.setCredentialsMatcher(credentialsMatcher);
        userRealm.setAuthenticationCachingEnabled(Boolean.FALSE);
        userRealm.setAuthorizationCachingEnabled(Boolean.FALSE);
        return userRealm;
    }

    @Bean
    public JavaUuidSessionIdGenerator sessionIdGenerator() {
        JavaUuidSessionIdGenerator sessionIdGenerator = new JavaUuidSessionIdGenerator();
        return sessionIdGenerator;
    }

    /**
     * session的cookie
     */
    public SimpleCookie sessionIdCookie() {
        SimpleCookie simpleCookie = new SimpleCookie(shiroConfigProperties.getSessionIdCookieName());
        simpleCookie.setDomain(shiroConfigProperties.getSessionIdCookieDomain());
        simpleCookie.setPath(shiroConfigProperties.getSessionIdCookiePath());
        simpleCookie.setHttpOnly(shiroConfigProperties.getSessionIdCookieHttpOnly());
        simpleCookie.setMaxAge(shiroConfigProperties.getSessionIdCookieMaxAge());
        return simpleCookie;
    }


    /**
     * 记住我的cookie
     */
    public SimpleCookie rememberMeCookie() {
        SimpleCookie simpleCookie = new SimpleCookie(shiroConfigProperties.getRememeberMeCookieName());
        simpleCookie.setDomain(shiroConfigProperties.getRememeberMeCookieDomain());
        simpleCookie.setPath(shiroConfigProperties.getRememeberMeCookiePath());
        simpleCookie.setHttpOnly(shiroConfigProperties.getRememeberMeCookieHttpOnly());
        simpleCookie.setMaxAge(shiroConfigProperties.getRememeberMeCookieMaxAge());
        return simpleCookie;
    }

    @Bean
    public OnlineSessionFactory onlineSessionFactory() {
        OnlineSessionFactory onlineSessionFactory = new OnlineSessionFactory();
        return onlineSessionFactory;
    }

    @Bean
    public CookieRememberMeManager rememberMeManager() {
        CookieRememberMeManager rememberMeManager = new CookieRememberMeManager();
        byte[] cipherKey = org.apache.shiro.codec.Base64.decode(shiroConfigProperties.getRememeberMeCookieBase64CipherKey());
        rememberMeManager.setCipherKey(cipherKey);
        rememberMeManager.setCookie(rememberMeCookie());
        return rememberMeManager;
    }

    @Bean
    public SessionManager sessionManager(OnlineSessionFactory onlineSessionFactory, SessionDAO sessionDAO, CacheManager shiroCacheManager) {
        SessionManager sessionManager = new SessionManager();
        sessionManager.setGlobalSessionTimeout(shiroConfigProperties.getSessionGlobalSessionTimeout());
        sessionManager.setSessionFactory(onlineSessionFactory);
        sessionManager.setSessionDAO(sessionDAO);
        sessionManager.setDeleteInvalidSessions(false);
        sessionManager.setSessionValidationInterval(shiroConfigProperties.getSessionValidationInterval());
        sessionManager.setSessionValidationSchedulerEnabled(true);
        sessionManager.setCacheManager(shiroCacheManager);
        sessionManager.setSessionIdCookieEnabled(true);
        sessionManager.setSessionIdCookie(sessionIdCookie());
        return sessionManager;
    }

    /**
     * 会话验证调度
     */
    @Bean
    public ExecutorServiceSessionValidationScheduler sessionValidationScheduler(SessionManager sessionManager) {
        ExecutorServiceSessionValidationScheduler sessionValidationScheduler = new ExecutorServiceSessionValidationScheduler();
        sessionValidationScheduler.setInterval(shiroConfigProperties.getSessionValidationInterval());
        sessionValidationScheduler.setSessionManager(sessionManager);
        return sessionValidationScheduler;
    }

    @Bean
    public DefaultWebSecurityManager securityManager(SessionManager sessionManager, UserRealm userRealm, CookieRememberMeManager rememberMeManager) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setSessionManager(sessionManager);
        securityManager.setRealm(userRealm);
        securityManager.setRememberMeManager(rememberMeManager);
        return securityManager;
    }

    /**
     * 替换默认的form 验证过滤器
     */
    private FormAuthenticationFilter formAuthenticationFilter() {
        FormAuthenticationFilter formAuthenticationFilter = new FormAuthenticationFilter();
        formAuthenticationFilter.setSuccessUrl(shiroConfigProperties.getDefaultSuccessUrl());
        formAuthenticationFilter.setUsernameParam("username");
        formAuthenticationFilter.setPasswordParam("password");
        formAuthenticationFilter.setRememberMeParam("rememberMe");
        return formAuthenticationFilter;
    }

    /**
     * 退出登录过滤器
     */
    private LogoutFilter logoutFilter() {
        LogoutFilter logoutFilter = new LogoutFilter();
        logoutFilter.setRedirectUrl(shiroConfigProperties.getLogoutSuccessUrl());
        return logoutFilter;
    }

    /**
     * 提取系统用户（User） 验证用户是否合法的过滤器（如是否删除了 是否锁定了）
     */
    private SysUserFilter sysUserFilter() {
        SysUserFilter sysUserFilter = new SysUserFilter();
        sysUserFilter.setUserLockedUrl(shiroConfigProperties.getUserLockedUrl());
        sysUserFilter.setUserNotfoundUrl(shiroConfigProperties.getUserNotfoundUrl());
        sysUserFilter.setUserUnknownErrorUrl(shiroConfigProperties.getUserUnknownErrorUrl());
        return sysUserFilter;
    }

    /**
     * 提取系统用户（User） 验证用户是否合法的过滤器（如是否删除了 是否锁定了）
     */
    private OnlineSessionFilter onlineSessionFilter(SessionDAO sessionDAO) {
        OnlineSessionFilter onlineSessionFilter = new OnlineSessionFilter();
        onlineSessionFilter.setForceLogoutUrl(shiroConfigProperties.getUserForceLogoutUrl());
        onlineSessionFilter.setSessionDAO(sessionDAO);
        return onlineSessionFilter;
    }

    /**
     * 验证码
     */
    @Bean
    public JCaptchaValidateFilter jCaptchaValidateFilter(SessionDAO sessionDAO) {
        JCaptchaValidateFilter jCaptchaValidateFilter = new JCaptchaValidateFilter();
        jCaptchaValidateFilter.setJcaptchaParam("jcaptchaCode");
        jCaptchaValidateFilter.setJcaptchaEbabled(shiroConfigProperties.getJcaptchaEnable());
        jCaptchaValidateFilter.setJcapatchaErrorUrl(shiroConfigProperties.getJcaptchaErrorUrl());
        return jCaptchaValidateFilter;
    }

    @Bean
    public ShiroFilterFactoryBean shiroFilter(SessionDAO sessionDAO, DefaultWebSecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        shiroFilterFactoryBean.setLoginUrl(shiroConfigProperties.getLoginUrl());
        shiroFilterFactoryBean.setUnauthorizedUrl(shiroConfigProperties.getUnauthorizedUrl());
        Map<String, Filter> filters = shiroFilterFactoryBean.getFilters();
        filters.put("jCaptchaValidate", jCaptchaValidateFilter(sessionDAO));
        filters.put("authc", formAuthenticationFilter());
        filters.put("logout", logoutFilter());
        filters.put("onlineSession", onlineSessionFilter(sessionDAO));
        filters.put("sysUser", sysUserFilter());
        shiroFilterFactoryBean.setFilterChainDefinitionsStr(shiroConfigProperties.getFilterChainDefinitions());
        return shiroFilterFactoryBean;
    }

    /**
     * 开启shiro aop注解支持.
     * 使用代理方式;所以需要开启代码支持;
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }

    @Bean
    public FilterRegistrationBean delegatingFilterProxy() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        DelegatingFilterProxy proxy = new DelegatingFilterProxy();
        filterRegistrationBean.addUrlPatterns("/*");
        proxy.setTargetFilterLifecycle(true);
        proxy.setTargetBeanName("shiroFilter");
        filterRegistrationBean.setFilter(proxy);
        return filterRegistrationBean;
    }
}