package com.example.online_shop.validator;

import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

public class FileValidator {
    public static boolean isImageFile(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        if (fileName != null) {
            String extension = StringUtils.getFilenameExtension(fileName);
            return extension != null && (
                extension.equalsIgnoreCase("jpg") ||
                extension.equalsIgnoreCase("jpeg") ||
                extension.equalsIgnoreCase("png")
            );
        }
        return false;
    }
}
