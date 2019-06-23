package com.github.restup.test.matchers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.hamcrest.Description;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ContentTypeMatcherTest {

    @Test
    public void testMatches() {
        ContentTypeMatcher matcher = new ContentTypeMatcher("application/foo");
        assertFalse(matcher.matches("foo"));
        assertTrue(matcher.matches("application/foo"));
        assertTrue(matcher.matches("application/foo; charset=utf-8"));
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
