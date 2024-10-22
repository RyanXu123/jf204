package online.jf204.control_204;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class aidisplay_204_controller {

    @Autowired
    private JdbcTemplate jdbc;

    // 静态变量用于存储上次访问时的最新时间
    private static String lastTime = null;


    @CrossOrigin
    @RequestMapping("/getData/204/aidisplay")
    @ResponseBody
//    @Scheduled(fixedRate = 30000)
    public Map<String,Object> aidisplay(){
//        String sql="select * from aidisplay where time=( select time from aidisplay order by id desc limit 1)";
//        String sql="select * from aidisplay where time=( select time from aidisplay order by time desc limit 1)";
        String sql="select * from aidisplay where time=( SELECT time FROM realdata_once order by id desc limit 1)";
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


    // 新增heartbeat接口
    @CrossOrigin
    @RequestMapping("/getData/203/heartbeat")
    @ResponseBody
    public int heartbeat() {
        // 查询 aidisplay 表中倒数第三条记录
        String sqlAIDisplay = "select Contents from aidisplay order by id desc limit 1 offset 2";
        String sqlHeartbeat = "select time from heartbeat order by id desc limit 1";

        Map<String, Object> aidisplayResult = jdbc.queryForMap(sqlAIDisplay);
        int content = Integer.parseInt(aidisplayResult.get("Contents").toString());

        if (content == 0) {//ai控制启停处于开启状态
            Map<String, Object> heartbeatResult = jdbc.queryForMap(sqlHeartbeat);
            String heartbeatTimeStr = heartbeatResult.get("time").toString();

            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date heartbeatTime = format.parse(heartbeatTimeStr);
                Date currentTime = new Date();

                //print现在时间和心跳信号时间
                System.out.println("heartbeatTime:" + heartbeatTime);
                System.out.println("currentTime:" + currentTime);

                // 计算两个时间的差值，并转换为分钟
                long diffInMillis = currentTime.getTime() - heartbeatTime.getTime();
                long diffInMinutes = diffInMillis / (60*1000);//毫秒转换为min

                if (diffInMinutes < 10) {
                    return 0; // 时间差小于10分钟，返回0，正常传递信号
                } else {
                    return 1; // 时间差大于10分钟，返回1，传递信号异常
                }
            } catch (Exception e) {
                e.printStackTrace();
                return 2; // 如果解析时间出现异常，返回2
            }
        } else {
            return 1; // content 不等于1，直接返回1，传递信号异常
        }
    }

    @CrossOrigin
    @RequestMapping("/getData/203/checkLatestTimes")
    @ResponseBody
    public Map<String, Object> checkLatestTimes() {
        String sql = "SELECT time FROM realdata_once ORDER BY time DESC LIMIT 1";
        List<Map<String, Object>> list = jdbc.queryForList(sql);

        Map<String, Object> result = new HashMap<>();
        if (list.isEmpty()) {
            result.put("实时数据", "无数据");
            return result;
        }

        String latestTime = list.get(0).get("time").toString();

        if (lastTime == null || !lastTime.equals(latestTime)) {
            lastTime = latestTime;
            result.put("实时数据", "更新");
        } else {
            result.put("实时数据", "未更新");
        }

        return result;
    }





}
