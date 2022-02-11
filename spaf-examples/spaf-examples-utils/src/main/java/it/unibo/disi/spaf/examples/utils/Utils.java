package it.unibo.disi.spaf.examples.utils;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Utils {
	
	private final static Logger logger = LoggerFactory.getLogger(Utils.class);

	public static final List<Image> buildImagesList(String imagesDirPath) {
		File imagesDir = new File(imagesDirPath);
		
		List<Image> imageTupleList = new ArrayList<>();
		
		File[] files = imagesDir.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				String subDirPath = imagesDir + "/" + file.getName();
				File[] subDirFiles = new File(subDirPath).listFiles();
				for (File subDirFile : subDirFiles) {
					try {
						String imageName = subDirFile.getName();
						byte[] imageBytes = Files.readAllBytes(subDirFile.toPath());
						
						imageTupleList.add(new Image(imageName, imageBytes));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} else if (file.isFile()) {
				try {
					String imageName = file.getName();
					byte[] imageBytes = Files.readAllBytes(file.toPath());
					
					imageTupleList.add(new Image(imageName, imageBytes));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		return imageTupleList;
	}

	public static String extractImageFormatName(String filename) {
		String formatName;
		
		int idx = filename.lastIndexOf('.');
		if (idx != -1) {
		    String extension = filename.substring(idx);
		    if (extension.matches("\\.[a-zA-Z0-9]+")) {
		        formatName = extension.replace(".", "");
		    } else {
		    	throw new RuntimeException("Invalid format name " + extension);
		    }
		} else {
			throw new RuntimeException("Missing format name " + filename);
		}
		
		return formatName;
	}

	public static void saveImage(String filename, byte[] bytes, String dirPath) {
		try {
			ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
			BufferedImage image = ImageIO.read(inputStream);
			
			File outputfile = new File(dirPath + filename);
			
			String formatName = extractImageFormatName(filename);
			
			boolean saved = ImageIO.write(image, formatName, outputfile);
			if (!saved) {
				logger.error("Image " + filename + " could not be saved, no appropriate writer was found.");
			}
		} catch (IOException e) {
			logger.error("Image " + filename + " could not be saved, an error occurred: {}", e.getMessage());
		}
	}

}
