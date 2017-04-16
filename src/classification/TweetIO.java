package classification;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

public class TweetIO {
	/**
	 * Reads tweets from input file and returns them in a Vector of tweets.
	 * @param filename Input file location
	 * @return Vector of tweets
	 */
	public static Vector<String> readTweetsFromFile(String filename) {
		Vector<String> tweets = new Vector<String>();
		try {
			String line = "";
			BufferedReader br = new BufferedReader(new FileReader(filename));
			while ((line = br.readLine()) != null && line.length()!=0) {
				tweets.add(line);
			}
			br.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return tweets;
	}

	/**
	 * Writes given tweets to a file.
	 * @param tweets Vector of tweets to write
	 * @param filename Location of output file
	 */
	public static void writeTweetsToFile(Vector<String> tweets, String filename) {
		try (PrintWriter writer = new PrintWriter(filename, "UTF-8");) {
			for (String tweet : tweets) {
				writer.println(tweet);
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
