package com.hqy.cloud.util;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import com.hqy.cloud.common.base.lang.StringConstants;
import com.hqy.cloud.util.config.ConfigurationContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/9/21 13:47
 */
@Slf4j
public class ImageUtil {
    public static final int MAX_FILE_SIZE = 9;
    public static final String DOWNLOAD_TMP_FOLDER = "/files/temp";

    public static String[] getXy(int size) {
        String[] s = new String[size];
        int _x;
        int _y;
        if (size == 1) {
            _x = _y = 6;
            s[0] = "6,6";
        }
        if (size == 2) {
            _x = _y = 4;
            s[0] = "4," + (132 / 2 - 60 / 2);
            s[1] = 60 + 2 * _x + "," + (132 / 2 - 60 / 2);
        }
        if (size == 3) {
            _x = _y = 4;
            s[0] = (132 / 2 - 60 / 2) + "," + _y;
            s[1] = _x + "," + (60 + 2 * _y);
            s[2] = (60 + 2 * _y) + "," + (60 + 2 * _y);
        }
        if (size == 4) {
            _x = _y = 4;
            s[0] = _x + "," + _y;
            s[1] = (_x * 2 + 60) + "," + _y;
            s[2] = _x + "," + (60 + 2 * _y);
            s[3] = (60 + 2 * _y) + "," + (60 + 2 * _y);
        }
        if (size == 5) {
            _x = _y = 3;
            s[0] = (132 - 40 * 2 - _x) / 2 + "," + (132 - 40 * 2 - _y) / 2;
            s[1] = ((132 - 40 * 2 - _x) / 2 + 40 + _x) + "," + (132 - 40 * 2 - _y) / 2;
            s[2] = _x + "," + ((132 - 40 * 2 - _x) / 2 + 40 + _y);
            s[3] = (_x * 2 + 40) + "," + ((132 - 40 * 2 - _x) / 2 + 40 + _y);
            s[4] = (_x * 3 + 40 * 2) + "," + ((132 - 40 * 2 - _x) / 2 + 40 + _y);
        }
        if (size == 6) {
            _x = _y = 3;
            s[0] = _x + "," + ((132 - 40 * 2 - _x) / 2);
            s[1] = (_x * 2 + 40) + "," + ((132 - 40 * 2 - _x) / 2);
            s[2] = (_x * 3 + 40 * 2) + "," + ((132 - 40 * 2 - _x) / 2);
            s[3] = _x + "," + ((132 - 40 * 2 - _x) / 2 + 40 + _y);
            s[4] = (_x * 2 + 40) + "," + ((132 - 40 * 2 - _x) / 2 + 40 + _y);
            s[5] = (_x * 3 + 40 * 2) + "," + ((132 - 40 * 2 - _x) / 2 + 40 + _y);
        }
        if (size == 7) {
            _x = _y = 3;
            s[0] = (132 - 40) / 2 + "," + _y;
            s[1] = _x + "," + (_y * 2 + 40);
            s[2] = (_x * 2 + 40) + "," + (_y * 2 + 40);
            s[3] = (_x * 3 + 40 * 2) + "," + (_y * 2 + 40);
            s[4] = _x + "," + (_y * 3 + 40 * 2);
            s[5] = (_x * 2 + 40) + "," + (_y * 3 + 40 * 2);
            s[6] = (_x * 3 + 40 * 2) + "," + (_y * 3 + 40 * 2);
        }
        if (size == 8) {
            _x = _y = 3;
            s[0] = (132 - 80 - _x) / 2 + "," + _y;
            s[1] = ((132 - 80 - _x) / 2 + _x + 40) + "," + _y;
            s[2] = _x + "," + (_y * 2 + 40);
            s[3] = (_x * 2 + 40) + "," + (_y * 2 + 40);
            s[4] = (_x * 3 + 40 * 2) + "," + (_y * 2 + 40);
            s[5] = _x + "," + (_y * 3 + 40 * 2);
            s[6] = (_x * 2 + 40) + "," + (_y * 3 + 40 * 2);
            s[7] = (_x * 3 + 40 * 2) + "," + (_y * 3 + 40 * 2);
        }
        if (size == 9) {
            _x = _y = 3;
            s[0] = _x + "," + _y;
            s[1] = _x * 2 + 40 + "," + _y;
            s[2] = _x * 3 + 40 * 2 + "," + _y;
            s[3] = _x + "," + (_y * 2 + 40);
            s[4] = (_x * 2 + 40) + "," + (_y * 2 + 40);
            s[5] = (_x * 3 + 40 * 2) + "," + (_y * 2 + 40);
            s[6] = _x + "," + (_y * 3 + 40 * 2);
            s[7] = (_x * 2 + 40) + "," + (_y * 3 + 40 * 2);
            s[8] = (_x * 3 + 40 * 2) + "," + (_y * 3 + 40 * 2);
        }
        return s;
    }

    public static int getWidth(int size) {
        int width = 0;
        if (size == 1) {
            width = 120;
        }
        if (size > 1 && size <= 4) {
            width = 60;
        }
        if (size >= 5) {
            width = 40;
        }
        return width;
    }

    public static void download(String urlString, String filename, String savePath) throws Exception {
        // 构造URL
        URL url = new URL(urlString);
        // 打开连接
        URLConnection con = url.openConnection();
        //设置请求超时为5s
        con.setConnectTimeout(5 * 1000);
        // 输入流
        InputStream is = con.getInputStream();

        // 1K的数据缓冲
        byte[] bs = new byte[1024];
        // 读取到的数据长度
        int len;
        // 输出的文件流
        File sf = new File(savePath);
        if (!sf.exists()) {
            sf.mkdirs();
        }
        OutputStream os = new FileOutputStream(sf.getPath() + File.separator + filename);
        // 开始读取
        while ((len = is.read(bs)) != -1) {
            os.write(bs, 0, len);
        }
        // 完毕，关闭所有链接
        os.close();
        is.close();
    }



    public static String zoom(String sourcePath, String targetPath, String targetFilename, int width, int height) throws IOException {
        String generatorFileName = null;
        File imageFile = null;
        if (sourcePath.startsWith(StringConstants.HTTP)) {
            // 尝试下载图片
            String path =  ConfigurationContext.getConfigPath() + DOWNLOAD_TMP_FOLDER;
            String filename = UUID.fastUUID().toString(true) + ".png";
            try {
                download(sourcePath, filename, path);
                generatorFileName = path + StrUtil.SLASH + filename;
                imageFile = new File(generatorFileName);
            } catch (Throwable cause) {
                log.warn("Failed execute to download file, casue: {}", cause.getMessage(), cause);
            }
        } else {
            new File(sourcePath);
        }
        if (imageFile == null || !imageFile.exists()) {
            throw new IOException("Not found the images:" + sourcePath);
        }
        String format = sourcePath.substring(sourcePath.lastIndexOf(".") + 1);
        BufferedImage image = ImageIO.read(imageFile);
        image = zoom(image, width, height);
        String targetFile = targetPath + StrUtil.SLASH + targetFilename;
        try {
            // 输出的文件流
            File sf = new File(targetPath);
            if (!sf.exists()) {
                sf.mkdirs();
            }
            ImageIO.write(image, format, new File(targetFile));
        } finally {
            if (StringUtils.isNotBlank(generatorFileName)) {
                imageFile.deleteOnExit();
            }
        }
        return targetFile;
    }

    private static BufferedImage zoom(BufferedImage sourceImage, int width, int height) {
        BufferedImage zoomImage = new BufferedImage(width, height, sourceImage.getType());
        Image image = sourceImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        Graphics gc = zoomImage.getGraphics();
        gc.setColor(Color.WHITE);
        gc.drawImage(image, 0, 0, null);
        return zoomImage;
    }

    public static void createImage(java.util.List<String> files, String outPath, String outFilename) throws Exception {
        String[] imageSize = getXy(files.size());
        int width = getWidth(files.size());
        BufferedImage ImageNew = new BufferedImage(132, 132, BufferedImage.TYPE_INT_RGB);
        //设置背景为白色
        for (int m = 0; m < 132; m++) {
            for (int n = 0; n < 132; n++) {
                ImageNew.setRGB(m, n, 0xFFFFFF);
            }
        }
        for (int i = 0; i < imageSize.length; i++) {
            String size = imageSize[i];
            String[] sizeArr = size.split(",");
            int x = Integer.parseInt(sizeArr[0]);
            int y = Integer.parseInt(sizeArr[1]);
            String f = zoom(files.get(i), outPath, outFilename, width, width);
            File fileOne = new File(f);
            BufferedImage ImageOne = ImageIO.read(fileOne);
            //从图片中读取RGB
            int[] ImageArrayOne = new int[width * width];
            ImageArrayOne = ImageOne.getRGB(0, 0, width, width, ImageArrayOne, 0, width);
            //设置左半部分的RGB
            ImageNew.setRGB(x, y, width, width, ImageArrayOne, 0, width);
        }
        //写图片
        File outFile = new File(outPath + StrUtil.SLASH + outFilename);
        ImageIO.write(ImageNew, "png", outFile);
    }





}
