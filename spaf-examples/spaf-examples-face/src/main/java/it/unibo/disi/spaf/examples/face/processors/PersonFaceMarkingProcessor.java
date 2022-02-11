package it.unibo.disi.spaf.examples.face.processors;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.colour.RGBColour;
import org.openimaj.image.typography.hershey.HersheyFont;

import it.unibo.disi.spaf.api.Collector;
import it.unibo.disi.spaf.api.Processor;
import it.unibo.disi.spaf.examples.face.processors.dto.FaceRecognitionOutputEnvelope;
import it.unibo.disi.spaf.examples.face.processors.dto.PersonMatch;

public class PersonFaceMarkingProcessor implements Processor<String, FaceRecognitionOutputEnvelope, String, byte[]> {

	private static final long serialVersionUID = 1L;

	@Override
	public void process(String imageFileName, FaceRecognitionOutputEnvelope envelope, Collector<String, byte[]> collector) {
		byte[] inputImageBytes = envelope.getInputImageBytes();
		List<PersonMatch> personMatches = envelope.getPersonMatches();
		
		ByteArrayInputStream inputImageStream = new ByteArrayInputStream(inputImageBytes);
		MBFImage outputImage;
		try {
			outputImage = ImageUtilities.readMBF(inputImageStream);
		} catch (IOException e) {
			e.printStackTrace();

			return;
		}

		for (PersonMatch personMatch : personMatches) {
			Float[] shapeColor = RGBColour.RED;
			if (personMatch.getDistance() <= 10) {
				shapeColor = RGBColour.GREEN;
			}

			// Draw a rectangle around people faces in the input image
			outputImage.drawShape(personMatch.getFaceDetectionResult().getBounds(), 2, shapeColor);
			
			// Draw the person name below the rectangle
			int fontSize = 12;
			int margin = 3;
			outputImage.drawText(
				personMatch.getPersonId(), 
				(int) personMatch.getFaceDetectionResult().getBounds().x, 
				(int) personMatch.getFaceDetectionResult().getBounds().maxY() + fontSize + margin, 
				HersheyFont.TIMES_BOLD, 
				fontSize,
				RGBColour.WHITE
			);
		}

		ByteArrayOutputStream outputImageStream = new ByteArrayOutputStream();
		try {
			ImageUtilities.write(outputImage, "jpg", outputImageStream);
		} catch (IOException e) {
			e.printStackTrace();

			return;
		}
		byte[] outputImageBytes = outputImageStream.toByteArray();


		String nowIsoDatetime = ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT);

		collector.collect(nowIsoDatetime + "_" + imageFileName, outputImageBytes);
	}
	
}
