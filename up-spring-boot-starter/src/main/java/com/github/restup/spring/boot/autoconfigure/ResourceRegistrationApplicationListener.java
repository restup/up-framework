package com.github.restup.spring.boot.autoconfigure;
import static com.github.restup.spring.boot.autoconfigure.UpAutoConfiguration.getResources;
import java.util.Collection;
import java.util.Set;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import com.github.restup.registry.Resource;
import com.github.restup.registry.ResourceRegistry;
import com.github.restup.service.ServiceFilter;

/**
 * {@link ApplicationListener} which builds {@link Resource}s using available spring beans and then
 * registers them to the {@link ResourceRegistry}
 * 
 * @author abuttaro
 *
 */
public class ResourceRegistrationApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext context = event.getApplicationContext();
        ResourceRegistry registry = context.getBean(ResourceRegistry.class);


        Collection<ServiceFilter> filters = context.getBeansOfType(ServiceFilter.class).values();

        // TODO additional configuration and/or detect resource specific resource /service implementations

        Set<Class<?>> resourceClasses = getResourceClasses(context);

        resourceClasses
                .parallelStream()
                .forEach(c -> registry
                        .registerResource(Resource.builder(c)
                                .serviceFilters(filters)));

    }

    static Set<Class<?>> getResourceClasses(ApplicationContext context) {
        try {
            return getResources(context);
        } catch (ClassNotFoundException e) {
            // not possible... classes found from classpath not found?
            throw new IllegalStateException(e);
        }
    }


}
