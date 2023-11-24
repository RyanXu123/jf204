package online.jf204.control_204;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
public class RealDataOut_204_Controller {
    @Autowired
    private JdbcTemplate jdbc;
    @CrossOrigin
    @PostMapping("/getData/204/realdata/out")
    @ResponseBody
    public List<Map<String,Object>> dataout(@RequestBody List<String> data) {
        //        kt2
        List<Map<String, Object>> list_all = new ArrayList<>();
        String sql_data = "  select * from realdata_once where Location='JF204' and Equipment='空调1' and PointName='送风温度1' ";//从表中筛选某空调的所有参数
//        TreeMap<Integer, Object> kt_all = new TreeMap<>();
        String start_time=data.get(0).toString();
        String end_time=data.get(1).toString();
        String equipment=data.get(2).toString();
        String pointname=data.get(3).toString();
        if(equipment.equals("全部空调")){
            sql_data=sql_data.replace("and Equipment='空调1'","and EquipmentType='KT'");
        }else if (equipment.equals("全部服务器")){
            sql_data=sql_data.replace("and Equipment='空调1'","and EquipmentType='Server'");
        }else{
            sql_data=sql_data.replace("空调1",equipment);
        }



        if(pointname.equals("全部参数")){
            sql_data=sql_data.replace("and PointName='送风温度1'"," ");
        }else{
            sql_data=sql_data.replace("送风温度1",pointname);
        }
        sql_data=sql_data.concat(" and time>'"+start_time+"' and time <'"+end_time+"';");
        list_all=jdbc.queryForList(sql_data);
//        list_all.add();
        return list_all;
    }
}
