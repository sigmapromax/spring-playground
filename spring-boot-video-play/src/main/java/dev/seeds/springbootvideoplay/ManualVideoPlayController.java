package dev.seeds.springbootvideoplay;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.RandomAccessFile;

@CrossOrigin("*")
@Slf4j
@RestController
public class ManualVideoPlayController {

    @GetMapping(value = "video-stream/{filename}")
    public ResponseEntity<String> getVideo(HttpServletRequest request, HttpServletResponse response, @PathVariable String filename) {
        log.info("Requesting video stream");
        File videoFile = new File("/Users/zhanglinfeng/Documents/Cache/video-play/" + filename);

        try (
                RandomAccessFile raf = new RandomAccessFile(videoFile, "r");
                ServletOutputStream out = response.getOutputStream()
        ) {
            long rangeStart = 0;
            long rangeEnd = videoFile.length() - 1;
            String range = request.getHeader("Range");
            if (range != null) {
                if (range.startsWith("bytes=")) {
                    range = range.substring(6);
                    int minus = range.indexOf('-');
                    try {
                        if (minus > 0) {
                            rangeStart = Long.parseLong(range.substring(0, minus));
                            rangeEnd = Long.parseLong(range.substring(minus + 1));
                        }
                    } catch (NumberFormatException ignored) {
                        log.error("Error parsing range header: {}", range);
                    }
                }
            }
            long length = rangeEnd - rangeStart + 1;
            response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
            response.setHeader("Accept-Ranges", "bytes");
            response.setHeader("Content-Type", "video/mp4");
            response.setHeader("Content-Length", String.valueOf(length));
            response.setHeader("Content-Range", "bytes " + rangeStart + "-" + rangeEnd + "/" + videoFile.length());
            raf.seek(rangeStart);
            byte[] buffer = new byte[1024];
            int len;
            while (length > 0) {
                len = raf.read(buffer, 0, (int) Math.min(1024, length));
                out.write(buffer, 0, len);
                length -= len;
            }
        } catch (Exception e) {
            log.error("Error writing video to output stream. Error: {}", e.getMessage());
        }
        return ResponseEntity.status(HttpServletResponse.SC_PARTIAL_CONTENT).build();
    }
}
