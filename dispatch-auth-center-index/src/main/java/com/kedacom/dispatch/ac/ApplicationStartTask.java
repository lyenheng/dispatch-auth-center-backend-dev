package com.kedacom.dispatch.ac;

import com.kedacom.avcs.dispatch.cache.common.CacheService;
import com.kedacom.dispatch.ac.data.constants.RedisKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * @Description:
 * @Auther: liuyanhui
 * @Date: 2022/08/04/ 13:21
 */
@Component
@Slf4j
public class ApplicationStartTask implements ApplicationRunner {

    @Autowired
    private CacheService cacheService;
    @Override
    public void run(ApplicationArguments args)  {
        try {
            cacheService.delete(RedisKey.DEVICE_AUTH_LOCAL);
            cacheService.delete(RedisKey.AUTH_PROGRESS_BAR_PERCENT);
        } catch (Exception e) {
            log.error( e.getMessage(),e);
        }
    }
}
