package com.example.stage24.shared;


import com.example.stage24.user.domain.User;
import org.springframework.core.io.InputStreamResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

public interface SharedServiceInterface {

    /*

    public String addFile(MultipartFile upload) throws IOException;

    public InputStreamResource downloadFile(String id) throws IOException;

    public GridFSFile getFileMetadata(String id);

     */

    public Optional<User> getConnectedUser();
}
