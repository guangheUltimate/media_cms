package io.guangsoft.media.web.interceptor;

import io.guangsoft.common.utils.MessageUtils;
import io.guangsoft.common.utils.StringUtils;
import io.guangsoft.media.web.modules.sys.entity.Menu;
import io.guangsoft.media.web.utils.MenuTreeHelper;
import io.guangsoft.media.web.utils.UserUtils;
import io.guangsoft.web.modules.sys.entity.Menu;
import io.guangsoft.web.security.shiro.realm.UserRealm;
import io.guangsoft.web.utils.MenuTreeHelper;
import io.guangsoft.web.utils.UserUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 公共拦截器
 */
public class WebInterceptor extends HandlerInterceptorAdapter {

    private static Logger logger = LoggerFactory.getLogger(WebInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //加入公用参数的
        String ctx = request.getServletContext().getContextPath();
        request.setAttribute("ctx", ctx);
        request.setAttribute("adminPath", ctx + "/admin");
        request.setAttribute("staticPath", ctx + "/static");
        request.setAttribute("platformName", MessageUtils.getMessage("platform.name"));
        request.setAttribute("platformCopyright", MessageUtils.getMessage("platform.copyright"));
        request.setAttribute("platformVersion", MessageUtils.getMessage("platform.version"));
        UserRealm.Principal principal = UserUtils.getPrincipal(); // 如果已经登录，则跳转到管理首页
        if (principal != null) {
            try {
                Menu currentMenu = UserUtils.getCurrentMenu();
                if (currentMenu == null) {
                    currentMenu = new Menu();
                }
                String pmenuids = currentMenu.getParentIds();
                request.setAttribute("currentMenu", currentMenu);
                if (!StringUtils.isEmpty(pmenuids)) {
                    request.setAttribute("pmenuids", pmenuids);
                } else {
                    request.setAttribute("pmenuids", "");
                }
                List<MenuTreeHelper.MenuNode> menuNodes = MenuTreeHelper.create().sort(UserUtils.getMenuList());
                request.setAttribute("menus", menuNodes);
            } catch (Exception e) {

            }
        }
        request.setAttribute("loginUser", UserUtils.getUser());
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        super.afterCompletion(request, response, handler, ex);
    }

    @Override
    public void afterConcurrentHandlingStarted(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        super.afterConcurrentHandlingStarted(request, response, handler);
    }

}