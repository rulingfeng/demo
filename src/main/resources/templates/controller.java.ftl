package ${package.Controller};

import com.inm.common.bean.BaseInput;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;
import javax.annotation.Resource;
import javax.validation.Valid;
import com.github.pagehelper.PageInfo;
import ${package.Entity}.${entity};
import ${package.Service}.${table.serviceName};
import java.util.List;
import com.inm.common.bean.Result;

/**
 * @author ${author}
 * @since ${date}
 */
@RestController
<#--@RequestMapping("<#if package.ModuleName??>/${package.ModuleName}</#if>/<#if controllerMappingHyphenStyle??>${controllerMappingHyphen}<#else>${table.entityPath}</#if>")-->
@RequestMapping("/${entity?uncap_first}")
@Api(tags = "${table.comment!}")
public class ${table.controllerName}{

    @Resource
    private ${table.serviceName} ${table.serviceName?uncap_first};

    @ApiOperation(value = "新增", httpMethod = "POST")
    @PostMapping(value = "/create")
    @ResponseBody
    public Result create(@Valid @RequestBody ${entity} ${entity?uncap_first}) {
        ${table.serviceName?uncap_first}.create(${entity?uncap_first});
        return Result.success();
    }

    @ApiOperation(value = "根据ID集合批量删除", httpMethod = "POST")
    @PostMapping(value = "/deleteBatch")
    @ResponseBody
    public Result deleteBatch(@Valid @RequestBody List<Integer> ids) {
        ${table.serviceName?uncap_first}.deleteBatch(ids);
        return Result.success();
    }

    @ApiOperation(value = "修改", httpMethod = "POST")
    @PostMapping(value = "/update")
    @ResponseBody
    public Result update(@Valid @RequestBody ${entity} ${entity?uncap_first}) {
        ${table.serviceName?uncap_first}.update(${entity?uncap_first});
        return Result.success();
    }

    @ApiOperation(value = "根据主键ID查询", httpMethod = "POST")
    @PostMapping(value = "/get")
    @ResponseBody
    public Result<${entity}> get(@Valid @RequestBody BaseInput baseInput) {
        return Result.success(${table.serviceName?uncap_first}.get(baseInput.getId()));
    }

    @ApiImplicitParams({ @ApiImplicitParam(name = "pageNum", value = "当前页码", required = true, dataType = "int"),
    @ApiImplicitParam(name = "pageSize", value = "每页显示的条数", required = true, dataType = "int")})
    @ApiOperation(value = "分页查询", httpMethod = "POST")
    @PostMapping(value = "/query")
    @ResponseBody
    public Result<PageInfo<#noparse><</#noparse>${entity}>> query(${entity} ${entity?uncap_first},int pageNum,int pageSize) {
        PageInfo<#noparse><</#noparse>${entity}> pageInfo = ${table.serviceName?uncap_first}.query(${entity?uncap_first},pageNum,pageSize);
        return Result.success(pageInfo);
    }
}