package vip.efactory.ejpa.i18n;

import java.util.Locale;

/**
 * Description:用来操作国际化，读取资源文件的接口
 *
 * @author dbdu
 * @date 18-6-25 下午3:12
 */
public interface ILocaleMsgSourceService {

    /**
     * 使用key获取国际化的值
     */
    String getMessage(String key);

    /**
     * 使用指定的key获取,如果没有值,则使用没有值
     */
    String getMessage(String key, String defaultValue);

    /**
     * 使用指定的Lacale,指定的key获取,如果没有值,则使用没有值
     */
    String getMessage(String key, String defaultValue, Locale locale);

    /**
     * 使用指定的Lacale,指定的key获取值
     */
    String getMessage(String key, Locale locale);

    /**
     * 使用指定的Key来获取值,但是需要指定参数来替换国际化资源文件里的占位符
     */
    String getMessage(String key, Object[] args);

    /**
     * 使用指定的Locale,指定的Key来获取值,但是需要指定参数来替换国际化资源文件里的占位符
     */
    String getMessage(String key, Object[] args, Locale locale);

    /**
     * 指定的Key来获取值,如果没有值则使用默认,
     * 但是需要指定参数来替换国际化资源文件里的占位符
     */
    String getMessage(String key, Object[] args, String defaultValue);

    /**
     * 使用指定的Locale,指定的Key来获取值,如果没有值则使用默认,
     * 但是需要指定参数来替换国际化资源文件里的占位符
     */
    String getMessage(String key, Object[] args, String defaultValue, Locale locale);
}
