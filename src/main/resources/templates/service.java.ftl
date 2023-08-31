package ${package.Service};

import ${package.Entity}.${entity};
import ${superServiceClassPackage};
import com.github.pagehelper.PageInfo;
import java.util.List;

/**
 * @author ${author}
 * @since ${date}
 */
<#if kotlin>
interface ${table.serviceName} : ${superServiceClass}<${entity}>
<#else>
public interface ${table.serviceName} extends ${superServiceClass}<${entity}> {

    void create(${entity} ${entity?uncap_first});

    void deleteBatch(List<Integer> ids);

    void update(${entity} ${entity?uncap_first});

    ${entity} get(Integer id);

    PageInfo<${entity}> query(${entity} ${entity?uncap_first}, int pageNum, int pageSize);
}
</#if>