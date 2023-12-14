package io.bootify.proyecto_batch.config;

import io.bootify.proyecto_batch.domain.Transacciones;
import io.bootify.proyecto_batch.repos.TransaccionesRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
@AllArgsConstructor
@NoArgsConstructor
public class SpringBatchConfig {

    private TransaccionesRepository repository;
    private JobRepository jobRepository;
    private PlatformTransactionManager transactionManager;

    @Bean
    public FlatFileItemReader<Transacciones> reader(){
        FlatFileItemReader<Transacciones> itemReader = new FlatFileItemReader();
        itemReader.setResource(new FileSystemResource("src/main/resources/transacciones_enum_3.csv"));
        itemReader.setName("csvReader");
        itemReader.setLinesToSkip(1);
        itemReader.setLineMapper(lineMapper());
        return itemReader;
    }

    private LineMapper<Transacciones> lineMapper() {
        DefaultLineMapper<Transacciones> lineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames("fecha", "cantidad", "tipo", "cuentaOrigen", "cuentaDestino");
        BeanWrapperFieldSetMapper<Transacciones> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Transacciones.class);
        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;
    }

    @Bean
    public TransaccionProcessor processor(){
        return new TransaccionProcessor();
    }

    @Bean
    public RepositoryItemWriter<Transacciones> writer(){
        RepositoryItemWriter<Transacciones> writer = new RepositoryItemWriter<>();
        writer.setRepository(repository);
        writer.setMethodName("save");
        return writer;
    }
    @Bean
    public Step step1(){
        return new StepBuilder("csv-step", jobRepository).<Transacciones, Transacciones>chunk(100, transactionManager)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .build();
    }

    public Job runjob(){
        return new JobBuilder("importTransacciones", jobRepository)
                .flow(step1())
                .end().build();
    }
}
