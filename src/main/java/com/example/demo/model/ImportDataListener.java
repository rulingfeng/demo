package com.example.demo.model;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Author: RU
 * @Date: 2020/6/10 19:25
 */
public class ImportDataListener extends AnalysisEventListener<ExportOrderDto> {

    //可以通过实例获取该值
    private List<ExportOrderDto> datas = new ArrayList<ExportOrderDto>();
    @Override
    public void invoke(ExportOrderDto o, AnalysisContext analysisContext) {
        datas.add(o);//数据存储到list，供批量处理，或后续自己业务逻辑处理。
        doSomething(o);//根据自己业务做处理
    }

    private void doSomething(Object object) {
        //1、入库调用接口
    }

    public List<ExportOrderDto> getDatas() {
        return datas;
    }

    public void setDatas(List<ExportOrderDto> datas) {
        this.datas = datas;
    }
@Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        // datas.clear();//解析结束销毁不用的资源
    }
}
