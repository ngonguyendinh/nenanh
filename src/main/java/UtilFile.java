import java.io.*;

public class UtilFile   {
    //đọc dữ liệu file và chuyển thành byte
    public   byte[] readFile(String filePath) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(filePath);
        byte[] data = fileInputStream.readAllBytes();
        fileInputStream.close();
        return data;
    }

    // Hàm ghi mảng byte vào file
    public static File writeFile(String filePath, byte[] data) throws IOException {
        File file = new File(filePath);
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            fileOutputStream.write(data);
        }
        return file;
    }

}
