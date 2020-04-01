package vip.efactory.ejpa.tenant;

/**
 * 租户策略类型的枚举，不同的策略处理是不一样的
 */
public enum TenantStrategyEnum {
    NONE("NONE", "不使用多租户"),               // 默认,不使用多租户
    SCHEMA("SCHEMA", "基于SCHEMA的多租户"),             // 同库不同Schema，租户间数据彼此逻辑不可见,看网上的有的文章意思是基于表
    DATABASE("DATABASE", "基于独立DB的多租户"),           // 独立数据库模式,隔离最彻底， 建议这一种方式
    DISCRIMINATOR("DISCRIMINATOR", "基于表租户列的多租户"),;      // 同库同表有租户列的模式

    // 多租户策略
    private String strategy;
    // 多租户策略描述
    private String description;

    TenantStrategyEnum(String strategy, String description) {
        this.strategy = strategy;
        this.description = description;
    }
}
