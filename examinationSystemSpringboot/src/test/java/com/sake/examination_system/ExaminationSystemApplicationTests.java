package com.sake.examination_system;

import com.sake.examination_system.util.SakeUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class ExaminationSystemApplicationTests {

    @Test
    void contextLoads() throws IOException {
        System.out.println(SakeUtil.getAccessToken(SakeUtil.getApiKey(),SakeUtil.getApiSecretKey()));
    }

}
