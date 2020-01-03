package vip.efactory.ejpa.base.controller;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.io.Serializable;
import java.util.List;

/**
 * EPage,意为ejpa的Page，用途是简化jpa的原始分页对象传输到前端！
 */
@Data
public class EPage implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 页面索引，即，当前页 */
    private int pageIndex;
    /** 记录总数 */
    private long totalCount;
    /** 每页元素数  */
    private int pageSize;
    /** 总页数 */
    private int totalPage;
    /** 记录集合 */
    private List<?> content;

    public EPage(Page<?> page) {
        this.pageIndex = page.getNumber();
        this.content = page.getContent();
        this.totalCount  = page.getTotalElements();
        this.totalPage = page.getTotalPages();
        this.pageSize = page.getSize();
    }
}
