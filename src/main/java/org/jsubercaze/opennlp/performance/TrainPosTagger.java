package org.jsubercaze.opennlp.performance;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSSample;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.postag.WordTagSampleStream;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.model.ModelType;

/**
 * Measure time spent training a pos tagger on the silver dataset built by
 * {@link SilverDatasetBuilder}
 * 
 * 
 * Code taken from
 * https://opennlp.apache.org/documentation/manual/opennlp.html#tools.postagger.
 * training
 * 
 * @author Julien
 *
 */
public class TrainPosTagger {
	private static final double NS_TO_MS = 1000000d;
	private static final DecimalFormat df = new DecimalFormat("#.000");

	public static void main(String[] args) {
		POSModel model = null;
		long nanoTime = System.nanoTime();
		InputStream dataIn = null;
		try {
			dataIn = new FileInputStream("gutenberg_tagged_small.txt");
			ObjectStream<String> lineStream = new PlainTextByLineStream(dataIn, "UTF-8");
			ObjectStream<POSSample> sampleStream = new WordTagSampleStream(lineStream);

			model = POSTaggerME.train("en", sampleStream, ModelType.MAXENT, null, null, 100, 5);
		} catch (IOException e) {
			// Failed to read or parse training data, training failed
			e.printStackTrace();
		} finally {
			if (dataIn != null) {
				try {
					dataIn.close();
				} catch (IOException e) {
					// Not an issue, training already finished.
					// The exception should be logged and investigated
					// if part of a production system.
					e.printStackTrace();
				}
			}
		}
		long nanoTimeEnd = System.nanoTime();
		System.out.println("Time spent " + (nanoTimeEnd - nanoTime) / NS_TO_MS + " milliseconds");
	}
}
