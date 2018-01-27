package com.github.restup.bind.converter;

import static org.junit.Assert.assertEquals;
import java.sql.Date;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import org.junit.Test;

public class StringToDateConverterTest {
    
    @Test
    public void testConvert() {
        StringToDateConverter converter = new StringToDateConverter();
        assertEquals(java.util.Date.from(ZonedDateTime.of(2012, 4, 29, 0, 0, 0, 0, ZoneOffset.UTC).toInstant()), converter.apply("2012-04-29T00:00:00Z"));
    }

    @Test
    public void testAndThen() {
        StringToDateConverter converter = new StringToDateConverter();
        assertEquals(java.util.Date.from(ZonedDateTime.of(2012, 4, 29, 0, 0, 0, 0, ZoneOffset.UTC)
                .toInstant()).getTime()
                // ensure use of andThen for coverage
                , converter.andThen(d-> d.getTime())
                .apply("2012-04-29T00:00:00Z").longValue());
    }

}
