package vip.efactory.ejpa.base.controller;


import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import vip.efactory.common.i18n.enums.CommAPIEnum;
import vip.efactory.common.i18n.enums.CommDBEnum;
import vip.efactory.common.i18n.service.ILocaleMsgSourceService;
import vip.efactory.ejpa.base.entity.BaseEntity;
import vip.efactory.ejpa.base.entity.BaseSearchField;
import vip.efactory.ejpa.base.enums.SearchTypeEnum;
import vip.efactory.ejpa.base.service.IBaseService;
import vip.efactory.ejpa.base.valid.Update;
import vip.efactory.ejpa.utils.CommUtil;
import vip.efactory.ejpa.utils.R;
import vip.efactory.ejpa.utils.UpdatePoUtil;
import vip.efactory.ejpa.utils.ValidateModelUtil;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;


/**
 * Description:这是一个基础的控制器基类，包含常见的基本的CRUD的请求的处理，其他特殊的方法通过子类继承实现。
 * T1,是操作的实体类，T2是对应的service接口类继承IBaseService,ID是主键的类型
 *
 * @author dbdu
 */
@SuppressWarnings("all")
@Slf4j
public class BaseController<T1 extends BaseEntity, T2 extends IBaseService, ID> {
    /**
     * 处理国际化资源的组件
     */
    @Autowired
    public ILocaleMsgSourceService msgSourceService;

    /**
     * T1实体对应的Service,在子类控制器则不需要再次注入了
     */
    @Autowired
    public T2 entityService;

    /**
     * Description:获取T的Class对象是关键，看构造方法
     *
     * @author dbdu
     */
    private Class<T1> clazz = null;

    /**
     * Description:无参构造函数，获得T1的clazz对象
     */
    public BaseController() {
        //为了得到T1的Class，采用如下方法
        //1得到该泛型类的子类对象的Class对象
        Class clz = this.getClass();
        //2得到子类对象的泛型父类类型（也就是BaseDaoImpl<T>）
        ParameterizedType type = (ParameterizedType) clz.getGenericSuperclass();
//        System.out.println(type);
        //
        Type[] types = type.getActualTypeArguments();
        clazz = (Class<T1>) types[0];
        //System.out.println(clazz.getSimpleName());
    }

    /**
     * Description:实体的分页查询，包括排序等,使用SpringData自己的对象接收分页参数
     *
     * @param page 分页参数对象
     * @return R
     */
    public R getByPage(Pageable page) {
        Page<T1> entities = entityService.findAll(page);
        EPage ePage = new EPage(entities);
        return R.ok().setData(ePage);
    }

    /**
     * Description: 高级查询加分页功能
     *
     * @param page   分页参数对象
     * @param entity 包含高级查询条件的实体
     * @return R
     */
    public R advancedQueryByPage(Pageable page, T1 entity) {
        Page<T1> entities = entityService.advancedQuery(entity, page);
        EPage ePage = new EPage(entities);
        return R.ok().setData(ePage);
    }

    /**
     * Description: 高级查询不分页
     *
     * @param entity 包含高级查询条件的实体
     * @return R
     */
    public R advancedQuery(T1 entity) {
        List<T1> entities = entityService.advancedQuery(entity);
        return R.ok().setData(entities);
    }

    /**
     * Description: 同一个值,在多个字段中模糊查询,不分页
     *
     * @param q      模糊查询的值
     * @param fields 例如:"name,address,desc",对这三个字段进行模糊匹配
     * @return R
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
     * @param q      模糊查询的值
     * @param fields 例如:"name,address,desc",对这三个字段进行模糊匹配
     * @param page   分页参数对象
     * @return R
     */
    public R queryMutiField(String q, String fields, Pageable page) {
        // 构造高级查询条件
        T1 be = buildQueryConditions(q, fields);
        Page<T1> entities = entityService.advancedQuery(be, page);
        EPage ePage = new EPage(entities);
        return R.ok().setData(ePage);
    }

    /**
     * Description:根据id获取一个实体的信息
     *
     * @param id 主键
     * @return java.lang.Object
     */
    public R getById(ID id) {
        if (null == id) {
            return R.error(CommDBEnum.KEY_NOT_NULL);
        }

        Optional entity = entityService.findById(id);

        if (entity.isPresent()) {
            return R.ok().setData(entity);
        } else {
            return R.error(CommDBEnum.SELECT_NON_EXISTENT);
        }

    }

    /**
     * Description:保存一个实体，保存之前会做检查
     *
     * @param entity 要保存的实体对象
     * @return R
     */
    public R save(T1 entity) {
        // 实体校验支持传递组规则，不传递则为Default组！
        Map<String, String> errors = ValidateModelUtil.validateModel(entity);

        if (!errors.isEmpty()) {
            return R.error(CommAPIEnum.PROPERTY_CHECK_FAILED).setData(errors);
        }

        entityService.save(entity);
        R r = R.ok().setData(entity);
        return r;

    }

    /**
     * 使用id来更新,如果属性空值,则不更新现有的值
     * @param entity
     * @return R
     */
    public R updateById(T1 entity) {
        // 检查实体的属性是否符合校验规则，使用Update组来校验实体，
        Map<String, String> errors = ValidateModelUtil.validateModel(entity, Update.class); // 可以传递多个校验组！

        if (!errors.isEmpty()) {
            return R.error(CommAPIEnum.PROPERTY_CHECK_FAILED).setData(errors);
        }

        // 检查数据记录是否已经被删除了，被删除了，则不允许更新
        Optional<T1> entityOptional = entityService.findById(entity.getId());
        if (!entityOptional.isPresent()) {
            return R.error(CommDBEnum.UPDATE_NON_EXISTENT);
        } else {
            // 检查更新时间戳，避免用旧的数据更新数据库里的新数据
            Date updateTime = entity.getUpdateTime();
            Date dbUpdateTime = entityOptional.get().getUpdateTime();
            if (updateTime != null && updateTime.compareTo(dbUpdateTime) != 0) {
                return R.error(CommDBEnum.UPDATE_NEW_BY_OLD_NOT_ALLOWED);
            }
        }
        //检查业务key的存在性，不应该存在重复的业务key,此处不知道业务key是什么属性，可以在在service层实现，重写方法即可！

        if (null != entity.getId()) {
            updateEntity(entityOptional.get(), entity, false, "createTime", "updateTime");
        }

        R r = R.ok().setData(entity);
        return r;

    }


    /**
     * Description:使用id删除指定的实体
     *
     * @param id 使用主键id
     * @return java.lang.Object
     */
    public R deleteById(ID id) {
        if (null == id) {
            return R.error(CommDBEnum.KEY_NOT_NULL);
        }

        //进行关联性检查,调用对应的方法
        // 在删除前用id到数据库查询一次,不执行空删除，不检查就可能会在数据库层面报错，尽量不让用户见到看不懂的信息
        Optional entity = entityService.findById(id);
        if (!entity.isPresent()) {
            return R.error(CommDBEnum.DELETE_NON_EXISTENT);
        }

        try {
            this.entityService.deleteById(id); // 关联关系可以在service层重写实现
        } catch (Exception e) {
            return R.error(e.getMessage());
        }

        return R.ok();
    }

    /**
     * Description:使用id的Set集合来删除指定的实体，不使用数组防止存在重复的数据
     *
     * @param entityIds 使用主键Set集合
     * @return java.lang.Object
     */
    public R deleteByIds(Set<ID> entityIds) {
        if (CollectionUtils.isEmpty(entityIds)) {
            return R.ok();
        }

        try {
            this.entityService.deleteAllById(entityIds); // 关联关系可以在service层重写实现
        } catch (Exception e) {
            return R.error(e.getMessage());
        }

        return R.ok();
    }

    /**
     * 获取map结构的数据供UI页面选择使用，例如下拉选择的key-value，支持模糊查询key
     *
     * @param key   实体的哪个属性作为key
     * @param value 实体的哪个属性作为value
     * @param q  需要模糊查询的key值
     * @return R 响应体
     */
//    public R map4PickLike(String key, String value, String q) {
//        // 构造高级查询条件
//
//
//        Map<Object, Object> map = new HashMap<>();
//
//
//        return R.ok(map);
//    }

    /**
     * 获取map结构的数据供UI页面选择使用，例如下拉选择的key-value
     *
     * @param key   实体的哪个属性作为key
     * @param value 实体的哪个属性作为value
     * @return R 响应体
     */
//    public R map4Pick(String key, String value) {
//        // 构造高级查询条件
//        Map<Object, Object> map = new HashMap<>();
//        return R.ok(map);
//    }

    /**
     * Description:根据实体编号检查实体是否存在,例如,员工工号可能不允许重复,此方法暂时没有使用
     *
     * @param entityNum 实体编号
     * @param flag      更新还是新增
     * @return java.lang.String
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
     * @param entityId 实体主键id
     * @return java.lang.String
     */
    public Boolean chkEntityIdExist(ID entityId) {
        return null != entityId && entityService.existsById(entityId);
    }


    /**
     * 获取某个属性集合,去除重复,通常是前端选择需要,支持模糊匹配
     * 非法属性自动过滤掉
     *
     * @param property 驼峰式的属性
     * @param value    模糊查询的value值
     * @return R
     */
    public R getPropertySet(String property, String value) {
        // 属性名不允许为空
        if (StringUtils.isEmpty(property)) {
            return R.error(CommDBEnum.SELECT_PROPERTY_NOT_EMPTY);
        }

        return R.ok(entityService.advanceSearchProperty(property, value));
    }

    /**
     * Description:实体的关联性检查的方法。
     * 如果存在关联性则返回true,否则返回false,这个方法只是模板，需要子类重,暂时没有使用
     *
     * @param entity 实体
     * @return java.lang.Boolean
     */
    private Boolean chkEntityRelationship(ID entityId) {
        return false;
    }

    /**
     * Description:是否使用前端的数据实体的空值属性更新数据库中的
     * true，则用空置更新;fasle则不用空值更新,还允许部分更新
     *
     * @param dbEntity         后端查询数据库的实体
     * @param entity           前端传来的实体
     * @param useNull          是否使用前端的空置更新数据库的有值属性
     * @param ignoreProperties 忽略的属性,就是前端需要用空值更新后端的属性,比如常见的:更新时间,更新时间由框架或者数据库自己维护
     * @return T1
     * @author dbdu
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
     * @param q      查询额值
     * @param fields 需要模糊匹配的字段
     * @return com.ddb.bss.base.entity.BaseEntity
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
//            entity.setRelationType(ConditionRelationEnum.OR.getValue());  // 所有条件或的关系
            entity.setConditions(conditions);
        }
        return entity;
    }

}
