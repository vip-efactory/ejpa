package vip.efactory.ejpa.utils;

import org.springframework.util.StringUtils;

/**
 * Description: SQL过滤，防止SQL注入
 *
 * @author dbdu
 * @date 19-3-12 下午2:26
 */
public class SQLFilter {

    /**
     * Description: 检测是否包含非法的从关键字，
     * true 包含非法；false 不包含
     *
     * @param [sql]
     * @return java.lang.Boolean
     * @author dbdu
     * @date 19-3-12 下午2:37
     */
    public static Boolean sqlInject(String sql) {
        if (StringUtils.isEmpty(sql)) {
            return false;
        }
        // 去除'|"|;|\字符
        sql = StringUtils.replace(sql, "'", "");
        sql = StringUtils.replace(sql, "\"", "");
        sql = StringUtils.replace(sql, ";", "");
        sql = StringUtils.replace(sql, "\\", "");

        //转换成小写
        sql = sql.toLowerCase();

        // 非法字符
        String[] keywords = {"master ", "truncate ", "insert ", "select ", "delete ", "update ", "declare ", "alter ", "drop "};

        //判断是否包含非法字符
        for (String keyword : keywords) {
            if (sql.indexOf(keyword) != -1) {
                return true;  //包含非法字符
            }
        }

        return false;
    }
}
