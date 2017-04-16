package feature_extraction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Normalizer {
	/**
	 * Indicates whether the given tweet contains a URL.
	 * @param tweet Input tweet
	 * @return Whether the input contains a URL
	 */
	public static boolean hasURL(String tweet) {
		return tweet.contains("http://") || tweet.contains("https://");
	}
	
	/**
	 * Replaces URLs by an Arabic token in the given tweet.
	 * @param tweet Input tweet
	 * @return Normalized tweet
	 */
	public static String normalizeURLs(String tweet) {
		String normalized = "";
		String[] patterns = {"http://", "https://"};
		
		for (int i=0; i<2; ++i) {
			int ind = tweet.indexOf(patterns[i]);
			int start = 0;
			while (ind != -1) {
				normalized = normalized + tweet.substring(start,ind) + "يوارالتوكن";
				int indSpace = tweet.indexOf(' ', ind);
				int indEnter = tweet.indexOf('\n', ind);
				if (indSpace==indEnter) {
					// if they're both == -1 -> end of tweet
					start = tweet.length();
					break;
				}
				else if	(indSpace < indEnter || indEnter==-1) {
					start = indSpace;
				}
				else {
					start = indEnter;
				}
				ind = tweet.indexOf(patterns[i], start);
			}

			normalized = normalized + tweet.substring(start);
			
			tweet = normalized;
			normalized = "";
		}
		return tweet;
	}

	/**
	 * Indicates whether the given tweet contains a user mention.
	 * @param tweet Input tweet
	 * @return Whether the input contains a user mention
	 */
	public static boolean hasMention(String tweet) {
		return tweet.contains("@");
	}
	
	/**
	 * Replaces user mentions by an Arabic token in the given tweet.
	 * @param tweet Input tweet
	 * @return Normalized tweet
	 */
	public static String normalizeMentions(String tweet) {
		String normalized = "";
		
		int ind = tweet.indexOf("@");
		int start = 0;
		while (ind != -1) {
			if (ind==0 || tweet.charAt(ind-1) == ' ' || tweet.charAt(ind-1) == '\n') {
				normalized = normalized + tweet.substring(start,ind) + "منشنتوكن";
				int indSpace = tweet.indexOf(' ', ind);
				int indEnter = tweet.indexOf('\n', ind);
				if (indSpace==indEnter) { // if they're both == -1 -> end of tweet
					return normalized;
				}
				else if	(indSpace < indEnter || indEnter==-1) {
					start = indSpace;
				}
				else {
					start = indEnter;
				}
				ind = tweet.indexOf("@", start);
			}
			else {
				if (ind != -1) {
					normalized = normalized + tweet.substring(start,ind);
				}
				start = ind;
				ind = tweet.indexOf("@", start+1);
			}
		}
		normalized = normalized + tweet.substring(start);
	
		return normalized;
	}
	
	/**
	 * Adds a space before and after Western Arabic numbers (0-9) and before and after Eastern Arabic numbers (٠-٩).
	 * @param tweet Input tweet
	 * @return Tweet with normalized numbers
	 */
	public static String normalizeNumbers(String tweet) {
	    String normalized = "";
		Pattern pattern = Pattern.compile("[0-9]+");
        Matcher matcher = pattern.matcher(tweet);
		
		int start = 0;
        while(matcher.find()) {
			int end = matcher.start();
            normalized = normalized + tweet.substring(start, end) + " " + matcher.group() + " ";
			start = matcher.end()+1;
        }
        if (start < tweet.length()) {
        	normalized += tweet.substring(start);
        }
        
        tweet = normalized;

	    normalized = "";
		pattern = Pattern.compile("[\u0660-\u0669]+");
        matcher = pattern.matcher(tweet);
		
		start = 0;
        while(matcher.find()) {
			int end = matcher.start();
            normalized = normalized + tweet.substring(start, end) + " " + matcher.group() + " ";
			start = matcher.end()+1;
        }
        if (start < tweet.length()) {
        	normalized += tweet.substring(start);
        }
        
        return normalized;
	}
	
	/**
	 * Adds a space before and after Latin words.
	 * @param tweet Input tweet
	 * @return Tweet with normalized Latin words
	 */
	public static String normalizeLatinLetters(String tweet) {
	    String normalized = "";

		Pattern pattern = Pattern.compile("[a-zA-Z]+");
        Matcher matcher = pattern.matcher(tweet);
		
		int start = 0;
        while(matcher.find()) {
			int end = matcher.start();
            normalized = normalized + tweet.substring(start, end) + " " + matcher.group() + " ";
			start = matcher.end()+1;
        }
        if (start < tweet.length()) {
        	normalized += tweet.substring(start);
        }
        return normalized;
	}
}
