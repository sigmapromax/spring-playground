package dev.seeds.springbootvideoplay;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.nio.charset.StandardCharsets;

@CrossOrigin("*")
@Slf4j
@RestController()
public class OfficialVideoPlayController {

    private final NonStaticResourceHttpRequestHandler nonStaticResourceHttpRequestHandler;

    public OfficialVideoPlayController(NonStaticResourceHttpRequestHandler nonStaticResourceHttpRequestHandler) {
        this.nonStaticResourceHttpRequestHandler = nonStaticResourceHttpRequestHandler;
    }

    @GetMapping(value = "/video/{filename}")
    public void video(
            HttpServletRequest request,
            HttpServletResponse response,
            @PathVariable String filename
    ) {
        try {
            String path = "/Users/zhanglinfeng/Documents/Cache/video-play/" + filename;
            File file = new File(path);
            if (file.exists()) {
                request.setAttribute(NonStaticResourceHttpRequestHandler.ATTR_FILE, path);
                nonStaticResourceHttpRequestHandler.handleRequest(request, response);
                ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).build();
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
            }
        } catch (Exception e) {
            log.error("BaseSourceApiController: " + e.getMessage());
        }
    }
}
