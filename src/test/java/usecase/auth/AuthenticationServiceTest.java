package usecase.auth;

import model.entity.Ban;
import model.entity.User;
import model.repository.GenericRepository;
import model.repository.UserRepository;
import org.apache.bval.cdi.BValInterceptor;
import org.apache.openejb.testing.Classes;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import rocks.limburg.cdimock.CdiMock;
import usecase.ServiceTest;

import javax.inject.Inject;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@Classes(cdi=true, value = {AuthenticationService.class, Pbkdf2PasswordHash.class},
            cdiInterceptors = BValInterceptor.class,
            cdiStereotypes = CdiMock.class)
class AuthenticationServiceTest extends ServiceTest {
    @Mock UserRepository userRepo;
    @Mock GenericRepository genericRepo;
    @Inject
    Pbkdf2PasswordHash passwordHash;
    @Inject AuthenticationService authenticationService;

    @Test
    void authenticateUserDoesntExist(){
        when(genericRepo.findByNaturalId(ArgumentMatchers.<Class<User>>any(),any())).thenReturn(null);
        assertFalse(authenticationService.authenticate("usecase/user", "pass"));
    }

    @Test
    void authenticationPasswordMatches(){
        User user = new User();
        user.setUsername("usecase/user");
        user.setId(5);
        Pbkdf2PasswordHash.HashedPassword expected = passwordHash.generate("password");
        user.setPassword(expected.getPassword());
        user.setSalt(expected.getSalt());

        when(genericRepo.findByNaturalId(ArgumentMatchers.<Class<User>>any(),any())).thenReturn(user);
        assertTrue(authenticationService.authenticate("usecase/user", "password"));
    }

    @Test
    void authenticationPasswordDoesntMatch(){
        User user = new User();
        user.setUsername("usecase/user");



        Pbkdf2PasswordHash.HashedPassword expected = passwordHash.generate("password");
        user.setPassword(expected.getPassword());
        user.setSalt(expected.getSalt());

        when(genericRepo.findByNaturalId(ArgumentMatchers.<Class<User>>any(),any())).thenReturn(user);
        assertFalse(authenticationService.authenticate("usecase/user", "notpassword"));
    }

    @Test
    void getCurrentUserIdZero(){
        User user = new User();
        user.setUsername("usecase/user");
        user.setId(0);
        Pbkdf2PasswordHash.HashedPassword expected = passwordHash.generate("password");
        user.setPassword(expected.getPassword());
        user.setSalt(expected.getSalt());
        when(genericRepo.findByNaturalId(ArgumentMatchers.<Class<User>>any(),any())).thenReturn(user);
        assertTrue(authenticationService.authenticate("usecase/user", "password"));

        assertFalse(authenticationService.getCurrentUser().isLoggedIn());
    }

    @Test
    void getCurrentUserNotFound(){
        User user = new User();
        user.setUsername("usecase/user");
        user.setId(2);
        Pbkdf2PasswordHash.HashedPassword expected = passwordHash.generate("password");
        user.setPassword(expected.getPassword());
        user.setSalt(expected.getSalt());
        when(genericRepo.findByNaturalId(ArgumentMatchers.<Class<User>>any(),any())).thenReturn(user);
        assertTrue(authenticationService.authenticate("usecase/user", "password"));


        when(genericRepo.findById(User.class,2)).thenReturn(null);
        assertFalse(authenticationService.getCurrentUser().isLoggedIn());
    }

    @Test
    void getCurrentUserWithoutBan(){
        User user = new User();
        user.setUsername("usecase/user");
        user.setId(2);
        user.setAdmin(false);
        user = spy(user);
        when(user.getBans()).thenReturn(List.of());

        Pbkdf2PasswordHash.HashedPassword expected = passwordHash.generate("password");
        user.setPassword(expected.getPassword());
        user.setSalt(expected.getSalt());
        when(genericRepo.findByNaturalId(ArgumentMatchers.<Class<User>>any(),any())).thenReturn(user);
        assertTrue(authenticationService.authenticate("usecase/user", "password"));


        when(genericRepo.findById(User.class,2)).thenReturn(user);
        assertNull(authenticationService.getCurrentUser().getBanDuration());
    }

    @Test
    void getCurrentUserWithBan(){
        User user = new User();
        user.setUsername("usecase/user");
        user.setId(2);
        user.setAdmin(false);
        user = spy(user);
        Ban ban = new Ban();
        ban.setEndTime(Instant.MAX);
        when(user.getBans()).thenReturn(List.of(ban));

        Pbkdf2PasswordHash.HashedPassword expected = passwordHash.generate("password");
        user.setPassword(expected.getPassword());
        user.setSalt(expected.getSalt());
        when(genericRepo.findByNaturalId(ArgumentMatchers.<Class<User>>any(),any())).thenReturn(user);
        assertTrue(authenticationService.authenticate("usecase/user", "password"));


        when(genericRepo.findById(User.class,2)).thenReturn(user);
        assertEquals(authenticationService.getCurrentUser().getBanDuration(), ban.getEndTime());
    }
}