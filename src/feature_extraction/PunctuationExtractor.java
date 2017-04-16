package feature_extraction;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PunctuationExtractor {
	/**
	 * Returns counts of interrogation points, exclamation points and both interrogation and exclamation points.
	 * @param tweet Input tweet
	 * @return HashMap from "?", "!" and "?!" to their respective count
	 */
	public static HashMap<String,Integer> getPunctuation(String tweet) {
		HashMap<String,Integer> punc = new HashMap<String,Integer>();
		int nbOfInterrogation = 0;
		Pattern pattern = Pattern.compile("[?؟]");
		Matcher matcher = pattern.matcher(tweet);
		while(matcher.find()) {
		   ++nbOfInterrogation;
		}
		int nbOfExclamation = 0;
		pattern = Pattern.compile("[!]");
		matcher = pattern.matcher(tweet);
		while(matcher.find()) {
		   ++nbOfExclamation;
		}
		int nbOfBoth = 0;
		pattern = Pattern.compile("(\\?!)|(!\\?)|(؟!)|(!؟)");
		matcher = pattern.matcher(tweet);
		while(matcher.find()) {
		   ++nbOfBoth;
		}
		punc.put("?", nbOfInterrogation);
		punc.put("!", nbOfExclamation);
		punc.put("?!", nbOfBoth);
		return punc;
	}
	
	/**
	 * Replaces punctuation marks in input with a space.
	 * @param tweet Input tweet
	 * @param skipSome Whether to skip the following punctuation marks: ';', ':', '.', ',', '?', '!', '؟', '('
	 * @return Cleaned tweet
	 */
	public static String replaceAllPunctuationsWithSpace(String tweet, boolean skipSome) {
		Pattern pattern;
		if (skipSome)
		{
			pattern = Pattern.compile("[\u0621-\u064A\u0660-\u0669 0-9a-zA-Z;:.,?!؟(]+");
			Matcher matcher = pattern.matcher(tweet);
			String fixedTweet = "";
			while(matcher.find()) {
				String word = matcher.group();
				Pattern pattern2 = Pattern.compile("[;:.,?!؟(]");
				Matcher matcher2 = pattern2.matcher(word);
				int start = 0;
				while (matcher2.find()) {
					int end = matcher2.start()+1;
					fixedTweet = fixedTweet + word.substring(start, end) + " ";
					start = end;
				}
				if (start < word.length()) {
					fixedTweet = fixedTweet + word.substring(start) + " ";
				}
			}
			return fixedTweet;
		}
		else
		{
			pattern = Pattern.compile("[\u0621-\u064A\u0660-\u0669 0-9a-zA-Z]+");
			Matcher matcher = pattern.matcher(tweet);
			String fixedTweet = "";
			while(matcher.find()) {
				fixedTweet = fixedTweet + matcher.group() + " ";
			}
			return fixedTweet;
		}
		
	}
}
