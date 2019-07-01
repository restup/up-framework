package com.github.restup.repository.dynamodb;

import static com.github.restup.util.UpUtils.nvl;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig.DefaultTableNameResolver;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig.SaveBehavior;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig.TableNameResolver;
import com.github.restup.identity.IdentityStrategy;
import com.github.restup.identity.UUIDIdentityStrategy;
import com.github.restup.registry.Resource;
import com.github.restup.repository.RepositoryFactory;

public class DynamoDBRepositoryFactory implements RepositoryFactory {

    private final DynamoDBRepository<?, ?> repository;

    DynamoDBRepositoryFactory(DynamoDBRepository<?, ?> repository) {
        this.repository = repository;
    }

    public static EndpointConfiguration getEndpointConfiguration(String amazonDynamoDBEndpoint,
        String amazonDynamoDBRegion) {
        return new EndpointConfiguration(amazonDynamoDBEndpoint, amazonDynamoDBRegion);
    }

    public static AmazonDynamoDB getAmazonDynamoDBDefault() {
        return AmazonDynamoDBClientBuilder.defaultClient();
    }

    public static DynamoDBMapperConfig getDynamoDBMapperConfigDefault(
        TableNameResolver tableNameResolver) {
        return getDynamoDBMapperConfigBuilder()
            .withTableNameResolver(tableNameResolver)
            .build();
    }

    public static DynamoDBMapper getDynamoDBMapperDefault(AmazonDynamoDB db,
        DynamoDBMapperConfig config) {
        return new DynamoDBMapper(db, config);
    }

    public static TableNameResolver getTableNameResolverDefault() {
        return new DefaultTableNameResolver();
    }

    /**
     * {@link DynamoDBMapperConfig.Builder} with save behavior to ignore nulls and unmodelled
     * attributes
     */
    public static com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig.Builder getDynamoDBMapperConfigBuilder() {
        return DynamoDBMapperConfig.builder()
            .withSaveBehavior(SaveBehavior.UPDATE_SKIP_NULL_ATTRIBUTES);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(AmazonDynamoDB ddb) {
        return builder().amazonDynamoDB(ddb);
    }

    @Override
    public Object getRepository(Resource<?, ?> resource) {
        return repository;
    }


    public static final class Builder {

        private IdentityStrategy identityStrategy;
        private DynamoDBMapper dynamoDBMapper;
        private AmazonDynamoDB amazonDynamoDB;
        private DynamoDBMapperConfig dynamoDBMapperConfig;


        private Builder() {

        }

        Builder me() {
            return this;
        }

        public Builder identityStrategy(IdentityStrategy identityStrategy) {
            this.identityStrategy = identityStrategy;
            return me();
        }

        public Builder dynamoDBMapper(DynamoDBMapper dynamoDBMapper) {
            this.dynamoDBMapper = dynamoDBMapper;
            return me();
        }


        /**
         * Used to create default {@link DynamoDBMapperConfig}  and {@link DynamoDBMapper}, ignored
         * DynamoDBMapper is configured.
         */
        public Builder dynamoDBMapperConfig(DynamoDBMapperConfig dynamoDBMapperConfig) {
            this.dynamoDBMapperConfig = dynamoDBMapperConfig;
            return me();
        }


        /**
         * Used to create default {@link DynamoDBMapperConfig}  and {@link DynamoDBMapper}, ignored
         * DynamoDBMapper is configured.
         */
        public Builder tableNameResolver(TableNameResolver tableNameResolver) {
            return dynamoDBMapperConfig(getDynamoDBMapperConfigDefault(tableNameResolver));
        }

        /**
         * Used to create default {@link DynamoDBMapper}, ignored DynamoDBMapper is configured.
         */
        public Builder amazonDynamoDB(AmazonDynamoDB amazonDynamoDB) {
            this.amazonDynamoDB = amazonDynamoDB;
            return me();
        }

        public Builder amazonDynamoDB(EndpointConfiguration endpointConfiguration) {
            return amazonDynamoDB(AmazonDynamoDBClientBuilder.standard()
                .withEndpointConfiguration(endpointConfiguration).build());
        }

        public Builder amazonDynamoDB(String amazonDynamoDBEndpoint, String amazonDynamoDBRegion) {
            if (isNotEmpty(amazonDynamoDBEndpoint)) {
                return amazonDynamoDB(
                    getEndpointConfiguration(amazonDynamoDBEndpoint,
                        nvl(amazonDynamoDBRegion, "us-east-1")));
            }
            return me();
        }


        public DynamoDBRepositoryFactory build() {

            DynamoDBMapper mapper = dynamoDBMapper;
            if (mapper == null) {
                AmazonDynamoDB ddb = nvl(amazonDynamoDB,
                    DynamoDBRepositoryFactory::getAmazonDynamoDBDefault);

                DynamoDBMapperConfig config = getDynamoDBMapperConfigBuilder().build();
                mapper = getDynamoDBMapperDefault(ddb, config);
            }

            IdentityStrategy identityStrategy = nvl(this.identityStrategy,
                () -> new UUIDIdentityStrategy());

            DynamoDBRepository repository = new DynamoDBRepository(mapper, identityStrategy);
            return new DynamoDBRepositoryFactory(repository);
        }

    }
}
