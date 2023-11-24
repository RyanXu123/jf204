package online.jf204.control_204;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class jfparams_204_controller {
    @Autowired
    private JdbcTemplate jdbc;


    @CrossOrigin
    @RequestMapping("/getData/204/aipreparams")
    @ResponseBody
//    @Scheduled(fixedRate = 30000)
    public List<Map<String,Object>> getdata204_ai(){
        String sql="select * from aipreparams";
        List <Map<String,Object>> list=jdbc.queryForList(sql);
        List <Map<String,Object>> list1=new ArrayList<>();
        List <Map<String,Object>> list2=new ArrayList<>();
        for (Map<String,Object> c :list){
            String content=c.get("Content").toString();
            if(content.equals("机房IT总功率") |content.equals("机房空调总功率") |content.equals("机房PUE") ){

            }else{
                list2.add(c);
            }
        }
        return list2;
    }


    @CrossOrigin
    @RequestMapping("/getData/204/jfparams")
    @ResponseBody
    @Scheduled(fixedRate = 30000)
    public List<Map<String,Object>> getdata204_ai2(){
        String sql="select * from aipreparams";
        List <Map<String,Object>> list=jdbc.queryForList(sql);
        List <Map<String,Object>> list1=new ArrayList<>();
        List <Map<String,Object>> list2=new ArrayList<>();
        for (Map<String,Object> c :list){
            String content=c.get("Content").toString();
            if(content.equals("机房IT总功率") |content.equals("机房空调总功率") |content.equals("机房PUE") ){
                list1.add(c);
            }else{

            }
        }
        return list1;
    }
}
