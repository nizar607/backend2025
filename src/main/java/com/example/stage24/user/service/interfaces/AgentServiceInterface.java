package com.example.stage24.user.service.interfaces;



import com.example.stage24.user.domain.User;
import com.example.stage24.user.model.request.NewAgentRequest;

import java.util.List;

public interface AgentServiceInterface {
    public User registerAgent(NewAgentRequest agent);
    public User getAgentById(Long id);
    public List<User> getAgents();
    public User deleteAgent(Long id);
    public User updateAgent(Long id,NewAgentRequest newAgentRequest);
}
