package online.jf204.entity;


import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class dataanalysis_server {
    @TableId
    private String Location;
    private String Equipment;
    private double Power;
    private String SampleTime;
    private Integer id;
}
