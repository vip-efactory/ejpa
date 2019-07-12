package vip.efactory.ejpa.utils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.beanutils.BeanMap;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * Description:与Map集合有关的工具类
 * 参考源码来自：http://www.jb51.net/article/84153.htm，需要的依赖：
 * <!-- https://mvnrepository.com/artifact/commons-beanutils/commons-beanutils -->
 * <dependency>
 * <groupId>commons-beanutils</groupId>
 * <artifactId>commons-beanutils</artifactId>
 * <version>1.9.3</version>
 * </dependency>
 * <p>
 * Created at:2018-02-08 16:42,
 * by dbdu
 */
public class MapUtil {
    private static Logger logger = LoggerFactory.getLogger(MapUtil.class);

    /**
     * Description:使用org.apache.commons.beanutils进行转换
     * 将对象转换为Map
     *
     * @param obj 要转换为Map的对象
     * @author dbdu
     */
    public static Map<?, ?> objectToMap1(Object obj) {
        if (obj == null) {
            return null;
        }
        return new BeanMap(obj);
    }

    /**
     * Description:使用org.apache.commons.beanutils进行转换
     * 将MAP转换为对象
     *
     * @param map       对象
     * @param beanClass 要转换为bean的class
     * @param map
     * @return java.lang.Object
     * @author dbdu
     */
    public static Object mapToObject1(Map<?, ?> map, Class beanClass) throws Exception {
        if (map == null) {
            return null;
        }
        Object obj = beanClass.newInstance();
        BeanUtils.populate(obj, (Map<String, ? extends Object>) map);
        return obj;
    }

    /**
     * Description:使用Introspector进行转换
     * 将对象转换为Map,但是以get开头的方法名及结果也会放在Map里面。,如果方法有参数可能会错误值或异常
     *
     * @param obj 对象
     * @author dbdu
     */
    public static Map<String, Object> objectToMap2(Object obj) {
        if (obj == null) {
            return null;
        }
        Map<String, Object> map = new HashMap<String, Object>();

        BeanInfo beanInfo = null;
        try {
            beanInfo = Introspector.getBeanInfo(obj.getClass());

            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();
                if (key.compareToIgnoreCase("class") == 0) {
                    continue;
                }
                Method getter = property.getReadMethod();
                Object value = getter != null ? getter.invoke(obj) : null;
                map.put(key, value);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        return map;
    }


    /**
     * Description:使用Introspector进行转换
     * 将Map转换为对象
     *
     * @param map       Map对象
     * @param beanClass bean的Class
     * @return java.lang.Object
     * @author dbdu
     */
    public static Object mapToObject2(Map<String, Object> map, Class<?> beanClass) {
        if (map == null) {
            return null;
        }
        Object obj = null;

        try {
            obj = beanClass.newInstance();
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor property : propertyDescriptors) {
                Method setter = property.getWriteMethod();
                if (setter != null) {
                    setter.invoke(obj, map.get(property.getName()));
                }
            }

        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        return obj;
    }


    /**
     * Description:使用reflect进行转换
     * 将Map转换为对象，继承而来的属性如果没有默认初始值则为null值。
     *
     * @param map       Map对象
     * @param beanClass bean的Class
     * @return java.lang.Object
     * @author dbdu
     */
    public static Object mapToObject3(Map<String, Object> map, Class<?> beanClass) throws Exception {
        if (map == null)
            return null;
        Object obj = beanClass.newInstance();
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            int mod = field.getModifiers();
            if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
                continue;
            }
            field.setAccessible(true);
            field.set(obj, map.get(field.getName()));
        }
        return obj;
    }

    /**
     * Description:使用reflect进行转换
     * 将对象转换为Map,注意此方法，对象继承的属性不会被处理，所以，如果实体有继承父级实体则不要使用此方法。
     *
     * @param obj 对象
     * @author dbdu
     */
    public static Map<String, Object> objectToMap3(Object obj) throws Exception {
        if (obj == null) {
            return null;
        }
        Map<String, Object> map = new HashMap<String, Object>();
        Field[] declaredFields = obj.getClass().getDeclaredFields();

        for (Field field : declaredFields) {
            field.setAccessible(true);
            map.put(field.getName(), field.get(obj));
        }
        return map;
    }

    /**
     * Description:将Map集合转换为Json字符串
     *
     * @param map Map对象
     * @return java.lang.String
     * @author dbdu
     */
    public static String map2Json(Map<String, Object> map) {
        ObjectMapper mapper = new ObjectMapper();
        String result = null;
        try {
            result = mapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage());
        }
        return result;
    }

    /**
     * Description: json字符串转为Map对象
     *
     * @param json 字符串
     * @return void
     * @author dbdu
     */
    public static Map json2Map(String json) {
        //Map<String, Map<String, Object>> maps = null;
        Map maps = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            //将json字符串转成map结合解析出来，这里以解析成map为例
            maps = objectMapper.readValue(json, Map.class);
            logger.debug(maps.toString());
        } catch (JsonParseException e) {
            logger.error(e.getMessage());
        } catch (JsonMappingException e) {
            logger.error(e.getMessage());
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return maps;
    }

}
