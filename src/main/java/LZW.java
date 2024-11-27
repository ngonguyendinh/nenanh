import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LZW {

    public List<Integer> compress(byte[] data) {
        // Khởi tạo từ điển với các byte đơn lẻ
        Map<String, Integer> dictionary = new HashMap<>();// k lưu các ký tự giống nhau
        for (int i = 0; i < 256; i++) {
            dictionary.put("" + (char) i, i);
        }

        String currentString = "";
        List<Integer> compressedData = new ArrayList<>();//lưu trữ mã nén
        int dictSize = 258;// khởi đầu thêm mới vào thư viện là 258
        //duyệt phần tử nếu không có sẽ thêm mới vào từ điển
        for (byte b : data) {
            String c = "" + (char) (b & 0xFF);
            String combinedString = currentString + c;

            if (dictionary.containsKey(combinedString))/* kiểm tra giá trị combinedString có tồn tại trong t điển*/{
                currentString = combinedString;
            } else {
                compressedData.add(dictionary.get(currentString));
                dictionary.put(combinedString, dictSize++);
                currentString = c;
            }
        }

        // Ghi giá trị còn lại
        if (!currentString.equals("")) {
            compressedData.add(dictionary.get(currentString));
        }

        return compressedData;
    }
    // Hàm giải nén file ảnh
    public byte[] decompress(List<Integer> compressedData) {
        Map<Integer, String> dictionary = new HashMap<>();
        for (int i = 0; i < 256; i++) {
            dictionary.put(i, "" + (char) i);
        }

        String currentString = "" + (char) (int) compressedData.remove(0);
        StringBuilder decompressedData = new StringBuilder(currentString);
        int dictSize = 258;

        for (int code : compressedData) {
            String entry;
            if (dictionary.containsKey(code)) {
                entry = dictionary.get(code);
            } else if (code == dictSize) {
                entry = currentString + currentString.charAt(0);
            } else {
                throw new IllegalArgumentException("Mã nén không hợp lệ: " + code);
            }

            decompressedData.append(entry);
            dictionary.put(dictSize++, currentString + entry.charAt(0));
            currentString = entry;
        }

        // Chuyển đổi StringBuilder thành mảng byte
        byte[] output = new byte[decompressedData.length()];
        for (int i = 0; i < decompressedData.length(); i++) {
            output[i] = (byte) decompressedData.charAt(i);
        }

        return output;
    }

}
