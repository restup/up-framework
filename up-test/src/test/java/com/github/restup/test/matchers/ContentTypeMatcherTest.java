package com.github.restup.test.matchers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import org.hamcrest.Description;

@RunWith(MockitoJUnitRunner.class)
public class ContentTypeMatcherTest {

    @Test
    public void testMatches() {
        ContentTypeMatcher matcher = new ContentTypeMatcher("application/foo");
        assertFalse(matcher.matches("foo"));
        assertTrue(matcher.matches(new String[] {"application/foo"}));
        assertTrue(matcher.matches(new String[] {"application/foo; charset=utf-8"}));
    }

    @Test
    public void testDescribeTo() {
        ContentTypeMatcher matcher = new ContentTypeMatcher("application/foo");
        Description desc = Mockito.mock(Description.class);
        when(desc.appendText(any())).thenReturn(desc);
        matcher.describeTo(desc);

        verify(desc).appendText("Content-Type=");
        verify(desc).appendValue("application/foo");
        verifyNoMoreInteractions(desc);
    }
}
