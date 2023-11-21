package online.jf204.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data

public class dataanalysis_kt {
    @TableId
    private String Equipment;
    private String Location;
    private Integer id;
    private double SF1;
    private double SF4;
    private double HF1;
    private double HF2;
    private double HF3;
    private double HF4;
    private double SFD;
    private double HFD;

    private double Power;
    private double FJ1;
    private double FJ2;
    private double YSJ1;
    private double YSJ2;
    private double LNFJ1;
    private double LNFJ2;
    private String SampleTime;
}
