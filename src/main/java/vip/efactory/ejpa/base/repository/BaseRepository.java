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
 * @date 18-6-25 下午3:09
 */
@NoRepositoryBean
public interface BaseRepository<T extends BaseEntity, ID> extends JpaRepository<T, ID> {

    /**
     * Description:根据实体的编号，判断数据库中是否存在实体
     *
     * @param [entityNum]
     * @return java.lang.Boolean
     * @author dbdu
     * @date 18-6-29 上午7:35
     */
//    Boolean existsByEntityNum(String entityNum);


    /**
     * Description:这是一个多条件动态查询的例子，类似Mybatis中动态sql的功能
     *
     * @param [spec, pageable]
     * @return org.springframework.data.domain.Page<T>
     * @author dbdu
     * @date 18-7-27 下午2:38
     */
    Page<T> findAll(Specification<T> spec, Pageable pageable);


    /**
     * Description:使用条件查询，不分页
     *
     * @param [spec]
     * @return java.util.List<T>
     * @author dbdu
     * @date 18-7-27 下午2:44
     */
    List<T> findAll(Specification<T> spec);

    /**
     * Description:获取前25条数据
     *
     * @return java.util.List<T>
     * @author dbdu
     * @date 18-7-27 下午3:34
     */
    List<T> findTop25ByOrderByIdDesc();
}
