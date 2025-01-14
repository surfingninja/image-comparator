import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImageComparator {

    public static void main(String[] args) {
        String image1Path = "tmp1.png";
        String image2Path = "tmp2.png";
        String outputPath = "result.jpg";

        try {
            BufferedImage image1 = ImageIO.read(new File(image1Path));
            BufferedImage image2 = ImageIO.read(new File(image2Path));

            if (image1.getWidth() != image2.getWidth() || image1.getHeight() != image2.getHeight()) {
                System.out.println("Images dimensions do not match!");
                return;
            }

            BufferedImage resultImage = compareImages(image1, image2, 10, null);
            ImageIO.write(resultImage, "jpg", new File(outputPath));
            System.out.println("Comparison complete. Output saved to " + outputPath);

        } catch (IOException e) {
            System.err.println("Error reading or writing images: " + e.getMessage());
        }
    }

    private static BufferedImage compareImages(BufferedImage img1, BufferedImage img2, int tolerance, Rectangle[] excludeRegions) {
        int width = img1.getWidth();
        int height = img1.getHeight();
        BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = output.createGraphics();
        g.drawImage(img1, 0, 0, null);

        boolean[][] differences = new boolean[height][width];

        // Identify pixel differences
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (isInExcludedRegion(x, y, excludeRegions)) continue;

                int rgb1 = img1.getRGB(x, y);
                int rgb2 = img2.getRGB(x, y);

                if (!areColorsSimilar(rgb1, rgb2, tolerance)) {
                    differences[y][x] = true;
                }
            }
        }

        // Group differences into rectangles
        List<Rectangle> rectangles = findDifferenceRectangles(differences);

        // Highlight rectangles on the output image
        g.setColor(Color.RED);
        for (Rectangle rect : rectangles) {
            g.drawRect(rect.x, rect.y, rect.width, rect.height);
        }
        g.dispose();
        return output;
    }

    private static boolean areColorsSimilar(int rgb1, int rgb2, int tolerance) {
        int r1 = (rgb1 >> 16) & 0xFF, g1 = (rgb1 >> 8) & 0xFF, b1 = rgb1 & 0xFF;
        int r2 = (rgb2 >> 16) & 0xFF, g2 = (rgb2 >> 8) & 0xFF, b2 = rgb2 & 0xFF;

        double distance = Math.sqrt(Math.pow(r1 - r2, 2) + Math.pow(g1 - g2, 2) + Math.pow(b1 - b2, 2));
        return distance <= tolerance * 2.55;
    }

    private static boolean isInExcludedRegion(int x, int y, Rectangle[] excludeRegions) {
        if (excludeRegions == null) return false;
        for (Rectangle rect : excludeRegions) {
            if (rect.contains(x, y)) return true;
        }
        return false;
    }

    private static List<Rectangle> findDifferenceRectangles(boolean[][] differences) {
        int height = differences.length;
        int width = differences[0].length;
        boolean[][] visited = new boolean[height][width];
        List<Rectangle> rectangles = new ArrayList<>();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (differences[y][x] && !visited[y][x]) {
                    Rectangle rect = floodFill(differences, visited, x, y);
                    rectangles.add(rect);
                }
            }
        }
        return rectangles;
    }

    private static Rectangle floodFill(boolean[][] differences, boolean[][] visited, int startX, int startY) {
        int minX = startX, minY = startY, maxX = startX, maxY = startY;
        int height = differences.length;
        int width = differences[0].length;

        List<int[]> queue = new ArrayList<>();
        queue.add(new int[]{startX, startY});
        visited[startY][startX] = true;

        while (!queue.isEmpty()) {
            int[] point = queue.remove(0);
            int x = point[0], y = point[1];

            minX = Math.min(minX, x);
            minY = Math.min(minY, y);
            maxX = Math.max(maxX, x);
            maxY = Math.max(maxY, y);

            for (int dy = -1; dy <= 1; dy++) {
                for (int dx = -1; dx <= 1; dx++) {
                    int nx = x + dx, ny = y + dy;

                    if (nx >= 0 && nx < width && ny >= 0 && ny < height &&
                            differences[ny][nx] && !visited[ny][nx]) {
                        queue.add(new int[]{nx, ny});
                        visited[ny][nx] = true;
                    }
                }
            }
        }

        return new Rectangle(minX, minY, maxX - minX + 1, maxY - minY + 1);
    }
}
