package com.warehouse.controllers;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.ss.controllers.BaseCtrl;
import com.ss.services.MaterialTypeService;
import com.ss.stock.services.SecurityStockService;
import com.utils.HanyuPinyinHelper;
import utils.bean.JsonHashMap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 仓库库存原材料树
 */
public class WarehouseStockMaterialTreeCtrl extends BaseCtrl{
    public void index(){
        JsonHashMap jhm=new JsonHashMap();
        String sql="select id,parent_id,code,name as label from material_type order by sort,id";
        String warehouseStockSql="select CONCAT(a.material_id,'-',batch_code) as id,'' as tid,a.material_id,a.code,a.name,CONCAT(a.name,'(',batch_code,')') as label,a.batch_code,a.number as warehouseStockNumber,(select name from goods_attribute where id=b.attribute_2) as attribute_2_text,(SELECT goods_unit.name FROM goods_unit WHERE id=b.unit) unit_text,0 as want_num,0 as send_number,CONCAT(a.name,'-',batch_code,'-',b.pinyin) as search_text,b.type_2 from warehouse_stock a left join material b on a.material_id=b.id order by a.material_id,a.batch_code,a.id";
        try {

            List<Record> typeList = Db.find(sql);
            for(Record r:typeList){
                String name=r.getStr("label");
                String pinyin= HanyuPinyinHelper.getFirstLettersLo(name);
                r.set("search_text",name+"-"+pinyin);
            }

            List<Record> warehouseStockList=Db.find(warehouseStockSql);
            //获取安存
            Map securityStock= SecurityStockService.getSecurityStockMap();

            Iterator<Record> it=warehouseStockList.iterator();
            while(it.hasNext()) {
                Record r=it.next();
                r.set("isEdit",true);
                String materialId=r.getStr("material_id");
                String type2InRecord=r.getStr("type_2");
                r.remove("type_2");
                /*
                放入安存数量
                 */
                Record securityStockR = (Record) securityStock.get(materialId);
                if (securityStockR != null) {
                    int security_stock = securityStockR.getInt("security_stock");
                    r.set("security_stock", security_stock);//安存数量
                } else {
                    r.set("security_stock", 0);//安存数量

                }

                /*
                将原材料加入到菜单中
                 */
                for(Record map:typeList){
                    String idInMap=(String)map.get("id");

                    if(idInMap.equals(type2InRecord)){
                        List childrenList=(List)map.get("children");
                        if(childrenList==null){
                            childrenList=new ArrayList();
                            map.set("children",childrenList);
                        }
                        childrenList.add(r.getColumns());
                    }
                }
            }

//            for(Record map:typeList){
//                String idInMap=(String)map.get("id");
////                for(Record r:warehouseStockList){
//                while(it.hasNext()){
//                    Record r=it.next();
//                    String type2InRecord=r.getStr("type_2");
//                    if(idInMap.equals(type2InRecord)){
//                        List childrenList=(List)map.get("children");
//                        if(childrenList==null){
//                            childrenList=new ArrayList();
//                            map.set("children",childrenList);
//                        }
//                        childrenList.add(r.getColumns());
//                        it.remove();
////                        warehouseStockList.remove(r);
//                    }
//                }
//            }
            List<Map> mtList= MaterialTypeService.getMe().sort(typeList);
            jhm.putCode(1).put("list",mtList);
        }catch (Exception e){
            e.printStackTrace();
            jhm.putCode(-1).putMessage(e.toString());
        }
        renderJson(jhm);
    }
}