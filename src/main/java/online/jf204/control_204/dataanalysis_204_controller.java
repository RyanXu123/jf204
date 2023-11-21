
package online.jf204.control_204;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import online.jf204.dto.KtDto;
import online.jf204.entity.dataanalysis_server;
import online.jf204.service.dataanalysisServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;


import com.example.demo2.entity.dataanalysis_kt;
import com.example.demo2.service.dataanalysisKtService;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;

    @Controller
    public class dataanalysis_204_controller {
        @Autowired
        dataanalysisKtService dataService;
        @Autowired
        dataanalysisServerService serverService;

        String time_start="2023-01-00 00:00:00";
        String time_end="2023-03-15 00:00:00";

        @CrossOrigin
        @RequestMapping("/getData/202/dataanalysisnew")
        @ResponseBody
//        @Scheduled(fixedRate = 30000)
        public List<Map<String,Object>> showAnalysis(){


            ArrayList<KtDto> res = new ArrayList<>();
            List <Map<String,Object>> final0= new ArrayList<>();
            Map<String,Object> kt_all=new LinkedHashMap<>();

            LinkedList<String> Time_line= new LinkedList<>();

            Date date = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            System.out.println(formatter.format(date));

            for (int i = 1; i < 21; i++) {
                LambdaQueryWrapper<dataanalysis_kt> andWrapper = new LambdaQueryWrapper<>();
//                La<dataanalysis> andWrapper = new
                andWrapper.eq(dataanalysis_kt::getEquipment , "KT"+i).le(dataanalysis_kt::getSampleTime,time_start+" 00:00:00").lt(dataanalysis_kt::getSampleTime,time_end+" 00:00:00");
                List <dataanalysis_kt> list =dataService.list(andWrapper);
                KtDto dto = new KtDto();
                List<Double> SFD = new LinkedList<>();
                List<Double> SF1 = new LinkedList<>();
                List<Double> SF4 = new LinkedList<>();
                List<Double> HFD = new LinkedList<>();
                List<Double> HF1 = new LinkedList<>();
                List<Double> HF2 = new LinkedList<>();
                List<Double> HF3 = new LinkedList<>();
                List<Double> HF4 = new LinkedList<>();
                List<Double> FJ1 = new LinkedList<>();
                List<Double> FJ2 = new LinkedList<>();
                List<Double> YSJ1 = new LinkedList<>();
                List<Double> YSJ2 = new LinkedList<>();
                List<Double> power = new LinkedList<>();
                dto.setSFD(SFD);
                dto.setFJ1(FJ1);
                dto.setSF1(SF1);
                dto.setSF4(SF4);
                dto.setHFD(HFD);
                dto.setHF1(HF1);
                dto.setHF2(HF2);
                dto.setHF3(HF3);
                dto.setHF4(HF4);
                dto.setFJ2(FJ2);
                dto.setYSJ1(YSJ1);
                dto.setYSJ2(YSJ2);
                dto.setPower(power);

                for(dataanalysis_kt kt : list){
                    SFD.add(kt.getSFD());
                    SF1.add(kt.getSF1());
                    SF4.add(kt.getSF4());
                    HFD.add(kt.getHFD());
                    HF1.add(kt.getHF1());
                    HF2.add(kt.getHF2());
                    HF3.add(kt.getHF3());
                    HF4.add(kt.getHF4());
                    FJ1.add(kt.getFJ1());
                    FJ2.add(kt.getFJ1());
                    YSJ1.add(kt.getYSJ1());
                    YSJ2.add(kt.getYSJ2());
                    power.add(kt.getPower());
                    if (i==1){
                        //第一台空调的时候取时间
                        Time_line.add(kt.getSampleTime());
                    }
                }
                res.add(dto);
            }
            kt_all.put("KT",res);
            kt_all.put("TimeLine",Time_line);
            final0.add(kt_all);
            return final0;
        }


        @CrossOrigin
        @PostMapping("/getData/202/dataanalysisnew")
        @ResponseBody
//        @Scheduled(fixedRate = 30000)
        public List<Map<String,Object>> showAnalysis1(@RequestBody List<String> data){

            time_start=data.get(0)+" 00:00:00";
            time_end=data.get(1)+" 00:00:00";
            System.out.println(data);
            ArrayList<KtDto> res = new ArrayList<>();
            List <Map<String,Object>> final0= new ArrayList<>();
            Map<String,Object> kt_all=new LinkedHashMap<>();

            LinkedList<String> Time_line= new LinkedList<>();

            Date date = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            System.out.println(formatter.format(date));

            for (int i = 1; i < 21; i++) {
                LambdaQueryWrapper<dataanalysis_kt> andWrapper = new LambdaQueryWrapper<>();
//                La<dataanalysis> andWrapper = new
                andWrapper.eq(dataanalysis_kt::getEquipment , "KT"+i).ge(dataanalysis_kt::getSampleTime,time_start).lt(dataanalysis_kt::getSampleTime,time_end);
                List <dataanalysis_kt> list =dataService.list(andWrapper);
                KtDto dto = new KtDto();
                List<Double> SFD = new LinkedList<>();
                List<Double> SF1 = new LinkedList<>();
                List<Double> SF4 = new LinkedList<>();
                List<Double> HFD = new LinkedList<>();
                List<Double> HF1 = new LinkedList<>();
                List<Double> HF2 = new LinkedList<>();
                List<Double> HF3 = new LinkedList<>();
                List<Double> HF4 = new LinkedList<>();
                List<Double> FJ1 = new LinkedList<>();
                List<Double> FJ2 = new LinkedList<>();
                List<Double> YSJ1 = new LinkedList<>();
                List<Double> YSJ2 = new LinkedList<>();
                List<Double> power = new LinkedList<>();
                dto.setSFD(SFD);
                dto.setFJ1(FJ1);
                dto.setSF1(SF1);
                dto.setSF4(SF4);
                dto.setHFD(HFD);
                dto.setHF1(HF1);
                dto.setHF2(HF2);
                dto.setHF3(HF3);
                dto.setHF4(HF4);
                dto.setFJ2(FJ2);
                dto.setYSJ1(YSJ1);
                dto.setYSJ2(YSJ2);
                dto.setPower(power);

                for(dataanalysis_kt kt : list){
                    SFD.add(kt.getSFD());
                    SF1.add(kt.getSF1());
                    SF4.add(kt.getSF4());
                    HFD.add(kt.getHFD());
                    HF1.add(kt.getHF1());
                    HF2.add(kt.getHF2());
                    HF3.add(kt.getHF3());
                    HF4.add(kt.getHF4());
                    FJ1.add(kt.getFJ1());
                    FJ2.add(kt.getFJ1());
                    YSJ1.add(kt.getYSJ1());
                    YSJ2.add(kt.getYSJ2());
                    power.add(kt.getPower());
                    if (i==1){
                        //第一台空调的时候取时间
                        Time_line.add(kt.getSampleTime());
                    }
                }
                res.add(dto);
            }
            kt_all.put("KT",res);
            kt_all.put("TimeLine",Time_line);
            final0.add(kt_all);
            return final0;
        }


        @CrossOrigin
        @PostMapping("/getData/202/dataanalysisnew0311")
        @ResponseBody
//        @Scheduled(fixedRate = 30000)
        public List<Map<String,Object>> dataanalysisnew(@RequestBody List<List<String>> data){


            String type=data.get(0).get(0);
            String Equipment=data.get(0).get(1);

            time_start=data.get(1).get(0);
            time_end=data.get(1).get(1);
            System.out.println(data);
            ArrayList<KtDto> res = new ArrayList<>();
            List <Map<String,Object>> final0= new ArrayList<>();
            Map<String,Object> kt_all=new LinkedHashMap<>();

            LinkedList<String> Time_line= new LinkedList<>();

            Date date = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            System.out.println(formatter.format(date));

            LambdaQueryWrapper<dataanalysis_kt> andWrapper = new LambdaQueryWrapper<>();
//                La<dataanalysis> andWrapper = new
            if(type.equals("kt")){
                if(!time_start.equals("-1")){
                    andWrapper.eq(dataanalysis_kt::getEquipment , "KT"+Equipment).ge(dataanalysis_kt::getSampleTime,time_start).lt(dataanalysis_kt::getSampleTime,time_end);
                }else{
                    andWrapper.eq(dataanalysis_kt::getEquipment , "KT"+Equipment).last("limit 1440");
                }

            }else if(type.equals("server")){
                LambdaQueryWrapper<dataanalysis_server> serverWrapper = new LambdaQueryWrapper<>();
                if(!time_start.equals("-1")){
                    serverWrapper.eq(dataanalysis_server::getEquipment , "server-"+Equipment).ge(dataanalysis_server::getSampleTime,time_start).lt(dataanalysis_server::getSampleTime,time_end);
                }else{
                    serverWrapper.eq(dataanalysis_server::getEquipment , "server-"+Equipment).last("limit 1440");
                }
                List<dataanalysis_server> list2= serverService.list(serverWrapper);
//                System.out.println(list2);
                List<Double> server_all = new ArrayList<>();
//                List<Double> server_each= new ArrayList<>();

                for (dataanalysis_server server :list2){
                    Time_line.add(server.getSampleTime());
                    server_all.add(server.getPower());
                }
                Map<String,Object> server_power = new HashMap<>();
                server_power.put("server_power",server_all);

                kt_all.put("Server",server_power);
                kt_all.put("TimeLine",Time_line);
                final0.add(kt_all);
                return final0;

            }

            List <dataanalysis_kt> list =dataService.list(andWrapper);
            KtDto dto = new KtDto();
            List<Double> SFD = new LinkedList<>();
            List<Double> SF1 = new LinkedList<>();
            List<Double> SF4 = new LinkedList<>();
            List<Double> HFD = new LinkedList<>();
            List<Double> HF1 = new LinkedList<>();
            List<Double> HF2 = new LinkedList<>();
            List<Double> HF3 = new LinkedList<>();
            List<Double> HF4 = new LinkedList<>();
            List<Double> FJ1 = new LinkedList<>();
            List<Double> FJ2 = new LinkedList<>();
            List<Double> YSJ1 = new LinkedList<>();
            List<Double> YSJ2 = new LinkedList<>();
            List<Double> LNFJ1 = new LinkedList<>();
            List<Double> LNFJ2 = new LinkedList<>();
            List<Double> power = new LinkedList<>();
            dto.setSFD(SFD);
            dto.setFJ1(FJ1);
            dto.setSF1(SF1);
            dto.setSF4(SF4);
            dto.setHFD(HFD);
            dto.setHF1(HF1);
            dto.setHF2(HF2);
            dto.setHF3(HF3);
            dto.setHF4(HF4);
            dto.setFJ2(FJ2);
            dto.setYSJ1(YSJ1);
            dto.setYSJ2(YSJ2);
            dto.setLNFJ1(LNFJ1);
            dto.setLNFJ2(LNFJ2);
            dto.setPower(power);

            for(dataanalysis_kt kt : list){
                SFD.add(kt.getSFD());
                SF1.add(kt.getSF1());
                SF4.add(kt.getSF4());
                HFD.add(kt.getHFD());
                HF1.add(kt.getHF1());
                HF2.add(kt.getHF2());
                HF3.add(kt.getHF3());
                HF4.add(kt.getHF4());
                FJ1.add(kt.getFJ1());
                FJ2.add(kt.getFJ1());
                YSJ1.add(kt.getYSJ1());
                YSJ2.add(kt.getYSJ2());
                power.add(kt.getPower());
                    //第一台空调的时候取时间
                Time_line.add(kt.getSampleTime());
            }
            res.add(dto);
            kt_all.put("KT",res);
            kt_all.put("TimeLine",Time_line);
            final0.add(kt_all);
            return final0;
        }
    }


