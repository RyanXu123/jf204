package online.jf204.control_204;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
public class preshow_204_controller {


    @Autowired
    private JdbcTemplate jdbc;

    @CrossOrigin
    @RequestMapping("/getData/204/preshow")
    @ResponseBody
//    @Scheduled(fixedRate = 30000)
    public Map<String,Object> preshow3() {//一个时刻68个数据

        String sql_power="select * from predata where Equipment='服务器AB' and Location='JF204' and PointName='服务器功率'  ORDER BY id DESC limit 0,10080";

        List <String>timeline_arr= new ArrayList<>();
        List<String> power_arr = new ArrayList<>();
        List<Map<String, Object>> list_power = jdbc.queryForList(sql_power);
        for(Map<String, Object> c:list_power) {
            String Value0 = c.get("Value0").toString();
            power_arr.add(Value0);
        }
        Collections.reverse(power_arr);



        String sql_power_now="select * from preshow where Equipment='服务器AB' and Location='JF204' and PointName='服务器功率'  and Value0>0.0 ORDER BY id DESC limit 0,10080";
        List<String> power_arr_now = new ArrayList<>();
        List<Map<String, Object>> list_power_now = jdbc.queryForList(sql_power_now);
        Integer cnt=0;
        for(Map<String, Object> c:list_power_now){
            String Value0=c.get("Value0").toString();
            String time0=c.get("time").toString();
            power_arr_now.add(Value0);
            timeline_arr.add(time0);
        }
        Collections.reverse(power_arr_now);
        Collections.reverse(timeline_arr);
        Map<String,Object> ret= new HashMap<>();

        ret.put("pre",power_arr);
        ret.put("real",power_arr_now);
        ret.put("timeline",timeline_arr);

        return ret;
    }



    @CrossOrigin
    @PostMapping("/getData/204/preshow")
    @ResponseBody
//    @Scheduled(fixedRate = 30000)
    public Map<String,Object> preshow2(@RequestBody List<List<String>> data) {//一个时刻68个数据
        Map<String,String> preshowdata = new HashMap<>();
        preshowdata.put("kt","空调");
        preshowdata.put("server","服务器");
        preshowdata.put("ltdwdmax","冷通道最大温度");
        preshowdata.put("ltdwdavg","冷通道平均温度");
        preshowdata.put("rtdwd","热通道平均温度");
        preshowdata.put("power","服务器功率");
        preshowdata.put("sfwd","送风温度");
        preshowdata.put("hfwd","回风温度");

        String type= preshowdata.get(data.get(0).get(0));
        String Equipment=data.get(0).get(2);
        String pointname=preshowdata.get(data.get(0).get(1));
        String time_start=data.get(1).get(0);
        String time_end=data.get(1).get(1);

        String sql_end=" limit 0,10080";
        String sql_end2=" limit 0,28800";
        String sql = "select * from predata where Equipment='服务器AB' and Location='JF204' and PointName='服务器功率' ";
        String sql1= sql.replace("'服务器AB'","'"+type+Equipment+"'");
        String sql2= sql1.replace("'服务器功率'","'"+pointname+"'");

        if(!time_start.equals("-1")){
            sql2=sql2+" and time > '"+time_start+"'  and time < '"+time_end+"'";
        }else{
            if(type.equals("kt")){
                sql2+=sql_end2;
            }else{
                sql2+=sql_end;
            }

        }
        List<String> timeline_arr = new ArrayList<>();
        List<String> power_arr = new ArrayList<>();
        List<Map<String, Object>> list_power = jdbc.queryForList(sql2);
        for (Map<String, Object> c : list_power) {
            String Value0 = c.get("Value0").toString();
            power_arr.add(Value0);
        }
//        Collections.reverse(power_arr);


        String sql_now = "select * from preshow where Equipment='服务器AB' and Location='JF204' and PointName='服务器功率' ";
        String sql1_now= sql_now.replace("'服务器AB'","'"+type+Equipment+"'");
        String sql2_now= sql1_now.replace("'服务器功率'","'"+pointname+"'");

        if(!time_start.equals("-1")){
            sql2_now=sql2_now+" and time > '"+time_start+"'  and time < '"+time_end+"'";
        }else{
            if(type.equals("kt")){
                sql2_now+=sql_end2;
            }else{
                sql2_now+=sql_end;
            }
        }

        List<String> power_arr_now = new ArrayList<>();
        List<Map<String, Object>> list_power_now = jdbc.queryForList(sql2_now);
        for (Map<String, Object> c : list_power_now) {
            String Value0 = c.get("Value0").toString();
            String time0 = c.get("time").toString();
            power_arr_now.add(Value0);
            timeline_arr.add(time0);
        }
//        Collections.reverse(power_arr_now);
//        Collections.reverse(timeline_arr);

        Map<String, Object> ret = new HashMap<>();

        ret.put("pre", power_arr);
        ret.put("real", power_arr_now);
        ret.put("timeline", timeline_arr);

        return ret;
    }

    @CrossOrigin
    @PostMapping("/getData/204/preshownew")
    @ResponseBody
//    @Scheduled(fixedRate = 30000)
    public Map<String,Object> preshownew2(@RequestBody List<List<String>> data) {//一个时刻68个数据


        String start_data= data.get(0).get(0).toString();
        String end_data= data.get(0).get(1).toString();
//        System.out.println(end_data);


        String sql_power_future="select * from predata where EquipmentType='Server'  ORDER BY id DESC limit 0,10080";
        String sql_temperature_future="select * from predata where EquipmentType='Outside'  ORDER BY id DESC limit 0,10080";

        String sql_power_now="select * from preshow where EquipmentType='Server'  ORDER BY id DESC limit 0,10080";
        String sql_temperature_now="select * from preshow where EquipmentType='Outside'  ORDER BY id DESC limit 0,10080";

        if(!end_data.equals("-1")){
            sql_power_future=sql_power_future.replace("ORDER BY id DESC limit 0,10080","and time > '"+start_data.toString()+"' and time < '"+end_data.toString()+"'");
            sql_temperature_future=sql_temperature_future.replace("ORDER BY id DESC limit 0,10080","and time > '"+start_data.toString()+"' and time < '"+end_data.toString()+"'");
            sql_power_now=sql_power_now.replace("ORDER BY id DESC limit 0,10080","and time > '"+start_data.toString()+"' and time < '"+end_data.toString()+"'");
            sql_temperature_now=sql_temperature_now.replace("ORDER BY id DESC limit 0,10080","and time > '"+start_data.toString()+"' and time < '"+end_data.toString()+"'");
        }


        LinkedList <String>timeline_arr= new LinkedList<>();
        LinkedList <String> temperature_arr_now = new LinkedList<>();
        LinkedList <String> power_arr_now = new LinkedList<>();
        LinkedList <String> temperature_arr_future = new LinkedList<>();
        LinkedList <String> power_arr_future = new LinkedList<>();


        List<Map<String, Object>> list_power_future = jdbc.queryForList(sql_power_future);
        List<Map<String, Object>> list_power_now = jdbc.queryForList(sql_power_now);
        List<Map<String, Object>> list_temperature_future = jdbc.queryForList(sql_temperature_future);
        List<Map<String, Object>> list_temperature_now = jdbc.queryForList(sql_temperature_now);




        for(Map<String, Object> c:list_power_now) {
            String time0 = c.get("time").toString();
            timeline_arr.add(time0);
        }
        Collections.reverse(timeline_arr);

        for(Map<String, Object> c:list_power_now) {
            String Value0 = c.get("Value0").toString();
            power_arr_now.add(Value0);
        }
        Collections.reverse(power_arr_now);

        for(Map<String, Object> c:list_power_future) {
            String Value0 = c.get("Value0").toString();
            power_arr_future.add(Value0);
        }
        Collections.reverse(power_arr_future);

        for(Map<String, Object> c:list_temperature_now) {
            String Value0 = c.get("Value0").toString();
            temperature_arr_now.add(Value0);
        }
        Collections.reverse(temperature_arr_now);

        for(Map<String, Object> c:list_temperature_future) {
            String Value0 = c.get("Value0").toString();
            temperature_arr_future.add(Value0);
        }
        Collections.reverse(temperature_arr_now);


        Map<String,Object> ret= new HashMap<>();

        ret.put("future_temperature",temperature_arr_future);
        ret.put("now_temperature",temperature_arr_now);
        ret.put("future_power",power_arr_future);
        ret.put("now_power",power_arr_now);
        ret.put("timeline",timeline_arr);
        return ret;
    }


    @CrossOrigin
    @RequestMapping("/getData/204/preshownew")
    @ResponseBody
//    @Scheduled(fixedRate = 30000)
    public Map<String,Object> preshownew() {//一个时刻68个数据

        String sql_power_future="select * from predata where EquipmentType='Server'  ORDER BY id DESC limit 0,10080";
        String sql_temperature_future="select * from predata where EquipmentType='Outside'  ORDER BY id DESC limit 0,10080";

        String sql_power_now="select * from preshow where EquipmentType='Server'  ORDER BY id DESC limit 0,10080";
        String sql_temperature_now="select * from preshow where EquipmentType='Outside'  ORDER BY id DESC limit 0,10080";

        LinkedList <String>timeline_arr= new LinkedList<>();
        LinkedList <String> temperature_arr_now = new LinkedList<>();
        LinkedList <String> power_arr_now = new LinkedList<>();
        LinkedList <String> temperature_arr_future = new LinkedList<>();
        LinkedList <String> power_arr_future = new LinkedList<>();


        List<Map<String, Object>> list_power_future = jdbc.queryForList(sql_power_future);
        List<Map<String, Object>> list_power_now = jdbc.queryForList(sql_power_now);
        List<Map<String, Object>> list_temperature_future = jdbc.queryForList(sql_temperature_future);
        List<Map<String, Object>> list_temperature_now = jdbc.queryForList(sql_temperature_now);




        for(Map<String, Object> c:list_power_now) {
            String time0 = c.get("time").toString();
            timeline_arr.add(time0);
        }
        Collections.reverse(timeline_arr);

        for(Map<String, Object> c:list_power_now) {
            String Value0 = c.get("Value0").toString();
            power_arr_now.add(Value0);
        }
        Collections.reverse(power_arr_now);

        for(Map<String, Object> c:list_power_future) {
            String Value0 = c.get("Value0").toString();
            power_arr_future.add(Value0);
        }
        Collections.reverse(power_arr_future);

        for(Map<String, Object> c:list_temperature_now) {
            String Value0 = c.get("Value0").toString();
            temperature_arr_now.add(Value0);
        }
        Collections.reverse(temperature_arr_now);

        for(Map<String, Object> c:list_temperature_future) {
            String Value0 = c.get("Value0").toString();
            temperature_arr_future.add(Value0);
        }
        Collections.reverse(temperature_arr_now);


        Map<String,Object> ret= new HashMap<>();

        ret.put("future_temperature",temperature_arr_future);
        ret.put("now_temperature",temperature_arr_now);
        ret.put("future_power",power_arr_future);
        ret.put("now_power",power_arr_now);
        ret.put("timeline",timeline_arr);
        return ret;
    }
}
