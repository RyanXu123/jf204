package online.jf204.control_204;

import online.jf204.entity.alert;
import online.jf204.entity.log;
import online.jf204.entity.sitecold;
import online.jf204.mapper.sitecoldMapper;
import online.jf204.mapper.logMapper;
import online.jf204.service.alertService;
import online.jf204.service.sitecoldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
public class Diagnosis_204_controller {

    @Autowired
    private JdbcTemplate jdbc;
    List <Double> sf_range =Arrays.asList(22.0,26.0);
    List  <Double>hf_range =Arrays.asList(32.0,38.0);
    List <Double>ysj_range =Arrays.asList(33.0,100.0);
    List <Double> fj_range =Arrays.asList(33.0,100.0);
    List  <Double>lnfj_range =Arrays.asList(33.0,100.0);

    List  cold_range =Arrays.asList(20.0,26.8);
    List  hot_range =Arrays.asList(28.0,38.0);

    @CrossOrigin
    @RequestMapping("/getData/204/realdata/server_display")
    @ResponseBody
//    @Scheduled(fixedRate = 30000)
    public List<Map<String,Object>> server_display(){

        List <Map<String,Object>> list_data= new ArrayList<>();  //储存返回的json
        Map<String, Object> data = new HashMap<String, Object>();

        List<String> server =  Arrays.asList("A","B","C","D","E","F","G","H","J","K","L","M","N","P");
        String sql="select Value0 from realdata_once where Location='JF204' and Equipment='服务器A' and SiteName='A1-上' limit 0,1";

        String sql1="select * from realdata_once where Location='JF204' and Equipment='服务器' limit 0,58"; //19*3+1

        Map<String, Object> servers_cold= new TreeMap<>();  //所有列列服务器冷通道
        Map<String, Object> servers_hot= new TreeMap<>();  //某列服务器冷通道
        Map<String, Object> servers_power= new TreeMap<>();  //某列服务器冷通道
        Integer siteNum=19;



        Map<String, Object> server_temp_power = new TreeMap<>();  //某列服务器功率 放在服务器外面，包裹所有服务器
        for (String c:server){  //遍历服务器 c为（"A","B","C","D" ...）

//            List <Map<String,Object>> list1= new jdbc.queryForList(sql1);
            Map<String, Object> server_temp_cold= new TreeMap<>();  //某列服务器冷通道
            Map<String, Object> server_temp_hot = new TreeMap<>();  //某列服务器热通道

            Integer cold_up_cnt_null=0;
            Integer cold_down_cnt_null=0;
            Integer hot_cnt_null=0;

            double cold_up_sum=0.0;
            double cold_down_sum=0.0;
            double hot_sum=0.0;//某列服务器热通道测点温度求和

            String sql_temp1=sql1.replace("'服务器'","'服务器"+c+"'"); //某服务器所有测点
            List <Map<String,Object>> list1 =jdbc.queryForList(sql_temp1);
            List<Double> server_site_cold_up =new ArrayList<>(); //某列服务器冷通道上测点
            List<Double> server_site_cold_down =new ArrayList<>();  //某列服务器冷通道下测点
            List<Double> server_site_hot_all =new ArrayList<>();  //某列服务器热通道测点
            Integer cnt=0;
            for(Map<String,Object>l:list1){
                Double value0=(double) l.get("Value0");
                if(cnt<siteNum*2){
                    if(cnt%2!=0){//奇数下测点
                        if( value0>=26.8){
                            server_site_cold_down.add(1.0);//冷上 0 2 4 6 8  过热1
                        }else if( value0<=0.0){
                            server_site_cold_down.add(-1.0);//+1      异常-1
                        }else{
                            server_site_cold_down.add(0.0);         //正常0
                        }
                    }else{

                        if(value0>=26.8){
                            server_site_cold_up.add(1.0);//冷下 1 3 5 7
                        }else if( value0<=0.0){
                            server_site_cold_up.add(-1.0);
                        }else{
                            server_site_cold_up.add(0.0);
                        }
                    }
                }else if(cnt<69){
                    if((value0<=28.0 & value0>0.0)| value0>=38.0){
                        server_site_hot_all.add(1.0);//热 46 47 48 49
                    }else if( value0<=0.0){
                        server_site_hot_all.add(-1.0);//热 46 47 48 49
                    }else{
                        server_site_hot_all.add(0.0);
                    }
                }
                cnt++;

            }

            Map<String, Object> server_site =new HashMap<String, Object>();  //服务器测点

            cold_up_sum=cold_up_sum/(siteNum-cold_up_cnt_null);//某列服务冷通道上测点的总和除非0测点的个数，为某列服务器冷通道上测点平均
            cold_down_sum=cold_down_sum/(siteNum-cold_down_cnt_null);//某列服务冷通道下测点的总和除非0测点的个数，为某列服务器冷通道下测点平均
            hot_sum=hot_sum/(siteNum-hot_cnt_null);//某列服务热通道测点的总和除非0测点的个数，为某列服务器热通道测点平均

            String t1 = String.format("%.2f", cold_up_sum);
            String t2 = String.format("%.2f", cold_down_sum);
            String t3 = String.format("%.2f", hot_sum); // 保留两位小数


            Map<String, Object> site_avg_cold = new TreeMap<>();//某服务器冷通道平均
            Map<String, Object> site_avg_hot = new TreeMap<>();//热通道平均
            Map<String, Object> site_cold = new TreeMap<>(); //冷通道
            Map<String, Object> site_hot = new TreeMap<>();//热通道
            Map<String, Object> site_power = new TreeMap<>();//功率
//            TreeMap<String, Object> server_site22_avg = new TreeMap<>(server_site2_avg);  //测点排序

            site_avg_cold.put("upall",t1);//冷通道上，（upall，某列冷上平均）
            site_avg_cold.put("downall",t2);//冷通道下 （downall，某列冷下平均）
            site_avg_hot.put("all",t3); ////热通道  （all，某列热平均）

            site_cold.put("up",server_site_cold_up); //某列服务器所有上测点  （up，{服务器所有测点（1，22）（2，22）..}）
            site_cold.put("down",server_site_cold_down);//某列服务器所有下测点  （down，{服务器所有测点（1，22）（2，22）..}）


            server_temp_cold.put("avg",site_avg_cold);  // (avg, {（upall，某列冷上平均）,（downall，某列冷下平均）})
            server_temp_cold.put("sitedetail",site_cold);// .put("sitedetail",serv);//(sitedetail,{（up，{服务器所有测点（1，22）（2，22）..}）,（down，{服务器所有测点（1，22）（2，22）..}）})

            //热通道
            server_temp_hot.put("sitedetail",server_site_hot_all); //(sitedetail,{服务器所有测点（1，22）（2，22）..})
            server_temp_hot.put("avg",site_avg_hot); //(avg,（all，某列热平均）)


            servers_cold.put(c,server_temp_cold); //冷通道（A，{(avg,xx),(sitedetail,xx)}）
            servers_hot.put(c,server_temp_hot); //热通道（A，{}）
//            servers_power.put(c,server_temp_power); //热通道（A，{}）
//            break;
//            server_num+=1;
//            data.put(sql_temp1,c);
        }

//        server_temp2=temp_p;
        data.put("servercold",servers_cold);
        data.put("serverhot",servers_hot);
//        data.put("serverpower",server_temp_power);

        list_data.add(data);
        return list_data;
    }

    ////新代码


    @CrossOrigin
    @RequestMapping("/getData/204/realdata/kt_diagnosis")
    @ResponseBody
//    @Scheduled(fixedRate = 30000)
    public List<Map<String,Object>> getdata204_judge(){

        //        kt2
        List <Map<String,Object>> list_all= new ArrayList<>();
        String sql20_sf=" select PointName,Equipment,Value0 from realdata_once where Location='JF204' and Equipment='空调0' limit 0,16";  //从表中筛选某空调的所有参数
        List <Map<String,Object>> list_temp= new ArrayList<>();
        Map<String,Object> a = new HashMap<>();
        Map<Integer,Object> kt_all = new HashMap<>();
        for(Integer i=1;i<=13;i++){
            String sql_temp=sql20_sf.replace("空调0","空调"+i);   //遍历所有空调 1，2....20
            List <Map<String,Object>> list_kt=jdbc.queryForList(sql_temp); //一台空调所有参数，list里面为一台空调的所有参数：回风温度、送风温度等等

            Map<String,Object> temp = new HashMap<>();
            for (Map<String,Object> c:list_kt){
                String params=c.get("PointName").toString();
                Double value0=(double) c.get("Value0");
                if(params.equals("送风温度1") | params.equals("送风温度4")){
                    if(value0<sf_range.get(0) | value0>sf_range.get(1)){
                        list_temp.add(c);
                    }
                }else if(params.equals("回风温度4")|params.equals("回风温度3")|params.equals("回风温度2")|params.equals("回风温度1")){
                    if(value0<hf_range.get(0) | value0>hf_range.get(1)){
                        list_temp.add(c);
                    }
                }else if(params.equals("压缩机1容量")|params.equals("压缩机2容量")){
                    if(value0<ysj_range.get(0)| value0>ysj_range.get(1)){
                        list_temp.add(c);
                    }
                }else if(params.equals("风机1转速")|params.equals("风机2转速")){
                    if(value0<fj_range.get(0) | value0>fj_range.get(1)){
                        list_temp.add(c);
                    }
                }
            }
        }

        return list_temp;
    }


    @CrossOrigin
    @RequestMapping("/getData/204/realdata/server_diagnosis")
    @ResponseBody
    @Scheduled(fixedRate = 30000)
    public List<Map<String,Object>> getdata204_judge2(){

        //        kt2
        List <Map<String,Object>> list_all= new ArrayList<>();
        List<String> server =  Arrays.asList("A","B","C","D","E","F","G","H","J","K","L","M","N","P");
        String sql20_sf=" select PointName,Equipment,Value0,SiteName from realdata_once where Location='JF204' and Equipment='服务器' limit 0,58";  //从表中筛选某空调的所有参数
        List <Map<String,Object>> list_temp= new ArrayList<>();
        Map<String,Object> a = new HashMap<>();
        Map<Integer,Object> kt_all = new HashMap<>();
        for(String c:server){
            String sql_temp=sql20_sf.replace("服务器","服务器"+c);   //遍历所有空调 1，2....20
            List <Map<String,Object>> list_kt=jdbc.queryForList(sql_temp); //一台空调所有参数，list里面为一台空调的所有参数：回风温度、送风温度等等
            Map<String,Object> temp = new HashMap<>();

            for (Map<String,Object> site:list_kt){
                String PointName=site.get("PointName").toString();
                String Equipment=site.get("Equipment").toString();
                String SiteName=site.get("SiteName").toString();
//                String params=site.get("PointName").toString();
                Double value0=(double) site.get("Value0");
                if(PointName.equals("冷通道温度")){
                    if(value0>26.8){
                        list_temp.add(site);
                    }
                }else if(PointName.equals("热通道温度")){
                    if((value0>0&value0<28) | value0>38){
                        list_temp.add(site);
                    }
                }
            }
        }

        return list_temp;
    }


    @CrossOrigin
    @RequestMapping("/getData/204/realdata/diagnosis_kt_design")
    @ResponseBody
//    @Scheduled(fixedRate = 30000)
    public List <List<Double>> diagnosis_design(){
        List <List<Double>> ret =new ArrayList<>();
        List temp= Arrays.asList(22,26);
        ret.add(sf_range);
        ret.add(hf_range);
        ret.add(ysj_range);
        ret.add(fj_range);
        ret.add(lnfj_range);
        return ret;
    }

    @CrossOrigin
    @PostMapping("/getData/204/realdata/diagnosis_kt_design")
    @ResponseBody
////    @Scheduled(fixedRate = 30000)
//    public List <List<Double>> diagnosis_design2(@RequestBody List <List<Double>> data){
//        sf_range =(List<Double>)data.get(0);
//        hf_range =(List<Double>)data.get(1);
//        ysj_range =(List<Double>)data.get(2);
//        fj_range =(List<Double>)data.get(3);
//        lnfj_range =(List<Double>)data.get(4);
//        return data;
//    }
    public List<Object> diagnosis_design2(@RequestBody List<Object> data){
        //输入数据库的数据
        String sfrangeLog = "";
        String hfrangeLog = "";
        String ysjrangeLog = "";
        String fjrangeLog = "";
        String lnfjrangeLog = "";
        //初始值
        List <Double> originSfrange =Arrays.asList(22.0,26.0);
        List <Double> originHfrange =Arrays.asList(32.0,38.0);
        List <Double> originYsjrange =Arrays.asList(33.0,100.0);
        List <Double> originFjrange =Arrays.asList(33.0,100.0);
        List <Double> originLnfjrange =Arrays.asList(33.0,100.0);
        //前端传来的值
        List<Double> sfRange =(List<Double>)data.get(0);
        List<Double> hfRange = (List<Double>) data.get(1);
        List<Double> ysjRange = (List<Double>) data.get(2);
        List<Double> fjRange = (List<Double>) data.get(3);
        List<Double> lnfjRange = (List<Double>) data.get(4);

        String userName = data.get(5).toString();
        String userRole = data.get(6).toString();
        String time_operate = data.get(7).toString();
//送风范围
        if (sfRange.equals(originSfrange)) {
            sfrangeLog = "送风温度范围正常";
            if (!sfRange.equals(sf_range)){
                log log1 = new log();
                log1.setDatacenter_room("JF204");
                log1.setContent(sfrangeLog);
                log1.setUserName(userName);
                log1.setUserRole(userRole);
                log1.setTime(time_operate);

                logMapper.insert(log1);
            }

        } else if (!sfRange.equals(originSfrange)) {
            sfrangeLog ="送风温度范围改变为";
            if (!sfRange.equals(sf_range)){
                log log1 = new log();
                log1.setDatacenter_room("JF204");
                log1.setContent(sfrangeLog + sfRange);
                log1.setUserName(userName);
                log1.setUserRole(userRole);
                log1.setTime(time_operate);

                logMapper.insert(log1);
            }
        }
        sf_range = sfRange;

        //回风温度范围
        if (hfRange.equals(originHfrange)) {
            hfrangeLog = "回风温度范围不变";
            if (!hfRange.equals(hot_range)){
                log log1 = new log();
                log1.setDatacenter_room("JF204");
                log1.setContent(hfrangeLog);
                log1.setUserName(userName);
                log1.setUserRole(userRole);
                log1.setTime(time_operate);

                logMapper.insert(log1);
            }

        } else if (!hfRange.equals(originHfrange)) {
            hfrangeLog ="回风温度范围改变为";
            if (!hfRange.equals(hot_range)){
                log log1 = new log();
                log1.setDatacenter_room("JF204");
                log1.setContent(hfrangeLog + hfRange);
                log1.setUserName(userName);
                log1.setUserRole(userRole);
                log1.setTime(time_operate);

                logMapper.insert(log1);
            }
        }
        hf_range =hfRange;

        //压缩机温度范围
        if (ysjRange.equals(originYsjrange)) {
            ysjrangeLog = "压缩机温度范围不变";
            if (!ysjRange.equals(ysj_range)){
                log log1 = new log();
                log1.setDatacenter_room("JF204");
                log1.setContent(ysjrangeLog);
                log1.setUserName(userName);
                log1.setUserRole(userRole);
                log1.setTime(time_operate);

                logMapper.insert(log1);
            }

        } else if (!ysjRange.equals(originYsjrange)) {
            ysjrangeLog ="压缩机温度范围改变为";
            if (!ysjRange.equals(ysj_range)){
                log log1 = new log();
                log1.setDatacenter_room("JF204");
                log1.setContent(ysjrangeLog + ysjRange);
                log1.setUserName(userName);
                log1.setUserRole(userRole);
                log1.setTime(time_operate);

                logMapper.insert(log1);
            }
        }
        ysj_range =ysjRange;

        //风机温度范围
        if (fjRange.equals(originFjrange)) {
            fjrangeLog = "风机温度范围不变";
            if (!fjRange.equals(fj_range)){
                log log1 = new log();
                log1.setDatacenter_room("JF204");
                log1.setContent(fjrangeLog);
                log1.setUserName(userName);
                log1.setUserRole(userRole);
                log1.setTime(time_operate);

                logMapper.insert(log1);
            }

        } else if (!fjRange.equals(originFjrange)) {
            fjrangeLog ="风机温度范围改变为";
            if (!fjRange.equals(fj_range)){
                log log1 = new log();
                log1.setDatacenter_room("JF204");
                log1.setContent(fjrangeLog + fjRange);
                log1.setUserName(userName);
                log1.setUserRole(userRole);
                log1.setTime(time_operate);

                logMapper.insert(log1);
            }
        }
        fj_range =fjRange;

        //冷凝风机温度范围
        if (lnfjRange.equals(originLnfjrange)) {
            lnfjrangeLog = "冷凝风机温度范围不变";
            if (!lnfjRange.equals(lnfj_range)){
                log log1 = new log();
                log1.setDatacenter_room("JF204");
                log1.setContent(lnfjrangeLog);
                log1.setUserName(userName);
                log1.setUserRole(userRole);
                log1.setTime(time_operate);

                logMapper.insert(log1);
            }

        } else if (!lnfjRange.equals(originLnfjrange)) {
            lnfjrangeLog ="冷凝风机温度范围改变为";
            if (!lnfjRange.equals(lnfj_range)){
                log log1 = new log();
                log1.setDatacenter_room("JF204");
                log1.setContent(lnfjrangeLog + lnfjRange);
                log1.setUserName(userName);
                log1.setUserRole(userRole);
                log1.setTime(time_operate);

                logMapper.insert(log1);
            }
        }
        lnfj_range =lnfjRange;
        return data;
    }



    @CrossOrigin
    @RequestMapping("/getData/204/realdata/diagnosis_server_design")
    @ResponseBody
//    @Scheduled(fixedRate = 30000)
    public List <List<Double>> diagnosis_server_design(){
        List <List<Double>> ret =new ArrayList<>();
        ret.add(cold_range);
        ret.add(hot_range);
        return ret;
    }


    Integer time_limit=6;
    @Autowired
    private alertService alertservice;

    @Autowired(required=false)
    private sitecoldService scService;
    @Autowired
    private sitecoldMapper sitecoldmapper;
    @CrossOrigin
    @RequestMapping("/getData/204/dataStatus_test")
    @ResponseBody
//    @Scheduled(fixedRate = 30000)
    public Integer dataStatus_test() {

        String sql="select * from data_reasonable order by Value0 desc limit 6" ;
        sql.replace("6",time_limit.toString());
//        String sql2="select * from aicmd where CommandType='保底控制' " ;
        List <Map<String,Object>> list=jdbc.queryForList(sql);
        Integer cnt=0;
        for(Map<String,Object> c : list){
            cnt+=Integer.parseInt(c.get("Value0").toString());
        }
        if(cnt==time_limit){
            return 1;
        }else{
            return 0;
        }
    }


    //    Integer time_limit=6;
    @CrossOrigin
    @PostMapping("/getData/204/dataStatus_time_limit_design")
    @ResponseBody
//    @Scheduled(fixedRate = 30000)
    public Integer time_limit_design(@RequestBody List<String> data) {
//        Integer TIME_design=time_limit*30;
        if (data.isEmpty()){
            return  time_limit/2;
        }
        Integer TIME_design=Integer.parseInt(data.get(0).toString())*2;
        time_limit=TIME_design;
        return  time_limit/2;
    }

    @CrossOrigin
    @RequestMapping("/getData/204/dataStatus")
    @ResponseBody
//    @Scheduled(fixedRate = 30000)
    public Integer dataStatus() {

        String sql="select * from data_reasonable order by Value0 desc limit 6" ;
        sql.replace("6",time_limit.toString());
//        String sql2="select * from aicmd where CommandType='保底控制' " ;
        List <Map<String,Object>> list=jdbc.queryForList(sql);
        Integer cnt=0;
        for(Map<String,Object> c : list){
            cnt+=Integer.parseInt(c.get("Value0").toString());
        }
        if(cnt.equals(time_limit)){
            return 1;
        }else{
            return 0;
        }
    }



    Boolean data_abnormal_alert=true;//数据异常报警
    Boolean real_alert=false;//热点报警
    Boolean coldsite_alert=false;//冷通道波动报警

    Double cold_unstable_fixed_time=10.0;
    Double cold_unstable_fixed_range=3.0;
//    Map<String,Object> cold_all_show= new HashMap<>();
//    Map<String,Object> cold_all2= new HashMap<>();
//    Long calendar_start = Calendar.getInstance().getTimeInMillis();

    List<List<String>> cold_list= new ArrayList<>();
    Integer start_flag=0;
    //    public Map<String,Object> cold_all_show= new HashMap<>();
    @CrossOrigin
    @RequestMapping("/getData/204/realdata/coldsite_change")
    @ResponseBody
    public List<Map<String,Object>> coldsite_change(){


        List <Map<String,Object>> list_data= new ArrayList<>();  //储存返回的json
        List<String> server =  Arrays.asList("A","B","C","D","E","F","G","H","J","K","L","M","N","P");
        Collections.reverse(server);//从P开始排序
        String sql_penultimate="select * from realdata_once where Location='JF204' and Equipment='服务器' and PointName='冷通道温度'  and time = ( SELECT time FROM realdata_once order by time desc limit 1 OFFSET 60)"; //19测点x3+功率
        String sql_last="select * from realdata_once where Location='JF204' and Equipment='服务器' and PointName='冷通道温度' and time = ( SELECT MAX(time) FROM realdata_once)"; //19测点x3+功率

        Map<String, Object> servers_cold= new TreeMap<>();  //所有列列服务器冷通道
        Integer siteNum=19;//测点个数

//        Integer id=0;
        for (String c:server) {  //遍历服务器 c为（"A","B","C","D" ...）
            Map<String, Object> server_temp_cold = new TreeMap<>();  //某列服务器冷通道

            sql_penultimate=sql_penultimate.replace("'服务器'", "'服务器" + c + "'"); //某服务器所有测点
            sql_last=sql_last.replace("'服务器'", "'服务器" + c + "'");

            List<Map<String, Object>> list_penultimate = jdbc.queryForList(sql_penultimate);
            List<Map<String, Object>> list_last = jdbc.queryForList(sql_last);

            List<Double> server_site_cold_up = new ArrayList<>(); //某列服务器冷通道上测点
            List<Double> server_site_cold_down = new ArrayList<>();  //某列服务器冷通道下测点
            List<Double> change_cold = new ArrayList<>();
            Integer cnt_change_site = 0;


            for(int i=0; i< list_penultimate.size();i++){
//                Map<String,Object> penultimateMap= list_penultimate.get(i);
//                Map<String,Object> lastMap= list_last.get(i);
                Double penultimateValue=(double)list_penultimate.get(i).get("Value0");
                Double lastValue=(double)list_last.get(i).get("Value0");
                Double gap=Math.abs(penultimateValue-lastValue);

                if(i%2==0){
                    if(lastValue<0.1){
                        server_site_cold_up.add(-1.0);
                    }else{
                        server_site_cold_up.add(Math.round(gap*100.0)/100.0);
                    }
                }else{
                    if(lastValue<0.1){
                        server_site_cold_down.add(-1.0);
                    }else{
                        server_site_cold_down.add(Math.round(gap*100.0)/100.0);
                    }
                }
                Map<String, Object> site_cold = new TreeMap<>(); //冷通道
                site_cold.put("up", server_site_cold_up); //某列服务器所有上测点  （up，{服务器所有测点（1，22）（2，22）..}）
                site_cold.put("down", server_site_cold_down);//某列服务器所有下测点  （down，{服务器所有测点（1，22）（2，22）..}）
                servers_cold.put(c, site_cold); //冷通道（A，{(avg,xx),(sitedetail,xx)}）
            }
        }
        list_data.add(servers_cold);

        return list_data;
    }


    @CrossOrigin
    @RequestMapping("/getData/204/realdata/cold_detect_design")
    @ResponseBody
    public List<Double> cold_detect_design(){
        List<Double> ret=new ArrayList<>();
        ret.add(cold_unstable_fixed_range);
        ret.add(cold_unstable_fixed_time);
        return ret;
    }
/**********************cold_detect_design********************/
    @CrossOrigin
    @PostMapping("/getData/204/realdata/cold_detect_design")
    @ResponseBody
//    public List<Double> cold_detect_design2(@RequestBody List<Double>data ){
////        List<Double> ret=new ArrayList<>();
//        cold_unstable_fixed_range=data.get(0);
//        cold_unstable_fixed_time=data.get(1);
//        return data;
//    }
    public List<Object> cold_detect_design2(@RequestBody List<Object> data){
        String coldfluctuationtimeLog = "";
        String coldfluctuationRangeLog = "";

        Double coldfluctuationRange = (Double) data.get(0);
        Double coldfluctuationTime = (Double) data.get(1);

        String userName = data.get(2).toString();
        String userRole = data.get(3).toString();
        String time_operate = data.get(4).toString();

        if (coldfluctuationRange.equals("3.0")) {
            coldfluctuationRangeLog = "冷通道波动范围阈值未改变";
            if (!coldfluctuationRange.equals(cold_unstable_fixed_range)){
                log log1 = new log();
                log1.setDatacenter_room("JF204");
                log1.setContent( coldfluctuationRangeLog);
                log1.setUserName(userName);
                log1.setUserRole(userRole);
                log1.setTime(time_operate);

                logMapper.insert(log1);
            }

        } else if (!coldfluctuationRange.equals("3.0")) {
            coldfluctuationRangeLog ="冷通道波动范围阈值改变为";
            if (!coldfluctuationRange.equals(cold_unstable_fixed_range)){
                log log1 = new log();
                log1.setDatacenter_room("JF204");
                log1.setContent(coldfluctuationRangeLog + "为" + coldfluctuationRange);
                log1.setUserName(userName);
                log1.setUserRole(userRole);
                log1.setTime(time_operate);

                logMapper.insert(log1);
            }
        }
        cold_unstable_fixed_range = coldfluctuationRange;

        if (coldfluctuationTime.equals("10.0")) {
            coldfluctuationtimeLog = "冷通道波动时间阈值未改变";
            if (!coldfluctuationTime.equals(cold_unstable_fixed_time)){
                log log1 = new log();
                log1.setDatacenter_room("JF204");
                log1.setContent(coldfluctuationtimeLog);
                log1.setUserName(userName);
                log1.setUserRole(userRole);
                log1.setTime(time_operate);

                logMapper.insert(log1);
            }

        } else if (!coldfluctuationTime.equals("10.0")) {
            coldfluctuationtimeLog ="冷通道波动时间阈值改变";
            if (!coldfluctuationTime.equals(cold_unstable_fixed_time)){
                log log1 = new log();
                log1.setDatacenter_room("JF204");
                log1.setContent(coldfluctuationtimeLog+"为"+ coldfluctuationTime);
                log1.setUserName(userName);
                log1.setUserRole(userRole);
                log1.setTime(time_operate);

                logMapper.insert(log1);
            }
        }
        cold_unstable_fixed_time = coldfluctuationTime;
        return data;

    }



    /*****************alert design************/
    @Autowired
    private logMapper logMapper;
    @CrossOrigin
    @PostMapping("/getData/204/realdata/alert_design")
    @ResponseBody

    public List<Object> alertDesign(@RequestBody List<Object> data) {
        String coldsiteAlertLog = "";

        Boolean coldsiteAlert = (Boolean) data.get(0);
        String userName = (String) data.get(1);
        String userRole = (String) data.get(2);
        String time_operate = (String) data.get(3);

        if (coldsiteAlert.equals(true)) {
            coldsiteAlertLog = "冷通道报警";
            if (!coldsiteAlert.equals(coldsite_alert)) {

                log logAlert = new log();
                logAlert.setDatacenter_room("JF204");
                logAlert.setContent(coldsiteAlertLog);
                logAlert.setUserName(userName);
                logAlert.setUserRole(userRole);
                logAlert.setTime(time_operate);

                logMapper.insert(logAlert);
            }
            } else if (coldsiteAlert.equals(false)) {
                coldsiteAlertLog = "冷通道未报警";
                if (!coldsiteAlert.equals(coldsite_alert)) {
                    log logAlert = new log();
                    logAlert.setDatacenter_room("JF204");
                    logAlert.setContent(coldsiteAlertLog);
                    logAlert.setUserName(userName);
                    logAlert.setUserRole(userRole);
                    logAlert.setTime(time_operate);

                    logMapper.insert(logAlert);
                }

            }
            coldsite_alert=coldsiteAlert;
            return data;
        }

    @CrossOrigin
    @RequestMapping("/getData/204/realdata/alert_design")
    @ResponseBody
    public List<Boolean> alert_design0(){
//        List<Double> ret=new ArrayList<>();
//        real_alert,data_abnormal_alert,

        return Arrays.asList(coldsite_alert);
    }


    Map<Integer,String> alert_content = new HashMap<>();

    Integer cnt=0;
    @CrossOrigin
    @RequestMapping("/getData/204/alert")
    @ResponseBody
    public Map<String,Object> alert2(){

        Map<String,Object> b= new HashMap<>();
        List<List<String>> real= new ArrayList<>();
        List<List<String>> data_abnormal_detail= new ArrayList<>();
        List<List<String>> cold_list= new ArrayList<>();
        List <sitecold> list_sitecold= sitecoldmapper.selectList(null);

//        <sitecold> find_list = new LambdaQueryWrapper<>();
//        find_list.allEq(null);

        String sql2="select * from predata where PointName='冷通道最大温度' ORDER BY id DESC limit 0,7"; //预测警告
        String sql3="select * from preshow where PointName='冷通道最大温度' ORDER BY id DESC limit 0,7"; //实时警告
        String sql_abnormal=" select * from  abnormal_detail where time=( select MAX(time) from abnormal_detail )";

        List <Map<String,Object>> list2=jdbc.queryForList(sql_abnormal);
        for (Map<String,Object> m:list2){
            data_abnormal_detail.add(Arrays.asList(m.get("time").toString(),"数据异常",m.get("Detail").toString()));
        }
        List <Map<String,Object>> list3=jdbc.queryForList(sql3);
        for (Map<String,Object> m:list3){
            if(Double.parseDouble(m.get("Value0").toString())>= 26.8){
                real.add(Arrays.asList(m.get("time").toString(),m.get("Equipment").toString().substring(3),m.get("PointName").toString()+"为"+String.format("%.2f",m.get("Value0"))+"°C"));
            }
        }

        String sql=" select * from realdata_once where Location='JF204' and PointName='冷通道温度' limit 0,380"; //19*12 10个服务器19个上下测点456
        List <Map<String,Object>> cold_temp_all=jdbc.queryForList(sql);


        Integer id=1;
        Iterator<sitecold> cold_before_all = list_sitecold.iterator();
        Iterator<Map<String,Object>> cold_all = cold_temp_all.iterator();

        while(cold_all.hasNext() && cold_before_all.hasNext()){
            sitecold cold_before = cold_before_all.next();
            Map<String,Object> cold= cold_all.next();

            Double value0= (double) cold.get("Value0");
            String SiteName= cold.get("SiteName").toString();
//            Double value_before=Double.parseDouble(cuc.cold_all_show.get(SiteName).toString());
            Double value_before=(double) cold_before.getGapValue();
            if(Math.abs(value0-value_before)>cold_unstable_fixed_range){
//                cold_list.add(Arrays.asList(id.toString(),cold.get("time").toString(),cold.get("Equipment").toString().substring(3),cold.get("SiteName").toString()+"波动"+String.format("%.2f",Math.abs(value0-value_before))+"度"));
                cold_list.add(Arrays.asList(id.toString(),cold.get("time").toString(),cold.get("Equipment").toString().substring(3),cold.get("SiteName").toString()+"波动"+String.format("%.2f",Math.abs(value0-value_before))+"度"));
                alert alert0 = new alert();
                alert0.setContent(cold.get("SiteName").toString()+"波动"+String.format("%.2f",Math.abs(value0-value_before))+"度");
                alert0.setEquipment(cold.get("Equipment").toString().substring(3));
                alert0.setLocation("FT204");
                alert0.setSampleTime(cold.get("time").toString());
                alertservice.save(alert0);
            }
            id++;
        }

        List<List<String>> temp= new ArrayList<>();
        if(real_alert==true){//实时报警
            b.put("real_hot",real);
        }else{
            b.put("real_hot",temp);
        }
        if (data_abnormal_alert==true){//数据异常报警
            b.put("data_abnormal_detail",data_abnormal_detail);
        }else{
            b.put("data_abnormal_detail",temp);
        }

        if(coldsite_alert==true){//波动报警
            b.put("cold_change",cold_list);
//            cold_list.clear();
        }else{
            b.put("cold_change",temp);

        }
        String sql_data_alert="select * from data_alert ORDER BY id DESC limit 0,1"; //实时警告

        String sql_data_reasonable="select * from data_reasonable order by Value0 desc limit 6" ;
        sql_data_reasonable.replace("6",time_limit.toString());
//        String sql2="select * from aicmd where CommandType='保底控制' " ;
        List <Map<String,Object>> list_data_reasonable=jdbc.queryForList(sql_data_reasonable);
        Integer cnt=0;
        Integer data_alert=0;
        for(Map<String,Object> c : list_data_reasonable){
            cnt+=Integer.parseInt(c.get("Value0").toString());
        }
        if(cnt>=time_limit){
            data_alert=1;

        }
        b.put("data_alert",data_alert);
        return b;
    }

/****************服务器design********/
    @CrossOrigin
    @PostMapping("/getData/204/realdata/diagnosis_server_design")
    @ResponseBody
//    @Scheduled(fixedRate = 30000)
    /*
    public List <List<Double>> diagnosis_server_design2(@RequestBody List <List<Double>> data){
//        List <List<Double>> ret =new ArrayList<>();

        cold_range =(List<Double>) data.get(0);
        hot_range =(List<Double>) data.get(1);
        return data;
    }
}
      */
public List<Object> diagnosis_server_design2(@RequestBody List<Object> data){
        String coldrangeLog = "";
        String hotrangeLog = "";
//      List  cold_range =Arrays.asList(20.0,26.8);
        List<Double> originColdrange = Arrays.asList(20.0, 26.8);
        List<Double> originHotrange =Arrays.asList(28.0,38.0);

        List<Double> coldRange=(List<Double>) data.get(0);
        List<Double> hotRange =(List<Double>) data.get(1);

        String userName = data.get(2).toString();
        String userRole = data.get(3).toString();
        String time_operate = data.get(4).toString();

        if (coldRange.equals(originColdrange)) {
            coldrangeLog = "冷通道正常阈值范围未改变";
            if (!coldRange.equals(cold_range)){
                log log1 = new log();
                log1.setDatacenter_room("JF204");
                log1.setContent(coldrangeLog);
                log1.setUserName(userName);
                log1.setUserRole(userRole);
                log1.setTime(time_operate);

                logMapper.insert(log1);
            }

        } else if (!coldRange.equals(originColdrange)) {
            coldrangeLog ="冷通道正常阈值范围改变为";
            if (!coldRange.equals(cold_range)){
                log log1 = new log();
                log1.setDatacenter_room("JF204");
                log1.setContent(coldrangeLog + coldRange );
                log1.setUserName(userName);
                log1.setUserRole(userRole);
                log1.setTime(time_operate);

                logMapper.insert(log1);
            }
        }
        cold_range =coldRange;

        if (hotRange.equals(originColdrange)) {
            hotrangeLog = "热通道正常阈值范围未改变";
            if (!coldRange.equals(hot_range)){
                log log1 = new log();
                log1.setDatacenter_room("JF204");
                log1.setContent(hotrangeLog);
                log1.setUserName(userName);
                log1.setUserRole(userRole);
                log1.setTime(time_operate);

                logMapper.insert(log1);
            }

        } else if (!hotRange.equals(originHotrange)) {
            hotrangeLog ="热通道正常阈值范围改变为";
            if (!coldRange.equals(hot_range)){
                log log1 = new log();
                log1.setDatacenter_room("JF204");
                log1.setContent(hotrangeLog + hotRange);
                log1.setUserName(userName);
                log1.setUserRole(userRole);
                log1.setTime(time_operate);

                logMapper.insert(log1);
            }
        }
        hot_range =hotRange;
        return data;

    }
}