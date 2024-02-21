package dev.seeds.fileuploadanddownload;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;

@SpringBootTest
public class FileServiceTests {
    @Autowired
    private FileService fileService;

    @Test
    void mergeFileChunksTest() {
//        assertTrue(fileService.mergeFileChunks());
    }

    @Test
    void getFileSuffixTest() {
        assertEquals(".txt", FileUtils.getFileSuffix("test.txt"));
        assertEquals("", FileUtils.getFileSuffix("test"));
    }
}
