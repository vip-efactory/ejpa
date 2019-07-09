package vip.efactory.ejpa.enums;

/**
 * 定义基本的错误枚举应有的方法
 */
public interface IBaseErrorEnum {
    String getReason();

    int getErrorCode();

    String name();
}
