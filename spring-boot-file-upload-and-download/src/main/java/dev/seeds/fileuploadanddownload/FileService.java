package dev.seeds.fileuploadanddownload;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;

@Slf4j
@Service
public class FileService {

    private final FileService fileService;

    @Lazy
    public FileService(FileService fileService) {
        this.fileService = fileService;
    }

    private static final String FILE_UPLOAD_PATH = "/Users/zhanglinfeng/Documents/Cache/file-upload-and-download";
    private static final String FILE_NAME = "video.mp4";

    @Transactional
    public Boolean upload(Chunk chunk) throws InterruptedException {
        File tempFolder = new File(FILE_UPLOAD_PATH + "/temp" + "/" + chunk.getMd5());
        if (chunk.getChunkNumber() == 0) {
            if (!tempFolder.exists()) {
                var result = tempFolder.mkdirs();
                if (!result) {
                    return false;
                }
            }
        }

        File tempFileChunk = new File(tempFolder, chunk.getChunkNumber().toString());
        try {
            chunk.getFileChunk().transferTo(tempFileChunk);
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }

        if (chunk.getChunkNumber() == chunk.getChunks() - 1) {
//            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
//                @Override
//                public void afterCommit() {
//                    fileService.mergeFileChunks(tempFolder.getAbsolutePath());
//                }
//            });
            fileService.mergeFileChunks(tempFolder.getAbsolutePath());
        }

        return true;
    }

    @Async
    protected void mergeFileChunks(String tempFolderPath) {
        File file = new File(tempFolderPath);

        if (!file.exists()) {
            log.error("Folder not found");
            return;
        }

        File[] files = file.listFiles();
        if (files == null || files.length == 0) {
            log.error("No files found");
            return;
        }

        String suffix = FileUtils.getFileSuffix(FILE_NAME);
        File mergedFile = new File(FILE_UPLOAD_PATH + "/" + "app" + suffix);

        try(RandomAccessFile raf = new RandomAccessFile(mergedFile, "rw")) {
            byte[] buffer = new byte[1024];
            int len = -1;
            // sort by chunk number
            for (int i = 0; i < files.length; i++) {
                File currentFile = new File(tempFolderPath + "/" + i);
                if (!currentFile.exists()) {
                    Thread.sleep(100);
                    log.info("Waiting for file to be uploaded");
                }
                try (FileInputStream fis = new FileInputStream(currentFile)) {
                    while ((len = fis.read(buffer)) != -1) {
                        raf.write(buffer, 0, len);
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public Boolean download(HttpServletRequest request, HttpServletResponse response) {
        int APP_MP4_SIZE = 120100568;  // 目标下载文件的大小

        String rangeHeader = request.getHeader("Range");
        // 分片下载
        if (rangeHeader == null) {
            return false;
        }

        int start, end;
        // TODO: Range 可以 bytes=-100 的形式传输
        String[] rangeArray = rangeHeader.replace("bytes=", "").split("-");
        if (rangeArray.length == 2) {
            start = Integer.parseInt(rangeArray[0]);
            end = Integer.parseInt(rangeArray[1]);
        } else {
            start = Integer.parseInt(rangeArray[0]);
            end = APP_MP4_SIZE - 1;
        }

        response.setHeader("Accept-Ranges", "bytes");
        response.setHeader("Content-Range", "bytes " + start + "-" + end + "/" + APP_MP4_SIZE);
        int contentLength = end - start + 1;
        response.setHeader("Content-Length", String.valueOf(contentLength));
        response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
        response.setContentType("video/mp4");
        response.setHeader("Content-Disposition", "attachment; filename=app.mp4");

        // 目标下载文件
        File file = new File("/Users/zhanglinfeng/Documents/app.mp4");
        if (!file.exists()) {
            log.error("File not found");
            return false;
        }

        try (
                RandomAccessFile raf = new RandomAccessFile(file, "r");
                ServletOutputStream sos = response.getOutputStream()
        ) {
            raf.skipBytes(start);
            byte[] buffer = new byte[1024];
            int len = -1;
            while ((len = raf.read(buffer)) != -1 && contentLength > 0) {
                if (contentLength >= len) {
                    sos.write(buffer, 0, len);
                    contentLength -= len;
                } else {
                    sos.write(buffer, 0, contentLength);
                    contentLength = 0;
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
        return true;
    }
}
