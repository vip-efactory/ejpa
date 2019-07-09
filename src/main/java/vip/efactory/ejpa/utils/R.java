package vip.efactory.ejpa.utils;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import vip.efactory.ejpa.enums.ErrorCodeUtil;
import vip.efactory.ejpa.enums.IBaseErrorEnum;

import java.io.Serializable;

/**
 * Description:响应的实体类,之所以继承MAP,因为有些时候不是整体对象,方便处理零散的情况
 * 比如统计信息.
 *
 * @author dbdu
 * @date 18-10-1 下午4:59
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

    // 响应返回码,0 表示正常
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
        this.code = 0;
        this.msg = "success";
    }

    public R(T data) {
        this.data = data;
    }

    public R(T data, String msg) {
        this.data = data;
        this.msg = msg;
    }

    public R(Throwable e) {
        this.msg = e.getMessage();
        this.code = 1;
    }

    // 正常返回
    public static R ok() {
        return new R();
    }

    // 返回错误信息
    public static R error(int code, String msg) {
        R r = new R();
        r.setCode(code);
        r.setMsg(msg);
        return r;
    }

    public static R error() {
        return error(500, "未知异常，请联系管理员");
    }

    public static R error(String msg) {
        return error(500, msg);
    }

    // 国际化返回错误信息

    /**
     * Description:使用枚举的错误信息!
     *
     * @param [errorEnum]
     * @return void
     * @author dbdu
     * @date 17-12-23 下午9:11
     */
    public static R error(IBaseErrorEnum errorEnum) {
        if (null != errorEnum) {
            return R.error(errorEnum.getErrorCode(), errorEnum.getReason());
        }
        return R.error();
    }

    /**
     * Description:使用国际化的枚举信息
     *
     * @param [errorEnum, args]
     * @return void
     * @author dbdu
     * @date 17-12-23 下午9:11
     */
    public static R i18nError(IBaseErrorEnum errorEnum, String... args) {
        if (null != errorEnum) {
            return R.error(errorEnum.getErrorCode(), ErrorCodeUtil.getMessage(errorEnum, args));
        }
        return R.error();
    }

}
