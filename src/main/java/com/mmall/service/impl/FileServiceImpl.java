package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.mmall.service.IFileService;
import com.mmall.util.FTPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * 功能描述: 文件服务接口
 *
 * @auther: Lee
 * @date: 2018/9/16 17:16
 */
@Service("iFileService")
public class FileServiceImpl implements IFileService {
    private Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);


    @Override
    public String upload(MultipartFile file, String path) {
        /**
         * 功能描述: 上传文件到path下,转存到ftp服务器，再删除本机的该文件
         *
         * @param: [file, path]
         * @return: java.lang.String
         * @auther: Lee
         * @date: 2018/9/16 17:17
         */
        String fileName = file.getOriginalFilename();
        //扩展名
        String fileExtensionName = fileName.substring(fileName.lastIndexOf(".") + 1);
        //防止文件重名
        String uploadFileName = UUID.randomUUID().toString() + "." + fileExtensionName;
        logger.info("开始上传文件,上传文件的文件名:{},上传的路径:{},新文件名:{}", fileName, path, uploadFileName);

        File fileDir = new File(path);
        if (!fileDir.exists()) {
            fileDir.setWritable(true);
            fileDir.mkdirs();
        }
        File targetFile = new File(path, uploadFileName);


        try {
            //将文件保存在本地
            file.transferTo(targetFile);
            //将文件上传给FTP服务器
            FTPUtil.uploadFile(Lists.newArrayList(targetFile));
            //把本地文件删除
            targetFile.delete();
        } catch (IOException e) {
            logger.error("上传文件异常", e);
            return null;
        }
        return targetFile.getName();
    }
}
