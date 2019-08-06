package encryption;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.AesKeyStrength;
import net.lingala.zip4j.model.enums.CompressionMethod;
import net.lingala.zip4j.model.enums.EncryptionMethod;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;


public class ZipUtils {
    private static final Config conf = ConfigFactory.load("configuration.conf");

    public static void encryptZip(String srcPath) {
        String password = conf.getString("conf.file.key");
        encryptZip(srcPath, password);
    }


    public static void encryptZip(String srcPath, String password) {
        ZipParameters parameters = new ZipParameters();
        parameters.setCompressionMethod(CompressionMethod.STORE);
        parameters.setEncryptFiles(true);
        parameters.setEncryptionMethod(EncryptionMethod.AES);
        parameters.setAesKeyStrength(AesKeyStrength.KEY_STRENGTH_256);


        ZipFile zipFile = new ZipFile(srcPath + ".zip", password.toCharArray());

        try {
            File file = new File(srcPath);
            if (file.isDirectory()) {
                zipFile.addFolder(new File(srcPath), parameters);
            } else {
                zipFile.addFile(new File(srcPath), parameters);
            }
        } catch (ZipException e) {
            e.printStackTrace();
        }
    }

    public static void decryptZip(String zipPath, String saveFileDir) {
        if(!saveFileDir.endsWith("\\") && !saveFileDir.endsWith("/") ){
            saveFileDir += File.separator;
        }
        String password = conf.getString("conf.file.key");
        decryptZip(zipPath, saveFileDir, password);
    }

    public static void decryptZip(String zipPath, String saveFileDir, String password) {
        try {
            if(!saveFileDir.endsWith("\\") && !saveFileDir.endsWith("/") ){
                saveFileDir += File.separator;
            }
            new ZipFile(zipPath, password.toCharArray()).extractAll(saveFileDir);
        } catch (ZipException e) {
            e.printStackTrace();
        }
    }

    public static String getFirstEntryNameFromZipFile(String zipFilePath) throws IOException {
        try(java.util.zip.ZipFile zipFile=new java.util.zip.ZipFile(zipFilePath)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            if(entries != null && entries.hasMoreElements()) {
                return entries.nextElement().getName();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



    public static void main(String[] args) throws IOException {
//        encryptZip("/mnt/data1/datadir/social_search/TWITTER_SearchUserInfo_realDonaldTrump_07_30_2019_16_51_14");
        decryptZip("/mnt/data1/datadir/social_search/TWITTER_SearchUserInfo_realDonaldTrump_07_30_2019_16_51_14.zip", "/mnt/data1/datadir/social_search/TWITTER_SearchUserInfo_realDonaldTrump_07_30_2019_16_51_14");
    }

}




