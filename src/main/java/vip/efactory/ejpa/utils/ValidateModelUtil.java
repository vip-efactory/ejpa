package vip.efactory.ejpa.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vip.efactory.ejpa.i18n.ILocaleMsgSourceService;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Description:this util is used for Bean annotation validate by manual
 *
 * @param
 * @author dbdu
 * @date 18-1-16 下午4:49
 * @return
 */
@Component
@Slf4j
public class ValidateModelUtil {
    private static ILocaleMsgSourceService localeMessageSourceService;

    //通过下面的方法为静态成员赋值!!!
    @Autowired
    public void setLocaleMessageSourceService(ILocaleMsgSourceService localeMessageSourceService) {
        ValidateModelUtil.localeMessageSourceService = localeMessageSourceService;
    }


    //验证某一个对象
    public static List<String> validateModel(Object obj) {

        //用于存储验证后的错误信息
        List<String> errors = new ArrayList<String>();
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

        //验证某个对象,，其实也可以只验证其中的某一个属性的
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(obj);
        Iterator<ConstraintViolation<Object>> iter = constraintViolations.iterator();

        while (iter.hasNext()) {
            ConstraintViolation<Object> currentObj = iter.next();   //再执行一次就会跳到下一个对象
            String message = currentObj.getMessage();      //校验要显示的错误信息
            // String property = currentObj.getPropertyPath().toString();    //错误信息对应的字段名

            // log.info("property：" + property + ",check failed:" + message);
            if (message.startsWith("{") && message.endsWith("}")) {
                //说明信息是键，不是硬编码的信息，使用对应的key来获取到制定的值
                //去除两端的｛｝大括号
                String key = message.substring(1, message.length() - 1);
                String value = localeMessageSourceService.getMessage(key);
                if (null == value || value.equals(key)) {
                    value = key;
                    log.warn("missing key [{}]", key);
                }
                errors.add(value);
            } else {
                //是硬编码的校验信息，直接取过来
                errors.add(message);
            }

        }
        return errors;
    }

}  
