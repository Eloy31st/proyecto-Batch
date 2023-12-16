package io.bootify.proyecto_batch.config;

import io.bootify.proyecto_batch.domain.Transacciones;
import io.bootify.proyecto_batch.repos.TransaccionesRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
@AllArgsConstructor
@NoArgsConstructor
public class SpringBatchConfig {
    @Autowired
    private TransaccionesRepository repository;
    @Autowired
    private DataSource dataSource;

    @Bean
    public FlatFileItemReader<Transacciones> reader(){
        FlatFileItemReader<Transacciones> itemReader = new FlatFileItemReader<Transacciones>();
        itemReader.setResource(new FileSystemResource("src/main/resources/transacciones_enum_3.csv"));
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
        writer.setSql("INSERT INTO transacciones (fecha, cantidad, tipo, cuentaOrigen, cuentaDestino) VALUES (:fecha, :cantidad, :tipo, :cuentaOrigen, :cuentaDestino)");
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Transacciones>());
        return writer;
    }
    /*
    @Bean
    public TransaccionProcessor processor(){
        return new TransaccionProcessor();
    }

    @Bean
    public FlatFileItemReader<Transacciones> reader(){
        FlatFileItemReader<Transacciones> itemReader = new FlatFileItemReader<Transacciones>();
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

     */
    /*
    @Bean
    public JpaItemWriter<Transacciones> writer(EntityManagerFactory emf){
        JpaItemWriter<Transacciones> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(emf);
        return writer;
    }

     */
    @Bean
    public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager){
        return new StepBuilder("csv-step", jobRepository).<Transacciones, Transacciones>chunk(100, transactionManager)
                .reader(reader())
                .writer(writer()).build();
    }
    @Bean
    public Job runjob(JobRepository jobRepository, PlatformTransactionManager transactionManager){
        return new JobBuilder("importTransacciones", jobRepository)
                .flow(step1(jobRepository, transactionManager))
                .end().build();
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
