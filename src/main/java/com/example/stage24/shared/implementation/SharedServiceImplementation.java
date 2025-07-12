package com.example.stage24.shared.implementation;




import com.example.stage24.shared.SharedServiceInterface;
import com.example.stage24.user.domain.User;
import com.example.stage24.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import java.io.IOException;
import java.util.Optional;


@Service
@AllArgsConstructor
public class SharedServiceImplementation implements SharedServiceInterface {

    private UserRepository userRepository;

    /*
    private GridFsTemplate template;

    private GridFsOperations operations;

    @Override
    public String addFile(MultipartFile upload) throws IOException {

        DBObject metadata = new BasicDBObject();
        metadata.put("fileSize", upload.getSize());
        metadata.put("contentType", upload.getContentType());

        ObjectId fileID = template.store(upload.getInputStream(), upload.getOriginalFilename(), upload.getContentType(), metadata);

        return fileID.toString();
    }

    @Override
    public InputStreamResource downloadFile(String agentId) throws IOException {
        User user  = userRepository.findById(agentId).orElseThrow(() -> new RuntimeException("User not found with id: " + agentId));
        GridFSFile gridFsFile = template.findOne(new org.springframework.data.mongodb.core.query.Query().addCriteria(org.springframework.data.mongodb.core.query.Criteria.where("_id").is(user.getImage())));

        if (gridFsFile == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found");
        }

        return new InputStreamResource(template.getResource(gridFsFile).getInputStream());
    }

    @Override
    public GridFSFile getFileMetadata(String id) {
        GridFSFile gridFsFile = template.findOne(new org.springframework.data.mongodb.core.query.Query().addCriteria(org.springframework.data.mongodb.core.query.Criteria.where("_id").is(id)));

        if (gridFsFile == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found");
        }

        return gridFsFile;
    }

*/

    @Override
    public Optional<User> getConnectedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated() || 
            "anonymousUser".equals(authentication.getPrincipal())) {
            return Optional.empty();
        }
        
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            String currentUsername = ((UserDetails) principal).getUsername();
            return userRepository.findByEmail(currentUsername);
        } else if (principal instanceof String) {
            // Handle case where principal is a username string
            return userRepository.findByEmail((String) principal);
        }
        
        return Optional.empty();
    }
}

