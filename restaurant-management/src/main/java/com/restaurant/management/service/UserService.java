package com.restaurant.management.service;

import com.restaurant.management.domain.User;
import com.restaurant.management.dto.LoginRequest;
import com.restaurant.management.dto.LoginResponse;
import com.restaurant.management.dto.PasswordChangeRequest;
import com.restaurant.management.dto.UserCreateRequest;
import com.restaurant.management.dto.UserResponse;
import com.restaurant.management.dto.UserType;
import com.restaurant.management.dto.UserUpdateRequest;
import com.restaurant.management.exception.AmbiguousUserLookupException;
import com.restaurant.management.exception.EmailAlreadyInUseException;
import com.restaurant.management.exception.InvalidUserLookupException;
import com.restaurant.management.exception.InvalidCredentialsException;
import com.restaurant.management.exception.InvalidCurrentPasswordException;
import com.restaurant.management.exception.LoginAlreadyInUseException;
import com.restaurant.management.exception.UserNotFoundException;
import com.restaurant.management.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponse create(UserCreateRequest request) {
        String email = request.email().trim();
        String login = request.login().trim();
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new EmailAlreadyInUseException(email);
        }
        if (userRepository.existsByLoginIgnoreCase(login)) {
            throw new LoginAlreadyInUseException(login);
        }
        String hash = passwordEncoder.encode(request.password());
        User user = UserMapper.createEntity(request, hash);
        user = userRepository.save(user);
        return UserMapper.toResponse(user);
    }

    @Transactional(readOnly = true)
    public UserResponse findById(Long id) {
        return UserMapper.toResponse(getUser(id));
    }

    /**
     * Localiza um usuário por exatamente um critério: id, e-mail, nome exato ou trecho do nome (nameContains).
     */
    @Transactional(readOnly = true)
    public UserResponse findByLookup(Long id, String email, String name, String nameContains) {
        Long resolvedId = resolveUserLookupId(id, email, name, nameContains);
        return findById(resolvedId);
    }

    /**
     * Lista usuários. Sem filtros, retorna todos. Use no máximo um entre:
     * {@code id}, {@code name} (trecho no nome), {@code email} (trecho no e-mail), {@code search} (trecho no nome **ou** no e-mail).
     */
    @Transactional(readOnly = true)
    public List<UserResponse> searchUsers(Long id, String name, String email, String search) {
        int n = (id != null ? 1 : 0) + (isNotBlank(name) ? 1 : 0) + (isNotBlank(email) ? 1 : 0) + (isNotBlank(search) ? 1 : 0);
        if (n > 1) {
            throw new InvalidUserLookupException(
                    "Use no máximo um filtro por vez: id, name, email ou search (barra geral nome/e-mail).");
        }
        if (id != null) {
            return userRepository.findById(id).stream().map(UserMapper::toResponse).toList();
        }
        if (isNotBlank(search)) {
            return userRepository.searchByTextInNameOrEmail(search.trim()).stream()
                    .map(UserMapper::toResponse)
                    .toList();
        }
        if (isNotBlank(email)) {
            return userRepository.findByEmailContainingIgnoreCase(email.trim()).stream()
                    .map(UserMapper::toResponse)
                    .toList();
        }
        if (isNotBlank(name)) {
            return userRepository.findByNameContainingIgnoreCase(name.trim()).stream()
                    .map(UserMapper::toResponse)
                    .toList();
        }
        return userRepository.findAll().stream().map(UserMapper::toResponse).toList();
    }

    @Transactional
    public UserResponse updateProfile(Long id, UserUpdateRequest request) {
        User user = getUser(id);
        String email = request.email().trim();
        String login = request.login().trim();
        if (userRepository.existsByEmailIgnoreCaseAndIdNot(email, id)) {
            throw new EmailAlreadyInUseException(email);
        }
        if (userRepository.existsByLoginIgnoreCaseAndIdNot(login, id)) {
            throw new LoginAlreadyInUseException(login);
        }
        UserMapper.applyUpdate(user, request);
        user = userRepository.save(user);
        return UserMapper.toResponse(user);
    }

    /**
     * Atualiza perfil localizando o usuário por exatamente um critério: id, e-mail ou nome (nome = igualdade exata,
     * ignorando maiúsculas; se houver mais de um com o mesmo nome, retorna erro).
     */
    @Transactional
    public UserResponse updateProfileByLookup(Long id, String email, String name, String nameContains, UserUpdateRequest request) {
        Long resolvedId = resolveUserLookupId(id, email, name, nameContains);
        return updateProfile(resolvedId, request);
    }

    /**
     * Exatamente um entre: id, email, name (igualdade exata, ignorando maiúsculas), nameContains (substring no nome, ignora maiúsculas).
     */
    private Long resolveUserLookupId(Long id, String email, String name, String nameContains) {
        int n = (id != null ? 1 : 0)
                + (isNotBlank(email) ? 1 : 0)
                + (isNotBlank(name) ? 1 : 0)
                + (isNotBlank(nameContains) ? 1 : 0);
        if (n != 1) {
            throw new InvalidUserLookupException();
        }
        if (id != null) {
            return id;
        }
        if (isNotBlank(email)) {
            return userRepository.findByEmailIgnoreCase(email.trim())
                    .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado com o e-mail informado."))
                    .getId();
        }
        if (isNotBlank(name)) {
            List<User> matches = userRepository.findAllByNameIgnoreCase(name.trim());
            if (matches.isEmpty()) {
                throw new UserNotFoundException("Usuário não encontrado com o nome exato informado.");
            }
            if (matches.size() > 1) {
                throw new AmbiguousUserLookupException(matches.size());
            }
            return matches.get(0).getId();
        }
        List<User> partial = userRepository.findByNameContainingIgnoreCase(nameContains.trim());
        if (partial.isEmpty()) {
            throw new UserNotFoundException("Nenhum usuário encontrado cujo nome contenha o texto informado.");
        }
        if (partial.size() > 1) {
            throw new AmbiguousUserLookupException(
                    "Existem " + partial.size() + " usuários cujo nome contém o texto informado; refine a busca ou use e-mail ou id.");
        }
        return partial.get(0).getId();
    }

    private static boolean isNotBlank(String s) {
        return s != null && !s.isBlank();
    }

    @Transactional
    public UserResponse changePassword(Long id, PasswordChangeRequest request) {
        User user = getUser(id);
        if (!passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
            throw new InvalidCurrentPasswordException();
        }
        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        user.touchModified();
        user = userRepository.save(user);
        return UserMapper.toResponse(user);
    }

    /**
     * Localiza o usuário (id, e-mail ou nome) e troca a senha.
     */
    @Transactional
    public UserResponse changePasswordByLookup(Long id, String email, String name, String nameContains, PasswordChangeRequest request) {
        Long resolvedId = resolveUserLookupId(id, email, name, nameContains);
        return changePassword(resolvedId, request);
    }

    @Transactional
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
    }

    /**
     * Localiza o usuário (id, e-mail ou nome) e exclui.
     */
    @Transactional
    public void deleteByLookup(Long id, String email, String name, String nameContains) {
        Long resolvedId = resolveUserLookupId(id, email, name, nameContains);
        delete(resolvedId);
    }

    @Transactional(readOnly = true)
    public LoginResponse validateLogin(LoginRequest request) {
        User user = userRepository.findByLoginIgnoreCase(request.login().trim())
                .orElseThrow(InvalidCredentialsException::new);
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }
        UserType type = UserMapper.userType(user);
        return new LoginResponse(true, user.getId(), type, "Login válido");
    }

    private User getUser(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }
}
