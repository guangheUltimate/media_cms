package io.guangsoft.media.shiro.filter.jcaptcha;

import io.guangsoft.media.utils.jcaptcha.JCaptcha;
import io.guangsoft.media.shiro.credential.RetryLimitHashedCredentialsMatcher;
import io.guangsoft.web.security.shiro.credential.RetryLimitHashedCredentialsMatcher;
import io.guangsoft.web.security.shiro.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.util.WebUtils;

import io.guangsoft.media.utils.ServletUtils;
import io.guangsoft.media.utils.SpringContextHolder;
import io.guangsoft.media.utils.StringUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class JCaptchaValidateFilter extends AccessControlFilter {

	private boolean jcaptchaEbabled = true;

	private String jcaptchaParam = "jcaptchaCode";

	private String jcapatchaErrorUrl;

	public void setJcaptchaEbabled(boolean jcaptchaEbabled) {
		this.jcaptchaEbabled = jcaptchaEbabled;
	}

	public boolean isJcaptchaEbabled() {
		return jcaptchaEbabled;
	}

	public String getJcaptchaParam() {
		return jcaptchaParam;
	}

	/**
	 * 前台传入的验证码
	 * @param jcaptchaParam
	 */
	public void setJcaptchaParam(String jcaptchaParam) {
		this.jcaptchaParam = jcaptchaParam;
	}

	public void setJcapatchaErrorUrl(String jcapatchaErrorUrl) {
		this.jcapatchaErrorUrl = jcapatchaErrorUrl;
	}

	public String getJcapatchaErrorUrl() {
		return jcapatchaErrorUrl;
	}

	@Override
	public boolean onPreHandle(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
		request.setAttribute("jcaptchaEbabled", jcaptchaEbabled);
		return super.onPreHandle(request, response, mappedValue);
	}

	@Override
	protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
		String useruame = WebUtils.getCleanParam(request, FormAuthenticationFilter.DEFAULT_USERNAME_PARAM);
		RetryLimitHashedCredentialsMatcher retryLimitHashedCredentialsMatcher = SpringContextHolder
				.getBean(RetryLimitHashedCredentialsMatcher.class);
		if (!retryLimitHashedCredentialsMatcher.isRepeatLogin(useruame)) {
			return true;
		}
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		//验证码禁用 或不是表单提交 允许访问
		if (jcaptchaEbabled == false || !"post".equals(httpServletRequest.getMethod().toLowerCase())) {
			return true;
		}
		return JCaptcha.validateResponse(httpServletRequest, httpServletRequest.getParameter(jcaptchaParam));
	}

	@Override
	protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
		redirectToLogin(request, response);
		return true;
	}

	protected void redirectToLogin(ServletRequest request, ServletResponse response) throws IOException {
		WebUtils.issueRedirect(request, response, getJcapatchaErrorUrl());
	}

	/**
	 * 是否需要验证
	 * @return
	 */
	public Boolean isValidateCaptcha() {
		// 验证码禁用 或不是表单提交 允许访问
		if (jcaptchaEbabled == false || !"post".equals(ServletUtils.getRequest().getMethod().toLowerCase())) {
			return false;
		}
		return true;
	}

	/**
	 * 是否提交验证码
	 * @return
	 */
	public Boolean isSubmitCapcha() {
		return !StringUtils.isEmpty(ServletUtils.getRequest().getParameter(jcaptchaParam));
	}

}
