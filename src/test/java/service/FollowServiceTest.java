package service;

import org.apache.bval.cdi.BValInterceptor;
import org.apache.openejb.testing.Classes;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import persistence.model.Follow;
import persistence.model.Section;
import persistence.model.User;
import persistence.repo.FollowRepository;
import persistence.repo.SectionRepository;
import persistence.repo.UserRepository;
import rocks.limburg.cdimock.CdiMock;
import service.dto.CurrentUser;
import javax.inject.Inject;
import javax.validation.ConstraintViolationException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Classes(cdi = true,
        value={FollowService.class},
        cdiInterceptors = BValInterceptor.class,
        cdiStereotypes = CdiMock.class)
public class FollowServiceTest extends ServiceTest{
    @Mock private SectionRepository sectionRepo;
    @Mock private FollowRepository followRepo;
    @Mock private UserRepository userRepo;
    @Mock private CurrentUser currentUser;
    @Inject private FollowService service;

    @ParameterizedTest
    @ValueSource(ints = {1, 5, 8})
    void successfulFollow(int sectionId){
        User user = new User();
        when(currentUser.getUsername()).thenReturn("username");
        when(userRepo.getByName(any())).thenReturn(user);
        Section section = new Section();
        when(sectionRepo.findById(sectionId)).thenReturn(section);
        Follow follow = new Follow();
        when(followRepo.insert(any())).thenReturn(follow);
        assertDoesNotThrow(() -> service.follow(sectionId));
        Follow follow1 = service.follow(sectionId);
        assertEquals(follow,follow1);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, -5, -8})
    void failFollowWithWrongId(int sectionId){
        User user = new User();
        when(currentUser.getUsername()).thenReturn("username");
        when(userRepo.getByName(any())).thenReturn(user);
        when(sectionRepo.findById(sectionId)).thenReturn(null);
        Follow follow = new Follow();
        when(followRepo.insert(any())).thenReturn(follow);
        assertThrows(ConstraintViolationException.class,() -> service.follow(sectionId));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 5, 8})
    void successfulUnFollow(int sectionId){
        User user = new User();
        when(currentUser.getUsername()).thenReturn("username");
        when(userRepo.getByName(any())).thenReturn(user);
        Section section = new Section();
        when(sectionRepo.findById(sectionId)).thenReturn(section);
        Follow follow = new Follow();
        when(followRepo.findById(any())).thenReturn(follow);
        assertDoesNotThrow(() -> service.unFollow(sectionId));
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, -5, -8})
    void failUnFollowWithWrongId(int sectionId){
        User user = new User();
        when(currentUser.getUsername()).thenReturn("username");
        when(userRepo.getByName(any())).thenReturn(user);
        when(sectionRepo.findById(sectionId)).thenReturn(null);
        Follow follow = new Follow();
        when(followRepo.findById(any())).thenReturn(follow);
        assertThrows(ConstraintViolationException.class,() -> service.unFollow(sectionId));
    }

}
