package online.jf204.control_204;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
public class AiCmd_204_controller {

    @Autowired
    private JdbcTemplate jdbc;


    List<Map<String,Object>> list_cmd = new ArrayList<>();//AI指令

    @CrossOrigin
    @RequestMapping("/getData/204/aicmd_history")
    @ResponseBody
//    @Scheduled(fixedRate = 30000)
    public List<Map<String,Object>> getdata204_aicmd_history(){
        String sql = "select * from aicmd where CommandType='群控控制' OR CommandType='保底控制' OR CommandType='预控控制' ";
//        String sql2 = "select * from aicmd where CommandType='保底控制' ";
        List<Map<String, Object>> list = jdbc.queryForList(sql);

        return list;

    }
    @CrossOrigin
    @PostMapping("/getData/204/aicmd_history")
    @ResponseBody
    public List<Map<String, Object>> getdata204_aicmd_history(@RequestBody List<String> data) {
        String start_time = data.get(0);
        String end_time = data.get(1);

        String sql = "SELECT * FROM aicmd WHERE (CommandType='群控控制' OR CommandType='保底控制' OR CommandType='预控控制') AND time BETWEEN ? AND ?";
        //     String sql = "SELECT * FROM aicmd WHERE (CommandType='群控控制' OR CommandType='保底控制' OR CommandType='预控控制') AND time BETWEEN '" + start_time + "' AND '" + end_time + "'";
        List<Map<String, Object>> list = jdbc.queryForList(sql, start_time, end_time);

        return list;
    }

    @CrossOrigin
    @PostMapping("/getData/204/aicmd_select")
    @ResponseBody
//    @Scheduled(fixedRate = 30000)
    public List<Map<String,Object>> getdata024_aicmd_select(@RequestBody Map<String,String> data){
        String sql = "select * from aicmd where CommandType='群控控制' OR CommandType='保底控制' OR CommandType='预控控制' ";
//        String sql2 = "select * from aicmd where CommandType='保底控制' ";
        List<Map<String, Object>> list = jdbc.queryForList(sql);

        return list;

    }
    @CrossOrigin
    @RequestMapping("/getData/204/aicmd")
    @ResponseBody
//    @Scheduled(fixedRate = 30000)
    public List<Map<String,Object>> getdata204_aicmd(){
        String sql="select * from aicmd where CommandType <> '心跳控制' and time = ( select MAX(time) from aicmd where CommandType <> '心跳控制')" ;
//        String sql2="select * from aicmd where CommandType='保底控制' " ;
        List <Map<String,Object>> list=jdbc.queryForList(sql);

        return list;
    }
}
