package online.jf204.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import online.jf204.entity.alert;
import online.jf204.mapper.alertMapper;
import online.jf204.service.alertService;
import org.springframework.stereotype.Service;

@Service
public class alertServiceImpl extends ServiceImpl<alertMapper, alert> implements alertService {
}
