package pl.edu.pw.elka.tin.spy.server.application;

import lombok.extern.slf4j.Slf4j;
import pl.edu.pw.elka.tin.spy.server.domain.SpyRepository;
import pl.edu.pw.elka.tin.spy.server.domain.user.User;
import pl.edu.pw.elka.tin.spy.server.domain.user.UserStatus;
import pl.edu.pw.elka.tin.spy.server.infrastructure.H2SpyRepository;

import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class UsersObserver {

    private static volatile UsersObserver instance;
    private SpyRepository repository = H2SpyRepository.getInstance();
    private ConcurrentHashMap<Integer, User> activeUsers;

    private UsersObserver() {
        log.debug("Starting users observer");

        this.activeUsers = new ConcurrentHashMap<>();
    }

    public static UsersObserver observer() {
        if (instance == null) {
            synchronized (UsersObserver.class) {
                if (instance == null) {
                    instance = new UsersObserver();
                }
            }
        }
        return instance;
    }

    public User registerUser(String name, String password) {
        return repository.addUser(name, password);
    }

    public User authenticateUser(int userID, String password) {
        User user = repository.authenticateUser(userID, password);
        if (user != null) {
            repository.updateUserStatus(user, UserStatus.ACTIVE);
            activeUsers.put(user.getID(), user);
            return user;
        } else {
            throw new IllegalArgumentException("Authentication for user" + userID + "failed ");
        }
    }

    public void logOutUser(User user) {
        repository.updateUserStatus(user, UserStatus.LOGOUT);
        activeUsers.put(user.getID(), null);
    }

}