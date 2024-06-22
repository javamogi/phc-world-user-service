package com.phcworld.userservice.medium;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("dev")
class DataSourceConfigTest {
    
    @Autowired
    private DataSource dataSource;

    @Test
    @DisplayName("readOnly Transactional dataSource 확인")
    @Transactional(readOnly = true)
    void getUrlContainsSlaveWhenReadOnly() throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            String url = metaData.getURL();
            assertThat(url).contains("slave");
        }
    }

    @Test
    @DisplayName("readOnly 없는 Transactional dataSource 확인")
    @Transactional
    void getUrlContainsMasterWhenTransactionalAnnotation() throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            String url = metaData.getURL();
            System.out.println("Read-write transaction URL: " + url);
            assertThat(url).contains("master");
        }
    }
}
