package online.jf204.entity;


import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class sitecold {
    @TableId
//    private String Equipment;
    private String Location;
    private String PointName;
    private Double GapValue;
//    private Double NOUSE;
}
