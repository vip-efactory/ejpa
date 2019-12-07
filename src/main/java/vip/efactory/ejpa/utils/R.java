package vip.efactory.ejpa.utils;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import vip.efactory.ejpa.enums.CommonEnum;
import vip.efactory.ejpa.enums.ErrorCodeUtil;
import vip.efactory.ejpa.enums.IBaseErrorEnum;

import java.io.Serializable;

/**
 * Description:响应的实体类
 *
 * @author dbdu
 */
@Builder
@ToString
@Accessors(chain = true)
@AllArgsConstructor
@Setter
@Getter
@ApiModel(value = "响应体", description = "封装请求的响应数据")
public class R<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    // 响应返回码,0 表示正常，后面可以定义国际化的错误编码
    @ApiModelProperty(value = "响应码,0正常;1不正常", name = "code")
    private int code = 0;
    // 响应描述信息
    @ApiModelProperty(value = "响应描述信息", name = "msg")
    private String msg = "success";
    // 响应返回的信息主题
    @ApiModelProperty(value = "响应数据,任意类型", name = "data")
    private T data;

    // 构造函数
    public R() {
    }

    public R(T data, int code, String msg) {
        this.data = data;
        this.code = code;
        this.msg = msg;
    }

    // 正常返回
    public static <T> R<T> ok() {
        return genr(CommonEnum.SUCCESS);
    }

    public static <T> R<T> ok(T data) {
        return genr(data, CommonEnum.SUCCESS);
    }

    public static <T> R<T> ok(T data, String msg) {
        return genr(data, CommonEnum.SUCCESS.getErrorCode(), msg);
    }

    // 返回错误信息
    public static <T> R<T> error() {
        return genr(CommonEnum.ERROR);
    }

    public static <T> R<T> error(String msg) {
        return genr(null, CommonEnum.ERROR.getErrorCode(), msg);
    }

    // 支持异常对象
    public static <T> R<T> error(Throwable e) {
        return genr(null, CommonEnum.ERROR.getErrorCode(), e.getMessage());
    }

    public static <T> R<T> error(int code, String msg) {
        return genr(null, code, msg);
    }

    public static <T> R<T> error(int code, T data, String msg) {
        return genr(data, code, msg);
    }

    // 国际化返回错误信息

    /**
     * Description:使用枚举的错误信息!
     *
     * @param errorEnum 指定的错误类型枚举
     * @return R
     * @author dbdu
     */
    public static <T> R<T> error(IBaseErrorEnum errorEnum) {
        if (null != errorEnum) {
            return R.error(errorEnum.getErrorCode(), errorEnum.getReason());
        }
        return R.error();
    }

    /**
     * Description:使用国际化的枚举信息
     *
     * @param errorEnum 指定的错误类型枚举
     * @param args      可选的替换占位符的参数
     * @return R
     * @author dbdu
     */
    public static <T> R<T> i18nError(IBaseErrorEnum errorEnum, String... args) {
        if (null != errorEnum) {
            return R.error(errorEnum.getErrorCode(), ErrorCodeUtil.getMessage(errorEnum, args));
        }
        return R.error();
    }

    /**
     * 生成响应对象r，
     *
     * @param IBaseErrorEnum 枚举类型
     * @param <T>            数据的泛型类型
     * @return R 响应的对象包装
     */
    private static <T> R<T> genr(IBaseErrorEnum errorEnum) {
        return genr(null, errorEnum.getErrorCode(), errorEnum.getReason());
    }

    /**
     * 生成响应对象r，
     *
     * @param data           响应对象的数据
     * @param IBaseErrorEnum 枚举类型
     * @param <T>            数据的泛型类型
     * @return R 响应的对象包装
     */
    private static <T> R<T> genr(T data, IBaseErrorEnum errorEnum) {
        return genr(data, errorEnum.getErrorCode(), errorEnum.getReason());
    }

    /**
     * 生成响应对象r，
     *
     * @param data 响应对象的数据
     * @param code 响应码
     * @param msg  错误信息
     * @param <T>  数据的泛型类型
     * @return R 响应的对象包装
     */
    private static <T> R<T> genr(T data, int code, String msg) {
        return new R<T>(data, code, msg);
//        R<T> r = new R<>();
//        r.setCode(code);
//        r.setData(data);
//        r.setMsg(msg);
//        return r;
    }
}
