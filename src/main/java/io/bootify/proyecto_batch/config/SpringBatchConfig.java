package io.bootify.proyecto_batch.config;

import io.bootify.proyecto_batch.domain.Transacciones;
import io.bootify.proyecto_batch.repos.TransaccionesRepository;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import java.beans.PropertyEditor;

import java.time.LocalDate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

@Configuration
@EnableBatchProcessing
@AllArgsConstructor
public class SpringBatchConfig {

    private TransaccionesRepository repository;

    private JobRepository jobRepository;

    private PlatformTransactionManager transactionManager;

    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");


    /*
    @Bean
    public FlatFileItemReader<Transacciones> reader(){
        FlatFileItemReader<Transacciones> itemReader = new FlatFileItemReader<Transacciones>();
        itemReader.setResource(new ClassPathResource("src/main/resources/transacciones_enum_3.csv"));
        itemReader.setLineMapper(new DefaultLineMapper<Transacciones>() {
            {
                setLineTokenizer(new DelimitedLineTokenizer() {
                    {
                        setNames(new String[] { "fecha", "cantidad", "tipo", "cuentaOrigen", "cuentaDestino" });
                    }
                });
                setFieldSetMapper(new BeanWrapperFieldSetMapper<Transacciones>(){
                    {
                        setTargetType(Transacciones.class);
                    }
                });
            }
        });
        return itemReader;
    }

    @Bean
    public JdbcBatchItemWriter<Transacciones> writer(){
        JdbcBatchItemWriter<Transacciones> writer = new JdbcBatchItemWriter<Transacciones>();
        writer.setDataSource(dataSource);
        System.out.println("writer");
        writer.setSql("INSERT INTO transaccioneses (fecha, cantidad, tipo, cuentaOrigen, cuentaDestino) VALUES (:fecha, :cantidad, :tipo, :cuentaOrigen, :cuentaDestino)");
        System.out.println("writer2");
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Transacciones>());
        return writer;
    }


    */

    @Bean
    public FlatFileItemReader<Transacciones> reader(){
        FlatFileItemReader<Transacciones> itemReader = new FlatFileItemReader<Transacciones>();
        itemReader.setResource(new FileSystemResource("src/main/resources/operaciones_bancarias.csv"));
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

        CustomDateEditor customDateEditor = new CustomDateEditor(format, false);
        HashMap<Class, PropertyEditor> customEditors = new HashMap<>();
        customEditors.put(Date.class, customDateEditor);
        fieldSetMapper.setCustomEditors(customEditors);

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
    public Step step1(ItemReader<Transacciones> reader, RepositoryItemWriter<Transacciones> writer, ItemProcessor<Transacciones, Transacciones> processor){
        return new StepBuilder("csv-step", jobRepository).<Transacciones, Transacciones>chunk(100, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }
    /*
    @Bean
    public Step step1(){
        return new StepBuilder("csv-step", jobRepository).<Transacciones, Transacciones>chunk(100, transactionManager)
                .reader(reader())
                .writer(writer()).build();
    }
    */
    @Bean
    public Job runjob(Step step1){
        return new JobBuilder("importTransacciones", jobRepository)
                .start(step1)
                .build();
    }

    @Bean
    public TaskExecutor taskExecutor() {
        SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();
        taskExecutor.setConcurrencyLimit(5);
        return taskExecutor;
    }
    /*
    @Bean
    public LocalContainerEntityManagerFactoryBean emf() {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setPackagesToScan("my.package");
        emf.setJpaVendorAdapter(jpaAdapter());
        emf.setJpaProperties(jpaProterties());
        return emf;
    }

     */
}
