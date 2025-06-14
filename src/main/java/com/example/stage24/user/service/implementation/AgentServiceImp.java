package com.example.stage24.user.service.implementation;




import com.example.stage24.user.domain.*;
import com.example.stage24.user.model.request.NewAgentRequest;
import com.example.stage24.user.repository.AccessRepository;
import com.example.stage24.user.repository.RoleRepository;
import com.example.stage24.user.repository.UserRepository;
import com.example.stage24.user.service.interfaces.AgentServiceInterface;
import com.example.stage24.user.service.interfaces.IUserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
@AllArgsConstructor
public class AgentServiceImp implements AgentServiceInterface {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AccessRepository accessRepository;
    private final PasswordEncoder encoder;
    private final IUserService userService;

    @Override
    public User registerAgent(NewAgentRequest newAgentRequest) {
        if (userRepository.existsByEmail(newAgentRequest.getEmail())) {
            throw new RuntimeException("Error: Email already exist.");
        }

        List<String> strAccesses = newAgentRequest.getAccesses();
        List<Access> accesses = new ArrayList<>();


        strAccesses.forEach(access -> {
            Access _access = accessRepository.findByType(AccessType.valueOf(access))
                    .orElseThrow(() -> new RuntimeException("Error: Access is not found."));

            accesses.add(_access);

        });

        List<Role> roles = new LinkedList();

        Role agentRole = roleRepository.findByName(RoleType.ROLE_AGENT)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        roles.add(agentRole);


        User user = new User(
                newAgentRequest.getFirstName(),
                newAgentRequest.getLastName(),
                newAgentRequest.getEmail(),
                encoder.encode(newAgentRequest.getPassword()),
                newAgentRequest.getPhoneNumber(),
                newAgentRequest.getAddress(),
                newAgentRequest.getImage());

        user.setRoles(roles);
        user.setAccesses(accesses);

        Optional<User> connectedUser = userService.getConnectedUser();
        connectedUser.ifPresent(user::setCreatedBy);

        return userRepository.save(user);

    }


    @Override
    public User getAgentById(long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public List<User> getAgents() {
        Role role = roleRepository.findByName(RoleType.ROLE_AGENT)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));

        return userRepository.findAllByRolesContaining(role);
    }

    @Override
    public User deleteAgent(long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("no such user with this id"));
        userRepository.delete(user);
        return user;
    }

    @Override
    public User updateAgent(long id, NewAgentRequest newAgentRequest) {

        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("user to updated not found"));

        List<String> strAccesses = newAgentRequest.getAccesses();
        List<Access> accesses = new ArrayList<>();

        strAccesses.forEach(access -> {
            Access _access = accessRepository.findByType(AccessType.valueOf(access))
                    .orElseThrow(() -> new RuntimeException("Error: Access is not found."));
            accesses.add(_access);
        });

        List<Role> roles = new LinkedList();

        Role agentRole = roleRepository.findByName(RoleType.ROLE_AGENT)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));

        roles.add(agentRole);

        user.setFirstName(newAgentRequest.getFirstName());
        user.setLastName(newAgentRequest.getLastName());
        user.setEmail(newAgentRequest.getEmail());

        if (newAgentRequest.getPassword() != null)
            user.setPassword(encoder.encode(newAgentRequest.getPassword()));

        user.setPhoneNumber(newAgentRequest.getPhoneNumber());
        user.setAddress(newAgentRequest.getAddress());

        if (newAgentRequest.getImage() != null)
            user.setImage(newAgentRequest.getImage());

        user.setRoles(roles);
        user.setAccesses(accesses);

        Optional<User> connectedUser = userService.getConnectedUser();
        connectedUser.ifPresent(user::setCreatedBy);

        return userRepository.save(user);
    }

    @Override
    public List<String> getAccesses(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("user not found"));
        List<Access> accesses = user.getAccesses() != null ? user.getAccesses() : new ArrayList<>();
        List<String> strAccesses = new ArrayList<>();
        accesses.forEach(access -> {
            strAccesses.add(access.getType().toString());
        });
        return strAccesses;
    }

}
