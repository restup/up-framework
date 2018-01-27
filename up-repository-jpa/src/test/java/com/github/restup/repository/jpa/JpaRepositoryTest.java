package com.github.restup.repository.jpa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import com.github.restup.path.ResourcePath;
import com.github.restup.query.Pagination;
import com.github.restup.query.PreparedResourceQueryStatement;
import com.github.restup.query.ResourceQueryDefaults;
import com.github.restup.query.ResourceSort;
import com.github.restup.registry.Resource;
import com.github.restup.registry.ResourceRegistry;
import com.github.restup.service.model.request.CreateRequest;
import com.github.restup.service.model.request.DeleteRequest;
import com.github.restup.service.model.request.ReadRequest;
import com.github.restup.service.model.request.UpdateRequest;
import com.github.restup.service.model.response.PagedResult;
import com.github.restup.service.model.response.PersistenceResult;
import com.github.restup.service.model.response.ReadResult;
import com.github.restup.util.TestRegistries;
import com.university.Course;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ApplicationConfig.class)
@SuppressWarnings({"unchecked", "rawtypes"})
public class JpaRepositoryTest {
    
    private ResourceRegistry registry;
    private Resource<Course,Long> courseResource;
    private JpaRepository<Course,Long> courseRepo;
    
    @PersistenceContext
    private EntityManager entityManager;
    
    @Before
    public void before() {
        registry = TestRegistries.universityRegistry();
        courseResource = courseResource();
        courseRepo = getRepository(courseResource);
    }

    private <T, ID extends Serializable> JpaRepository<T, ID> getRepository(Resource<T, ID> resource) {
        JpaRepositoryFactory factory = new JpaRepositoryFactory(entityManager);
        JpaRepository<?,?> repo = (JpaRepository<?,?>) factory.getRepository(resource);
        return (JpaRepository) repo;
    }
    
    private Resource<Course,Long> courseResource() {
        return (Resource) registry.getResource(Course.class);
    }
    
    @Test
    public void testCRUD() {
        String name = "Foo";
        Course course = new Course();
        course.setName(name);
        
        Course result = create(course);
        
        assertNotNull(result.getId());
        
        Course read = findCourse(result.getId());
        assertNotNull(read);
        assertNull(read.getNameLowerCase());
        assertEquals(name, read.getName());
        
        Course update = new Course();
        update.setId(read.getId());
        update.setNameLowerCase(name.toLowerCase());
        
        update(update, "nameLowerCase");

        read = findCourse(result.getId());
        assertNotNull(read);
        assertEquals(name.toLowerCase(), read.getNameLowerCase());
        assertEquals(name, read.getName());

        
        deleteCourse(result.getId());
        
        assertNull(findCourse(result.getId()));
    }
    
    private void verifyList(PreparedResourceQueryStatement ps) {
        verify(ps).getPagination();
        verify(ps).getRequestedCriteria();
        verify(ps).getRequestedSort();
        verify(ps).getResource();
        verifyNoMoreInteractions(ps);
    }

    private void assertList(PagedResult<Course> result, String... names) {
        List<Course> data = result.getData();
        assertEquals(names.length, data.size());
        for ( int i=0; i< names.length; i++ ) {
            assertEquals(names[0], data.get(0).getName());
        }
    }
    
    private PreparedResourceQueryStatement paginated(Integer limit, Integer offset) {
        nato();
        PreparedResourceQueryStatement ps = mock(PreparedResourceQueryStatement.class);
        when(ps.getPagination()).thenReturn(limit < 1 ? Pagination.disabled() : Pagination.of(limit, offset));
        when(ps.getResource()).thenReturn((Resource)courseResource);
        return ps;
    }
    
    private void sort(PreparedResourceQueryStatement ps, boolean b) {
        when(ps.getRequestedSort()).thenReturn(Arrays.asList(ResourceSort.of(courseResource, "name", b)));
    }
    
    @Test
    public void testPagedListLimit2() {
        PreparedResourceQueryStatement ps = paginated(2, 0);
        when(ps.getResource()).thenReturn((Resource)courseResource);
        PagedResult<Course> result = courseRepo.list(ps);
        assertList(result, "Alpha", "Bravo");
        verify(ps).getResource();
        verifyList(ps);
    }
    
    @Test
    public void testPagedListLimit1Offset2() {
        PreparedResourceQueryStatement ps = paginated(1, 2);
        PagedResult<Course> result = courseRepo.list(ps);
        assertList(result, "Charlie");
        verifyList(ps);
    }
    
    @Test
    public void testPagedListLimit1Offset2SortDesc() {
        PreparedResourceQueryStatement ps = paginated(3, 0);
        sort(ps, false);
        PagedResult<Course> result = courseRepo.list(ps);
        assertList(result, "India", "Hotel", "Golf");
        verifyList(ps);
    }

    @Test
    public void testUnpagedSortedList() {
        PreparedResourceQueryStatement ps = paginated(0,0);
        sort(ps, true);
        PagedResult<Course> result = courseRepo.list(ps);
        assertList(result, "Alpha","Bravo","Charlie","Delta","Echo","Fox Trot","Golf","Hotel","India");
        verifyList(ps);
    }

    private Course update(Course course, String field) {
        UpdateRequest<Course, Long> request = mock(UpdateRequest.class);
        when(request.getId()).thenReturn(course.getId());
        when(request.getData()).thenReturn(course);
        when(request.getResource()).thenReturn((Resource) courseResource);
        when(request.getRequestedPaths()).thenReturn(Arrays.asList(ResourcePath.path(courseResource, field)));
        PersistenceResult<Course> result = courseRepo.update(request, mock(ResourceQueryDefaults.class));
        verify(request).getId();
        verify(request).getData();
        verify(request).getRequestedPaths();
        verify(request).getResource();
        verifyNoMoreInteractions(request);
        return result.getData();
    }

    private Course findCourse(Long id) {
        ReadRequest<Course, Long> request = mock(ReadRequest.class);
        when(request.getId()).thenReturn(id);
        when(request.getResource()).thenReturn((Resource)courseResource);
        ReadResult<Course> result = courseRepo.find(request);
        verify(request).getId();
        verify(request).getResource();
        verifyNoMoreInteractions(request);
        return result.getData();
    }

    private Course deleteCourse(Long id) {
        DeleteRequest<Course, Long> request = mock(DeleteRequest.class);
        when(request.getId()).thenReturn(id);
        when(request.getResource()).thenReturn((Resource)courseResource);
        Course result = courseRepo.delete(request);
        verify(request).getId();
        verify(request).getResource();
        verifyNoMoreInteractions(request);
        return result;
    }
    
    private void nato() {
        create("Alpha","Bravo","Charlie","Delta","Echo","Fox Trot","Golf","Hotel","India");
    }
    
    private void create(String... names) {
        for ( String name : names ) {
            Course c = new Course();
            c.setName(name);
            c.setNameLowerCase(name.toLowerCase());
            create(c);
        }
    }

    private Course create(Course course) {
        CreateRequest<Course> request = mock(CreateRequest.class);
        when(request.getData()).thenReturn(course);
        Course result = courseRepo.create(request);
        verify(request).getData();
        verifyNoMoreInteractions(request);
        return result;
    }

}
