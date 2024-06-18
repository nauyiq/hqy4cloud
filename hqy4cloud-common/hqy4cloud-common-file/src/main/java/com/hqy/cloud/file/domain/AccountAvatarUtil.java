package com.hqy.cloud.file.domain;

import cn.hutool.extra.spring.SpringUtil;
import com.hqy.cloud.common.base.lang.PatternConstants;
import com.hqy.cloud.common.base.lang.StringConstants;
import com.hqy.cloud.file.common.utils.FileUtil;
import com.hqy.cloud.file.domain.support.Domain;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;

/**
 * 账号头像工具类
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/4/8
 */
public class AccountAvatarUtil {

    public static final String DEFAULT_AVATAR = "/files/avatar/default_avatar.png";

    /**
     * 获取头像路径.
     * @param avatar 入库的头像, 需要检查的头像...
     * @return       头像路径.
     */
    public static String getAvatar(String avatar) {
        if (StringUtils.isBlank(avatar)) {
            return StringConstants.EMPTY;
        }
        // 判断是否图片.
        if (!FileUtil.validateImgFileType(avatar)) {
            return StringConstants.EMPTY;
        }
        if (!avatar.startsWith(StringConstants.HTTP)) {
            // 如果不是以域名开头, 则获取对应的域名进行拼接
            DomainServer domainServer = SpringUtil.getBean(DomainServer.class);
            String avatarDomain = domainServer.getDomain(Domain.AVATAR.scene);
            // 默认返回http的路径.
            avatar = StringConstants.Host.HTTP.concat(avatarDomain).concat(avatar);
        }
        return avatar;
    }

    /**
     * 提取头像
     * @param avatar 头像
     * @return       头像
     */
    public static String extractAvatar(String avatar) {
        return FileUtil.extractPathByUrl(avatar);
    }


    /**
     * 检查是否是可用的头像, 即头像文件的格式是否正确
     * @param avatar 头像
     * @return       是否可用
     */
    public static boolean availableAvatar(String avatar) {
        if (StringUtils.isBlank(avatar)) {
            return false;
        }
        // 检查是否是文件, 并且是图片
        if (!FileUtil.validateImgFileType(avatar)) {
            return false;
        }
        // 如果是以域名开头，则需要检查域名是否是系统配置的域名
        if (avatar.startsWith(StringConstants.HTTP)) {
            // 获取文件的域名.
            String domain = getDomain(avatar);
            if (StringUtils.isBlank(domain)) {
                return false;
            }
            DomainServer domainServer = SpringUtil.getBean(DomainServer.class);
            String avatarDomain = domainServer.getDomain(Domain.AVATAR.scene);
            // 判断和配置的domain是否一致
            return StringUtils.isBlank(avatarDomain) || avatarDomain.equals(domain);
        }
        return true;
    }


    /**
     * 获取域名
     * @param url 提取url中的域名
     * @return    url中的域名
     */
    public static String getDomain(String url) {
        if (org.apache.commons.lang3.StringUtils.isBlank(url)) {
            return StringConstants.EMPTY;
        }
        Matcher matcher = PatternConstants.DOMAIN_PATTERN.matcher(url);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return StringConstants.EMPTY;
    }


}
