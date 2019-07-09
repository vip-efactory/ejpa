package vip.efactory.ejpa.base.controller;


import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.util.StringUtils;
import vip.efactory.ejpa.base.entity.BaseEntity;
import vip.efactory.ejpa.base.entity.BaseSearchField;
import vip.efactory.ejpa.base.enums.SearchRelationEnum;
import vip.efactory.ejpa.base.enums.SearchTypeEnum;
import vip.efactory.ejpa.base.service.IBaseService;
import vip.efactory.ejpa.i18n.ILocaleMsgSourceService;
import vip.efactory.ejpa.utils.CommUtil;
import vip.efactory.ejpa.utils.R;
import vip.efactory.ejpa.utils.UpdatePoUtil;
import vip.efactory.ejpa.utils.ValidateModelUtil;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;


/**
 * Description:这是一个基础的控制器基类，包含常见的基本的CRUD的请求的处理，其他特殊的方法通过子类继承实现。
 * T1,是操作的实体类，T2是对应的service接口类继承IBaseService
 *
 * @author dbdu
 * @date 18-6-27 上午7:25
 */
@SuppressWarnings("all")
@Slf4j
public class BaseController<T1 extends BaseEntity, T2 extends IBaseService> {

    @Autowired
    public ILocaleMsgSourceService msgSourceService;

    @Autowired
    public T2 entityService;
    /**
     * Description:获取T的Class对象是关键，看构造方法
     *
     * @author dbdu
     * @date 18-6-27 上午8:16
     * @param
     * @return
     */
    private Class<T1> clazz = null;

    /**
     * Description:无参构造函数，获得T1的clazz对象
     *
     * @author dbdu
     * @date 18-6-27 上午8:19
     */
    public BaseController() {
        //为了得到T1的Class，采用如下方法
        //1得到该泛型类的子类对象的Class对象
        Class clz = this.getClass();
        //2得到子类对象的泛型父类类型（也就是BaseDaoImpl<T>）
        ParameterizedType type = (ParameterizedType) clz.getGenericSuperclass();
        System.out.println(type);
        //
        Type[] types = type.getActualTypeArguments();
        clazz = (Class<T1>) types[0];
        //System.out.println(clazz.getSimpleName());
    }

    /**
     * Description:实体的分页查询，包括排序等,使用SpringData自己的对象接收分页参数
     *
     * @param [page]
     * @return com.ddb.bss.utils.R
     * @author dbdu
     * @date 19-6-10 下午2:00
     */
    public R getByPage(@PageableDefault(value = 25, sort = {"id"}, direction = Sort.Direction.DESC) Pageable page) {
        Page<T1> entities = entityService.findAll(page);
        return R.ok().setData(entities);
    }

    /**
     * Description: 高级查询加分页
     *
     * @param [page, entity]
     * @return com.ddb.bss.utils.R
     * @author dbdu
     * @date 19-7-5 下午12:44
     */
    public R advancedQueryByPage(Pageable page, T1 entity) {
        Page<T1> entities = entityService.advancedQuery(entity, page);
        return R.ok().setData(entities);
    }

    /**
     * Description: 高级查询不分页
     *
     * @param [entity]
     * @return com.ddb.bss.utils.R
     * @author dbdu
     * @date 19-7-5 下午12:44
     */
    public R advancedQuery(T1 entity) {
        List<T1> entities = entityService.advancedQuery(entity);
        return R.ok().setData(entities);
    }

    /**
     * Description: 同一个值,在多个字段中模糊查询,不分页
     *
     * @param [q, fields]
     *            fields   例如:"name,address,desc"
     * @return com.ddb.bss.utils.R
     * @author dbdu
     * @date 19-7-5 下午1:04
     */
    public R queryMutiField(String q, String fields) {
        // 构造高级查询条件
        T1 be = buildQueryConditions(q, fields);

        List<T1> entities = entityService.advancedQuery(be);
        return R.ok().setData(entities);
    }

    /**
     * Description:同一个值,在多个字段中模糊查询,分页
     *
     * @param [q, fields, page]
     *            fields   例如:"name,address,desc"
     * @return com.ddb.bss.utils.R
     * @author dbdu
     * @date 19-7-5 下午1:06
     */
    public R queryMutiField(String q, String fields, Pageable page) {
        // 构造高级查询条件
        T1 be = buildQueryConditions(q, fields);
        Page<T1> entities = entityService.advancedQuery(be, page);
        return R.ok().setData(entities);
    }

    /**
     * Description:根据id获取一个实体的信息
     *
     * @param [request, response, session, entityId]
     * @return java.lang.Object
     * @author dbdu
     * @date 18-6-17 下午12:47
     */
    public R getById(Long id) {
        if (CommUtil.isEmptyLong(id)) {
            return R.error("id 不允许为空！");
        }

        Optional entity = entityService.findById(id);

        if (entity.isPresent()) {
            return R.ok().setData(entity);
        } else {
            return R.error("id=" + id + "对应的实体不存在");
        }

    }

    /**
     * Description:保存一个实体，保存之前会做检查
     *
     * @param [request, response, session, cloud, result]
     * @return java.lang.Object
     * @author dbdu
     * @date 18-6-17 下午12:48
     */
    public R save(T1 entity) {
        List<String> errors = ValidateModelUtil.validateModel(entity);

        if (!errors.isEmpty()) {
            return R.error(1, "校验失败！").setData(errors);
        }

        entityService.save(entity);
        R r = R.ok().setData(entity);
        return r;

    }

    /**
     * Description:使用id来更新,如果属性空值,则不更新现有的值
     *
     * @param [entity]
     * @return com.ddb.bss.utils.R
     * @author dbdu
     * @date 19-6-11 上午8:59
     */
    public R updateById(T1 entity) {
        List<String> errors = ValidateModelUtil.validateModel(entity);

        if (!errors.isEmpty()) {
            return R.error(1, "校验失败！").setData(errors);
        }

        // 检查数据记录是否已经被删除了，被删除了，则不允许更新
        Optional<T1> entityOptional = entityService.findById(entity.getId());
        if (!entityOptional.isPresent()) {
            return R.error(1, "您更新的记录[" + entity.getId() + "]不存在！");
        } else {
            // 检查更新时间戳，避免用旧的数据更新数据库里的新数据
            Date updateTime = entity.getModifiedTime();
            Date dbUpdateTime = entityOptional.get().getModifiedTime();
            if (updateTime != null && updateTime.compareTo(dbUpdateTime) != 0) {
                return R.error("请获取最新的数据更新！");
            }
        }
        //检查业务key的存在性，不应该存在重复的业务key,此处不知道业务key是什么属性，可以在在service层实现，重写方法即可！

        if (null != entity.getId()) {
            updateEntity(entityOptional.get(), entity, false, "createTime", "modifiedTime");
        }

        R r = R.ok().setData(entity);
        return r;

    }


    /**
     * Description:使用id删除指定的实体
     *
     * @param [request, response, session, entityId]
     * @return java.lang.Object
     * @author dbdu
     * @date 18-6-17 下午12:49
     */
    public R deleteById(Long id) {
        if (CommUtil.isEmptyLong(id)) {
            return R.ok();
        }

        //进行关联性检查,调用对应的方法
        try {
            this.entityService.deleteById(id);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }

        return R.ok();
    }

    /**
     * Description:使用id删除指定的实体
     *
     * @param [request, response, session, entityId]
     * @return java.lang.Object
     * @author dbdu
     * @date 18-6-17 下午12:49
     */
    public R deleteByIds(Long[] entityIds) {
        if (CommUtil.isEmptyArrary(entityIds)) {
            return R.ok();
        }

        //进行关联性检查,调用对应的方法
        try {
            this.entityService.deleteAllById(Arrays.asList(entityIds));
        } catch (Exception e) {
            return R.error(e.getMessage());
        }

        return R.ok();
    }

    /**
     * Description:根据实体编号检查实体是否存在
     *
     * @param [entityNum]
     * @return java.lang.String
     * @author dbdu
     * @date 18-6-16 下午7:34
     */
    public String chkEntityExist(String entityNum, String flag) {
        if (CommUtil.isEmptyString(entityNum)) {
            return null;
        }
        String msg = "";
        return msg;
    }

    /**
     * Description:检查操作的实体的id是否存在，因为多人操作，可能被其他人删除了！
     *
     * @param [entityId]
     * @return java.lang.String
     * @author dbdu
     * @date 18-6-16 下午7:39
     */
    public Boolean chkEntityIdExist(Long entityId) {
        return null != entityId && entityService.existsById(entityId);
    }


    /**
     * Description:实体的关联性检查的方法。
     * 如果存在关联性则返回true,否则返回false,这个方法只是模板，需要子类重
     *
     * @param [entity]
     * @return java.lang.Boolean
     * @author dbdu
     * @date 18-6-17 下午12:00
     */
    private Boolean chkEntityRelationship(Long entityId) {
        return false;
    }

    /**
     * Description:是否使用前端的数据实体的空值属性更新数据库中的
     * true，则用空置更新;fasle则不用空值更新,还允许部分更新
     *
     * @param [dbEntity, entity, useNull, ignoreProperties]
     * @return T1
     * @author dbdu
     * @date 19-7-8 下午5:04
     */
    private T1 updateEntity(T1 dbEntity, T1 entity, Boolean useNull, String... ignoreProperties) {
        if (!useNull) {
            // 将前端来的数据空值用数据库中的值补上
            UpdatePoUtil.copyNullProperties(dbEntity, entity, ignoreProperties);
        }
        return (T1) entityService.update(entity);
    }

    /**
     * Description:根据查询值及多字段,来构建高级查询条件
     *
     * @param [q, fields]
     * @return com.ddb.bss.base.entity.BaseEntity
     * @author dbdu
     * @date 19-7-5 下午1:33
     */
    @SneakyThrows
    private T1 buildQueryConditions(String q, String fields) {
        // 如果q不为空,则构造高级查询条件
        T1 entity = clazz.newInstance();
        if (!CommUtil.isMutiHasNull(q, fields)) {
            Set<BaseSearchField> conditions = new HashSet<>();
            // 判断filds是一个字段还是多个字段,若是多个字段则进行切分
            if (fields.contains(",")) {
                String[] rawFields = StringUtils.split(fields, ",");
                for (String c : rawFields) {
                    BaseSearchField condition = new BaseSearchField();
                    condition.setName(c);
                    condition.setSearchType(SearchTypeEnum.FUZZY.getValue());
                    condition.setVal(q);
                    conditions.add(condition);
                }
            } else {
                // 构建模糊查询的条件
                BaseSearchField condition = new BaseSearchField();
                condition.setName(fields);
                condition.setSearchType(SearchTypeEnum.FUZZY.getValue());
                condition.setVal(q);
                conditions.add(condition);
            }
            entity.setRelationType(SearchRelationEnum.OR.getValue());  // 所有条件或的关系
            entity.setConditions(conditions);
        }
        return entity;
    }

}
