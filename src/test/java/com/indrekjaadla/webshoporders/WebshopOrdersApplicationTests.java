package com.indrekjaadla.webshoporders;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.simple.JdbcClient;

import static org.assertj.core.api.Assertions.assertThat;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class WebshopOrdersApplicationTests {

    @Autowired
    private JdbcClient jdbcClient;

    @Test
    void contextLoads() {
    }

    @Test
    void doesNotLoadSeedDataOutsideDevProfile() {
        Long orderCount = jdbcClient.sql("SELECT count(*) FROM orders")
                .query(Long.class)
                .single();

        assertThat(orderCount).isZero();
    }
}
