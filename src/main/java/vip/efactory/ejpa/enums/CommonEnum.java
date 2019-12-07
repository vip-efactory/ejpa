package vip.efactory.ejpa.enums;


import lombok.Getter;

/**
 * 通用的，不便分类的枚举类型
 */
@Getter
public enum CommonEnum implements IBaseErrorEnum {
    SUCCESS(0, "成功"),
    ERROR(1, "未知错误"),
    ;

    private int errorCode;
    private String reason;
    private static final int MODULE_TYPE = ModuleTypeDefine.COMMON;
    private static int offset = ErrorCodeUtil.register(MODULE_TYPE);

    CommonEnum(int errorCode, String reason) {
        this.errorCode = errorCode;
        this.reason = reason;
    }


    public int getErrorCode() {
        return errorCode + offset;
    }

}
