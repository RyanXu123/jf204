package online.jf204.control_204;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import online.jf204.entity.log;
import online.jf204.service.logService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller
public class logNew_204_controller {
    @Autowired
    private logService logservice;
    @CrossOrigin
    @RequestMapping("/getData/204/logs/out-1")
    @ResponseBody
    public List<log> LogsSelect(){
        LambdaQueryWrapper<log> andWrapper = new LambdaQueryWrapper<>();
        andWrapper.last("limit 1000");
        List<log> list = logservice.list(andWrapper);
        return list;
    }

    /***************/

    @CrossOrigin
    @PostMapping("/getData/204/logs-1")
    @ResponseBody
    public List<log> logsSelectOne(@RequestBody List<String> data) {
        String start_time = data.get(0);
        String end_time = data.get(1);

        LambdaQueryWrapper<log> andWrapper = new LambdaQueryWrapper<>();
        andWrapper.between(log::getTime, start_time, end_time);
        List<log> list = logservice.list(andWrapper);
        return list;
    }

}

