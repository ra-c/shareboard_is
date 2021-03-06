package usecase.user;

import media.MediaRepository;
import model.entity.User;
import model.repository.GenericRepository;
import model.repository.UserRepository;
import org.apache.bval.cdi.BValInterceptor;
import org.apache.openejb.testing.Classes;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import rocks.limburg.cdimock.CdiMock;
import usecase.ServiceTest;
import usecase.auth.AuthorizationException;
import usecase.auth.CurrentUser;
import usecase.auth.Pbkdf2PasswordHash;

import javax.inject.Inject;
import javax.validation.ConstraintViolationException;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;



@Classes(cdi = true,
        value={Pbkdf2PasswordHash.class, UserService.class},
        cdiInterceptors = BValInterceptor.class,
        cdiStereotypes = CdiMock.class)
public class UserServiceTest extends ServiceTest {

    @Mock
    MediaRepository mediaRepository;
    @Mock UserRepository userRepository;
    @Mock GenericRepository genericRepository;
    @Mock CurrentUser currentUser;
    @Inject UserService service;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @ParameterizedTest
    @CsvSource({"1,true", "5,false", "100,true"})
    void successfulToggleAdmin(int id, boolean currentlyAdmin) {
        User user = new User();
        user.setId(1);
        user.setAdmin(currentlyAdmin);
        when(genericRepository.findById(User.class,id)).thenReturn(user);
        service.toggleAdmin(id);
        assertEquals(!currentlyAdmin, user.getAdmin());
    }

    @Test
    void nullToggleAdmin(){
        User user = new User();
        user.setId(1);
        user.setAdmin(null);
        when(genericRepository.findById(User.class,1)).thenReturn(user);
        assertThrows(NullPointerException.class , () -> service.toggleAdmin(1));
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0, 50})
    void userNotExistsToggleAdmin(int id){
        when(genericRepository.findById(User.class,id)).thenReturn(null);
        assertThrows(ConstraintViolationException.class,() -> service.toggleAdmin(id));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 3, 40})
    void successfulGetUserById(int id){
        User user = new User();
        user.setId(1);
        user.setUsername("username");
        user.setAdmin(true);
        when(genericRepository.findById(User.class,id)).thenReturn(user);
        assertDoesNotThrow(() -> service.getUser(id));
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, -3, -40})
    void failGetUserWithWrongId(int id){
        when(genericRepository.findById(User.class,id)).thenReturn(null);
        assertThrows(ConstraintViolationException.class,() -> service.getUser(id));
    }

    @ParameterizedTest
    @ValueSource(strings = {"name1", "name2", "name3"})
    void successfulGetUserByName(String name){
        User user = new User();
        user.setId(1);
        user.setUsername("username");
        user.setAdmin(true);
        when(genericRepository.findByNaturalId(User.class,name)).thenReturn(user);
        assertDoesNotThrow(() -> service.getUser(name));
    }

    @ParameterizedTest
    @ValueSource(strings = {"wrong1", "wrong2", "wrong3"})
    void failGetUserWithWrongName(String name){
        when(genericRepository.findByNaturalId(User.class,name)).thenReturn(null);
        assertThrows(ConstraintViolationException.class,() -> service.getUser(name));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 3, 40})
    void successfulGetUsernameById(int id){
        User user = new User();
        user.setId(1);
        user.setUsername("username");
        when(genericRepository.findById(User.class,id)).thenReturn(user);
        assertDoesNotThrow(() -> service.getUsernameById(id));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 3, 40})
    void failGetUsernameWithWrongId(int id){
        when(genericRepository.findById(User.class,id)).thenReturn(null);
        assertThrows(ConstraintViolationException.class,() -> service.getUsernameById(id));
    }

    @Test
    public void testShowUsers() {
        List<User> users = IntStream.range(1, 10).mapToObj(n -> {
            User user = new User();
            user.setUsername("usecase/user" + n);
            user.setId(n);
            user.setEmail("email" + n + "@email.it");
            user.setAdmin(false);
            return user;
        }).collect(Collectors.toList());

        List<UserProfile> usersDto =
                users.stream().map(user -> UserProfile.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .creationDate(user.getCreationDate())
                        .description(user.getDescription())
                        .picture(user.getPicture())
                        .username(user.getUsername())
                        .isAdmin(user.getAdmin()).build())
                .collect(Collectors.toList());

        when(genericRepository.findAll(User.class)).thenReturn(users);
        assertEquals(service.showUsers(), usersDto);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 3, 40})
    void successfulDeleteById(int id){
        User user = new User();
        user.setId(1);
        when(genericRepository.findById(User.class,id)).thenReturn(user);
        assertDoesNotThrow(() -> service.delete(id));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 3, 40})
    void failDeleteWithWrongId(int id){
        when(genericRepository.findById(User.class,id)).thenReturn(null);
        assertThrows(ConstraintViolationException.class,() -> service.delete(id));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 3, 40})
    void successfulEditAsAdmin(int id) throws IOException {
        when(currentUser.isAdmin()).thenReturn(true);
        BufferedInputStream stream = new BufferedInputStream(new ByteArrayInputStream("GIF8".getBytes(StandardCharsets.UTF_8)));
        UserEditPage userEditPage = new UserEditPage("description","email@email.email",stream,"password");
        User user = new User();
        user.setId(1);
        when(genericRepository.findById(User.class,id)).thenReturn(user);
        when(mediaRepository.insert(any())).thenReturn("pictureName");
        assertDoesNotThrow(() -> service.edit(userEditPage,id));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 3, 40})
    void successfulEditAsSelf(int id) throws IOException {
        when(currentUser.getId()).thenReturn(id);

        BufferedInputStream stream = new BufferedInputStream(new ByteArrayInputStream("GIF8".getBytes(StandardCharsets.UTF_8)));
        UserEditPage userEditPage = new UserEditPage("description","email@email.email",stream,"password");
        User user = new User();
        user.setId(id);
        when(genericRepository.findById(User.class,id)).thenReturn(user);
        when(mediaRepository.insert(any())).thenReturn("pictureName");
        assertDoesNotThrow(() -> service.edit(userEditPage,id));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 3, 40})
    void failEditNotAuthorized(int id) throws IOException {
        when(currentUser.getId()).thenReturn(id);
        BufferedInputStream stream = new BufferedInputStream(new ByteArrayInputStream("GIF8".getBytes(StandardCharsets.UTF_8)));
        UserEditPage userEditPage = new UserEditPage("description","email@email.email",stream,"password");
        User user = new User();
        user.setId(id+1);
        when(genericRepository.findById(User.class,id+1)).thenReturn(user);
        when(mediaRepository.insert(any())).thenReturn("pictureName");
        assertThrows(AuthorizationException.class, () -> service.edit(userEditPage,id+1));
    }


    @ParameterizedTest
    @ValueSource(ints = {-1, -3, -40})
    void failEditWithWrongID(int id) throws IOException {
        BufferedInputStream stream = new BufferedInputStream(InputStream.nullInputStream());
        UserEditPage userEditPage = new UserEditPage("description","email",stream,"password");
        when(genericRepository.findById(User.class,id)).thenReturn(null);
        when(mediaRepository.insert(any())).thenReturn("pictureName");
        assertThrows(ConstraintViolationException.class,() -> service.edit(userEditPage,id));
    }


    @ParameterizedTest
    @CsvSource({"email2@gmail.com,username,password", "email45@gmail.com,username2,password2"})
    void successfulNewUser(String email, String username, String password) {
        User user = new User();
        user.setId(1);
        when(genericRepository.insert(any())).thenReturn(user);
        assertDoesNotThrow(() -> service.newUser(email,username,password));
    }

    @ParameterizedTest
    @CsvSource({"email2gmail,username,password", "email45gmail,username2,password2"})
    void failNewUserWrongEmail(String email, String username, String password) {
        User user = new User();
        user.setId(1);
        when(genericRepository.insert(any())).thenReturn(user);
        assertThrows(ConstraintViolationException.class,() -> service.newUser(email,username,password));
    }

    @Test
    void failNewUserBlankEmail() {
        User user = new User();
        user.setId(1);
        when(genericRepository.insert(any())).thenReturn(user);
        when(userRepository.getByEmail(any())).thenReturn(user);
        assertThrows(ConstraintViolationException.class, () -> {
            service.newUser("","username","mypassword123");
        });
    }

    @Test
    void failNewUserBlankUsername() {
        User user = new User();
        user.setId(1);
        doReturn(user).when(genericRepository).findByNaturalId(eq(User.class),any());
        when(genericRepository.insert(any())).thenReturn(user);
        assertThrows(ConstraintViolationException.class, () -> {
            service.newUser("email@email.email"," \n\t","mypassword123");
        });
    }

    @Test
    void failNewUserEmptyPassword() {
        User user = new User();
        user.setId(1);
        when(genericRepository.insert(any())).thenReturn(user);
        assertThrows(ConstraintViolationException.class,() -> {
            service.newUser("email@email.email","username","\t");
        });
    }
}