package com.github.restup.spring.boot.autoconfigure;

import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig.DefaultTableNameResolver;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig.TableNameResolver;
import com.github.restup.identity.IdentityStrategy;
import com.github.restup.identity.UUIDIdentityStrategy;
import com.github.restup.repository.RepositoryFactory;
import com.github.restup.repository.dynamodb.DynamoDBRepository;
import com.github.restup.repository.dynamodb.DynamoDBRepositoryFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass({DynamoDBMapper.class, DynamoDBRepository.class})
@ConditionalOnProperty(value = "up.dynamodb.enabled", havingValue = "true", matchIfMissing = true)
@AutoConfigureBefore(UpAutoConfiguration.class)
public class DynamoDBRepositoryAutoConfiguration {


    @Bean
    @ConditionalOnMissingBean(value = {RepositoryFactory.class, DynamoDBRepositoryFactory.class})
    public RepositoryFactory defaultUpDynamoDBRepositoryFactory(DynamoDBMapper mapper,
        IdentityStrategy identityStrategy) {
        return DynamoDBRepositoryFactory.builder()
            .dynamoDBMapper(mapper)
            .identityStrategy(identityStrategy)
            .build();
    }


    @Bean
    @ConditionalOnMissingBean(value = {RepositoryFactory.class, DynamoDBRepositoryFactory.class,
        IdentityStrategy.class})
    public IdentityStrategy defaultUpIdentityStrategy() {
        return new UUIDIdentityStrategy();
    }

    @Bean
    @ConditionalOnProperty("aws.dynamodb.endpoint")
    @ConditionalOnMissingBean(value = {DynamoDBMapper.class})
    public EndpointConfiguration EndpointConfiguration(
        @Value("${aws.dynamodb.endpoint:}") String amazonDynamoDBEndpoint,
        @Value("${aws.dynamodb.region:us-east-1}") String amazonDynamoDBRegion) {
        return new EndpointConfiguration(amazonDynamoDBEndpoint, amazonDynamoDBRegion);
    }

    @Bean
    @ConditionalOnMissingBean(value = {AmazonDynamoDB.class, EndpointConfiguration.class,
        DynamoDBMapper.class})
    public AmazonDynamoDB amazonDynamoDBDefault() {
        return AmazonDynamoDBClientBuilder.defaultClient();
    }

    @Bean
    @ConditionalOnMissingBean(value = {AmazonDynamoDB.class, DynamoDBMapper.class})
    public AmazonDynamoDB amazonDynamoDBStandard(EndpointConfiguration endpointConfiguration) {
        return AmazonDynamoDBClientBuilder.standard()
            .withEndpointConfiguration(endpointConfiguration).build();
    }

    @Bean
    @ConditionalOnMissingBean(value = {DynamoDBMapperConfig.class, DynamoDBMapper.class})
    public DynamoDBMapperConfig defaultDynamoDBMapperConfig(TableNameResolver tableNameResolver) {
        return DynamoDBRepositoryFactory.getDynamoDBMapperConfigDefault(tableNameResolver);
    }

    @Bean
    @ConditionalOnMissingBean
    public DynamoDBMapper defaultDynamoDBMapperProvider(AmazonDynamoDB db,
        DynamoDBMapperConfig config) {
        return new DynamoDBMapper(db, config);
    }
    
    @Bean
    @ConditionalOnMissingBean
    public TableNameResolver defaultTableNameResolver() {
        return new DefaultTableNameResolver();
    }

}
