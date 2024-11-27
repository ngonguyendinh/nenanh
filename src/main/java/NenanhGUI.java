import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

public class NenanhGUI extends JFrame {
    private JLabel originalImageLabel;
    private JLabel compressedImageLabel;
    private JTextArea infoArea;
    private File selectedFile;
    private File compressedFile;
    private List<Integer> compressedData;
    private File decompressFile;

    public NenanhGUI() {
        // Cài đặt cửa sổ chính
        setTitle("Nén và giải nén LZW");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel chính chứa hai khu vực cho ảnh gốc và ảnh nén
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(1, 2));

        // Khu vực chọn file và hiển thị ảnh gốc
        originalImageLabel = new JLabel("Chọn ảnh gốc", SwingConstants.CENTER);
        originalImageLabel.setBorder(BorderFactory.createTitledBorder("Ảnh gốc"));
        JButton chooseImageButton = new JButton("Chọn Ảnh");
        chooseImageButton.addActionListener(e -> openImageFile());

        JPanel originalPanel = new JPanel(new BorderLayout());
        originalPanel.add(chooseImageButton, BorderLayout.NORTH);
        originalPanel.add(originalImageLabel, BorderLayout.CENTER);

        // Khu vực hiển thị ảnh đã nén
        compressedImageLabel = new JLabel("Ảnh giải đã nén", SwingConstants.CENTER);
        compressedImageLabel.setBorder(BorderFactory.createTitledBorder("Ảnh giải đã nén"));

        JPanel compressedPanel = new JPanel(new BorderLayout());
        compressedPanel.add(compressedImageLabel, BorderLayout.CENTER);

        mainPanel.add(originalPanel);
        mainPanel.add(compressedPanel);

        // Panel chứa các nút
        JPanel buttonPanel = new JPanel(new FlowLayout());

        // Nút thực hiện nén
        JButton compressButton = new JButton("Thực hiện mã nén");
        compressButton.addActionListener(e -> performCompression());

        // Nút giải nén
        JButton decompressButton = new JButton("Giải nén");
        decompressButton.addActionListener(e -> performDecompression());

        // Thêm các nút vào panel
        buttonPanel.add(compressButton);
        buttonPanel.add(decompressButton);

        // Thêm các thành phần vào cửa sổ
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.PAGE_END);
    }

    // Mở hộp thoại chọn tệp ảnh gốc
    private void openImageFile() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            try {
                // Hiển thị ảnh gốc
                BufferedImage image = ImageIO.read(selectedFile);
                if (image != null) {
                    originalImageLabel.setIcon(new ImageIcon(image.getScaledInstance(originalImageLabel.getWidth(), originalImageLabel.getHeight(), Image.SCALE_SMOOTH)));
                } else {
                    JOptionPane.showMessageDialog(this, "Không thể đọc tệp ảnh.");
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi đọc ảnh: " + e.getMessage());
            }
        }
    }

    // Thực hiện nén và hiển thị ảnh nén cùng thông tin
    private void performCompression() {
        if (selectedFile == null) {
            ImageIcon imageIcon = new ImageIcon("src/main/resources/picture/err.jpg");

            // Lấy hình ảnh gốc từ icon
            Image originalImage = imageIcon.getImage();

// Thu nhỏ hình ảnh (ví dụ: thu nhỏ còn 16x16 pixel)
            Image scaledImage = originalImage.getScaledInstance(20, 20, Image.SCALE_SMOOTH);

// Tạo icon mới từ ảnh đã thu nhỏ
            ImageIcon scaledIcon = new ImageIcon(scaledImage);
            JOptionPane.showMessageDialog(this, "Vui lòng chọn ảnh gốc trước.","Thông báo",JOptionPane.INFORMATION_MESSAGE,scaledIcon);
            return;
        }

        try {
            String outputCompressedFilePath = "compressed.lzw";
            LZW lzw = new LZW();
            // Đọc ảnh gốc
            BufferedImage image = ImageIO.read(selectedFile);
            UtilFile file = new UtilFile();
            byte[] inputData = file.readFile(String.valueOf(selectedFile));
            // Thực hiện nén bằng thuật toán LZW
            compressedData = lzw.compress(inputData);
            System.out.println("Đã nén xong file ảnh.");
            // Lưu tệp nén
            // Chuyển đổi danh sách mã nén thành mảng byte (để lưu vào file)
            byte[] compressedBytes = new byte[compressedData.size() * 4]; // mỗi số nguyên cần 4 byte
            for (int i = 0; i < compressedData.size(); i++) {
                int value = compressedData.get(i);
                compressedBytes[i * 4] = (byte) ((value >> 24) & 0xFF);
                compressedBytes[i * 4 + 1] = (byte) ((value >> 16) & 0xFF);
                compressedBytes[i * 4 + 2] = (byte) ((value >> 8) & 0xFF);
                compressedBytes[i * 4 + 3] = (byte) (value & 0xFF);
            }

            // Ghi dữ liệu đã nén ra file
            // Lưu compressedData vào tệp compressedFile
            compressedFile = UtilFile.writeFile(outputCompressedFilePath, compressedBytes);
            System.out.println("Đã lưu file nén.");

            //xử lý ảnh icon ở thông báo
            ImageIcon successIcon = new ImageIcon("src/main/resources/picture/done.jpg");
            Image originalImage = successIcon.getImage();
            Image scaledImage = originalImage.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            ImageIcon scaledIcon = new ImageIcon(scaledImage);
            // Hiển thị thông báo nén thành công
            JOptionPane.showMessageDialog(this, "Quá trình nén hoàn tất!", "Thông báo", JOptionPane.INFORMATION_MESSAGE,scaledIcon);

//             thông tin về file nén
            long fileSizeOld = selectedFile.length();
            String fileNameOld = selectedFile.getName();
            long fileSize = compressedFile.length(); // Kích thước file (bytes)
            String fileName = compressedFile.getName();
            int width = image.getWidth();
            int height = image.getHeight();
            // Hiển thị thông tin tệp chưa nén và tệp nén
            DecimalFormat df = new DecimalFormat("#,###");
            String message = "<html>"+"File chưa nén: " + fileNameOld+"<br>Kích thước: " + df.format(fileSizeOld) +" bytes"+
                    "<br>Kích thước ảnh: " + width + " x " + height + " pixels"+
                    "<br>File nén: " + fileName + "<br>Kích thước: " + df.format(fileSize) + " bytes</html>";
            JOptionPane.showMessageDialog(this,
                    message,
                    "Thông tin",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi nén ảnh: " + e.getMessage());
        }
    }

    // Thực hiện giải nén và hiển thị ảnh đã giải nén
    private void performDecompression() {
        if (compressedFile == null) {
            ImageIcon imageIcon = new ImageIcon("src/main/resources/picture/err.jpg");
            Image originalImage = imageIcon.getImage();
            Image scaledImage = originalImage.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            ImageIcon scaledIcon = new ImageIcon(scaledImage);
            JOptionPane.showMessageDialog(this, "Không có file nén nào được tìm thấy..","Thông báo",JOptionPane.INFORMATION_MESSAGE,scaledIcon);
            return;

        }

        try {
            // Thực hiện giải nén bằng thuật toán LZW

            LZW lzw = new LZW();
            byte[] decompressedData = lzw.decompress(compressedData);
            String outputDecompressedFilePath = "decompressed_image.bmp";
            // Ghi dữ liệu giải nén ra file ảnh
            decompressFile = UtilFile.writeFile(outputDecompressedFilePath, decompressedData);
            System.out.println("Đã giải nén file ảnh.");
            // Hiển thị thông tin tệp giải nén (ví dụ về kích thước ảnh)
            BufferedImage decompressedImage = ImageIO.read(decompressFile);
            // Hiển thị ảnh đã giải nén
            compressedImageLabel.setIcon(new ImageIcon(decompressedImage.getScaledInstance(compressedImageLabel.getWidth(), compressedImageLabel.getHeight(), Image.SCALE_SMOOTH)));
            // xử lý icon
            ImageIcon successIcon = new ImageIcon("src/main/resources/picture/done.jpg");
            Image originalImage = successIcon.getImage();
            Image scaledImage = originalImage.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            ImageIcon scaledIcon = new ImageIcon(scaledImage);
            // Hiển thị thông báo giải nén thành công
            JOptionPane.showMessageDialog(this, "Quá trình giải nén hoàn tất!", "Thông báo", JOptionPane.INFORMATION_MESSAGE,scaledIcon);

            int width = decompressedImage.getWidth();
            int height = decompressedImage.getHeight();
            long filezie = decompressFile.length();
            String fileName = decompressFile.getName();
            String ifo ="<html>"+"File giải nén:"+fileName+"<br>Kích thước: "+filezie+"<br>Kích thước ảnh sau khi giải nén: " + width + " x " + height + " pixels</html>";

            JOptionPane.showMessageDialog(this, ifo, "Thông tin file giải nén", JOptionPane.INFORMATION_MESSAGE);

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi giải nén ảnh: " + e.getMessage());
        }
    }

    // Giả sử đây là phương thức nén bằng LZW của bạn
    private byte[] compressLZW(BufferedImage image) {
        // Thực hiện nén LZW
        // Trả về dữ liệu nén dưới dạng mảng byte
        return new byte[0]; // Thay thế bằng thuật toán của bạn
    }



    // Khởi động ứng dụng
    public static void main(String[] args) {
        // Tạo và hiển thị giao diện người dùng
        SwingUtilities.invokeLater(() -> {
            NenanhGUI frame = new NenanhGUI();
            frame.setVisible(true);
        });
    }
}
