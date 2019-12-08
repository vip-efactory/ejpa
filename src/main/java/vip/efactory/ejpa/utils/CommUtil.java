package vip.efactory.ejpa.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class CommUtil {

    /**
     * 判断一个String是否为null或者空串?
     *
     * @param string
     * @return
     */
    public static boolean isNull(String string) {
        if (string == null || "".equals(string)) {
            return true;
        }
        return false;
    }

    /**
     * Description:判断是否有空值或者null，只要有一个是空串或者null，就返回true
     * 因为在有些场景，不允许任何一个为空串或者null。
     *
     * @param mutiStr 多个字符串参数
     * @return boolean
     * @author dbdu
     */
    public static boolean isMutiHasNull(String... mutiStr) {
        //是否全是空串或者null
        boolean hasNull = false;
        if (mutiStr.length > 0) {
            //说明有元素，不是空的
            for (String str : mutiStr) {
                if (isNull(str)) {
                    hasNull = true;
                    break;
                }
            }
        }

        return hasNull;
    }

    public static boolean isEmptyList(Collection<?> list) {
        if (null == list || list.isEmpty()) {
            return true;
        } else {
            boolean allNull = true;
            for (Object object : list) {
                if (null != object) {
                    allNull = false;
                }
            }
            return allNull;
        }
    }

    public static boolean isEmptyArrary(Object[] arrary) {
        return (null == arrary || arrary.length == 0);
    }

    public static boolean isEmptyString(String str) {
        return (str == null || "".equals(str.trim()));
    }

    public static boolean isEmptyLong(String l) {
        return (isEmptyString(l) || "-1".equals(l.trim()));
    }

    public static boolean isEmptyLong(Long l) {
        return (null == l || -1 == l);
    }

    public static boolean isEmptyInt(Integer i) {
        return (null == i || -1 == i);
    }

    public static boolean isEmptyBoolean(Boolean i) {
        return (null == i);
    }


    public static long getTaskRelativeStartTime(long currentTime, long start_time, long period) {
        if (start_time >= currentTime) {
            return (start_time - currentTime) / 1000;
        }
        long periodTime = ((currentTime - start_time) / 1000) % period;
        return period - periodTime;
    }

    public static long getTaskRelativeEndTime(long currentTime, long relativeStartTime, long end_time, long period) {
        if (currentTime > end_time/* || currentTime + period * 1000 > end_time */) {
            return relativeStartTime;
        }
        return (end_time - currentTime) / 1000;
    }

    public static long getTestId(long currentTime, long scheduleStartTime, long schedulePeriod) {
        if (currentTime < scheduleStartTime)
            return 0;
        long count = (currentTime - scheduleStartTime) / 1000 / schedulePeriod;
        return count + 1;
    }

    public static String arrayJoin(char separate, Object[] arr, int size) {
        StringBuilder sb = new StringBuilder();
        if (arr != null) {
            Object obj = null;
            for (int i = 0; i < size && i < arr.length; i++) {
                obj = arr[i];
                if (obj == null)
                    continue;
                sb.append(obj.toString()).append(separate);
            }
        }
        if (sb.length() > 0 && separate == sb.charAt(sb.length() - 1)) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    public static String arrayJoin(char separate, List<?> arr, int size) {
        StringBuilder sb = new StringBuilder();
        if (arr != null) {
            Object obj = null;
            for (int i = 0; i < size && i < arr.size(); i++) {
                obj = arr.get(i);
                if (obj == null)
                    continue;
                sb.append(obj.toString()).append(separate);
            }
        }
        if (sb.length() > 0 && separate == sb.charAt(sb.length() - 1)) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    public static String getDayHourTime(long time) {
        if (time <= 0)
            return "";
        StringBuilder sb = new StringBuilder();
        long days = time / 24 * 60 * 60;
        long hours = (time % (24 * 60 * 60)) / (60 * 60);
        long mins = ((time % (24 * 60 * 60)) % (60 * 60)) / 60;
        long secs = ((time % (24 * 60 * 60)) % (60 * 60)) % 60;
        sb.append(days).append(" days ").append(hours).append(" hours ").append(mins).append(" minutes ").append(secs)
                .append(" seconds");
        return sb.toString();

    }


    public static Calendar getCalendar() {
        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        return cal;
    }


    public static String getRootPath() {

        String classPath = CommUtil.class.getResource("/").getPath();
        return classPath;
    }


    public static boolean isEmpty(String value) {
        int strLen;
        if ((value == null) || ((strLen = value.length()) == 0)) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(value.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNumeric(Object obj) {
        if (obj == null) {
            return false;
        }
        char[] chars = obj.toString().toCharArray();
        int length = chars.length;
        if (length < 1) {
            return false;
        }
        int i = 0;
        if ((length > 1) && (chars[0] == '-')) {
        }
        for (i = 1; i < length; i++) {
            if (!Character.isDigit(chars[i])) {
                return false;
            }
        }
        return true;
    }

    public static boolean areNotEmpty(String... values) {
        boolean result = true;
        if ((values == null) || (values.length == 0)) {
            result = false;
        } else {
            for (String value : values) {
                result &= !isEmpty(value);
            }
        }
        return result;
    }

    public static String toTitle(String str) {
        if (CommUtil.isEmptyString(str)) {
            return "";
        } else {
            return str.substring(0, 1).toUpperCase() + str.substring(1);
        }
    }

    public static String joinUrl(String root, String url) {
        if (CommUtil.isEmptyString(root)) {
            return url;
        }

        if (CommUtil.isEmptyString(url)) {
            return "";
        }

        while (root.endsWith("/") || root.endsWith("\\")) {
            root = root.substring(0, root.length() - 1);
        }

        while (url.startsWith("/") || url.startsWith("\\")) {
            url = url.substring(1);
        }

        return root + "/" + url;

    }

    /**
     * 将异常信息转化成字符串
     *
     * @param t
     * @return
     * @throws IOException
     */
    public static String exception(Throwable t) {
        if (t == null)
            return null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            t.printStackTrace(new PrintStream(baos));
        } finally {
            try {
                baos.close();
            } catch (IOException e) {
                log.error("Error to close the stream", e);
            }
        }
        return baos.toString();
    }


    public static String idListToString(Collection<?> ids) {
        if (CommUtil.isEmptyList(ids)) {
            return "";
        } else {
            StringBuffer sb = new StringBuffer();
            sb.append(" (");
            for (Object id : ids) {
                sb.append("'");
                sb.append(id.toString().trim());
                sb.append("',");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append(") ");
            return sb.toString();
        }
    }

    public static String getFormatUpgradeVersion(String version) {
        if (version == null)
            return "";
        StringBuffer ret = new StringBuffer();
        String[] cver = version.split("\\.");
        int length = 0;
        for (int i = 0; i < cver.length; i++) {
            length = cver[i].length();
            switch (length) {
                case 2:
                    ret.append("0");
                    break;
                case 1:
                    ret.append("00");
                    break;
                default:
                    break;
            }
            ret.append(cver[i]);
            ret.append(".");
        }
        return ret.substring(0, ret.length() - 1);
    }

    public static String getFormatVersion(String formatUpgradeVersion) {
        if (formatUpgradeVersion == null)
            return "";
        StringBuffer ret = new StringBuffer();
        String[] cver = formatUpgradeVersion.split("\\.");
        for (int i = 0; i < cver.length; i++) {
            if (cver[i].length() == 3) {
                if (cver[i].startsWith("00")) {
                    ret.append(cver[i].substring(2));
                } else if (cver[i].startsWith("0")) {
                    ret.append(cver[i].substring(1));
                }
            } else {
                ret.append(cver[i]);
            }
            ret.append(".");
        }
        return ret.substring(0, ret.length() - 1);
    }


    /**
     * 比较两个版本号的大小，如果version1大于version2，返回值为1，等于为0，小于为-1
     *  版本号样例：1.0.1
     * @param version1
     *            第一个版本号
     * @param version2
     *            目标版本号
     * @return
     */
    public static Integer compareVersion(String version1, String version2) {
        try {
            if (version1 == null || version2 == null) {
                throw new IllegalArgumentException("Bad version number");
            }
            String[] versionArray1 = version1.split("\\.");
            String[] versionArray2 = version2.split("\\.");
            if (versionArray1.length != 3 || versionArray2.length != 3) {
                throw new IllegalArgumentException("Bad version number");
            }
            // 逐段比较版本号，先比较第一位
            Integer result = null;
            for (int i = 0; i < 3; i++) {
                Integer v1 = Integer.parseInt(versionArray1[i]);
                Integer v2 = Integer.parseInt(versionArray2[i]);
                result = Integer.compare(v1 - v2, 0);
                if (result != 0) {
                    break;
                }
            }
            return result;
        } catch (Exception ex) {
            log.error("Error to compare version", ex);
            return -2;
        }
    }


    /**
     * Description:从富文本的字符串中使用正则表达式,抽出所有的url为Set集合
     *
     * @param content 内容
     * @return java.util.Set<java.lang.String>
     * @author dbdu
     */
    public static Set<String> getUrlFromString(String content) {
        Set<String> urls = new HashSet<>();
        if (!CommUtil.isEmptyString(content)) {
            // 例:"http://ioss-dbdu.oss-cn-beijing.aliyuncs.com/upload/PROD_SYNOPSIS_IMAGE/20181217/2/Autumn_in_Kanas_by_Wang_Jinyu.jpg"
            String regUrl = "[a-zA-z]+://[^\\s\"$]*";
            Pattern _pattern = Pattern.compile(regUrl);
            Matcher _match = _pattern.matcher(content);
            while (_match.find()) {
                String url = _match.group();
                urls.add(url);
            }
        }
        return urls;
    }


    public static void main(String[] args) {
        Integer result = compareVersion("3.0.1", "3.0.1");
        System.out.println(result);

        String _str = "&lt;!DOCTYPE html&gt;\n&lt;html&gt;\n&lt;head&gt;\n&lt;/head&gt;\n&lt;body&gt;\n&lt;p&gt;&lt;font color=\"#FF0000\"&gt;关闭给大家库覆盖天赋i发人员&lt;/font&gt;&lt;/p&gt;&lt;p&gt;&lt;font color=\"#FF0000\"&gt;&lt;img src=\"http://ioss-dbdu.oss-cn-beijing.aliyuncs.com/upload/PROD_SYNOPSIS_IMAGE/20181217/2/Autumn_in_Kanas_by_Wang_Jinyu.jpg\" width=\"400\" height=\"225\" alt=\"\"&gt;&lt;br data-mce-bogus=\"1\"&gt;&lt;/font&gt;&lt;/p&gt;&lt;p&gt;是否健康绿色的家里附近&lt;/p&gt;&lt;p&gt;&lt;font color=\"#FF0000\"&gt;&lt;img src=\"http://ioss-dbdu.oss-cn-beijing.aliyuncs.com/upload/PROD_SYNOPSIS_IMAGE/20181217/2/Beach_by_Samuel_Scrimshaw.jpg\" width=\"378\" height=\"236\" alt=\"\"&gt;&lt;br data-mce-bogus=\"1\"&gt;&lt;/font&gt;&lt;/p&gt;&lt;p&gt;&lt;font color=\"#FF0000\"&gt;&lt;br data-mce-bogus=\"1\"&gt;&lt;/font&gt;&lt;/p&gt;&lt;p&gt;&lt;font color=\"#FF0000\"&gt;还十分大地方&lt;/font&gt;&lt;/p&gt;&lt;p&gt;&lt;font color=\"#FF0000\"&gt;&lt;br data-mce-bogus=\"1\"&gt;&lt;/font&gt;&lt;/p&gt;&lt;p&gt;&lt;font color=\"#FF0000\"&gt;&lt;img src=\"http://ioss-dbdu.oss-cn-beijing.aliyuncs.com/upload/PROD_SYNOPSIS_IMAGE/20181217/2/Reflection_of_the_Kanas_Lake_by_Wang_Jinyu.jpg\" width=\"400\" height=\"225\" alt=\"\"&gt;&lt;br data-mce-bogus=\"1\"&gt;&lt;/font&gt;&lt;/p&gt;\n&lt;/body&gt;\n&lt;/html&gt;";

        System.out.println(getUrlFromString(_str));
    }
}
