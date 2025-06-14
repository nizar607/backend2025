package com.example.stage24.user.service.interfaces;



import com.example.stage24.user.domain.User;
import com.example.stage24.user.model.request.NewAgentRequest;

import java.util.List;

public interface AgentServiceInterface {
    public User registerAgent(NewAgentRequest agent);
    public User getAgentById(long id);
    public List<User> getAgents();
    public User deleteAgent(long id);
    public User updateAgent(long id,NewAgentRequest newAgentRequest);
    public List<String> getAccesses(String email);
}

