package com.nttdata.user_service.domain.model;

import org.junit.jupiter.api.Test;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void validateTokenOwnership_ShouldReturnTrue_WhenTokenMatches() {
        User user = new User();
        user.setToken("validToken");

        assertTrue(user.validateTokenOwnership("validToken"));
    }

    @Test
    void validateTokenOwnership_ShouldReturnFalse_WhenTokenDoesNotMatch() {
        User user = new User();
        user.setToken("validToken");

        assertFalse(user.validateTokenOwnership("invalidToken"));
    }

    @Test
    void validateTokenOwnership_ShouldReturnFalse_WhenUserHasNoToken() {
        User user = new User();
        user.setToken(null);

        assertFalse(user.validateTokenOwnership("anyToken"));
    }

    @Test
    void onCreate_ShouldSetDatesAndActive_WhenExecuted() throws Exception {
        User user = new User();

        Method onCreateMethod = User.class.getDeclaredMethod("onCreate");
        onCreateMethod.setAccessible(true);

        onCreateMethod.invoke(user);

        assertNotNull(user.getCreated());
        assertNotNull(user.getLastLogin());
        assertTrue(user.isActive());
        assertEquals(user.getCreated(), user.getLastLogin());
    }

    @Test
    void onUpdate_ShouldSetModifiedDate_WhenExecuted() throws Exception {
        User user = new User();

        Method onUpdateMethod = User.class.getDeclaredMethod("onUpdate");
        onUpdateMethod.setAccessible(true);

        onUpdateMethod.invoke(user);

        assertNotNull(user.getModified());
        assertTrue(user.getModified().isAfter(LocalDateTime.now().minusSeconds(1)));
    }
}