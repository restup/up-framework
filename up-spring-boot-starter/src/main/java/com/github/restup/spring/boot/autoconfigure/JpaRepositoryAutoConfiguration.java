package com.github.restup.spring.boot.autoconfigure;

import javax.persistence.EntityManager;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.github.restup.repository.RepositoryFactory;
import com.github.restup.repository.jpa.JpaRepository;
import com.github.restup.repository.jpa.JpaRepositoryFactory;

@Configuration
@ConditionalOnClass({EntityManager.class, JpaRepository.class})
@AutoConfigureBefore(UpAutoConfiguration.class)
public class JpaRepositoryAutoConfiguration {
    // TODO perhaps this should extend HibernateJpaAutoConfiguration to create an EntityManager if
    // needed?

    @Bean
    @ConditionalOnMissingBean
    public JpaRepository<?, ?> defaultUpJpaRepository(EntityManager entityManager) {
        // JpaRepository has to be defined as a spring bean to be proxied for transaction management
        return new JpaRepository<>(entityManager);
    }

    @Bean
    @ConditionalOnMissingBean(value = {RepositoryFactory.class, JpaRepositoryFactory.class})
    public RepositoryFactory defaultUpJpaRepositoryFactory(JpaRepository<?, ?> jpaRepository) {
        return new JpaRepositoryFactory(jpaRepository);
    }

}
