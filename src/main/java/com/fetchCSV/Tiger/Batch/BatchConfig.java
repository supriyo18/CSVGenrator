package com.fetchCSV.Tiger.Batch;


import com.fetchCSV.Tiger.Repository.CustomRepository;
import com.fetchCSV.Tiger.dto.CustomDto;
import org.jetbrains.annotations.NotNull;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.builder.StepBuilder; // Recommended approach (Spring Batch 5+)
import org.springframework.batch.core.job.builder.JobBuilder; // Recommended approach (Spring Batch 5+)
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static java.lang.reflect.Array.get;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    @Autowired
    private PlatformTransactionManager transactionManager; // Autowire the transaction manager


    @Bean
    public Job exportJob(JobRepository jobRepository, Step step1) {
        return new  JobBuilder("exportJob",jobRepository)// Creates a new Job instance
                .incrementer(new RunIdIncrementer()) // Sets the incrementer
                .start(step1) // Starts the job with the specified step
                .build(); // Builds and returns the Job object
    }

    @Bean
    public Step step1(JobRepository jobRepository, RepositoryItemReader<CustomDto> reader,
                      ItemProcessor<CustomDto, CustomDto> processor, FlatFileItemWriter<CustomDto> writer) {
        return new StepBuilder("step1",jobRepository)
                .<CustomDto, CustomDto>chunk(1000, transactionManager) // Inject PlatformTransactionManager
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }
/*
    @Bean
    public RepositoryItemReader<CustomDto> reader(CustomRepository customRepository) {
        Map<String, Sort.Direction> sorts = new HashMap<>();
        sorts.put("id", Sort.Direction.ASC);

        return new RepositoryItemReaderBuilder<CustomDto>()
                .name("reader")
                .repository(customRepository)
                .methodName("findJoinedData")
                .pageSize(1000)
                .sorts(sorts)
                .build();
    }

 */
@Bean
public RepositoryItemReader<CustomDto> reader(CustomRepository customRepository) {
    Map<String, Sort.Direction> sorts = Collections.singletonMap("id", Sort.Direction.ASC);

    return new RepositoryItemReaderBuilder<CustomDto>()
            .name("reader")
            .repository(customRepository)
            .methodName("findJoinedData")
            .pageSize(50000)
            .sorts(sorts)
            .build();
}
    @Bean
    public ItemProcessor<CustomDto, CustomDto> processor() {
        return item -> item; // Add processing logic if necessary
    }

    @Bean
    public FlatFileItemWriter<CustomDto> writer() {
        BeanWrapperFieldExtractor<CustomDto> fieldExtractor = new BeanWrapperFieldExtractor<>();
        fieldExtractor.setNames(new String[] {"id", "column1A", "column2A", "column1B", "column2B", "column1C", "column2C"});

        DelimitedLineAggregator<CustomDto> lineAggregator = new DelimitedLineAggregator<>() {
            @NotNull
            @Override
            public String aggregate(CustomDto item) {
                StringBuilder sb = new StringBuilder();
                for (Object field : fieldExtractor.extract(item)) {
                    if (sb.length() > 0) {
                        sb.append(",");
                    }
                    sb.append("\"").append(field).append("\"");
                }
                return sb.toString();
            }
        };
        lineAggregator.setFieldExtractor(fieldExtractor);

        return new FlatFileItemWriterBuilder<CustomDto>()
                .name("customDtoItemWriter")
                .resource(new FileSystemResource("src/main/resources/output.csv"))
                .lineAggregator(lineAggregator)
                .build();
    }
}
