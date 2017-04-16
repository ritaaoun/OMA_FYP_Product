package feature_extraction;
import java.util.HashMap;
import java.util.Vector;

public class Preprocessor {
	/**
	 * Performs all necessary preprocessing steps on given tweet, and a HashMap:
	 * From -> To
	 * negated -> Vector of Booleans that specifies whether each word is negated
	 * emojis -> Vector of emojis
	 * emoticons -> Vector of emoticons
	 * original -> Vector of words in original tweet
	 * hashtags -> Vector of parsed hashtags (each in a Vector)
	 * normalized -> Vector of words in normalized tweet
	 * urls -> Boolean that specifies whether the tweet contains a URL
	 * mentions -> Boolean that specifies whether the tweet contains a user mention
	 * ? -> Integer that specifies the number of interrogation points
	 * ! -> Integer that specifies the number of exclamation points
	 * ?! -> Integer that specifies the number of both interrogation and exclamation points
	 * elongated -> Integer that specifies the number of elongated words in the original tweet
	 * @param tweet Input tweet
	 * @return HashMap of all needed outputs
	 */
	public static HashMap<String,Object> preprocess(String tweet)
	{
		HashMap<String,Object> preprocessed = new HashMap<String,Object>();

		Vector<String> original = new Vector<String>();
		Vector<Vector<String>> hashtags = new Vector<Vector<String>>();
		Vector<String> normalized = new Vector<String>();
		
		Vector<String> emojis = EmojiExtractor.getEmojis(tweet);
		tweet = EmojiExtractor.normalizeAllEmojis(tweet);

		Vector<String> emoticons = EmoticonExtractor.getEmoticons(tweet);
		tweet = EmoticonExtractor.normalizeAllEmoticons(tweet);

		HashMap<String,Integer> punctuation = PunctuationExtractor.getPunctuation(tweet);
		
		boolean hasURLs = Normalizer.hasURL(tweet);
		
		if (hasURLs) {	
			tweet = Normalizer.normalizeURLs(tweet);
		}
		
		boolean hasMentions = Normalizer.hasMention(tweet);
		
		if (hasMentions) {
			tweet = Normalizer.normalizeMentions(tweet);
		}

		tweet = Normalizer.normalizeNumbers(tweet);
		tweet = Normalizer.normalizeLatinLetters(tweet);
		
		int numberOfElongatedWords = 0;
		
		int i = 0;
		int tweetLength = tweet.length();
		
		Vector<String> normalizedWithPunctuation = new Vector<String>();
		
		while (i < tweetLength)
		{
			String word = "";
			char currentChar = tweet.charAt(i);
			while(currentChar == ' ' || currentChar == '\n' || currentChar == '\t')
			{
				++i;
				if (i < tweetLength) {
					currentChar = tweet.charAt(i);
				}
				else {
					break;
				}
			}
			while(currentChar != ' ' && currentChar != '\n' && currentChar != '\t')
			{
				word = word + currentChar;
				++i;
				if (i < tweetLength) {
					currentChar = tweet.charAt(i);
				}
				else {
					break;
				}
			}
			
			if (word.length()!=0) {
				original.addElement(word);

				String normalizedWord = ElongationExtractor.removeElongation(word);
				
				if (!normalizedWord.equals(word)) {
					numberOfElongatedWords++;
					word = normalizedWord;
				}
				
				if (word.charAt(0) == '#')
				{
					word = PunctuationExtractor.replaceAllPunctuationsWithSpace(word, false);
					Vector <String> hashtag = extractWords(word);
					hashtags.addElement(hashtag);
					normalized.addElement("هاشتاغتوكن");
					normalizedWithPunctuation.addElement("هاشتاغتوكن");
				}
				else
				{
					String wordWithSomePunc = PunctuationExtractor.replaceAllPunctuationsWithSpace(word, true);
					Vector<String> words = extractWords(wordWithSomePunc);
					normalizedWithPunctuation.addAll(words);
					
					word = PunctuationExtractor.replaceAllPunctuationsWithSpace(word, false);
					words = extractWords(word);
					normalized.addAll(words);
				}
				
			}
		}
		
		preprocessed.put("negated", Negation.isNegated(normalizedWithPunctuation));
		preprocessed.put("emojis", emojis);
		preprocessed.put("original", original);
		preprocessed.put("hashtags", hashtags);
		preprocessed.put("normalized", normalized);
		preprocessed.put("urls", hasURLs);
		preprocessed.put("mentions", hasMentions);
		preprocessed.put("emoticons", emoticons);
		preprocessed.put("?", punctuation.get("?"));
		preprocessed.put("!", punctuation.get("!"));
		preprocessed.put("?!", punctuation.get("?!"));
		preprocessed.put("elongated", numberOfElongatedWords);
		return preprocessed;
	}
	
	/**
	 * Given a String, separates words and returns them in a Vector.
	 * @param word Input String
	 * @return Vector of words in input String
	 */
	private static Vector<String> extractWords(String word)
	{
		int ind = word.indexOf(' ');
		int start = 0;
		
		Vector<String> out = new Vector<String>();

		while (ind != -1) {
			String sub = word.substring(start,ind);
			if (sub.length() > 0) {
				out.addElement(sub);
			}
			start = ind + 1;
			ind = word.indexOf(' ', start);
		}
		if (start < word.length()) {
			out.addElement(word.substring(start));
		}
		return out;
	}
}
