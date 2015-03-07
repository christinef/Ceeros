package com.conferresystems.pixelated;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class PixelatedMain {
	
	private static int blockSize;
	
	/**
		Reads in input.jpg file and outputs the resulting pixelate.png file at a 5-block granularity.
	*/
	public static void main(String[] args) {
		try {
			BufferedImage inputImage = ImageIO.read(new File("input.jpg"));
			blockSize = inputImage.getWidth() / 5;
			if (blockSize > 0 ){
				BufferedImage outputImage = pixelate(inputImage);
				ImageIO.write(outputImage, "png", new File("pixelate.png"));
			} else {
				System.out.println("Pixelation failed: image too small");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
		Processes the image by calculating the average pixel color for each subblock. 
		Returns pixelated image.
	*/	
	private static BufferedImage pixelate(BufferedImage image) {
		final int width = image.getWidth();
		final int height = image.getHeight();
		if (image == null || width == 0 || height == 0) {
			return null;
		}
		int blockWidth = width / blockSize;
		int blockHeight = height / blockSize;
		int excessWidth = width - (blockSize * blockWidth);
		int excessHeight = height - (blockSize * blockHeight);
		
		//hat tip: http://stackoverflow.com/questions/6524196/java-get-pixel-array-from-image
		final byte[] pxls = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		
		int[][] avgPixels = new int[blockSize][blockSize];
		for(int i = 0; i < blockSize; i++) {
			for(int j = 0; j < blockSize; j++){ 
				int rCounter = 0;
				int gCounter = 0;
				int bCounter = 0;
				for (int y = 0; y < blockHeight; y++){
					int iterator = j * blockHeight * width * 3 +  y * width * 3 + i * blockWidth * 3;
					for (int x = 0; x < blockWidth; x++) {
						bCounter += (int)pxls[iterator] & 0xff;
						gCounter += (int)pxls[iterator + 1] & 0xff;
						rCounter += (int)pxls[iterator + 2] & 0xff;
						iterator += 3;
					}
				}
				
				rCounter /= (blockWidth * blockHeight);
				gCounter /= (blockWidth * blockHeight);
				bCounter /= (blockWidth * blockHeight);
				
				avgPixels[j][i] = new Color(rCounter, gCounter, bCounter, 255).getRGB();
			}
			
		}
		
		for(int y = 0; y < height; y++) {
			int yMark = y / blockHeight < blockSize ? y / blockHeight : blockSize - 1;
			for (int x = 0; x < width; x++){
				int xMark = x / blockWidth < blockSize ? x / blockWidth : blockSize - 1;
				int pxl = avgPixels[yMark][xMark];
				image.setRGB(x, y, pxl);
			}
		}
		
		return image.getSubimage(0, 0, width - excessWidth, height - excessHeight);
	}

}
