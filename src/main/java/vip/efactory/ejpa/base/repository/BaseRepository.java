package vip.efactory.ejpa.base.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import vip.efactory.ejpa.base.entity.BaseEntity;

import java.util.List;

/**
 * Description:项目自定义的一些常用的扩展
 *
 * @author dbdu
 */
@NoRepositoryBean
public interface BaseRepository<T extends BaseEntity, ID> extends JpaRepository<T, ID> {

//    /**
//     * Description:根据实体的编号，判断数据库中是否存在实体
//     *
//     * @param entityNum 实体编码
//     * @return java.lang.Boolean
//     * @author dbdu
//     */
//    Boolean existsByEntityNum(String entityNum);


    /**
     * Description:这是一个多条件动态查询的例子，类似Mybatis中动态sql的功能
     *
     * @param spec     高级条件
     * @param pageable 分页对象
     * @return 分页对象
     * @author dbdu
     */
    Page<T> findAll(Specification<T> spec, Pageable pageable);


    /**
     * Description:使用条件查询，不分页
     *
     * @param spec 高级条件
     * @return 列表集合
     * @author dbdu
     */
    List<T> findAll(Specification<T> spec);

    /**
     * Description:获取前25条数据
     *
     * @return 列表集合
     * @author dbdu
     */
    List<T> findTop25ByOrderByIdDesc();
}
