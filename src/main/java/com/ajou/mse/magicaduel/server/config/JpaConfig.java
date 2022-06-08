package com.ajou.mse.magicaduel.server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing // 자동으로 시간 값을 매핑해주기 위해 사용
public class JpaConfig {
}
