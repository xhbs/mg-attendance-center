package com.unisinsight.business.common.utils;

import cn.hutool.extra.servlet.ServletUtil;
import com.unisinsight.framework.common.exception.BaseErrorCode;
import com.unisinsight.framework.common.exception.BaseException;
import com.unisinsight.framework.common.util.bean.CheckUtils;
import com.unisinsight.framework.common.util.user.User;
import com.unisinsight.framework.common.util.user.UserHandler;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.nio.charset.Charset;

/**
 * @author cn [cheng.nian@unisinsight.com]
 * @desc
 * @time 2020/9/2 17:24
 */
public class UserUtils {

    public static User getUser() {
        User user = UserHandler.getUser();
        if (CheckUtils.isNullOrEmpty(user) || user.getUserCode() == null || user.getUserName() == null) {
            throw BaseException.of(BaseErrorCode.USER_NOT_LOGGED_IN.of());
        }
        return user;
    }

    /**
     * 供后端调用一些需要权限验证的接口时使用
     *
     * @return
     */
    public static User getUserAuto() {
        User user = UserHandler.getUser();
        if (CheckUtils.isNullOrEmpty(user) || user.getUserCode() == null || user.getUserName() == null) {
            user = new User();
            user.setUserCode("admin");
            user.setUserName("admin");
        }
        return user;
    }

    public static User mustGetUser() {
        User user = UserHandler.getUser();
        if (user == null) {
            throw BaseException.of(BaseErrorCode.USER_NOT_LOGGED_IN.of());
        }
        return user;
    }

    public static User getUserFromHeader() {
        try {
            User user = new User();
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            if (requestAttributes != null) {
                HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
                String userStr = request.getHeader("User");
                if (userStr != null) {
                    userStr = URLDecoder.decode(userStr, "UTF-8");
                    String[] array = userStr.split("&");
                    String[] var5 = array;
                    int var6 = array.length;

                    for (int var7 = 0; var7 < var6; ++var7) {
                        String line = var5[var7];
                        String[] keyValue = line.split(":");
                        if (keyValue.length >= 2) {
                            if ("usercode".equalsIgnoreCase(keyValue[0])) {
                                user.setUserCode(keyValue[1]);
                            }

                            if ("username".equalsIgnoreCase(keyValue[0])) {
                                user.setUserName(keyValue[1]);
                            }
                        }
                    }
                    return user;
                }

            }
        } catch (Exception ignored) {
        }
        return null;
    }
}
