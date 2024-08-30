package com.bekrenovr.ecommerce.keycloakserver.providers.userstorage;

import com.bekrenovr.ecommerce.common.exception.EcommerceApplicationException;
import com.bekrenovr.ecommerce.common.security.Role;
import com.bekrenovr.ecommerce.keycloakserver.dao.EcommerceUserDao;
import com.bekrenovr.ecommerce.keycloakserver.model.EcommerceUser;
import com.bekrenovr.ecommerce.keycloakserver.model.EcommerceUserAdapter;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.models.GroupModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.credential.PasswordCredentialModel;
import org.keycloak.models.utils.SHAPasswordEncoder;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.user.UserLookupProvider;
import org.keycloak.storage.user.UserQueryProvider;

import java.util.Map;
import java.util.stream.Stream;

import static com.bekrenovr.ecommerce.keycloakserver.exception.KeycloakApplicationExceptionReason.USER_ALREADY_EXISTS;
import static com.bekrenovr.ecommerce.keycloakserver.exception.KeycloakApplicationExceptionReason.USER_NOT_FOUND;

@Slf4j
public class EcommerceUserStorageProvider implements
        UserStorageProvider, UserLookupProvider, UserQueryProvider, CredentialInputValidator {

    private KeycloakSession keycloakSession;
    private ComponentModel componentModel;
    private SHAPasswordEncoder passwordEncoder;
    private EcommerceUserDao userDao;

    public EcommerceUserStorageProvider(KeycloakSession keycloakSession, ComponentModel componentModel, SHAPasswordEncoder passwordEncoder) {
        this.keycloakSession = keycloakSession;
        this.componentModel = componentModel;
        this.passwordEncoder = passwordEncoder;
        this.userDao = new EcommerceUserDao(componentModel);
    }

    @Override
    public void close() {
        log.info("close()");
    }

    @Override
    public boolean supportsCredentialType(String credentialType) {
        log.info("supportsCredentialType()");
        return PasswordCredentialModel.TYPE.endsWith(credentialType);
    }

    @Override
    public boolean isConfiguredFor(RealmModel realmModel, UserModel userModel, String credentialType) {
        log.info("isConfiguredFor()");
        return supportsCredentialType(credentialType);
    }

    @Override
    public boolean isValid(RealmModel realmModel, UserModel userModel, CredentialInput credentialInput) {
        log.info("isValid()");
        String encryptedPassword = userDao.getPasswordByUsername(userModel.getUsername());
        if (encryptedPassword == null)
            return false;
        return passwordEncoder.verify(credentialInput.getChallengeResponse(), encryptedPassword);
    }

    @Override
    public UserModel getUserById(RealmModel realmModel, String id) {
        log.info("getUserById({})", id);
        StorageId storageId = new StorageId(id);
        return getUserByUsername(realmModel, storageId.getExternalId());
    }

    @Override
    public UserModel getUserByUsername(RealmModel realmModel, String username) {
        log.info("getUserByUsername({})", username);
        EcommerceUser user = userDao.getUserByUsername(username);
        return user != null ? mapUser(realmModel, user) : null;
    }

    @Override
    public UserModel getUserByEmail(RealmModel realmModel, String email) {
        log.info("getUserByEmail({})", email);
        return getUserByUsername(realmModel, email);
    }

    @Override
    public Stream<UserModel> searchForUserStream(RealmModel realmModel, Map<String, String> map, Integer integer, Integer integer1) {
        return userDao.getAllUsers()
                .stream()
                .map(user -> mapUser(realmModel, user));
    }

    @Override
    public Stream<UserModel> getGroupMembersStream(RealmModel realmModel, GroupModel groupModel, Integer integer, Integer integer1) {
        return null;
    }

    @Override
    public Stream<UserModel> searchForUserByUserAttributeStream(RealmModel realmModel, String s, String s1) {
        return null;
    }

    public void addUser(String username, String rawPassword, Role role) {
        if(userDao.existsByUsername(username))
            throw new EcommerceApplicationException(USER_ALREADY_EXISTS, username);
        String encodedPassword = passwordEncoder.encode(rawPassword);
        userDao.addUser(username, encodedPassword, role);
    }

    public UserModel enableUser(RealmModel realmModel, String username) {
        if(!userDao.existsByUsername(username))
            throw new EcommerceApplicationException(USER_NOT_FOUND, username);
        userDao.enableUser(username);
        EcommerceUser enabledUser = userDao.getUserByUsername(username);
        return mapUser(realmModel, enabledUser);
    }

    private UserModel mapUser(RealmModel realm, EcommerceUser user) {
        return new EcommerceUserAdapter(keycloakSession, realm, componentModel, user);
    }
}