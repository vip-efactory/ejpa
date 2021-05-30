package vip.efactory.ejpa.base.service.impl;


import com.querydsl.jpa.impl.JPAQueryFactory;
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
import vip.efactory.common.base.entity.BaseSearchField;
import vip.efactory.common.base.enums.ConditionRelationEnum;
import vip.efactory.common.base.enums.SearchTypeEnum;
import vip.efactory.common.base.utils.DateTimeUtil;
import vip.efactory.common.base.utils.MapUtil;
import vip.efactory.common.base.utils.SQLFilter;
import vip.efactory.ejpa.base.entity.BaseEntity;
import vip.efactory.ejpa.base.repository.BaseRepository;
import vip.efactory.ejpa.base.service.IBaseService;
import vip.efactory.ejpa.datafilter.DataFilter;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.*;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

/**
 * Description:这个类是BaseServcie的实现类，组件的实现类可以继承这个类来利用可以用的方法
 * 当然，也可以在这里添加调用前的检查逻辑
 * 复杂条件处理,可以参照:https://blog.csdn.net/J080624/article/details/84581231
 * 继承JDK中的Observable，是为了是观察者模式来处理多表缓存的一致性
 * 继承Observable，说明自己可以被别人观察，实现Observer说明自己也可以观察别人
 * Created at:2018-06-25 16:10,
 * by dbdu
 */
@SuppressWarnings("all")
@Slf4j
@Transactional
public class BaseServiceImpl<T extends BaseEntity, ID, BR extends BaseRepository> extends Observable implements IBaseService<T, ID>, Observer {
    // 默认组的名称
    private final String DEFAULT_GROUP_NAME = "DEFAULT_NO_GROUP";
    // 常见的数字类型
    private static List<String> numberTypeList;
    // 常见日期时间类型
    private static List<String> dateTypeList;

    static { // 静态初始化以便提高性能
        // 保存常见的数字类型，以便避免逐个枚举类型处理
        numberTypeList = new ArrayList<>();
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

        // 保存常见的日期时间类型
        dateTypeList = new ArrayList<>();
        dateTypeList.add("Date");
        dateTypeList.add("LocalDateTime");
        dateTypeList.add("LocalTime");
        dateTypeList.add("LocalDate");
    }

    @PersistenceContext
    protected EntityManager em;

    // QueryDSL查询工厂实体
    protected JPAQueryFactory queryFactory;

    //实例化QueryDSL的JPAQueryFactory
    @PostConstruct
    public void initFactory() {
        queryFactory = new JPAQueryFactory(em);
    }

    /**
     * Description:获取T的Class对象是关键，看构造方法
     *
     * @author dbdu
     * @date 18-12-22 下午5:15
     */
    protected Class<T> clazz = null;

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

    /**
     * Description: 高级查询带分页的功能,如果没有查询条件,仅分页返回
     *
     * @param [entity]     含有高级条件的实体
     * @param [pageable]   分页参数
     * @param [dataFilter] 数据过滤范围，优先级高于高级搜索
     * @return org.springframework.data.domain.Page<T>
     * @author dbdu
     * @date 19-7-5 下午12:27
     */
    @Override
    public Page<T> advancedQuery(T entity, Pageable pageable, DataFilter dataFilter) {
        if (entity.getConditions() != null && entity.getConditions().size() > 0) {
            //构造动态查询的条件
            Specification<T> specification = getSpecification(entity);
            return getPageByFilter(pageable, specification, dataFilter);
        } else {
            return getPageByFilter(pageable, dataFilter);
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
            if (!keys.contains(condition.getName())) {
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
            // 处理查询值的类型转换
            String fieldType = getPropType(key, entity); //直接通过当前实体或者父类来获取属性的类型
            switch (searchType) {                   // cb支持更多的方法,此处仅使用常用的!
                case 1:     //  EQ(1, "等于查询"),
                    if (numberTypeList.contains(fieldType)) {
                        fieldP = cb.equal(root.get(key), convertType4PropertyValue(fieldType, startVal));
                    } else if (dateTypeList.contains(fieldType)) {
                        if (fieldType.equalsIgnoreCase("Date")) {
                            fieldP = cb.equal(root.<Date>get(key), DateTimeUtil.getDateFromString(startVal));
                        } else if (fieldType.equalsIgnoreCase("LocalDateTime")) {
                            fieldP = cb.equal(root.<LocalDateTime>get(key), DateTimeUtil.getLocalDateTimeFromString(startVal));
                        } else if (fieldType.equalsIgnoreCase("LocalDate")) {
                            fieldP = cb.equal(root.<LocalDate>get(key), DateTimeUtil.getLocalDateFromString(startVal));
                        } else if (fieldType.equalsIgnoreCase("LocalTime")) {
                            fieldP = cb.equal(root.<LocalTime>get(key), DateTimeUtil.getLocalTimeFromString(startVal));
                        }
                    } else {
                        fieldP = cb.equal(root.get(key).as(String.class), startVal);
                    }
                    break;
                case 2:     //  RANGE(2, "范围查询"),  如果结束值大于开始值，则交换位置避免查不到数据
                    if (numberTypeList.contains(fieldType)) {
                        fieldP = getPredicate4NumberBetweenConditiong(root, cb, key, fieldType, startVal, endVal);
                    } else if (dateTypeList.contains(fieldType)) {
                        if (fieldType.equalsIgnoreCase("Date")) {
                            Date start = DateTimeUtil.getDateFromString(startVal);
                            Date end = DateTimeUtil.getDateFromString(endVal);
                            fieldP = end.compareTo(start) > 0 ? cb.between(root.<Date>get(key), start, end) : cb.between(root.<Date>get(key), end, start);
                        } else if (fieldType.equalsIgnoreCase("LocalDateTime")) {
                            LocalDateTime start = DateTimeUtil.getLocalDateTimeFromString(startVal);
                            LocalDateTime end = DateTimeUtil.getLocalDateTimeFromString(endVal);
                            fieldP = end.compareTo(start) > 0 ? cb.between(root.<LocalDateTime>get(key), start, end) : cb.between(root.<LocalDateTime>get(key), end, start);
                        } else if (fieldType.equalsIgnoreCase("LocalDate")) {
                            LocalDate start = DateTimeUtil.getLocalDateFromString(startVal);
                            LocalDate end = DateTimeUtil.getLocalDateFromString(endVal);
                            fieldP = end.compareTo(start) > 0 ? cb.between(root.<LocalDate>get(key), start, end) : cb.between(root.<LocalDate>get(key), end, start);
                        } else if (fieldType.equalsIgnoreCase("LocalTime")) {
                            LocalTime start = DateTimeUtil.getLocalTimeFromString(startVal);
                            LocalTime end = DateTimeUtil.getLocalTimeFromString(endVal);
                            fieldP = end.compareTo(start) > 0 ? cb.between(root.<LocalTime>get(key), start, end) : cb.between(root.<LocalTime>get(key), end, start);
                        }
                    } else {
                        fieldP = cb.between(root.get(key).as(String.class), startVal, endVal);
                    }
                    break;
                case 3:     //  NE(3, "不等于查询"),
                    if (numberTypeList.contains(fieldType)) {
                        fieldP = cb.notEqual(root.get(key), convertType4PropertyValue(fieldType, startVal));
                    } else if (dateTypeList.contains(fieldType)) {
                        if (fieldType.equalsIgnoreCase("Date")) {
                            Date start = DateTimeUtil.getDateFromString(startVal);
                            fieldP = cb.notEqual(root.<Date>get(key), start);
                        } else if (fieldType.equalsIgnoreCase("LocalDateTime")) {
                            LocalDateTime start = DateTimeUtil.getLocalDateTimeFromString(startVal);
                            fieldP = cb.notEqual(root.<LocalDateTime>get(key), start);
                        } else if (fieldType.equalsIgnoreCase("LocalDate")) {
                            LocalDate start = DateTimeUtil.getLocalDateFromString(startVal);
                            fieldP = cb.notEqual(root.<LocalDate>get(key), start);
                        } else if (fieldType.equalsIgnoreCase("LocalTime")) {
                            LocalTime start = DateTimeUtil.getLocalTimeFromString(startVal);
                            fieldP = cb.notEqual(root.<LocalTime>get(key), start);
                        }
                    } else {
                        fieldP = cb.notEqual(root.get(key), startVal);
                    }
                    break;
                case 4:     //  LT(4, "小于查询"),
                    if (numberTypeList.contains(fieldType)) {
                        // fieldP = cb.lessThan(root.get(key), convertType4PropertyValue(fieldType, startVal));
                        fieldP = cb.lt(root.get(key), convertType4PropertyValue(fieldType, startVal));
                    } else if (dateTypeList.contains(fieldType)) {
                        if (fieldType.equalsIgnoreCase("Date")) {
                            Date start = DateTimeUtil.getDateFromString(startVal);
                            fieldP = cb.lessThan(root.<Date>get(key), start);
                        } else if (fieldType.equalsIgnoreCase("LocalDateTime")) {
                            LocalDateTime start = DateTimeUtil.getLocalDateTimeFromString(startVal);
                            fieldP = cb.lessThan(root.<LocalDateTime>get(key), start);
                        } else if (fieldType.equalsIgnoreCase("LocalDate")) {
                            LocalDate start = DateTimeUtil.getLocalDateFromString(startVal);
                            fieldP = cb.lessThan(root.<LocalDate>get(key), start);
                        } else if (fieldType.equalsIgnoreCase("LocalTime")) {
                            LocalTime start = DateTimeUtil.getLocalTimeFromString(startVal);
                            fieldP = cb.lessThan(root.<LocalTime>get(key), start);
                        }
                    } else {
                        fieldP = cb.lessThan(root.get(key).as(String.class), startVal);
                    }
                    break;
                case 5:     //  LE(5, "小于等于查询"),
                    if (numberTypeList.contains(fieldType)) {
                        // fieldP = cb.lessThanOrEqualTo(root.get(key), convertType4PropertyValue(fieldType, startVal));
                        fieldP = cb.le(root.get(key), convertType4PropertyValue(fieldType, startVal));
                    } else if (dateTypeList.contains(fieldType)) {
                        if (fieldType.equalsIgnoreCase("Date")) {
                            Date start = DateTimeUtil.getDateFromString(startVal);
                            fieldP = cb.lessThanOrEqualTo(root.<Date>get(key), start);
                        } else if (fieldType.equalsIgnoreCase("LocalDateTime")) {
                            LocalDateTime start = DateTimeUtil.getLocalDateTimeFromString(startVal);
                            fieldP = cb.lessThanOrEqualTo(root.<LocalDateTime>get(key), start);
                        } else if (fieldType.equalsIgnoreCase("LocalDate")) {
                            LocalDate start = DateTimeUtil.getLocalDateFromString(startVal);
                            fieldP = cb.lessThanOrEqualTo(root.<LocalDate>get(key), start);
                        } else if (fieldType.equalsIgnoreCase("LocalTime")) {
                            LocalTime start = DateTimeUtil.getLocalTimeFromString(startVal);
                            fieldP = cb.lessThanOrEqualTo(root.<LocalTime>get(key), start);
                        }
                    } else {
                        fieldP = cb.lessThanOrEqualTo(root.get(key).as(String.class), startVal);
                    }
                    break;
                case 6:     //  GT(6, "大于查询"),
                    if (numberTypeList.contains(fieldType)) {
                        // fieldP = cb.greaterThan(root.get(key), convertType4PropertyValue(fieldType, startVal));
                        fieldP = cb.gt(root.get(key), convertType4PropertyValue(fieldType, startVal));
                    } else if (dateTypeList.contains(fieldType)) {
                        if (fieldType.equalsIgnoreCase("Date")) {
                            Date start = DateTimeUtil.getDateFromString(startVal);
                            fieldP = cb.greaterThan(root.<Date>get(key), start);
                        } else if (fieldType.equalsIgnoreCase("LocalDateTime")) {
                            LocalDateTime start = DateTimeUtil.getLocalDateTimeFromString(startVal);
                            fieldP = cb.greaterThan(root.<LocalDateTime>get(key), start);
                        } else if (fieldType.equalsIgnoreCase("LocalDate")) {
                            LocalDate start = DateTimeUtil.getLocalDateFromString(startVal);
                            fieldP = cb.greaterThan(root.<LocalDate>get(key), start);
                        } else if (fieldType.equalsIgnoreCase("LocalTime")) {
                            LocalTime start = DateTimeUtil.getLocalTimeFromString(startVal);
                            fieldP = cb.greaterThan(root.<LocalTime>get(key), start);
                        }
                    } else {
                        fieldP = cb.greaterThan(root.get(key).as(String.class), startVal);
                    }
                    break;
                case 7:     //  GE(7, "大于等于查询");
                    if (numberTypeList.contains(fieldType)) {
                        // fieldP = cb.greaterThanOrEqualTo(root.get(key), convertType4PropertyValue(fieldType, startVal));
                        fieldP = cb.ge(root.get(key), convertType4PropertyValue(fieldType, startVal));
                    } else if (dateTypeList.contains(fieldType)) {
                        if (fieldType.equalsIgnoreCase("Date")) {
                            Date start = DateTimeUtil.getDateFromString(startVal);
                            fieldP = cb.greaterThanOrEqualTo(root.<Date>get(key), start);
                        } else if (fieldType.equalsIgnoreCase("LocalDateTime")) {
                            LocalDateTime start = DateTimeUtil.getLocalDateTimeFromString(startVal);
                            fieldP = cb.greaterThanOrEqualTo(root.<LocalDateTime>get(key), start);
                        } else if (fieldType.equalsIgnoreCase("LocalDate")) {
                            LocalDate start = DateTimeUtil.getLocalDateFromString(startVal);
                            fieldP = cb.greaterThanOrEqualTo(root.<LocalDate>get(key), start);
                        } else if (fieldType.equalsIgnoreCase("LocalTime")) {
                            LocalTime start = DateTimeUtil.getLocalTimeFromString(startVal);
                            fieldP = cb.greaterThanOrEqualTo(root.<LocalTime>get(key), start);
                        }
                    } else {
                        fieldP = cb.greaterThanOrEqualTo(root.get(key).as(String.class), startVal);
                    }
                    break;
                case 8:     // IS_NULL(8, "Null值查询"),
                    fieldP = cb.isNull(root.get(key));
                    break;
                case 9:     // NOT_NULL(9, "非Null值查询")
                    fieldP = cb.isNotNull(root.get(key));
                    break;
                case 10:     // LEFT_LIKE(10, "左模糊查询"),
                    fieldP = cb.like(root.get(key).as(String.class), "%" + startVal);
                    break;
                case 11:     // RIGHT_LIKE(11, "右模糊查询")
                    fieldP = cb.like(root.get(key).as(String.class), startVal + "%");
                    break;
                case 12:     // IN(12, "包含查询"),   // 3.4+
                    // 切分属性值为集合
                    String[] values = startVal.split(",|;|、|，|；"); // 支持的分隔符：中英文的逗号分号，和中文的顿号！
                    List<String> valueList = Arrays.asList(values);
                    // 日期类型特殊处理
                    if (dateTypeList.contains(fieldType)) {
                        if (fieldType.equalsIgnoreCase("Date")) {
                            List<Date> valueDateList = new ArrayList<>();
                            valueList.forEach(v -> {
                                valueDateList.add(DateTimeUtil.getDateFromString(v));
                            });
                            Expression<Date> exp = root.<Date>get(key);
                            fieldP = exp.in(valueDateList);
                        } else if (fieldType.equalsIgnoreCase("LocalDateTime")) {
                            List<LocalDateTime> valueDateList = new ArrayList<>();
                            valueList.forEach(v -> {
                                valueDateList.add(DateTimeUtil.getLocalDateTimeFromString(v));
                            });
                            Expression<LocalDateTime> exp = root.<LocalDateTime>get(key);
                            fieldP = exp.in(valueDateList);
                        } else if (fieldType.equalsIgnoreCase("LocalDate")) {
                            List<LocalDate> valueDateList = new ArrayList<>();
                            valueList.forEach(v -> {
                                valueDateList.add(DateTimeUtil.getLocalDateFromString(v));
                            });
                            Expression<LocalDate> exp = root.<LocalDate>get(key);
                            fieldP = exp.in(valueDateList);
                        } else if (fieldType.equalsIgnoreCase("LocalTime")) {
                            List<LocalTime> valueDateList = new ArrayList<>();
                            valueList.forEach(v -> {
                                valueDateList.add(DateTimeUtil.getLocalTimeFromString(v));
                            });
                            Expression<LocalTime> exp = root.<LocalTime>get(key);
                            fieldP = exp.in(valueDateList);
                        }
                    } else {
                        Expression exp = root.get(key);
                        fieldP = exp.in(valueList);
                    }
                    break;
                case 13:     // NOT_IN(13, "不包含查询"),   // 不支持，啥也不做
                    break;
                case 14:     // IS_EMPTY_STRING(14, "空串查询"),
                    fieldP = cb.equal(root.get(key), "");
                    break;
                case 15:     // NOT_EMPTY_STRING(15, "非空串查询")
                    fieldP = cb.notEqual(root.get(key), "");
                    break;
                default:
                    // 0 或其他情况,则为模糊查询,FUZZY(0, "模糊查询"),
                    fieldP = cb.like(root.get(key).as(String.class), "%" + startVal + "%");
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
    private Predicate getPredicate4NumberBetweenConditiong(Root<T> root, CriteriaBuilder cb, String key, String fieldType, String startVal, String endVal) {
        if ("Byte".equalsIgnoreCase(fieldType)) {
            Byte start = Byte.valueOf(startVal);
            Byte end = Byte.valueOf(endVal);
            return end >= start ? cb.between(root.<Byte>get(key), start, end) : cb.between(root.<Byte>get(key), end, start);
        } else if ("Short".equalsIgnoreCase(fieldType)) {
            Short start = Short.valueOf(startVal);
            Short end = Short.valueOf(endVal);
            return end >= start ? cb.between(root.<Short>get(key), start, end) : cb.between(root.<Short>get(key), end, start);
        } else if ("int".equals(fieldType) || "Integer".equals(fieldType)) {
            Integer start = Integer.valueOf(startVal);
            Integer end = Integer.valueOf(endVal);
            return end >= start ? cb.between(root.<Integer>get(key), start, end) : cb.between(root.<Integer>get(key), end, start);
        } else if ("Long".equalsIgnoreCase(fieldType)) {
            Long start = Long.valueOf(startVal);
            Long end = Long.valueOf(endVal);
            return end >= start ? cb.between(root.<Long>get(key), start, end) : cb.between(root.<Long>get(key), end, start);
        } else if ("Float".equalsIgnoreCase(fieldType)) {
            Float start = Float.valueOf(startVal);
            Float end = Float.valueOf(endVal);
            return end >= start ? cb.between(root.<Float>get(key), start, end) : cb.between(root.<Float>get(key), end, start);
        } else if ("Double".equalsIgnoreCase(fieldType)) {
            Double start = Double.valueOf(startVal);
            Double end = Double.valueOf(endVal);
            return end >= start ? cb.between(root.<Double>get(key), start, end) : cb.between(root.<Double>get(key), end, start);
        } else if ("BigInteger".equalsIgnoreCase(fieldType)) {
            BigInteger start = new BigInteger(startVal);
            BigInteger end = new BigInteger(endVal);
            return end.compareTo(start) > 0 ? cb.between(root.<BigInteger>get(key), start, end) : cb.between(root.<BigInteger>get(key), end, start);
        } else if ("BigDecimal".equalsIgnoreCase(fieldType)) {
            BigDecimal start = new BigDecimal(startVal);
            BigDecimal end = new BigDecimal(endVal);
            return end.compareTo(start) > 0 ? cb.between(root.<BigDecimal>get(key), start, end) : cb.between(root.<BigDecimal>get(key), end, start);
        } else {
            return null;
        }
    }

    // ######################################################################################
    // 注意下面的三个方法是是维护多表关联查询结果缓存的一致性的，除非你知道在做什么，否则不要去修改!         #
    // 三个方法是：registObservers,notifyOthers,update                                        #
    // 此处使用了jdk自带的观察者的设计模式。  当前对象既是被观察者，也是观察者!                          #
    // ######################################################################################

    /**
     * 注册观察者,即哪些组件观察自己，让子类调用此方法实现观察者注册
     */
    @Override
    public void registObservers(Observer... observers) {
        for (Observer observer : observers) {
            this.addObserver(observer);
        }
    }

    /**
     * 自己的状态改变了，通知所有依赖自己的组件进行缓存清除，
     * 通常的增删改的方法都需要调用这个方法，来维持 cache right!
     * @param arg 通知观察者时可以传递礼物arg，即数据，如果不需要数据就传递null;
     */
    @Override
    public void notifyOthers(Object arg) {
        //注意在用Java中的Observer模式的时候下面这句话不可少
        this.setChanged();
//        if (null != arg) {
        // 然后主动通知， 这里用的是推的方式
        this.notifyObservers(arg);
//        } else {
//            // 如果用拉的方式，这么调用
//            this.notifyObservers();
//        }
    }

    /**
     * 这是观察别人，别人更新了之后来更新自己的
     * 其实此处不需要被观察者的任何数据，只是为了知道被观察者状态变了，自己的相关缓存也就需要清除了，否则不一致
     * 例如：观察Ａ对象，但是Ａ对象被删除了，那个自己这边关联查询与Ａ有关的缓存都应该清除
     * 子类重写此方法在方法前面加上清除缓存的注解，或者在方法体内具体执行一些清除缓存的代码。
     *
     * @param o   被观察的对象
     * @param arg 传递的数据
     */
    @Override
    public void update(Observable o, Object arg) {

    }

    /***************************************以下是数据范围相关的查询方法实现***************************************************/

    /**
     * 处理数据范围查询条件的查询条件转换
     */
    private Specification<T> handleDataFilterCondition(DataFilter filter) {

        return new Specification<T>() {
            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                String key = filter.getFilterPropName();
                Set<Long> valueList = filter.getDeptIds();

                Expression<Comparable> exp = root.<Comparable>get(key);
                Predicate finalPredicat = exp.in(valueList);
                return finalPredicat;
            }
        };
    }


    @Override
    public Page<T> findAll(Pageable page, DataFilter filter) {
        // TODO 检测实体是否包含过滤的属性
        Specification<T> spec = handleDataFilterCondition(filter);
        Page<T> page2 = br.findAll(spec, page);
        return page2;
    }

    @Override
    public Iterable<T> getListByFilter(Specification<T> spec, DataFilter filter) {
        Specification<T> filterSpec = handleDataFilterCondition(filter);
        Specification<T> finalSpec = filterSpec.and(spec);
        List<T> data = br.findAll(finalSpec);
        return data;
    }

    @Override
    public Page<T> getPageByFilter(Pageable pageable, DataFilter filter) {
        Specification<T> filterSpec = handleDataFilterCondition(filter);
        Page<T> page = br.findAll(filterSpec, pageable);
        return page;
    }

    @Override
    public Page<T> getPageByFilter(Pageable pageable, Specification<T> spec, DataFilter filter) {
        Specification<T> filterSpec = handleDataFilterCondition(filter);
        Specification<T> finalSpec = filterSpec.and(spec);
        Page<T> page = br.findAll(finalSpec, pageable);
        return page;
    }


//    public static void main(String[] args) {
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
//    }
}
