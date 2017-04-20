package classification;

//import java.util.LinkedHashMap;
import java.util.Vector;

import feature_extraction.FeatureExtractor;

public class Main {
	/**
	 * Reads tweets from a file, generates their feature file then classifies them.
	 * Command-line arguments should be: "location_of_tweets location_of_output_file"
	 * @param args Input of main
	 */
	public static void main(String[] args) {
		try {
			String tweetLocation = args[0];
			
			Vector<String> tweets = TweetIO.readTweetsFromFile(tweetLocation);
			
			String featureFile = "Intermediate\\features.txt";
			
			FeatureExtractor.generateFeatureFile(featureFile, tweets, false);
			
			String modelFile = "Resources\\Model\\rbfc+2g-4.txt";
			
			String predictionFile = args[1];
			String [] input = {featureFile, modelFile, predictionFile};
			svm_predict.predict(input);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
