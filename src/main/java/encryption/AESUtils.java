package encryption;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.NoSuchAlgorithmException;

public class AESUtils {
    private static final Config conf = ConfigFactory.load("configuration.conf");

    /**
     * generate key
     *
     * @return
     */
    public static byte[] initKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128);
        SecretKey secretKey = keyGenerator.generateKey();
        byte[] key = secretKey.getEncoded();
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(conf.getString("conf.file.keyFile"));
            fileOutputStream.write(key);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return key;
    }

    /**
     * init aes cipher
     *
     * @param codeFormat
     * @param cipherMode
     * @return
     */
    public static Cipher initAESCipher(byte[] codeFormat, int cipherMode) {
        Cipher cipher = null;
        try {
            SecretKeySpec key = new SecretKeySpec(codeFormat, "AES");
            cipher = Cipher.getInstance("AES");
            // init
            cipher.init(cipherMode, key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cipher;
    }

    /**
     * encrypt file
     *
     * @param sourceFile
     * @param encrypFile
     */
    public static void encryptFile(String sourceFile, String encrypFile) {
        InputStream inputStream = null;
        InputStream keyStream = null;
        OutputStream outputStream = null;
        try {
//            keyStream = new FileInputStream(conf.getString("conf.file.keyFile"));
            inputStream = new FileInputStream(new File(sourceFile));
            outputStream = new FileOutputStream(new File(encrypFile));
//            byte[] key = toByteArray(keyStream);
            byte[] key = conf.getString("conf.file.key").getBytes();
            Cipher cipher = initAESCipher(key, Cipher.ENCRYPT_MODE);

            CipherInputStream cipherInputStream = new CipherInputStream(inputStream, cipher);
            byte[] cache = new byte[1024];
            int nRead = 0;
            while ((nRead = cipherInputStream.read(cache)) != -1) {
                outputStream.write(cache, 0, nRead);
                outputStream.flush();
            }
            cipherInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void decryptFile(String sourceFile, String decryptFile) {
        InputStream inputStream = null;
        InputStream keyStream = null;
        OutputStream outputStream = null;
        try {
//            keyStream = new FileInputStream(conf.getString("conf.file.keyFile"));
//            byte[] key = toByteArray(keyStream);
            byte[] key = conf.getString("conf.file.key").getBytes();
            Cipher cipher = initAESCipher(key, Cipher.DECRYPT_MODE);
            inputStream = new FileInputStream(new File(sourceFile));
            outputStream = new FileOutputStream(new File(decryptFile));

            CipherOutputStream cipherOutputStream = new CipherOutputStream(
                    outputStream, cipher);
            byte[] buffer = new byte[1024];
            int r;
            while ((r = inputStream.read(buffer)) >= 0) {
                cipherOutputStream.write(buffer, 0, r);
            }
            cipherOutputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static byte[] toByteArray(InputStream in) throws IOException {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024 * 4];
        int n = 0;
        while ((n = in.read(buffer)) != -1) {
            out.write(buffer, 0, n);
        }
        return out.toByteArray();
    }

    public static void main(String[] args) throws NoSuchAlgorithmException {
//        initKey();
        encryptFile("/home/rukhlyn/IdeaProjects/AESUtils/src/test.txt", "/home/rukhlyn/IdeaProjects/AESUtils/src/encrypt.txt");
        decryptFile("/home/rukhlyn/IdeaProjects/AESUtils/src/encrypt.txt", "/home/rukhlyn/IdeaProjects/AESUtils/src/decrypt.txt");
    }
}
