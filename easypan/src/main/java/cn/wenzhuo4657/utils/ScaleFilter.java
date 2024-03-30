package cn.wenzhuo4657.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.math.BigDecimal;

/**
 * @author: 35238
 * 功能: 生成文件缩略图
 * 时间: 2023-12-10 21:30
 */
@Slf4j
public class ScaleFilter {
    /**
     * 生成视频的缩略图，需要有ffmpeg的本地环境
     * @param sourceFile 需要进行缩略的视频路径
     * @param width 形成封面的宽度
     * @param targetFile 生成的封面路径
     */
    public static void createCover4Video(File sourceFile, Integer width, File targetFile) {
        try {
            //提取输入视频文件的第一帧，并将其按指定比例进行缩放
            String cmd = "ffmpeg -i %s -y -vframes 1 -vf scale=%d:%d/a %s";
            ProcessUtils.executeCommand(String.format(cmd, sourceFile.getAbsoluteFile(), width, width, targetFile.getAbsoluteFile()), false);
        } catch (Exception e) {
            log.error("生成视频封面失败", e);
        }
    }

    /**
     *  生成图片的缩略图
     * @param file 文件
     * @param thumbnailWidth
     * @param targetFile
     * @param delSource
     * @return
     */
    public static Boolean createThumbnailWidthFFmpeg(File file, int thumbnailWidth, File targetFile, Boolean delSource) {
        try {
            BufferedImage src = ImageIO.read(file);
            //thumbnailWidth 缩略图的宽度   thumbnailHeight 缩略图的高度
            int sorceW = src.getWidth();
            int sorceH = src.getHeight();
            //小于 指定高宽不压缩
            if (sorceW <= thumbnailWidth) {
                return false;
            }
            compressImage(file, thumbnailWidth, targetFile, delSource);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void compressImageWidthPercentage(File sourceFile, BigDecimal widthPercentage, File targetFile) {
        try {
            BigDecimal widthResult = widthPercentage.multiply(new BigDecimal(ImageIO.read(sourceFile).getWidth()));
            compressImage(sourceFile, widthResult.intValue(), targetFile, true);
        } catch (Exception e) {
            log.error("压缩图片失败");
        }
    }
    public static void compressImage(File sourceFile, Integer width, File targetFile, Boolean delSource) {
        try {
            String cmd = "ffmpeg -i %s -vf scale=%d:-1 %s -y";
            ProcessUtils.executeCommand(String.format(cmd, sourceFile.getAbsoluteFile(), width, targetFile.getAbsoluteFile()), false);
            if (delSource) {
                FileUtils.forceDelete(sourceFile);
            }
        } catch (Exception e) {
            log.error("压缩图片失败");
        }
    }
}