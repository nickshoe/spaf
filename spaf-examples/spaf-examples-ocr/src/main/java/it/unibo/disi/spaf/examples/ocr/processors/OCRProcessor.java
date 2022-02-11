package it.unibo.disi.spaf.examples.ocr.processors;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.unibo.disi.spaf.api.Collector;
import it.unibo.disi.spaf.api.Processor;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

public class OCRProcessor implements Processor<String, byte[], String, String> {

	private static final Logger LOGGER = LoggerFactory.getLogger(OCRProcessor.class);

	private static final long serialVersionUID = 1L;

	private Tesseract tesseract;

	private final String datapath;

	public OCRProcessor(String datapath) {
		super();
		this.datapath = datapath;
	}

	@Override
	public void init() {
		this.tesseract = new Tesseract();

		this.tesseract.setDatapath(this.datapath);

		LOGGER.info("Tesseract has been initialized");
	}

	@Override
	public void process(String key, byte[] value, Collector<String, String> collector) {
		String recognizedText;
		
		String imageName = key;
		byte[] image = value;
		
		BufferedImage bufferedImage;
		try {
			bufferedImage = ImageIO.read(new ByteArrayInputStream(image));
			
		} catch (IOException e) {
			LOGGER.error("Cannot read image {}, discarding...", imageName);
			
			e.printStackTrace();
			
			return;
		}
		
		try {
			recognizedText = this.tesseract.doOCR(bufferedImage);
		} catch (TesseractException e) {
			LOGGER.error("Cannot perform OCR on image {}, discarding...", imageName);
			
			e.printStackTrace();
			
			return;
		}
		
		collector.collect(imageName, recognizedText);
	}

}
