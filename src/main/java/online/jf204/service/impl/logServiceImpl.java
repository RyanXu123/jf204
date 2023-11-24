package online.jf204.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import online.jf204.entity.log;
import online.jf204.mapper.logMapper;
import online.jf204.service.logService;
import org.springframework.stereotype.Service;

@Service
public class logServiceImpl extends ServiceImpl <logMapper, log> implements logService {
}
