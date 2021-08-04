package com.hqy.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.util.HtmlUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author qy
 * @project: hqy-parent-all
 * @create 2021-08-04 10:56
 */
@Slf4j
public class HtmlCommonUtil {


    /***
     * 1执行URL解码  将 %*字符都 整出来.... 2 html转义字符(&*)处理<br>
     //  < &#60; &lt; 小于号Less than
     //  = &#61;   — 等于符号Equals sign
     //  > &#62; &gt; 大于号Greater than
     //  ( &#40; — 小括号左边部分Left parenthesis
     //  / &#47; — 斜杠Solidus (slash)
     //  on  &#111;&#110;

     a &#97; — 小写a Small a
     b &#98; — 小写b Small b
     c &#99; — 小写c Small c
     d &#100; — 小写d Small d
     e &#101; — 小写e Small e
     f &#102; — 小写f Small f
     g &#103; — 小写g Small g
     h &#104; — 小写h Small h
     i &#105; — 小写i Small i
     j &#106; — 小写j Small j
     k &#107; — 小写k Small k
     l &#108; — 小写l Small l
     m &#109; — 小写m Small m
     n &#110; — 小写n Small n
     o &#111; — 小写o Small o
     p &#112; — 小写p Small p
     q &#113; — 小写q Small q
     r &#114; — 小写r Small r
     s &#115; — 小写s Small s
     t &#116; — 小写t Small t
     u &#117; — 小写u Small u
     v &#118; — 小写v Small v
     w &#119; — 小写w Small w
     x &#120; — 小写x Small x
     y &#121; — 小写y Small y
     z &#122; — 小写z Small z
     ***/
    public static String htmlUnescape(String s) {
        s = s.replaceAll("%0A", "");
        s = s.replaceAll("%0D", "");
        s = s.replaceAll("%0a", "");
        s = s.replaceAll("%0d", "");
        s = s.replaceAll("%09", "");
        //执行URL解码  将 %字符都 整出来....
        try {
            s = java.net.URLDecoder.decode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.warn("UnsupportedEncodingException for: {}", s);
            log.warn(e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            String cssError = "Illegal hex characters in escape";
            if (e.getMessage() != null && e.getMessage().contains(cssError)) {
                log.warn("ignored URLDecoder.decode for: {}", s);
                log.warn(e.getMessage(), e);
            }
        }
        return HtmlUtils.htmlUnescape(s);
    }

    public static final List<String> HACK_WORDS_IN_PARAM = new LinkedList<String>(Arrays.asList("javascript", "<script",
            "/etc/passwd", "../../", "sleep(", "location.href",
            "onpointer", "alert(",
            "onload=", "onloadend=", "onloadstart=",
            "onpageshow", "onerror=", "<svg ", "<iframe", "srcdoc=", "\"><", "\'><", "<x ", "atob("));


    public static final List<String> HACK_WORDS_IN_URI = new LinkedList<String>(Arrays.asList("javascript", "WEB-INF", "web.xml",
            "/etc/passwd", "../../", "sleep(", "location.href", "onload", "onloadend", "onloadstart",
            "onpageshow", "onerror=", "<svg ", "/.svn/", "/.git/", "/.env/", ".yml", ".swf",
            ".sql", ".sql.gz", ".asp", ".aspx", ".php", ".rb", ".py", ".tar.gz", "/cgi-bin/",
            "nslookup", "'||'", "load_file(", "eval(", "{{", "win.ini"
            //add 2020 11 17  https://github.com/heroanswer/XSS_Cheat_Sheet_2020_Edition
            , "onpointer", "alert(", "onfocus", "onkeyup", "onkeypress", "onkeydown", "onmouse", "contenteditable",
            "onanimation", "ontransition", "onwebkit"   //css3
            , "onscroll", "onhelp", "onfocus", "<iframe", "srcdoc=",
            "/jkstatus", "/jkmanager", "/Search-Replace-DB-master", "/server-status", "/server-info", "/solr",
            "\"><", "\'><", "<x ", "atob("
    ));


    /*public static String getTextFromRemoteHtml(String url) {
        Assert.hasText(url, "url must be not null or empty");
        try {
            Document document = Jsoup.connect(url).get();
            return document.html();
//            return replaceHtml(document.html());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }*/

    public static String replaceHtml(String html) {
        if (!StringUtils.hasText(html)) {
            return "";
        }
//        String regEx = "(<script.*?</script>)|(<style.*?</style>)|(<.+?>)";

        String regEx = "[^\\u4e00-\\u9fa5]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(html);
        String s = m.replaceAll("");

        return s;
    }


    /**
     * 特殊字符过滤，防止Xss注入式攻击...
     * @param input
     * @return
     */
    public static String processHtml(String input) {
        if (StringUtils.isEmpty(input)) {
            return input;
        } else {
            Pattern pattern = Pattern.compile("[<>*'\"]");
            Matcher m = pattern.matcher(input);
            input = m.replaceAll("");

            //提取自spring
            StringBuilder escaped = new StringBuilder(input.length() * 2);
            for (int i = 0; i < input.length(); ++i) {
                char character = input.charAt(i);

                String reference = null;
                switch (character) {
                    case '\"':
                        reference = "&quot;";
                        break;
                    case '&':
                        reference = "&amp;";
                        break;
                    case '\'':
                        reference = "&#39;";
                        break;
                    case '<':
                        reference = "&lt;";
                        break;
                    case '>':
                        reference = "&gt;";
                        break;
                }
                if (reference != null) {
                    escaped.append(reference);
                } else {
                    escaped.append(character);
                }
            }
            String escapedString = escaped.toString();
            //20190604 过滤SVG 威胁  //"%3csvg";
            escapedString = filterSVG(escapedString);
            return escapedString;
        }
    }


    private static final String SVG = "%3csvg";

    private static String filterSVG(String escapedString) {
        String tmpString = escapedString.toLowerCase();
        if (tmpString.contains(SVG)) {
            escapedString = escapedString.replace("%3c", " ");
            escapedString = escapedString.replace("%3C", " ");
        }
        return escapedString;
    }

    /**
     * 特殊字符过滤，防止Xss注入式攻击...
     * @param obj
     */
    public static void processHtmlObject(Object obj) {
        if (obj != null) {
            Field[] fields = obj.getClass().getDeclaredFields();
            for (Field f : fields) {
                try {
                    f.setAccessible(true);
                    Object fv = f.get(obj);
                    if (fv instanceof String) {
                        f.set(obj, processHtml(fv.toString()));
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }
}
