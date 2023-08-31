package ${package.ServiceImpl};

import ${package.Entity}.${entity};
import ${package.Mapper}.${table.mapperName};
import ${package.Service}.${table.serviceName};
import ${superServiceImplClassPackage};
import org.springframework.stereotype.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import javax.annotation.Resource;
import java.util.List;
import java.util.Date;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author ${author}
 * @since ${date}
 */
@Service
<#if kotlin>
open class ${table.serviceImplName} : ${superServiceImplClass}<${table.mapperName}, ${entity}>(), ${table.serviceName} {
}
<#else>
public class ${table.serviceImplName} extends ${superServiceImplClass}<${table.mapperName}, ${entity}> implements ${table.serviceName} {

    @Resource
    private ${table.mapperName} ${table.mapperName?uncap_first};

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(${entity} ${entity?uncap_first}){
        ${entity?uncap_first}.setCreateTime(new Date());
        ${entity?uncap_first}.setModifyTime(new Date());
        save(${entity?uncap_first});
    }

    @Override
    public void deleteBatch(List<Integer> ids) {
        removeByIds(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(${entity} ${entity?uncap_first}) {
        ${entity?uncap_first}.setModifyTime(new Date());
        updateById(${entity?uncap_first});
    }

    @Override
    public ${entity} get(Integer id) {
        ${entity} ${entity?uncap_first} = getById(id);
        return ${entity?uncap_first};
    }

    @Override
    public PageInfo<${entity}> query(${entity} ${entity?uncap_first},int pageNum,int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<${entity}> ${entity?uncap_first}List = ${table.mapperName?uncap_first}.query(${entity?uncap_first});
        PageInfo<${entity}> pageInfo = new PageInfo<>(${entity?uncap_first}List);
        return pageInfo;
    }
}
</#if>