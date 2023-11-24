package online.jf204.control_204;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
public class aidisplay_204_controller {

    @Autowired
    private JdbcTemplate jdbc;


    @CrossOrigin
    @RequestMapping("/getData/204/aidisplay")
    @ResponseBody
//    @Scheduled(fixedRate = 30000)
    public Map<String,Object> aidisplay(){
//        String sql="select * from aidisplay where time=( select time from aidisplay order by id desc limit 1)";
//        String sql="select * from aidisplay where time=( select time from aidisplay order by time desc limit 1)";
        String sql="select * from aidisplay where time=( SELECT MAX(time) FROM realdata_once )";
        List <Map<String,Object>> list=jdbc.queryForList(sql);
       Map<String,Object> ret= new HashMap<>();
        LinkedHashMap<String,Object> ai= new LinkedHashMap<>();

        List <String> jf_hot= new ArrayList<>();

        for(Map<String,Object> c:list){
            Object Details = new Object();
            Details=c.get("Detail");
            if(c==list.get(0)){
                ret.put("AI启停状态",Details.toString());
            }else if(c== list.get(list.size()-1)){
                ret.put("机房状态",jf_hot);
                ret.put("AI触发模块",Details.toString());
            }else{
                jf_hot.add(Details.toString());
            }
        }
        return ret;
    }




}
