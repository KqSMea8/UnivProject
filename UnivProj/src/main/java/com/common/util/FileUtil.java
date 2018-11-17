package com.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Encoder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.UUID;

/**
 * @author : EMMA
 * @Date: 2018/6/19
 * @Description: 文件处理工具类
 * @Since:
 */
public class FileUtil {
    private static final Logger log = LoggerFactory.getLogger(FileUtil.class);
    private static boolean needInit = true;
    private static boolean hasParent = false;
    private static String filePath = File.separator + "app" + File.separator + "univ" + File.separator + "mp3" + File.separator;
    private static String cmdPath = File.separator + "app" + File.separator + "univ" + File.separator ;
    private static String WIN_SYSTEM = "win";
    /**
     * 根据操作系统获取根路径地址
     * */
    public static String getParentpath() {
        initSystemPath();
        return filePath;
    }
    /**
     * 根据操作系统获取根路径地址
     * */
    public static String getCmdpath() {
        initSystemPath();
        return cmdPath;
    }
    private static void initSystemPath() {
        String os = System.getProperty("os.name");
        if(needInit) {
            if (os.toLowerCase().startsWith( WIN_SYSTEM )) {
                filePath = "C:" + filePath;
                cmdPath = "C:" + cmdPath;
            }
        }
        needInit = false;
        if(!hasParent) {
            File cmdParent = new File( cmdPath );
            File fileParent = new File( filePath );
            boolean flag1 = true;
            boolean flag2 = true;
            if (!cmdParent.exists()) {
                flag1  = cmdParent.mkdirs() ;
                log.info( "create parent dir result: {}",hasParent );
            }
            if (!fileParent.exists()) {
                flag2  = fileParent.mkdirs() ;
                log.info( "create parent dir result: {}",hasParent );
            }
            hasParent = flag1 & flag2;
        }
    }
    /**
     * 创建文件
     * @param inputStream 输入流
     * @param fileName 文件名
     * */
    public static File createFile(InputStream inputStream, String fileName) {
        File file  = null;
        FileOutputStream fileOut = null;
        try{
            if(inputStream!=null){
                file = new File(fileName);
            }else{
                return file;
            }

            //写入到文件
            fileOut = new FileOutputStream(file);
            if(fileOut!=null){
                int c = inputStream.read();
                while(c!=-1){
                    fileOut.write(c);
                    c = inputStream.read();
                }
            }
        } catch (Exception e) {
            log.warn( "create file {} error",fileName ,e );
        } finally {
            try {
                fileOut.close();
            } catch (Exception e1) {
                log.warn( "createFile close stream error" ,e1 );
            }

        }
        return file;
    }

    /**
     * amr转码MP3
     * */
    public static File changeCode(File sourceFile, String targetPath, String cmdPath) {
        File targetFile = new File(targetPath);

        Runtime run = null;
        try {
            run = Runtime.getRuntime();
            long start=System.currentTimeMillis();
            //执行ffmpeg.exe,前面是ffmpeg.exe的地址，中间是需要转换的文件地址，后面是转换后的文件地址。-i是转换方式，意思是可编码解码，mp3编码方式采用的是libmp3lame
            Process p=run.exec(cmdPath + File.separator+"ffmpeg -i "+sourceFile.getAbsolutePath()+" -acodec libmp3lame "+targetFile.getAbsolutePath());
            //释放进程
            p.getOutputStream().close();
            p.getInputStream().close();
            p.getErrorStream().close();
            p.waitFor();
            long end=System.currentTimeMillis();
            System.out.println(sourceFile.getName()+" convert success, costs:"+(end-start)+"ms");
        } catch (Exception e) {
            log.warn( "changeCode error",e );
        }finally{
            //run调用lame解码器最后释放内存
            run.freeMemory();
        }
        return targetFile;
    }
    public static void main(String agrs[]) {
        for(int i = 0; i< 1000000; i++) {
            System.out.println( "http://univ.ngrok.xiaomiqiu.cn/univ/user/getCode/" + UUID.randomUUID().toString());
        }
//        File s = new File("C:\\app\\univ\\mp3\\012002.amr");
//        File t = new File("C:\\app\\univ\\mp3\\012002.mp3");
//        changeCode(s,t);
    }


    /**
     * 将文件转成base64 字符串
     * @param file
     * @return  *
     * @throws Exception
     */

    public static String encodeBase64File(File file) throws Exception {
        FileInputStream inputFile = new FileInputStream(file);
        byte[] buffer = new byte[(int) file.length()];
        inputFile.read(buffer);
        inputFile.close();
        return new BASE64Encoder().encode(buffer);
    }
}
