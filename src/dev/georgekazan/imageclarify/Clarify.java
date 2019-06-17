package dev.georgekazan.imageclarify;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

public class Clarify {

	private static final String INPUT = "C:/Users/George/Desktop/a.png";
	private static final String RESULT_DIR = "C:/Users/George/Desktop/Result";
	
	public static void main(String[] args) throws IOException {
		BufferedImage img = ImageIO.read(new File(INPUT));

		int xBlocks = img.getWidth() / (img.getWidth() / 5);
		int yBlocks = img.getHeight() / (img.getHeight() / 5);
		
		int blockWidth = img.getWidth() / xBlocks;
		int blockHeight = img.getHeight() / yBlocks;
		
		List<Color> colors = new ArrayList<Color>();
		
		int xD = 0; 
		int yD = 0;
		for(int count = 0; count < xBlocks * yBlocks; count++) {
			BufferedImage result = new BufferedImage(blockWidth, blockHeight, img.getType());

			for(int x = 0; x < blockWidth; x++) {
				for(int y = 0; y < blockHeight; y++) {
					int qx = blockWidth * xD + x;
					int qy = blockHeight * yD + y;
					
					boolean good = true;
					
					Color similar = null;
					int clr = img.getRGB(qx, qy);
					int r = (clr & 0x00ff0000) >> 16;
					int g = (clr & 0x0000ff00) >> 8;
					int b = clr & 0x000000ff;
					
					for(Color c : colors) {
						if(colorsSimilar(c, new Color(r, g, b))) {
							good = false;
							similar = c;
						}
					}
					
					if(good) {
						colors.add(new Color(r, g, b));
						result.setRGB(x, y, img.getRGB(qx, qy));
					}
					else {
						result.setRGB(x, y, similar.getRGB());
					}
				}
			}
			
			ImageIO.write(result, "png", new File(RESULT_DIR + "/result" + count + ".png"));

			if(xD >= xBlocks - 1) {
				xD = 0;
				yD++;
			}
			else {
				xD++;
			}
		}
		
		BufferedImage totalResult = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
		yD = xD = 0;
		for(int i = 0; i < xBlocks * yBlocks; i++) {
			
			totalResult.getGraphics().drawImage(ImageIO.read(new File(RESULT_DIR + "/result" + i + ".png")), blockWidth * xD, blockHeight * yD, null);
			
			if(xD >= xBlocks - 1) {
				xD = 0;
				yD++;
			}
			else {
				xD++;
			}
		}
		ImageIO.write(totalResult, "png", new File(RESULT_DIR + "/final.png"));
	}
	
	public static boolean colorsSimilar(Color a, Color b) {
		return colorsSimilar(a, b, 20);
	}
	
	public static boolean colorsSimilar(Color a, Color b, int bound) {
		int r = a.getRed() - b.getRed(), g = a.getGreen() - b.getGreen(), bl = a.getBlue() - b.getBlue();
		return (r * r + g * g + bl * bl) <= bound * bound;
	}
	
}
