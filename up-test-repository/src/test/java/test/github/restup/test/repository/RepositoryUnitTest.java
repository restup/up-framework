package test.github.restup.test.repository;

import static com.github.restup.test.assertions.Assertions.assertPrivateConstructor;
import static com.github.restup.util.TestRegistries.universityRegistry;
import org.junit.Test;
import com.github.restup.registry.ResourceRegistry;
import com.github.restup.test.repository.RepositoryUnit;

public class RepositoryUnitTest {

    @Test
    public void testLoadWithDefaults() {
        ResourceRegistry registry = universityRegistry();
        RepositoryUnit.load(registry, "course");
    }

    @Test
    public void testLoad() {
        ResourceRegistry registry = universityRegistry();
        RepositoryUnit.loader()
            .mapper(RepositoryUnit.getMapper())
            .registry(registry)
            .relativeTo(getClass())
            .fileName("course")
            .load();
    }
    
    @Test 
    public void testConstructor() {
        assertPrivateConstructor(RepositoryUnit.class);
    }
    
}
