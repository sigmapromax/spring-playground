package dev.seeds.fileuploadanddownload;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> upload(@ModelAttribute Chunk chunk) throws InterruptedException {
        log.info("Chunk Number: {}", chunk.getChunkNumber());
        Boolean result = fileService.upload(chunk);
        if (result) {
            return ResponseEntity.ok("success");
        } else {
            return ResponseEntity.status(500).body("fail");
        }
    }

    @GetMapping("/download")
    public ResponseEntity<String> download(HttpServletRequest request, HttpServletResponse response) {
        log.info(request.getHeader("Range"));
        Boolean result = fileService.download(request, response);
        if (result) {
            return ResponseEntity.ok().body("success");
        } else {
            return ResponseEntity.status(500).body("fail");
        }
    }
}
