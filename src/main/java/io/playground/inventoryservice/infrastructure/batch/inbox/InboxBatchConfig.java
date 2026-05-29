package io.playground.inventoryservice.infrastructure.batch.inbox;

import io.playground.inventoryservice.infrastructure.kafka.consumer.ConsumedEvent;
import io.playground.inventoryservice.infrastructure.persistence.eventstream.InboxEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class InboxBatchConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final DataSource dataSource;
    private final InboxProcessor processor;

    @Bean
    public Job inboxJob() throws Exception {
        return new JobBuilder("inboxJob", jobRepository)
                .start(inboxStep())
                .build();
    }

    @Bean
    public Step inboxStep() throws Exception {
        return new StepBuilder("inboxStep", jobRepository)
                .<InboxEntity, InboxEntity>chunk(1, platformTransactionManager)
                .reader(inboxReader())
                .processor(processor)
                .writer(inboxWriter())
                .faultTolerant()
                .retry(Exception.class)
                .retryLimit(3)
                .skip(Exception.class)
                .skipLimit(3)
                .build();
    }

    @Bean
    public JdbcPagingItemReader<InboxEntity> inboxReader() throws Exception {
        JdbcPagingItemReader<InboxEntity> reader = new JdbcPagingItemReader<>();

        reader.setDataSource(dataSource);
        reader.setPageSize(100);
        reader.setRowMapper(
                (rs, rowNum) ->
                        InboxEntity.builder()
                                .id(rs.getLong("id"))
                                .eventId(rs.getString("event_id"))
                                .eventType(
                                        ConsumedEvent.EventType.valueOf(
                                                rs.getString("event_type")
                                        )
                                ).occurredAt(
                                        rs.getTimestamp("occurred_at")
                                                .toInstant()
                                ).traceId(rs.getString("trace_id"))
                                .payload(rs.getString("payload"))
                                .processed(rs.getBoolean("processed"))
                                .build()
        );

        reader.setQueryProvider(queryProvider());
        reader.afterPropertiesSet();
        return reader;
    }

    private MySqlPagingQueryProvider queryProvider() {
        MySqlPagingQueryProvider queryProvider = new MySqlPagingQueryProvider();

        queryProvider.setSelectClause("SELECT *");
        queryProvider.setFromClause("FROM inboxes");
        queryProvider.setWhereClause("WHERE processed = false");

        queryProvider.setSortKeys(Map.of("id", Order.ASCENDING));
        return queryProvider;
    }

    @Bean
    public JdbcBatchItemWriter<InboxEntity> inboxWriter() {
        JdbcBatchItemWriter<InboxEntity> writer = new JdbcBatchItemWriter<>();

        writer.setDataSource(dataSource);
        writer.setSql(
                "UPDATE inboxes " +
                "SET processed = true " +
                "WHERE id = :id"
        );

        writer.setItemSqlParameterSourceProvider(
                new BeanPropertyItemSqlParameterSourceProvider<>()
        );
        writer.afterPropertiesSet();

        return writer;
    }
}
