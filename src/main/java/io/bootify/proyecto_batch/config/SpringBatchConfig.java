package io.bootify.proyecto_batch.config;

import io.bootify.proyecto_batch.domain.Transacciones;
import io.bootify.proyecto_batch.repos.TransaccionesRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

@Configuration
@EnableBatchProcessing
@AllArgsConstructor
@NoArgsConstructor
public class SpringBatchConfig {

    private JobBuilder jobBuilderFactory;
    private StepBuilder stepBuilderFactory;
    private TransaccionesRepository repository;

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

    public RepositoryItemWriter
}
