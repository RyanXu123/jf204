package online.jf204.control_204;

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




    @CrossOrigin
    @RequestMapping("/getData/202/aiparams")
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
        temp1.put("送风调整梯度",sf_range);
//        Collections.sort(cold_range);
//        temp1.put("冷通道分段阈值",cold_range);
        return temp1;
    }

    @CrossOrigin
    @PostMapping("/getData/202/aiparams")
    @ResponseBody
//    @Scheduled(fixedRate = 30000)
    public Map<String,Object> getdata202_aiparams1(@RequestBody Map<String,Object>data){
//        hot_max=data.get(0).get("Value0").toString();
//        ai_range=data.get(1).get("Value0").toString();
//        ai_time=data.get(2).get("Value0").toString();
        hot_max=data.get("热点检查阈值").toString();
        ai_range=data.get("AI群控控制范围").toString();
        ai_time=data.get("AI群控控制周期").toString();

        sf_up_set=data.get("送风上阈值").toString();
        sf_down_set=data.get("送风下阈值").toString();
        sf_range=(List) data.get("送风调整梯度");
        cold_range = (List)data.get("冷通道分段阈值");
        return data;
    }

    String Safe="0";
    String restart="0";
    @CrossOrigin
    @RequestMapping("/getData/202/aicontrol")
    @ResponseBody
//    @Scheduled(fixedRate = 30000)
    public List<String> aicontrol1(){
        List ret= new ArrayList<>();

//        ret.add(Enengy);
        ret.add(Safe);
        ret.add(restart);
        return  ret;
    }

//    @CrossOrigin
//    @RequestMapping("/getData/202/dataalert")
//    @ResponseBody
////    @Scheduled(fixedRate = 30000)
//    public Integer dataalert(){
//        return 1;
//    }

    @CrossOrigin
    @PostMapping("/getData/202/aicontrol")
    @ResponseBody
//    @Scheduled(fixedRate = 30000)
    public List<Integer> aicontrol2(@RequestBody List<Integer> data){
        Safe=data.get(0).toString();  //保存更改的ai控制状态数据
//        Enengy=data.get(0).toString(); //保存更改的ai控制状态数据
        return  data;
    }
    @Autowired
    private JdbcTemplate jdbc;


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
        m.put("送风下阈值",sf_up_set);
        m.put("送风调整梯度",sf_range);
        m.put("冷通道分段阈值",Arrays.asList(Arrays.asList(cold_range.get(0),cold_range.get(1)),Arrays.asList(cold_range.get(2),cold_range.get(3)),Arrays.asList(cold_range.get(4),cold_range.get(5)),Arrays.asList(cold_range.get(6),cold_range.get(7))));
        m.put("AI控制开关",Safe);
        m.put("恢复初始值",restart);
        m.put("冷通道偏差",coldsite_gap);
        if(restart.equals("1")) {
            restart = "0";
        }
        return m;
    }
}
