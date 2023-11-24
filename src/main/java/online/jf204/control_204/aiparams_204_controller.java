package online.jf204.control_204;

import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.databind.util.JSONPObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
//import org.springframework.stereotype.Controller.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
public class aiparams_204_controller {

    String hot_max="26.5";
    String ai_range="1";
    String ai_time="30";

    String sf_up_set="24";
    String sf_down_set="26";
    List sf_range=Arrays.asList(0.3,0.5,0.8,1.0);
    List cold_range =Arrays.asList(22,23,21,22,19,21,0,19);
//    String ai_time="30min";
    @Autowired
    private JdbcTemplate jdbc;

    @CrossOrigin
    @RequestMapping("/getData/204/aiparams")
    @ResponseBody
//    @Scheduled(fixedRate = 30000)
    public Map<String,Object> aiparams(){

        List <Map<String,Object>> list2= new ArrayList<>();
        Map<String,Object> temp1= new LinkedHashMap<>();
//        temp1.put("id",1);
        temp1.put("热点检查阈值",hot_max);
//        temp1.put("Value0",);

//        temp2.put("id",2);
        temp1.put("AI群控控制范围",ai_range);
//        temp2.put("Value0",);

//        temp3.put("id",3);
        temp1.put("AI群控控制周期",ai_time);
//        temp3.put("Value0",ai_time);
        temp1.put("送风上阈值",sf_up_set);
        temp1.put("送风下阈值",sf_down_set);
//        temp1.put("送风调整梯度",sf_range);
//        Collections.sort(cold_range);
//        temp1.put("冷通道分段阈值",cold_rang);
        return temp1;
    }


    @CrossOrigin
    @PostMapping("/getData/204/aiparams")
    @ResponseBody
//    @Scheduled(fixedRate = 30000)
    public Map<String, Object> getdata204_aiparams1(@RequestBody JSONObject data){
        Map<String, Object> ret = new LinkedHashMap<>();
        JSONObject content = data.getJSONObject("content");
        JSONObject userInfo = data.getJSONObject("user");

        // 前端传来的数据
        String newHotMax = content.get("热点检查阈值").toString();
        String newAiRange = content.get("AI群控控制范围").toString();
        String newAiTime = content.get("AI群控控制周期").toString();
        String newSfUpSet = content.get("送风上阈值范围").toString();
        String newSfDownSet = content.get("送风下阈值范围").toString();

        String userName = userInfo.getString("userName");
        String userRole = userInfo.getString("userRole");
        String timeOperate = userInfo.getString("time_operate");


        // 检查并更新全局变量及数据库
        checkAndUpdate("热点检查阈值更改为", hot_max, newHotMax, userName, userRole, timeOperate);
        checkAndUpdate("AI群控控制范围更改为", ai_range, newAiRange, userName, userRole, timeOperate);
        checkAndUpdate("AI群控控制周期更改为", ai_time, newAiTime, userName, userRole, timeOperate);
        checkAndUpdate("送风上阈值范围更改为", sf_up_set, newSfUpSet, userName, userRole, timeOperate);
        checkAndUpdate("送风下阈值范围更改为", sf_down_set, newSfDownSet, userName, userRole, timeOperate);

        // 将所有数据添加到返回的Map中
        ret.put("热点检查阈值", newHotMax);
        ret.put("AI群控控制范围", newAiRange);
        ret.put("AI群控控制周期", newAiTime);
        ret.put("送风上阈值", newSfUpSet);
        ret.put("送风下阈值", newSfDownSet);

        return ret;
    }
    private void checkAndUpdate(String key, String currentValue, String newValue, String userName, String userRole, String timeOperate) {
        if (!newValue.equals(currentValue)) {
            // 更新全局变量
            switch (key) {
                case "热点检查阈值更改为":
                    hot_max = newValue;
                    break;
                case "AI群控控制范围更改为":
                    ai_range = newValue;
                    break;
                case "AI群控控制周期更改为":
                    ai_time = newValue;
                    break;
                case "送风上阈值范围更改为":
                    sf_up_set = newValue;
                    break;
                case "送风下阈值范围更改为":
                    sf_down_set = newValue;
                    break;
            }

            // 写入数据库
            String sql_insert = "INSERT INTO log(datacenter_room, content, userName, userRole, time) VALUES('JF204','" + key + newValue + "','" + userName + "','" + userRole + "','" + timeOperate + "');";
            jdbc.execute(sql_insert);
        }
    }


    String openAi="0";
    String content="0";
    String time="0";
    String restart="0";
    //    private String userName = "user123"; // 默认用户名
//    private String userRole = "admin";   //默认用户权限
    private String aiOpenTime; //AI打开时设置
    @CrossOrigin
    @RequestMapping("/getData/204/aicontrol")
    @ResponseBody
//    @Scheduled(fixedRate = 30000)
    public List<String> aicontrol1(){
        List ret= new ArrayList<>();
        ret.add(openAi);
        ret.add(restart);
        return  ret;
    }

    @CrossOrigin
    @PostMapping("/getData/204/aicontrol")
    @ResponseBody
//    @Scheduled(fixedRate = 30000)
    public List<String> aicontrol2(@RequestBody List<String> data) {
        String aiopenlog = "";
        String rebootlog = "";

        String openAiTemp = data.get(0).toString();
        String restartTemp = data.get(1).toString();
        //return data;
        String userName = data.get(2).toString();
        String userRole = data.get(3).toString();
        String time_operate = data.get(4).toString();

        if (restartTemp.equals("1")) {
            rebootlog = "一键恢复开启";
            if (!restartTemp.equals(restart)){
                String sql_insert1 = "insert into log(datacenter_room, content, userName, userRole, time) values('JF204','" + rebootlog + "','" + userName + "','" + userRole + "','" + time_operate + "');";
                jdbc.execute(sql_insert1);
            }

        } else if (restart.equals("0")) {
            rebootlog = "一键恢复关闭";
            if (!restartTemp.equals(restart)){
                String sql_insert1 = "insert into log(datacenter_room, content, userName, userRole, time) values('JF204','" + rebootlog + "','" + userName + "','" + userRole + "','" + time_operate + "');";
                jdbc.execute(sql_insert1);
            }
        }
        restart=restartTemp;


        if (openAi.equals("1")) {
            aiopenlog = "AI开启";
            if (!openAiTemp.equals(openAi)){
                String sql_insert = "insert into log(datacenter_room, content, userName, userRole, time) values('JF204','" + aiopenlog + "','" + userName + "','" + userRole + "','" + time_operate + "');";
                jdbc.execute(sql_insert);
            }
        } else if (openAi.equals("0")) {
            aiopenlog = "AI关闭";
            if (!openAiTemp.equals(openAi)){
                String sql_insert = "insert into log(datacenter_room, content, userName, userRole, time) values('JF204','" + aiopenlog + "','" + userName + "','" + userRole + "','" + time_operate + "');";
                jdbc.execute(sql_insert);
            }
        }
        openAi=openAiTemp;
        return data;
    }

    @CrossOrigin
    @PostMapping("/AI_data")
    @ResponseBody
//    @Scheduled(fixedRate = 30000)
    public Map<String,Object> params_post(@RequestBody Integer data){
        LinkedList<Double> coldsite_gap=new LinkedList<>();
        String sql_ab="select * from coldsite_gap where Equipment='server-AB' ORDER BY id DESC limit 0,20160" ;//14天数据个数 ;
        String sql_cd="select * from coldsite_gap where Equipment='server-CD' ORDER BY id DESC limit 0,20160" ;
        String sql_ef="select * from coldsite_gap where Equipment='server-EF' ORDER BY id DESC limit 0,20160" ;
        String sql_gh="select * from coldsite_gap where Equipment='server-GH' ORDER BY id DESC limit 0,20160" ;
        String sql_jk="select * from coldsite_gap where Equipment='server-JK' ORDER BY id DESC limit 0,20160" ;
        String sql_lm="select * from coldsite_gap where Equipment='server-LM' ORDER BY id DESC limit 0,20160" ;
        String sql_np="select * from coldsite_gap where Equipment='server-NP' ORDER BY id DESC limit 0,20160" ;

        List <Map<String,Object>> list_ab=jdbc.queryForList(sql_ab);
        List <Map<String,Object>> list_cd=jdbc.queryForList(sql_cd);
        List <Map<String,Object>> list_ef=jdbc.queryForList(sql_ef);
        List <Map<String,Object>> list_gh=jdbc.queryForList(sql_gh);
        List <Map<String,Object>> list_jk=jdbc.queryForList(sql_jk);
        List <Map<String,Object>> list_lm=jdbc.queryForList(sql_lm);
        List <Map<String,Object>> list_np=jdbc.queryForList(sql_np);

        List<Double> gap_all=new ArrayList<>();
        for(Map<String,Object>c:list_ab) {
            Double gap = (Double)c.get("Value0");
            gap_all.add(gap);
        }
        coldsite_gap.add(Collections.max(gap_all));

        gap_all.clear();
        for(Map<String,Object>c:list_cd) {
            Double gap = (Double)c.get("Value0");
            gap_all.add(gap);
        }
        coldsite_gap.add(Collections.max(gap_all));

        gap_all.clear();
        for(Map<String,Object>c:list_ef) {
            Double gap = (Double)c.get("Value0");
            gap_all.add(gap);
        }
        coldsite_gap.add(Collections.max(gap_all));

        gap_all.clear();
        for(Map<String,Object>c:list_gh) {
            Double gap = (Double)c.get("Value0");
            gap_all.add(gap);
        }
        coldsite_gap.add(Collections.max(gap_all));

        gap_all.clear();
        for(Map<String,Object>c:list_jk) {
            Double gap = (Double)c.get("Value0");
            gap_all.add(gap);
        }
        coldsite_gap.add(Collections.max(gap_all));

        gap_all.clear();
        for(Map<String,Object>c:list_lm) {
            Double gap = (Double)c.get("Value0");
            gap_all.add(gap);
        }
        coldsite_gap.add(Collections.max(gap_all));

        gap_all.clear();
        for(Map<String,Object>c:list_np) {
            Double gap = (Double)c.get("Value0");
            gap_all.add(gap);
        }
        coldsite_gap.add(Collections.max(gap_all));

        Map<String,Object> m=new LinkedHashMap<>();
        m.put("热点检查阈值",hot_max);
//        m.put("AI预控控制范围","2℃");
        m.put("AI群控控制范围",ai_range);
        m.put("AI群控控制周期",ai_time);
        m.put("送风上阈值",sf_up_set);
        m.put("送风下阈值",sf_down_set);
//        m.put("送风调整梯度",sf_range);
//        m.put("冷通道分段阈值",Arrays.asList(Arrays.asList(cold_range.get(0),cold_range.get(1)),Arrays.asList(cold_range.get(2),cold_range.get(3)),Arrays.asList(cold_range.get(4),cold_range.get(5)),Arrays.asList(cold_range.get(6),cold_range.get(7))));
        m.put("AI控制开关",openAi);
        restart=data.toString();
        m.put("恢复初始值",restart);
        m.put("冷通道偏差",coldsite_gap);
//        if(restart.equals("1")) {
//            restart = "0";
//        }
        return m;
    }

    @CrossOrigin
    @RequestMapping("/AI_data")
    @ResponseBody
//    @Scheduled(fixedRate = 30000)
    public Map<String,Object> params(){
        LinkedList<Double> coldsite_gap=new LinkedList<>();
        String sql_ab="select * from coldsite_gap where Equipment='server-AB' ORDER BY id DESC limit 0,20160" ;//14天数据个数 ;
        String sql_cd="select * from coldsite_gap where Equipment='server-CD' ORDER BY id DESC limit 0,20160" ;
        String sql_ef="select * from coldsite_gap where Equipment='server-EF' ORDER BY id DESC limit 0,20160" ;
        String sql_gh="select * from coldsite_gap where Equipment='server-GH' ORDER BY id DESC limit 0,20160" ;
        String sql_jk="select * from coldsite_gap where Equipment='server-JK' ORDER BY id DESC limit 0,20160" ;
        String sql_lm="select * from coldsite_gap where Equipment='server-LM' ORDER BY id DESC limit 0,20160" ;
        String sql_np="select * from coldsite_gap where Equipment='server-NP' ORDER BY id DESC limit 0,20160" ;

        List <Map<String,Object>> list_ab=jdbc.queryForList(sql_ab);
        List <Map<String,Object>> list_cd=jdbc.queryForList(sql_cd);
        List <Map<String,Object>> list_ef=jdbc.queryForList(sql_ef);
        List <Map<String,Object>> list_gh=jdbc.queryForList(sql_gh);
        List <Map<String,Object>> list_jk=jdbc.queryForList(sql_jk);
        List <Map<String,Object>> list_lm=jdbc.queryForList(sql_lm);
        List <Map<String,Object>> list_np=jdbc.queryForList(sql_np);

        List<Double> gap_all=new ArrayList<>();
        for(Map<String,Object>c:list_ab) {
            Double gap = (Double)c.get("Value0");
            gap_all.add(gap);
        }
        coldsite_gap.add(Collections.max(gap_all));

        gap_all.clear();
        for(Map<String,Object>c:list_cd) {
            Double gap = (Double)c.get("Value0");
            gap_all.add(gap);
        }
        coldsite_gap.add(Collections.max(gap_all));

        gap_all.clear();
        for(Map<String,Object>c:list_ef) {
            Double gap = (Double)c.get("Value0");
            gap_all.add(gap);
        }
        coldsite_gap.add(Collections.max(gap_all));

        gap_all.clear();
        for(Map<String,Object>c:list_gh) {
            Double gap = (Double)c.get("Value0");
            gap_all.add(gap);
        }
        coldsite_gap.add(Collections.max(gap_all));

        gap_all.clear();
        for(Map<String,Object>c:list_jk) {
            Double gap = (Double)c.get("Value0");
            gap_all.add(gap);
        }
        coldsite_gap.add(Collections.max(gap_all));

        gap_all.clear();
        for(Map<String,Object>c:list_lm) {
            Double gap = (Double)c.get("Value0");
            gap_all.add(gap);
        }
        coldsite_gap.add(Collections.max(gap_all));

        gap_all.clear();
        for(Map<String,Object>c:list_np) {
            Double gap = (Double)c.get("Value0");
            gap_all.add(gap);
        }
        coldsite_gap.add(Collections.max(gap_all));

        Map<String,Object> m=new LinkedHashMap<>();
        m.put("热点检查阈值",hot_max);
//        m.put("AI预控控制范围","2℃");
        m.put("AI群控控制范围",ai_range);
        m.put("AI群控控制周期",ai_time);
        m.put("送风上阈值",sf_up_set);
        m.put("送风下阈值",sf_down_set);
//        m.put("送风调整梯度",sf_range);
//        m.put("冷通道分段阈值",Arrays.asList(Arrays.asList(cold_range.get(0),cold_range.get(1)),Arrays.asList(cold_range.get(2),cold_range.get(3)),Arrays.asList(cold_range.get(4),cold_range.get(5)),Arrays.asList(cold_range.get(6),cold_range.get(7))));
        m.put("AI控制开关",openAi);
//        restart=data.toString();
        m.put("恢复初始值",restart);
        m.put("冷通道偏差",coldsite_gap);
//        if(restart.equals("1")) {
//            restart = "0";
//        }
        return m;
    }
}
