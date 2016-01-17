package org.jsubercaze.opennlp.performance;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.DecimalFormat;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;

/**
 * Create a silver dataset using maxen pos tagger
 * 
 * Get the *.txt file made by concatenating gutenberg project files :
 * 
 * 
 * @author Julien
 *
 */
public class SilverDatasetBuilder {

	private static final double NS_TO_S = 1000000000d;
	private static final DecimalFormat df = new DecimalFormat("#.000");
	private static final int THROUGHPUT = 10000;
	private static final Object SEPARATOR = '_';
	private static final Object SPACE = ' ';
	private static final Object NEWLINE = '\n';
	private static final StringBuilder sb = new StringBuilder(10000);
	

	public static void main(String[] args) throws Throwable {
		long nanoTime = System.nanoTime();
		POSTaggerME tagger = null;
		try {
			tagger = new POSTaggerME(new POSModel(new File("en-pos-maxent.bin")));
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		// Stream file and write output
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File("gutenberg_tagged_small.txt")));
		BufferedReader reader = new BufferedReader(new FileReader(new File("gutenberg_small.txt")));
		String line;
		long t1 = nanoTime, t2;
		int i = 0;
		while ((line = reader.readLine()) != null) {
			if (i % THROUGHPUT == 0) {
				t2 = System.nanoTime();
				double timeSec = (double) (t2 - t1) / NS_TO_S;
				System.out.println("Throughput = " + df.format((THROUGHPUT / timeSec)) + " sentences/second");
				t1 = t2;
			}
			String[] split = line.split("\\s");
			String[] tag = tagger.tag(split);
			writer.write(buildstr(split, tag));
			i++;
		}
		writer.close();
		reader.close();
		long nanoTimeEnd = System.nanoTime();
		System.out
				.println("Total time " + (nanoTimeEnd - nanoTime) / 1000000 + " milliseconds for " + i + " sentences");
		double timeSec = (double) (nanoTimeEnd - nanoTime) / (NS_TO_S);
		System.out.println("Average throughput " + (i / timeSec) + " sentences/second");
	}

	private static String buildstr(String[] split, String[] tag) {
		sb.setLength(0);
		for (int i = 0; i < split.length; i++) {
			sb.append(split[i]);
			sb.append(SEPARATOR);
			sb.append(tag[i]);
			sb.append(SPACE);
		}
		sb.append(NEWLINE);
		return sb.toString();
	}
}
