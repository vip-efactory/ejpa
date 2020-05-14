package vip.efactory.ejpa.base.service.impl;


import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import vip.efactory.ejpa.base.entity.BaseEntity;
import vip.efactory.ejpa.base.entity.BaseSearchField;
import vip.efactory.ejpa.base.enums.ConditionRelationEnum;
import vip.efactory.ejpa.base.enums.SearchTypeEnum;
import vip.efactory.ejpa.base.repository.BaseRepository;
import vip.efactory.ejpa.base.service.IBaseService;
import vip.efactory.ejpa.utils.DateTimeUtil;
import vip.efactory.ejpa.utils.MapUtil;
import vip.efactory.ejpa.utils.SQLFilter;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.*;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * Description:这个类是BaseServcie的实现类，组件的实现类可以继承这个类来利用可以用的方法
 * 当然，也可以在这里添加调用前的检查逻辑
 * 复杂条件处理,可以参照:https://blog.csdn.net/J080624/article/details/84581231
 * Created at:2018-06-25 16:10,
 * by dbdu
 */
@SuppressWarnings("all")
@Slf4j
@Transactional
public class BaseServiceImpl<T extends BaseEntity, ID, BR extends BaseRepository> implements IBaseService<T, ID> {
    private final String DEFAULT_GROUP_NAME = "DEFAULT_NO_GROUP"; //默认组的名称
    private static List<String> numberTypeList;

    static { // 静态初始化以便提高性能
        numberTypeList = new ArrayList<>();  //保存常见的数字类型，以便避免逐个枚举类型处理
        numberTypeList.add("byte");
        numberTypeList.add("Byte");
        numberTypeList.add("short");
        numberTypeList.add("Short");
        numberTypeList.add("int");
        numberTypeList.add("Integer");
        numberTypeList.add("Long");
        numberTypeList.add("long");
        numberTypeList.add("float");
        numberTypeList.add("Float");
        numberTypeList.add("double");
        numberTypeList.add("Double");
        numberTypeList.add("BigInteger");
        numberTypeList.add("BigDecimal");
        // numberTypeList.add("AtomicInteger"); // 注释掉就说明JPA目前还不支持这些类型，类型来源于JDK
        // numberTypeList.add("AtomicLong");
        // numberTypeList.add("DoubleAccumulator");
        // numberTypeList.add("DoubleAdder");
        // numberTypeList.add("LongAccumulator");
        // numberTypeList.add("LongAdder");
    }

    @PersistenceContext
    EntityManager em;

    /**
     * Description:获取T的Class对象是关键，看构造方法
     *
     * @author dbdu
     * @date 18-12-22 下午5:15
     */
    private Class<T> clazz = null;

    /**
     * Description:注入需要的Repository接口的代理类
     *
     * @author dbdu
     * @date 18-6-25 下午4:14
     */
    @Autowired
    public BR br;

    /**
     * Description:无参构造函数，获得T1的clazz对象
     *
     * @author dbdu
     * @date 18-6-27 上午8:19
     */
    public BaseServiceImpl() {
        //为了得到T1的Class，采用如下方法
        //1得到该泛型类的子类对象的Class对象
        Class clz = this.getClass();
        //2得到子类对象的泛型父类类型（也就是BaseDaoImpl<T>）
        ParameterizedType type = (ParameterizedType) clz.getGenericSuperclass();
        Type[] types = type.getActualTypeArguments();
        clazz = (Class<T>) types[0];
    }


    @Override
    public Iterable<T> findAll() {
        return br.findAll();
    }

    @Override
    public Page<T> findAll(Pageable var1) {
        return br.findAll(var1);
    }

    @Override
    public Iterable<T> findAll(Sort var1) {
        return br.findAll(var1);
    }

    @Override
    public Iterable<T> findAllById(Iterable<ID> var1) {
        return br.findAllById(var1);
    }

    @Override
    //@CachePut(value = "className", key = "this.className+'--' +#id.keyString()")
    public T getOne(ID id) {
        return (T) br.getOne(id);
    }

    @Override
    public Optional<T> findById(ID var1) {
        return br.findById(var1);
    }

    @Override
    public <S extends T> Optional<S> findOne(Example<S> example) {
        return br.findOne(example);
    }

    @Override
    public <S extends T> List<S> findAll(Example<S> var1) {
        return br.findAll(var1);
    }

    @Override
    public <S extends T> List<S> findAll(Example<S> var1, Sort var2) {
        return br.findAll(var1, var2);
    }

    @Override
    public <S extends T> Page<S> findAll(Example<S> example, Pageable pageable) {
        return br.findAll(example, pageable);
    }

    @Override
    public <S extends T> Iterable<S> saveAll(Iterable<S> var1) {
        return br.saveAll(var1);
    }

    @Override
    public void flush() {
        br.flush();
    }

    @Override
    public <S extends T> S saveAndFlush(S var1) {
        return (S) br.saveAndFlush(var1);
    }

    @Override
    public <S extends T> S save(S var1) {
        return (S) br.save(var1);
    }

    @Override
    public void delete(T var1) {
        br.delete(var1);
    }

    @Override
    public void deleteById(ID var1) {
        br.deleteById(var1);
    }

    @Override
    public void deleteAll() {
        br.deleteAll();
    }

    @Override
    public void deleteAll(Iterable<? extends T> var1) {
        br.deleteAll(var1);
    }

    @Override
    public void deleteAllInBatch() {
        br.deleteAllInBatch();
    }

    @Override
    public void deleteInBatch(Iterable<T> var1) {
        br.deleteInBatch(var1);
    }

    @Override
    public boolean existsById(ID var1) {
        return br.existsById(var1);
    }

    @Override
    public long count() {
        return br.count();
    }

    @Override
    public <S extends T> long count(Example<S> example) {
        return br.count(example);
    }

    @Override
    public <S extends T> boolean exists(Example<S> example) {
        return br.exists(example);
    }

    //以下为自定义的方法：

    @Override
    public boolean existsByEntityProperty(String propertyName, String propertyValue) throws NoSuchFieldException {
        // 检查属性名是否合法，不合法抛异常
        if (isPropertyIllegal(propertyName)) {
            throw new NoSuchFieldException(propertyName + "不存在！");
        }
        // 查询该属性值的记录是否存在
        Set resultSet = advanceSearchProperty(propertyName, propertyValue);
        return resultSet.size() > 0 ? true : false;
    }

    @Override
    public int deleteAllById(Iterable<ID> var1) {
        String hql = "delete from " + clazz.getSimpleName() + " t where t.id in (?1)";
        Query query = em.createQuery(hql);
        query.setParameter(1, var1);
        int result = query.executeUpdate();
        return result;
    }

    @Override
    public <S extends T> S update(S var1) {
        return (S) br.save(var1);
    }

    //   @Override
//    public Boolean existsByEntityNum(String entityNum) {
//        return br.existsByEntityNum(entityNum);
//    }


    /**
     * Description: 不分页的高级查询,无条件时,最多返回最新的25条记录
     *
     * @param [entity]
     * @return java.util.List<T>
     * @author dbdu
     * @date 19-7-5 下午12:26
     */
    @Override
    public List<T> advancedQuery(T entity) {
        if (entity.getConditions() != null && entity.getConditions().size() > 0) {
            return br.findAll(getSpecification(entity));
        } else {
            //返回前25条数据！
            return br.findTop25ByOrderByIdDesc();
        }

    }

    /**
     * Description: 高级查询带分页的功能,如果没有查询条件,仅分页返回
     *
     * @param [entity, pageable]
     * @return org.springframework.data.domain.Page<T>
     * @author dbdu
     * @date 19-7-5 下午12:27
     */
    @Override
    public Page<T> advancedQuery(T entity, Pageable pageable) {
        if (entity.getConditions() != null && entity.getConditions().size() > 0) {
            //构造动态查询的条件
            Specification<T> specification = getSpecification(entity);
            return br.findAll(specification, pageable);
        } else {
            return br.findAll(pageable);
        }
    }

    @Override
    public Set advanceSearchProperty(String property, String value) {
        // 检查属性名是否合法
        if (isPropertyIllegal(property)) {
            return new HashSet();
        }
        // 构造查询条件
        Specification<Object> specification = getSpec4PropSetByLike(property, value);
        List<Object> result = br.findAll(specification);

        if (result != null && result.size() > 0) {
            return new TreeSet<Object>(result); // 去除重复数据
        }

        return new TreeSet();
    }


    /**
     * Description:检查属性名和属性值的合法性,不合法的属性和值都会被移除
     * 支持级联属性，例如:a.b.c.d.e   最多5级关联，即4个级联点属性
     *
     * @param [entity]
     * @return void
     * @author dbdu
     * @date 19-7-5 下午12:31
     */
    private void checkPropertyAndValueValidity(T entity) {
        Set<BaseSearchField> conditions = entity.getConditions();
        if (conditions == null || conditions.size() == 0) {
            return;
        }

        // 检查属性名是否合法 非法
        Set<BaseSearchField> illegalConditions = new HashSet<>();        //存放非法的查询条件
        Map<String, String> properties = (Map<String, String>) MapUtil.objectToMap1(entity);
        Set<String> keys = properties.keySet();
        // 如果条件的字段名称与属性名不符，则移除，不作为选择条件；
        conditions.forEach(condition -> {
            // 条件的名称里不包含点，即级联属性，也不是查询类型的属性时为非法条件
            if (!keys.contains(condition.getName()) && !condition.getName().contains(".")) {
                illegalConditions.add(condition);
            } else if (condition.getName().contains(".") && !validateCascadingProperty(condition.getName())) { // 含有类似 a.b.c这样的级联属性
                illegalConditions.add(condition);
            }
        });
        // 移除非法的条件
        conditions.removeAll(illegalConditions);

        //继续检查条件的值是否有非法敏感的关键字
        conditions.forEach(condition -> {
            String value1 = condition.getVal();
            if (SQLFilter.sqlInject(value1)) {
                illegalConditions.add(condition);
            }

            // 如果是范围需要检查两个值是否合法
            int searchType = condition.getSearchType() == null ? 0 : condition.getSearchType(); // searchType 用户可以不写,不写默认为0,模糊查询
            if (SearchTypeEnum.RANGE.getValue() == searchType) {
                String value2 = condition.getVal2();
                if (SQLFilter.sqlInject(value2)) {
                    illegalConditions.add(condition);
                }
            }
        });

        // 移除非法条件
        conditions.removeAll(illegalConditions);
    }

    /**
     * Description: 根据条件集合构建查询的表达式
     *
     * @param [entity]
     * @return org.springframework.data.jpa.domain.Specification<T>
     * @author dbdu
     * @date 19-7-5 下午12:25
     */
    private Specification<T> getSpecification(T entity) {
        Set<BaseSearchField> conditions = entity.getConditions();
        // 检查条件是否合法,移除非法的条件
        checkPropertyAndValueValidity(entity);
        // 将条件按照各自所在的组进行分组
        Map<String, List<BaseSearchField>> groups = checkHasGroup(conditions);
        // 判断条件是否只有一个默认组，若是一个组，则说明没有组
        if (groups.size() == 1) {
            return handleSingleGroupCondition(groups.get(DEFAULT_GROUP_NAME), entity);
        } else {
            // 有多个组
            return handleGroupsCondition(groups, entity);
        }
    }

    /**
     * 构造查询条件: 获取某个属性的模糊查询匹配的不重复集合,通常数据给ui界面选择使用
     *
     * @param property 要查询的属性
     * @param value    属性值,可以为空串
     * @return
     */
    private Specification<Object> getSpec4PropSetByLike(String property, String value) {
        return new Specification<Object>() {
            @Override
            public Predicate toPredicate(Root<Object> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Path pp = root.get(property);
                Predicate predicate = cb.like(pp, "%" + value + "%");
                query.where(predicate);     // 添加查询条件
                query.select(pp);           // 选择的属性,此处仅一个属性
                query.distinct(true);       // 不允许重复
                Predicate restriction = query.getRestriction();
                return restriction;
            }
        };
    }


    /**
     * Description:利用反射获取属性的类型
     *
     * @param [key, entity]
     * @return java.lang.String
     * @author dbdu
     * @date 19-7-7 下午6:10
     */
    private String getPropType(String key, T entity) {
        Class clazz = entity.getClass();
        List<Field> fieldList = new ArrayList<>();  //存
        while (clazz != null) {
            fieldList.addAll(new ArrayList<>(Arrays.asList(clazz.getDeclaredFields())));
            clazz = clazz.getSuperclass();
        }

        for (Field field : fieldList) {
            if (field.getName().equals(key)) {
                return field.getType().getSimpleName();
            }
        }

        return "";
    }

    /**
     * 检查属性名是否非法
     *
     * @param 属性名
     * @return true--非法;false--合法
     */
    @SneakyThrows
    private boolean isPropertyIllegal(String property) {
        // 检查属性名是否合法 非法
        // k为属性名,v为属性值
        Map<String, String> properties = (Map<String, String>) MapUtil.objectToMap1(clazz.newInstance());
        return properties.keySet().contains(property) ? false : true;
    }

    /**
     * 处理多个分组条件的，条件查询构造
     */
    private Specification<T> handleGroupsCondition(Map<String, List<BaseSearchField>> groups, T entity) {
        return new Specification<T>() {
            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                // 先处理默认组
                Predicate defaultGroupP = genPredicate4SingleGroup(groups.get(DEFAULT_GROUP_NAME), root, cb, entity);
                // 处理其他组
                for (Map.Entry<String, List<BaseSearchField>> entry : groups.entrySet()) {
                    if (DEFAULT_GROUP_NAME.equalsIgnoreCase(entry.getKey())) {
                        continue;
                    }

                    Predicate tmpGroupP = genPredicate4SingleGroup(entry.getValue(), root, cb, entity);
                    if (tmpGroupP == null) { // 若也为空则没有必要继续进行了！
                        continue;
                    }

                    // 从组内的一个条件里找到组的逻辑关系
                    if (defaultGroupP == null) { // 当默认组条件为空时，defaultGroupP为null，不处理会导致空指针异常！
                        defaultGroupP = tmpGroupP;
                    } else {
                        Integer logicalTypeGroup = entry.getValue().get(0).getLogicalTypeGroup();
                        if (logicalTypeGroup == ConditionRelationEnum.AND.getValue()) {
                            defaultGroupP = cb.and(defaultGroupP, tmpGroupP);
                        } else {
                            defaultGroupP = cb.or(defaultGroupP, tmpGroupP);
                        }
                    }
                }

                return defaultGroupP;
            }
        };
    }

    /**
     * 处理同一个组内查询条件的查询条件转换
     */
    private Specification<T> handleSingleGroupCondition(List<BaseSearchField> fields, T entity) {

        return new Specification<T>() {
            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return genPredicate4SingleGroup(fields, root, cb, entity);
            }
        };

    }

    /**
     * 处理单个组内的条件生成
     */
    private Predicate genPredicate4SingleGroup(List<BaseSearchField> fields, Root<T> root, CriteriaBuilder cb, T entity) {
        Predicate finalPredicat = null;
        int count = fields.size();
        Predicate fieldP = null;
        for (int i = 0; i < count; i++) {
            BaseSearchField field = fields.get(i);
            // 得到条件的搜索类型，若为空默认模糊搜索
            int searchType = field.getSearchType() == null ? 0 : field.getSearchType();
            String key = field.getName();
            String startVal = field.getVal();      // 开始值
            String endVal = field.getVal2();    // 结束值

            // 获取keyPath查询路径,是级联属性就通过级联方法获取，否则通过属性获取
            Path<T> keyPath = key.contains(".") ? getPathExpressionFromKey(key, root) : root.get(key);
            // 处理查询值的类型,如果不是级联属性直接通过当前实体或者父类来获取属性的类型
            String fieldType = key.contains(".") ? getFinalCascadingPropertyType(key) : getPropType(key, entity);
            switch (searchType) {                   // cb支持更多的方法,此处仅使用常用的!
                case 1:     //  EQ(1, "等于查询"),
                    if (numberTypeList.contains(fieldType)) {
                        fieldP = cb.equal(keyPath, convertType4PropertyValue(fieldType, startVal));
                    } else if (fieldType.equalsIgnoreCase("Date")) {
                        fieldP = cb.equal(keyPath.as(Date.class), DateTimeUtil.getDateFromString(startVal));
                    } else {
                        fieldP = cb.equal(keyPath.as(String.class), startVal);
                    }
                    break;
                case 2:     //  RANGE(2, "范围查询"),  如果结束值大于开始值，则交换位置避免查不到数据
                    if (numberTypeList.contains(fieldType)) {
                        fieldP = getPredicate4NumberBetweenConditiong(root, cb, keyPath, fieldType, startVal, endVal);
                    } else if (fieldType.equalsIgnoreCase("Date")) {
                        Date start = DateTimeUtil.getDateFromString(startVal);
                        Date end = DateTimeUtil.getDateFromString(endVal);
                        fieldP = end.compareTo(start) > 0 ? cb.between(keyPath.as(Date.class), start, end) : cb.between(keyPath.as(Date.class), end, start);
                    } else {
                        fieldP = cb.between(keyPath.as(String.class), startVal, endVal);
                    }
                    break;
                case 3:     //  NE(3, "不等于查询"),
                    if (numberTypeList.contains(fieldType)) {
                        fieldP = cb.notEqual(keyPath, convertType4PropertyValue(fieldType, startVal));
                    } else if (fieldType.equalsIgnoreCase("Date")) {
                        fieldP = cb.notEqual(keyPath.as(Date.class), DateTimeUtil.getDateFromString(startVal));
                    } else {
                        fieldP = cb.notEqual(keyPath, startVal);
                    }
                    break;
                case 4:     //  LT(4, "小于查询"),
                    if (numberTypeList.contains(fieldType)) {
//                        fieldP = cb.lessThan(keyPath, convertType4PropertyValue(fieldType, startVal));
                        fieldP = cb.lt(keyPath.as(Number.class), convertType4PropertyValue(fieldType, startVal));
                    } else if (fieldType.equalsIgnoreCase("Date")) {
                        fieldP = cb.lessThan(keyPath.as(Date.class), DateTimeUtil.getDateFromString(startVal));
                    } else {
                        fieldP = cb.lessThan(keyPath.as(String.class), startVal);
                    }
                    break;
                case 5:     //  LE(5, "小于等于查询"),
                    if (numberTypeList.contains(fieldType)) {
                        // fieldP = cb.lessThanOrEqualTo(root.get(key), convertType4PropertyValue(fieldType, startVal));
                        fieldP = cb.le(keyPath.as(Number.class), convertType4PropertyValue(fieldType, startVal));
                    } else if (fieldType.equalsIgnoreCase("Date")) {
                        fieldP = cb.lessThanOrEqualTo(keyPath.as(Date.class), DateTimeUtil.getDateFromString(startVal));
                    } else {
                        fieldP = cb.lessThanOrEqualTo(keyPath.as(String.class), startVal);
                    }
                    break;
                case 6:     //  GT(6, "大于查询"),
                    if (numberTypeList.contains(fieldType)) {
                        // fieldP = cb.greaterThan(root.get(key), convertType4PropertyValue(fieldType, startVal));
                        fieldP = cb.gt(keyPath.as(Number.class), convertType4PropertyValue(fieldType, startVal));
                    } else if (fieldType.equalsIgnoreCase("Date")) {
                        fieldP = cb.greaterThan(keyPath.as(Date.class), DateTimeUtil.getDateFromString(startVal));
                    } else {
                        fieldP = cb.greaterThan(keyPath.as(String.class), startVal);
                    }
                    break;
                case 7:     //  GE(7, "大于等于查询");
                    if (numberTypeList.contains(fieldType)) {
                        // fieldP = cb.greaterThanOrEqualTo(root.get(key), convertType4PropertyValue(fieldType, startVal));
                        fieldP = cb.ge(keyPath.as(Number.class), convertType4PropertyValue(fieldType, startVal));
                    } else if (fieldType.equalsIgnoreCase("Date")) {
                        fieldP = cb.greaterThanOrEqualTo(keyPath.as(Date.class), DateTimeUtil.getDateFromString(startVal));
                    } else {
                        fieldP = cb.greaterThanOrEqualTo(keyPath.as(String.class), startVal);
                    }
                    break;
                case 8:     // IS_NULL(8, "Null值查询"),
                    fieldP = cb.isNull(keyPath);
                    break;
                case 9:     // NOT_NULL(9, "非Null值查询")
                    fieldP = cb.isNotNull(keyPath);
                    break;
                case 10:     // LEFT_LIKE(10, "左模糊查询"),
                    fieldP = cb.like(keyPath.as(String.class), "%" + startVal);
                    break;
                case 11:     // RIGHT_LIKE(11, "右模糊查询")
                    fieldP = cb.like(keyPath.as(String.class), startVal + "%");
                    break;
                case 12:     // IN(12, "包含查询"),   // 3.4+
                    // 切分属性值为集合
                    String[] values = startVal.split(",|;|、|，|；"); // 支持的分隔符：中英文的逗号分号，和中文的顿号！
                    List<String> valueList = Arrays.asList(values);
                    // 日期类型特殊处理
                    if (fieldType.equalsIgnoreCase("Date")) {
                        List<Date> valueDateList = new ArrayList<>();
                        valueList.forEach(v -> {
                            valueDateList.add(DateTimeUtil.getDateFromString(v));
                        });
                        Expression<Date> exp = keyPath.as(Date.class);
                        fieldP = exp.in(valueDateList);
                    } else {
                        Expression exp = root.get(key);
                        fieldP = exp.in(valueList);
                    }
                    break;
                default:
                    // 0 或其他情况,则为模糊查询,FUZZY(0, "模糊查询"),
                    fieldP = cb.like(keyPath.as(String.class), "%" + startVal + "%");
            }

            if (i == 0) { // 第一个直接赋值
                finalPredicat = fieldP;
            } else {
                // 获取当前条件的逻辑类型,即和上一个条件之间的关系，是或还是与
                Integer logicalType = field.getLogicalType();
                if (logicalType == ConditionRelationEnum.AND.getValue()) {
                    finalPredicat = cb.and(finalPredicat, fieldP);
                } else { // 其他为 logicalType == ConditionRelationEnum.OR.getValue()
                    finalPredicat = cb.or(finalPredicat, fieldP);
                }
            }
        }

        return finalPredicat;
    }

    /**
     * 检测条件中是否含有分组信息，例如：类似这样的条件：（A=3 || B=4） && （ C= 5 || D=6）
     */
    private Map<String, List<BaseSearchField>> checkHasGroup(Set<BaseSearchField> conditions) {
        Map<String, List<BaseSearchField>> groups = new HashMap<>();
        groups.put(DEFAULT_GROUP_NAME, new ArrayList<BaseSearchField>()); //存放没有明确分组的条件

        // 遍历所有的条件进行分组
        for (BaseSearchField searchField : conditions) {
            String groupName = searchField.getBracketsGroup();

            if (StringUtils.isEmpty(groupName)) { // 条件没有分组信息
                groups.get(DEFAULT_GROUP_NAME).add(searchField);
            } else { // 条件有分组信息
                // 检查groups是否有此分组，有则用，没有则创建
                if (groups.get(groupName) == null) {
                    groups.put(groupName, new ArrayList<BaseSearchField>()); //创建新的分组，
                }
                groups.get(groupName).add(searchField);    // 再将条件放进去
            }
        }

        // 对所有的分组按照 order排序
        for (Map.Entry<String, List<BaseSearchField>> entry : groups.entrySet()) {
            entry.getValue().sort(Comparator.comparingInt(BaseSearchField::getOrder));  // 条件排序,排序后默认是升序
        }

        return groups;
    }

    // 将查询条件的值转换为对应类型的值
    private Number convertType4PropertyValue(String type, String value) {
        if ("Byte".equalsIgnoreCase(type)) {
            return Byte.valueOf(value);
        } else if ("Short".equalsIgnoreCase(type)) {
            return Short.valueOf(value);
        } else if ("int".equals(type) || "Integer".equals(type)) {
            return Integer.valueOf(value);
        } else if ("Long".equalsIgnoreCase(type)) {
            return Long.valueOf(value);
        } else if ("Float".equalsIgnoreCase(type)) {
            return Float.valueOf(value);
        } else if ("Double".equalsIgnoreCase(type)) {
            return Double.valueOf(value);
        } else if ("BigInteger".equalsIgnoreCase(type)) {
            return new BigInteger(value);
        } else if ("BigDecimal".equalsIgnoreCase(type)) {
            return new BigDecimal(value);
        } else {
            return null;
        }
    }

    /**
     * 专门用于处理Number子类的区间查询条件的生成，此处之所以枚举类型，是因为内置的不支持这种的泛型！
     *
     * @param root
     * @param cb
     * @param key
     * @param fieldType
     * @param startVal
     * @param endVal
     * @return
     */
    private Predicate getPredicate4NumberBetweenConditiong(Root<T> root, CriteriaBuilder cb, Path<T> keyPath, String fieldType, String startVal, String endVal) {
        if ("Byte".equalsIgnoreCase(fieldType)) {
            Byte start = Byte.valueOf(startVal);
            Byte end = Byte.valueOf(endVal);
            return end >= start ? cb.between(keyPath.as(Byte.class), start, end) : cb.between(keyPath.as(Byte.class), end, start);
        } else if ("Short".equalsIgnoreCase(fieldType)) {
            Short start = Short.valueOf(startVal);
            Short end = Short.valueOf(endVal);
            return end >= start ? cb.between(keyPath.as(Short.class), start, end) : cb.between(keyPath.as(Short.class), end, start);
        } else if ("int".equals(fieldType) || "Integer".equals(fieldType)) {
            Integer start = Integer.valueOf(startVal);
            Integer end = Integer.valueOf(endVal);
            return end >= start ? cb.between(keyPath.as(Integer.class), start, end) : cb.between(keyPath.as(Integer.class), end, start);
        } else if ("Long".equalsIgnoreCase(fieldType)) {
            Long start = Long.valueOf(startVal);
            Long end = Long.valueOf(endVal);
            return end >= start ? cb.between(keyPath.as(Long.class), start, end) : cb.between(keyPath.as(Long.class), end, start);
        } else if ("Float".equalsIgnoreCase(fieldType)) {
            Float start = Float.valueOf(startVal);
            Float end = Float.valueOf(endVal);
            return end >= start ? cb.between(keyPath.as(Float.class), start, end) : cb.between(keyPath.as(Float.class), end, start);
        } else if ("Double".equalsIgnoreCase(fieldType)) {
            Double start = Double.valueOf(startVal);
            Double end = Double.valueOf(endVal);
            return end >= start ? cb.between(keyPath.as(Double.class), start, end) : cb.between(keyPath.as(Double.class), end, start);
        } else if ("BigInteger".equalsIgnoreCase(fieldType)) {
            BigInteger start = new BigInteger(startVal);
            BigInteger end = new BigInteger(endVal);
            return end.compareTo(start) > 0 ? cb.between(keyPath.as(BigInteger.class), start, end) : cb.between(keyPath.as(BigInteger.class), end, start);
        } else if ("BigDecimal".equalsIgnoreCase(fieldType)) {
            BigDecimal start = new BigDecimal(startVal);
            BigDecimal end = new BigDecimal(endVal);
            return end.compareTo(start) > 0 ? cb.between(keyPath.as(BigDecimal.class), start, end) : cb.between(keyPath.as(BigDecimal.class), end, start);
        } else {
            return null;
        }
    }

    /**
     * 检查级联属性是否合法，例如：级联属性a.b.c.d.e各层是否都存在。  最多支持4个级联点,多于5个则认为不合法
     *
     * @param conditionName
     * @return false 不合法; true 合法。
     */
    @SneakyThrows
    private boolean validateCascadingProperty(String conditionName) {
        // 用点切分属性
        String[] props = conditionName.split("\\.");
        // 多于5个则认为不合法
        int propLength = props.length;
        if (propLength > 5) {
            log.warn("基于性能考虑，级联查询属性不允许超过5级!");
            return false;
        }
        // 检查切分出来的各层属性是否都存在
        // 第1层属性a
        if (isPropertyIllegal(props[0])) {
            return false;
        }
        // a的属性反射字段类型
        Field propTypeA = clazz.getDeclaredField(props[0]);

        // 第2层属性b
        // 得到a的clazz
        Class clazzA = propTypeA.getType();
        // 从a类型的clazz中检查b属性名是否存在
        Field propTypeB;
        try {
            propTypeB = clazzA.getDeclaredField(props[1]);
        } catch (NoSuchFieldException nsfe) {
            log.warn("级联查询属性{}不存在!", props[0] + "." + props[1]);
            return false;
        }
        if (propLength == 2) { // 没有c，例如只是a.b级联属性
            return true;
        }

        // 第3层属性c
        // 得到b的clazz，然后检查c
        Class clazzB = propTypeB.getType();
        Field propTypeC;
        try {
            propTypeC = clazzB.getDeclaredField(props[2]);
        } catch (NoSuchFieldException nsfe) {
            StringBuffer sb = new StringBuffer(props[0]).append(".").append(props[1]).append(".").append(props[2]);
            log.warn("级联查询属性{}不存在!", sb.toString());
            return false;
        }
        if (propLength == 3) { // 没有d，例如只是a.b.c级联属性
            return true;
        }

        // 第4层属性d
        // 得到c的clazz，然后检查d
        Class clazzC = propTypeC.getType();
        Field propTypeD;
        try {
            propTypeD = clazzC.getDeclaredField(props[3]);
        } catch (NoSuchFieldException nsfe) {
            StringBuffer sb = new StringBuffer(props[0]).append(".").append(props[1])
                    .append(".").append(props[2])
                    .append(".").append(props[3]);
            log.warn("级联查询属性{}不存在!", sb.toString());
            return false;
        }
        if (propLength == 4) { // 没有e，例如只是a.b.c.d级联属性
            return true;
        }

        // 第5层属性e
        // 得到d的clazz，然后检查e
        Class clazzD = propTypeD.getType();
        Field propTypeE;
        try {
            propTypeE = clazzD.getDeclaredField(props[4]);
        } catch (NoSuchFieldException nsfe) {
            StringBuffer sb = new StringBuffer(props[0]).append(".").append(props[1])
                    .append(".").append(props[2])
                    .append(".").append(props[3])
                    .append(".").append(props[4]);
            log.warn("级联查询属性{}不存在!", sb.toString());
            return false;
        }
        return true;
    }

    /**
     * 处理级联属性，转换a.b.c.d.e为对应的path
     *
     * @param key  是必须含有点的级联属性
     * @param root
     * @return
     */
    private Path<T> getPathExpressionFromKey(String key, Root<T> root) {
        // 如Task的名为"user.name"的filedName, 转换为Task.user.name属性
        String[] props = StringUtils.split(key, "\\.");
        Path<T> expression = root.get(props[0]);
        for (int i = 1; i < props.length; i++) {
            expression = expression.get(props[i]);
        }
        return expression;
    }

    /**
     * 获取级联属性的最终属性的数据类型，例如a.b.c的属性，那么将返回最终属性c的数据类型，如String/Date/Integer等的字符串
     *
     * @param key 是必须含有点的级联属性
     * @return
     */
    @SneakyThrows
    private String getFinalCascadingPropertyType(String key) {
        // 用点切分属性
        String[] props = key.split("\\.");
        int propLength = props.length;
        // a的属性反射字段类型
        Field propTypeA = clazz.getDeclaredField(props[0]);
        if (propLength == 1) { // 没有b，例如只是a属性,返回a的数据类型的字符串
            return propTypeA.getType().getSimpleName();
        }

        // 得到a的clazz，检查b
        Class clazzA = propTypeA.getType();
        // 从a类型的clazz中检查b属性名是否存在
        Field propTypeB = clazzA.getDeclaredField(props[1]);
        if (propLength == 2) { // 没有c，例如只是a.b级联属性,返回b的数据类型的字符串
            return propTypeB.getType().getSimpleName();
        }

        // 得到b的clazz，然后检查c
        Class clazzB = propTypeB.getType();
        Field propTypeC = clazzB.getDeclaredField(props[2]);
        if (propLength == 3) { // 没有d，例如只是a.b.c级联属性，返回c的数据类型的字符串
            return propTypeC.getType().getSimpleName();
        }

        // 得到c的clazz，然后检查d
        Class clazzC = propTypeC.getType();
        Field propTypeD = clazzC.getDeclaredField(props[3]);
        if (propLength == 4) { // 没有e，例如只是a.b.c.d级联属性，返回d的数据类型的字符串
            return propTypeD.getType().getSimpleName();
        }

        // 得到d的clazz，然后检查e
        Class clazzD = propTypeD.getType();
        Field propTypeE = clazzD.getDeclaredField(props[4]);
        return propTypeE.getType().getSimpleName(); // 返回e的数据类型的字符串
    }

    public static void main(String[] args) {
//        Set<BaseSearchField> conditions = new HashSet<BaseSearchField>();
//        for (int i = 0; i < 15; i++) {
//            BaseSearchField condition = new BaseSearchField();
//            condition.setName("age");
//            condition.setOrder(i);
//            condition.setVal("" + 20 + i);
//            conditions.add(condition);
//        }
//        // 所有的条件按照order排序
//        ArrayList<BaseSearchField> fields = new ArrayList<>(conditions);
//        fields.sort(Comparator.comparingInt(BaseSearchField::getOrder));  // 条件排序
//        System.out.println(fields);
//
//        String val = "AA,BB;CC、DD；EE，FF";
//        String[] tmp = val.split(",|;|、|，|；");
//        System.out.println(tmp);

    }

}
