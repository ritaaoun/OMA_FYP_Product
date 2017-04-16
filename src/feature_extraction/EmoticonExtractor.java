package feature_extraction;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmoticonExtractor {
	/**
	 * Private array of positive emoticons.
	 */
	private static final String [] positive = {":-)", ":)", ":-]", ":]", ":-3", ":3", ":->", ":>", "8-)", "8)", ":-}",
			":}", ":o)", ":c)", ":^)", "=]", "=)", ":-D", ":D", "8-D", "8D", "x-D", "X-D", "xD", "X-D", "XD", "=D",
			"=3", "B^D", ":'-)", ":')", ":-*", ":*", ":Ã—", ";-)", ";)", ";-]", ";]", ";^)", ";D", ":-P", ":P",
			"X-P", "XP", "x-p", "xp", ":-p", ":p", ":-b", ":b", "d:", "=p", "<3", "^_^", "^.^", "^^"};
	
	/**
	 * Private array of neutral emoticons.
	 */
	private static final String [] neutral = {":-O", ":O", ":-o", ":o", ":-0", "8-0"};
	
	/**
	 * Private array of negative emoticons.
	 */
	private static final String[] negative = {":-(", ":(", ":-c", ":c", ":-<", ":<", ":-[", ":[", ":{", ":@", ":'-(", ";'(",
			"D-':", "D:", "D8", "D;", "D=", "DX", ":-/", ":\\", "=/", "=\\", ":S", ":-|", ":|", "</3", "<\3", "-_-",
			"-.-", ">_<", ">.<", "~_~", "_|_", "-|-", "=_="};
	
	/**
	 * Public hashset of positive emoticons.
	 */
	public static final Set<String> positiveEmoticons = new HashSet<String>(Arrays.asList(positive));

	/**
	 * Public hashset of negative emoticons.
	 */
	public static final Set<String> negativeEmoticons = new HashSet<String>(Arrays.asList(negative));

	
	/**
	 * Public hashset of neutral emoticons.
	 */
	public static final Set<String> neutralEmoticons = new HashSet<String>(Arrays.asList(neutral));

	
	/**
	 * Public hashset of all emoticons.
	 */
	public static final Set<String> allEmoticons = getAllEmoticons();
			
	/**
	 * Given an emoticon, returns its polarity.
	 * @param emoticon Given emoticon
	 * @return Polarity of input emoticon
	 */
	public static Polarity polarity(String emoticon) {
		if (positiveEmoticons.contains(emoticon)) {
			return Polarity.Positive;
		}
		else if (neutralEmoticons.contains(emoticon)){
			return Polarity.Neutral;
		}
		else {
			return Polarity.Negative;
		}
	}
	
	/**
	 * Extracts emoticons from given tweet and returns them in a vector.
	 * @param tweet Input tweet
	 * @return Vector of emoticons in the tweet
	 */
	public static Vector<String> getEmoticons(String tweet) {		
		Vector<String> emoticons = new Vector<String>();
		StringBuilder regex = new StringBuilder();
		regex.append("(");
		Iterator<String> iterator = allEmoticons.iterator();
		regex.append(Pattern.quote(iterator.next()));
		while (iterator.hasNext())
		{
		    regex.append('|');
		    regex.append(Pattern.quote(iterator.next()));
		}
		regex.append(")");
		Pattern pattern = Pattern.compile(regex.toString());
		Matcher matcher = pattern.matcher(tweet);
		while(matcher.find()) {
		   emoticons.addElement(matcher.group());  
		}
		return emoticons;
	}

	/**
	 * Replaces all emoticons in the input String by an Arabic token.
	 * @param str Input tweet
	 * @return Normalized tweet
	 */
	public static String normalizeAllEmoticons(String tweet) {
		String fixedTweet = "";
		StringBuilder regex = new StringBuilder();
		regex.append("(");
		Iterator<String> iterator = allEmoticons.iterator();
		regex.append(Pattern.quote(iterator.next()));
		while (iterator.hasNext())
		{
		    regex.append('|');
		    regex.append(Pattern.quote(iterator.next()));
		}
		regex.append(")");
		Pattern pattern = Pattern.compile(regex.toString());
		Matcher matcher = pattern.matcher(tweet);
		int end = 0;

		while(matcher.find()) {
		    fixedTweet = fixedTweet + tweet.substring(end, matcher.start()) + " ايموتيكونتوكن ";
		    end = matcher.end();
		}
		fixedTweet = fixedTweet + tweet.substring(end);

		return fixedTweet;
	}
	
	/**
	 * Joins emoticons in one hashset.
	 * @return Union of emoticons of the three polarities
	 */
	private static Set<String> getAllEmoticons() {
		Set<String> emoticons = new HashSet<String>();
		emoticons.addAll(positiveEmoticons);
		emoticons.addAll(negativeEmoticons);
		emoticons.addAll(neutralEmoticons);
		return emoticons;
	}
}