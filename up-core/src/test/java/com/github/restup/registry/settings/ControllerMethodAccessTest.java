package com.github.restup.registry.settings;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class ControllerMethodAccessTest {

    @Test
    public void testEnabled() {
        ControllerMethodAccess access = ControllerMethodAccess.builder().setAllEnabled().build();
        assertAccess(true, access);
    }

    @Test
    public void testDisabled() {
        ControllerMethodAccess access = ControllerMethodAccess.builder().setAllDisabled().build();
        assertAccess(false, access);
    }

    private void assertAccess(boolean b, ControllerMethodAccess access) {
        assertEquals(!b, access.isCreateDisabled());
        assertEquals(!b, access.isCreateMultipleDisabled());
        assertEquals(!b, access.isDeleteByIdDisabled());
        assertEquals(!b, access.isDeleteByIdsDisabled());
        assertEquals(!b, access.isDeleteByQueryDisabled());
        assertEquals(!b, access.isGetByIdDisabled());
        assertEquals(!b, access.isGetByIdsDisabled());
        assertEquals(!b, access.isListDisabled());
        assertEquals(!b, access.isPatchByIdDisabled());
        assertEquals(!b, access.isPatchByIdsDisabled());
        assertEquals(!b, access.isPatchByQueryDisabled());
        assertEquals(!b, access.isPatchMultipleDisabled());
        assertEquals(!b, access.isUpdateByIdDisabled());
        assertEquals(!b, access.isUpdateMultipleDisabled());

    }

}
