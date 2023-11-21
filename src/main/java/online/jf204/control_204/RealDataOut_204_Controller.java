package online.jf203.control_203;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
public class RealDataOut_203_Controller {
    @Autowired
    private JdbcTemplate jdbc;
    @CrossOrigin
    @RequestMapping("/getData/203/realdata/out1")
    @ResponseBody
    public List<Map<String,Object>> ktnew() {
        //        kt2
        List<Map<String, Object>> list_all = new ArrayList<>();
        String sql20_sf = "  select * from realdata_once where Location='JF203' and Equipment='空调1' ";//从表中筛选某空调的所有参数
        TreeMap<Integer, Object> kt_all = new TreeMap<>();
        for (Integer i = 1; i <= 13; i++) {
            String sql_temp = sql20_sf.replace("空调0", "空调" + i);   //遍历所有空调 1，2....20

            List<Map<String, Object>> list_kt = jdbc.queryForList(sql_temp); //一台空调所有参数，list里面为一台空调的所有参数：回风温度、送风温度等等
            //select * from realdata_once where Location='JF203' and Equipment='空调1'  在数据库里面运行该sql，查看返回的字段
            LinkedHashMap<String, Object> temp = new LinkedHashMap<>();
            LinkedHashMap<String, Object> temp2 = new LinkedHashMap<>();

            for (Map<String, Object> kt : list_kt) {//遍历返回的list，截取PointName和value0列，即参数名称和参数值
                Object PointName = kt.get("PointName");
                String PointName2 = PointName.toString();
                Object Value0 = kt.get("Value0");
                temp.put(PointName2, Value0);
            }
            // 存入键值对，如空调1 为（1，空调1所有参数的PointName和value0对）
            temp2.put("压缩机1容量", temp.get("压缩机1容量"));
            temp2.put("压缩机2容量", temp.get("压缩机2容量"));
            temp2.put("风机1转速", temp.get("风机1转速"));
            temp2.put("风机2转速", temp.get("风机2转速"));
            temp2.put("冷凝风机1转速", temp.get("冷凝风机1转速"));
            temp2.put("冷凝风机2转速", temp.get("冷凝风机2转速"));
            temp2.put("回风温度设定", temp.get("回风温度设定"));
            temp2.put("回风温度1", temp.get("回风温度1"));
            temp2.put("回风温度2", temp.get("回风温度2"));
            temp2.put("回风温度3", temp.get("回风温度3"));
            temp2.put("回风温度4", temp.get("回风温度4"));
            temp2.put("送风温度设定", temp.get("送风温度设定"));
            temp2.put("送风温度1", temp.get("送风温度1"));
            temp2.put("送风温度4", temp.get("送风温度4"));
            temp2.put("空调功率", temp.get("空调功率"));
            temp2.put("空调异常状态",temp.get("空调异常状态"));
            kt_all.put(i, temp2);
        }
        Map<String,Object> kt_name = new HashMap<>();
        kt_name.put("机房空调",kt_all);   // 存入键值对 ,样式为（机房空调，{[1，空调1参数键值对],[2,空调2参数键值对]...})）
        list_all.add(kt_name);
        return list_all;
    }



    @CrossOrigin
    @PostMapping("/getData/203/realdata/out")
    @ResponseBody
    public List<Map<String,Object>> dataout(@RequestBody List<String> data) {
        //        kt2
        List<Map<String, Object>> list_all = new ArrayList<>();
        String sql_data = "  select * from realdata_once where Location='JF203' and Equipment='空调1' and PointName='送风温度1' ";//从表中筛选某空调的所有参数
//        TreeMap<Integer, Object> kt_all = new TreeMap<>();
        String start_time=data.get(0).toString();
        String end_time=data.get(1).toString();
        String equipment=data.get(2).toString();
        String pointname=data.get(3).toString();
        sql_data=sql_data.replace("空调1",equipment);
        sql_data=sql_data.replace("送风温度1",pointname);
        sql_data=sql_data.concat(" and time>'"+start_time+"' and time <'"+end_time+"';");
        list_all=jdbc.queryForList(sql_data);
//        list_all.add();
        return list_all;
    }
}
