package dev.seeds.fileuploadanddownload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Chunk {
    private String fileName;
    private Integer chunks;
    private Integer chunkNumber;
    private String md5;
    private MultipartFile fileChunk;
}
