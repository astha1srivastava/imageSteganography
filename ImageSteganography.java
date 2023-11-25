import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ImageSteganography {

    public static void main(String[] args) {
        String imagePath = "C:\\Users\\Astha Srivastava\\OneDrive\\Pictures\\Screenshots\\Screenshot (47).png";
        String outputImagePath = "C:\\Users\\Astha Srivastava\\OneDrive\\Pictures\\Screenshots\\Screenshot (1).png";
        String message = "This is a secret message.";

        hideMessage(imagePath, outputImagePath, message);
        System.out.println("Message hidden successfully!");
    }

    public static void hideMessage(String imagePath, String outputImagePath, String message) {
        try {
            BufferedImage image = ImageIO.read(new File(imagePath));

            // Convert the message to an array of bytes
            byte[] messageBytes = message.getBytes();

            // Get the number of bytes needed to represent the message length
            int messageLength = messageBytes.length;
            int lengthBytes = Integer.SIZE / 8;

            // Check if the image can hold the message
            int imageCapacity = image.getWidth() * image.getHeight() * 3 / 8;
            if (messageLength + lengthBytes > imageCapacity) {
                throw new IllegalArgumentException("The message is too large to be hidden in the image.");
            }

            // Set the message length in the first few pixels
            for (int i = 0; i < lengthBytes; i++) {
                int lengthByte = (messageLength >> (i * 8)) & 0xFF;
                hideByte(image, i, lengthByte);
            }

            // Set each byte of the message in the image pixels
            for (int i = 0; i < messageLength; i++) {
                hideByte(image, i + lengthBytes, messageBytes[i]);
            }

            // Save the modified image
            File outputFile = new File(outputImagePath);
            ImageIO.write(image, "png", outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void hideByte(BufferedImage image, int offset, int value) {
        int width = image.getWidth();
        int x = offset % width;
        int y = offset / width;
        int rgb = image.getRGB(x, y);

        // Clear the least significant bit of each color component
        rgb &= 0xFFFFFFF8;

        // Set the least significant bit of each color component with the value bits
        rgb |= (value >> 5) & 0x00000007;
        rgb <<= 3;
        image.setRGB(x, y, rgb);
    }
}