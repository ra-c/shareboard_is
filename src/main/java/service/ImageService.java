package service;

import persistence.repo.BinaryContentRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.constraints.NotBlank;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;

@ApplicationScoped
public class ImageService {
    @Inject private BinaryContentRepository binaryContentRepository;

    public InputStream getImage(@NotBlank String filename) throws IOException {
        InputStream inputStream = binaryContentRepository.get(filename);
        if(inputStream == null)
            return null;

        inputStream = new BufferedInputStream(inputStream);
        String mimetype = URLConnection.guessContentTypeFromStream(inputStream);
        if(mimetype == null || !mimetype.startsWith("image/"))
            return  null;

        return inputStream;
    }
}