package vip.efactory.ejpa.base.controller;


import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
 */
@SuppressWarnings("all")
@Slf4j
public class BaseController<T1 extends BaseEntity, T2 extends IBaseService> {
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
     *
     * @author dbdu
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
     * @return com.ddb.bss.utils.R
     * @author dbdu
     */
    public R getByPage(Pageable page) {
        Page<T1> entities = entityService.findAll(page);
        return R.ok().setData(entities);
    }

    /**
     * Description: 高级查询加分页功能
     *
     * @param page   分页参数对象
     * @param entity 包含高级查询条件的实体
     * @return com.ddb.bss.utils.R
     * @author dbdu
     */
    public R advancedQueryByPage(Pageable page, T1 entity) {
        Page<T1> entities = entityService.advancedQuery(entity, page);
        return R.ok().setData(entities);
    }

    /**
     * Description: 高级查询不分页
     *
     * @param entity 包含高级查询条件的实体
     * @return com.ddb.bss.utils.R
     * @author dbdu
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
     * @return com.ddb.bss.utils.R
     * @author dbdu
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
     * @return com.ddb.bss.utils.R
     * @author dbdu
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
     * @param id 主键
     * @return java.lang.Object
     * @author dbdu
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
     * @param entity 要保存的实体对象
     * @return java.lang.Object
     * @author dbdu
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
     * @param entity 要更新的实体对象
     * @return com.ddb.bss.utils.R
     * @author dbdu
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
            Date updateTime = entity.getUpdateTime();
            Date dbUpdateTime = entityOptional.get().getUpdateTime();
            if (updateTime != null && updateTime.compareTo(dbUpdateTime) != 0) {
                return R.error("请获取最新的数据更新！");
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
     * @author dbdu
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
     * Description:使用id数组来删除指定的实体
     *
     * @param entityIds 使用主键数组集合
     * @return java.lang.Object
     * @author dbdu
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
     * Description:根据实体编号检查实体是否存在,例如,员工工号可能不允许重复,此方法暂时没有使用
     *
     * @param entityNum 实体编号
     * @return java.lang.String
     * @author dbdu
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
     * @author dbdu
     */
    public Boolean chkEntityIdExist(Long entityId) {
        return null != entityId && entityService.existsById(entityId);
    }


    /**
     * Description:实体的关联性检查的方法。
     * 如果存在关联性则返回true,否则返回false,这个方法只是模板，需要子类重,暂时没有使用
     *
     * @param entity 实体
     * @return java.lang.Boolean
     * @author dbdu
     */
    private Boolean chkEntityRelationship(Long entityId) {
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
     * @author dbdu
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
