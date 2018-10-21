package com.agh.bsct.view.config;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Objects;

import static com.agh.bsct.view.config.PathsConstants.RESOURCES_API_KEY_ROOT_PATH;


public class ApiKeyProvider {

    private static final String KEY_FILE_NOT_FOUND_MESSAGE = "missing-key-file";

    public static String getApiKey() {
        var file = getFileWithApiKey();
        try {
            return new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            e.printStackTrace();
            return KEY_FILE_NOT_FOUND_MESSAGE;
        }
    }

    private static File getFileWithApiKey() {
        URL apiKeyResource = ApiKeyProvider.class.getClassLoader().getResource(RESOURCES_API_KEY_ROOT_PATH);
        return new File(Objects.requireNonNull(apiKeyResource).getFile());
    }

}
