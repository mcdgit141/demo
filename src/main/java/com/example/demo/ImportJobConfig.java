package com.example.demo;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import javax.sql.DataSource;

@Configuration

public class ImportJobConfig {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private DataSource dataSource;

    @Bean
    public Job importJob() {
        return jobBuilderFactory.get("importJob")
                .incrementer(new RunIdIncrementer())
                .start(importStep())
                .build();
           }
    @Bean
    public Step importStep() {
        return stepBuilderFactory.get("import-step").<BookDTO, BookDTO>chunk(10)
                .reader(reader())
                .writer(writer())
                .build()
                ;
    }

    @Bean
    public ItemReader<BookDTO> reader() {
       {
              return new FlatFileItemReaderBuilder<BookDTO>()
                    .name("bookItemReader") //
                    .resource(new FileSystemResource("src/main/resources/sample-data.csv"))
                    .delimited()
                    .delimiter(";")
                    .names(new String[] { "title", "author", "isbn", "publisher","publishedOn" })
                    .linesToSkip(1)
                    .fieldSetMapper(new BeanWrapperFieldSetMapper<BookDTO>() {
                        {
                            setTargetType(BookDTO.class);
                        }
                    }).build();
        }
    }

    @Bean
    public JdbcBatchItemWriter<BookDTO> writer() {
        final JdbcBatchItemWriter<BookDTO> writer = new JdbcBatchItemWriter<BookDTO>();
        writer.setDataSource(dataSource);
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<BookDTO>());
        writer.setSql("INSERT INTO book (title, author, isbn, publisher, year) VALUES (:title, :author, :isbn, :publisher, :publishedOn )");
        return writer;
    }

}
