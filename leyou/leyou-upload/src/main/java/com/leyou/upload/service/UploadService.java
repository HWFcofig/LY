package com.leyou.upload.service;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * @author admin
 * @ClassName UploadService
 * @date 2020/4/4
 * @Version 1.0
 **/
@Service
public class UploadService {

    @Autowired
    private FastFileStorageClient storageClient;

    //设置白名单
    private static final List<String> CONTENT_TYPES =Arrays.asList("image/jpeg", "image/gif");
    //输出日志
    private static final Logger LOGGER = LoggerFactory.getLogger(UploadService.class);

    /**
     * 图片上传
     * @param file
     * @return
     */
    public String upload(MultipartFile file) {

        String originalFilename = file.getOriginalFilename(); //获取到原始的全文件名称
        //处理当前年月日格式
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
        String dateNowStr = sdf.format(d);
        // 校验文件的类型
        String contentType = file.getContentType();
        if (!CONTENT_TYPES.contains(contentType)){//白名单内没有该文件类型
            // 文件类型不合法，直接返回null
            LOGGER.info("文件类型不合法：{}",originalFilename);
            return null;
        }
        try {
        // 校验文件的内容
        BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
        if (bufferedImage == null){//文件内容为空
            LOGGER.info("文件内容不合法：{}",originalFilename);
            return null;
        }
        // 保存到服务器
        String ext = StringUtils.substringAfterLast(originalFilename, ".");
        StorePath storePath = this.storageClient.uploadFile(file.getInputStream(), file.getSize(), ext, null);
        // 生成url地址，返回
        return "http://image.leyou.com/" + storePath.getFullPath();
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.info("服务器内部出错：{}",originalFilename);
        }
        return null;
    }
}
