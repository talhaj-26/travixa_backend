package com.travixa.services.platform;

import com.travixa.exceptions.FileStorageException;
import com.travixa.exceptions.InvalidRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Service
public class FileStorageService {
    private static final long MAX_FILE_SIZE = 5L * 1024 * 1024;
    private static final Map<String, String> MIME_BY_EXTENSION = Map.of(
            "jpg", "image/jpeg", "jpeg", "image/jpeg", "png", "image/png", "webp", "image/webp");
    private final Path root;

    public FileStorageService(@Value("${travixa.media.storage-directory:uploads}") String directory) {
        this.root = Path.of(directory).toAbsolutePath().normalize();
    }

    public String store(MultipartFile file) {
        if (file == null || file.isEmpty() || file.getSize() > MAX_FILE_SIZE) {
            throw new InvalidRequestException("Image must be non-empty and no larger than 5 MB");
        }
        String extension = extension(file.getOriginalFilename());
        String expectedMime = MIME_BY_EXTENSION.get(extension);
        if (expectedMime == null || !expectedMime.equalsIgnoreCase(file.getContentType())) {
            throw new InvalidRequestException("Only JPEG, PNG, and WebP images are accepted");
        }
        try {
            byte[] bytes = file.getBytes();
            if (!matchesSignature(extension, bytes)) {
                throw new InvalidRequestException("Image content does not match its file type");
            }
            Files.createDirectories(root);
            String key = UUID.randomUUID() + "." + extension;
            Path destination = resolve(key);
            Files.write(destination, bytes, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
            return key;
        } catch (IOException ex) {
            throw new FileStorageException("Image could not be stored");
        }
    }

    public byte[] load(String key) {
        try {
            return Files.readAllBytes(resolve(key));
        } catch (IOException ex) {
            throw new FileStorageException("Stored image could not be read");
        }
    }

    public void delete(String key) {
        try {
            Files.deleteIfExists(resolve(key));
        } catch (IOException ex) {
            throw new FileStorageException("Stored image could not be deleted");
        }
    }

    public String contentType(String key) {
        String extension = extension(key);
        String mime = MIME_BY_EXTENSION.get(extension);
        if (mime == null) throw new FileStorageException("Unsupported stored image type");
        return mime;
    }

    private Path resolve(String key) {
        if (key == null || !key.matches("[0-9a-fA-F-]{36}\\.(jpg|jpeg|png|webp)")) {
            throw new FileStorageException("Invalid image key");
        }
        Path path = root.resolve(key).normalize();
        if (!path.startsWith(root)) throw new FileStorageException("Invalid image key");
        return path;
    }

    private String extension(String name) {
        if (name == null) return "";
        int dot = name.lastIndexOf('.');
        return dot < 0 ? "" : name.substring(dot + 1).toLowerCase(Locale.ROOT);
    }

    private boolean matchesSignature(String extension, byte[] bytes) {
        if (bytes.length < 12) return false;
        return switch (extension) {
            case "jpg", "jpeg" -> (bytes[0] & 0xff) == 0xff && (bytes[1] & 0xff) == 0xd8 && (bytes[2] & 0xff) == 0xff;
            case "png" -> (bytes[0] & 0xff) == 0x89 && bytes[1] == 0x50 && bytes[2] == 0x4e && bytes[3] == 0x47;
            case "webp" -> bytes[0] == 'R' && bytes[1] == 'I' && bytes[2] == 'F' && bytes[3] == 'F'
                    && bytes[8] == 'W' && bytes[9] == 'E' && bytes[10] == 'B' && bytes[11] == 'P';
            default -> false;
        };
    }
}
