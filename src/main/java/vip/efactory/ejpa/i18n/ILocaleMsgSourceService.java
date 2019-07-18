package vip.efactory.ejpa.i18n;

import java.util.Locale;

/**
 * Description:用来操作国际化，读取资源文件的接口
 *
 * @author dbdu
 */
public interface ILocaleMsgSourceService {

    /**
     * Description:使用key获取国际化的值
     *
     * @param key 键
     * @return java.lang.String
     * @author dbdu
     */
    String getMessage(String key);

    /**
     * Description:使用指定的key获取,如果没有值,则使用没有值
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return java.lang.String
     * @author dbdu
     */
    String getMessage(String key, String defaultValue);

    /**
     * Description:使用指定的Lacale,指定的key获取,如果没有值,则使用没有值
     *
     * @param key          键
     * @param defaultValue 默认值
     * @param locale       区域
     * @return java.lang.String
     * @author dbdu
     */
    String getMessage(String key, String defaultValue, Locale locale);


    /**
     * Description:使用指定的Lacale,指定的key获取值
     *
     * @param key    键
     * @param locale 区域
     * @return java.lang.String
     * @author dbdu
     */
    String getMessage(String key, Locale locale);

    /**
     * Description:使用指定的Key来获取值,但是需要指定参数来替换国际化资源文件里的占位符
     *
     * @param key  键
     * @param args 要替换的参数
     * @return java.lang.String
     * @author dbdu
     */
    String getMessage(String key, Object[] args);

    /**
     * Description:使用指定的Locale,指定的Key来获取值,但是需要指定参数来替换国际化资源文件里的占位符
     *
     * @param key    键
     * @param args   替换占位符的参数
     * @param locale 区域
     * @return java.lang.String
     * @author dbdu
     */
    String getMessage(String key, Object[] args, Locale locale);

    /**
     * Description:指定的Key来获取值,如果没有值则使用默认,
     * 但是需要指定参数来替换国际化资源文件里的占位符
     *
     * @param key          键
     * @param args         替换占位符的参数
     * @param defaultValue 默认值
     * @return java.lang.String
     * @author dbdu
     */
    String getMessage(String key, Object[] args, String defaultValue);

    /**
     * Description:使用指定的Locale,指定的Key来获取值,如果没有值则使用默认,
     * 但是需要指定参数来替换国际化资源文件里的占位符
     *
     * @param key          键
     * @param args         替换占位符的参数
     * @param defaultValue 默认值
     * @param locale       区域
     * @return java.lang.String
     * @author dbdu
     */
    String getMessage(String key, Object[] args, String defaultValue, Locale locale);
}
