package online.jf204.entity;


import lombok.Data;

@Data
public class ResultMassage {
        //相应码
        private Integer code;
        //信息
        private String message;
        //返回数据
        private Object data;
        //省略getter、setter、构造方法
        public ResultMassage(Integer code,String message,Object data){
                this.code=code;
                this.message=message;
                this.data=data;
        }
}

