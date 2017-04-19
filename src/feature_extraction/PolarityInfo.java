package feature_extraction;
import java.util.Vector;

public class PolarityInfo
{
	/**
	 * Returns a Vector of word polarities from tweet where:
	 * - The first element is the number of positive words in the tweet
	 * - The second element is the number of negative words in the tweet
	 * Polarities are looked up in ArSenL from lemmatized tweet.
	 * If not found, they are looked up in AraSenTi then DAHL from normalized tweet.
	 * @param lemmatizedTweet Vector of lemmas
	 * @param normalizedTweet Vector of normalized words
	 * @param isNegated Vector of whether words are negated
	 * @return Vector of word polarities
	 */
	public static Vector<Integer> getWordsInfo(Vector<String> lemmatizedTweet, Vector<String> normalizedTweet, Vector<Boolean> isNegated)
	{
		int numberOfWords = lemmatizedTweet.size();
		Vector<Integer> out = new Vector<Integer>();
		int positive = 0;
		int negative = 0;
		for (int i=0; i<numberOfWords; ++i) {
			Boolean negated;
			try{
				negated = isNegated.elementAt(i);
			}
			catch(Exception e) {
				System.out.println("isNegated vector is not large enough. Please contact authors with output.");
				e.printStackTrace();
				negated=false;
				for(String ss : lemmatizedTweet){
					System.out.print(ss+" ");
				}
				System.out.println();
			}
			String word = lemmatizedTweet.elementAt(i);
			if (LexiconMapping.arsenl.containsKey(word)) {
				Polarity pol = getPolarity(LexiconMapping.arsenl.get(word));
				if (pol == Polarity.Positive) {
					if(negated == false){
						++positive;
					}else{
						++negative;
					}
				}
				else if (pol == Polarity.Negative) {
					if(negated == false){
						++negative;
					}else{
						++positive;
					}
				}
			}
			else
			{
				try{
					word = normalizedTweet.elementAt(i);
				}
				catch(Exception e) {					
					System.out.println("normalizedTweet vector is not large enough. Please contact authors with output.");
					e.printStackTrace();
					word = "";
					for(String ss : normalizedTweet){
						System.out.print(ss+" ");
					}
					System.out.println();
				}
				if (LexiconMapping.arasenti.containsKey(word))
				{
					double pol = LexiconMapping.arasenti.get(word);
					
					if (pol > 0)
					{
						if(!negated){
							++positive;
						}else{
							++negative;
						}
					}
					else if (pol < 0)
					{
						if(!negated){
							++negative;
						}else{
							++positive;
						}
					}
				}
				else if (LexiconMapping.dahl.containsKey(word))
				{
					double pol = LexiconMapping.dahl.get(word);
					if (pol > 0)
					{
						if(!negated){
							++positive;
						}else{
							++negative;
						}
					}
					else if (pol < 0)
					{
						if(!negated){
							++negative;
						}else{
							++positive;
						}
					}
				}
			}
		}
		out.addElement(positive);
		out.addElement(negative);
		
		return out;
	}

	/**
	 * Returns a Vector of emoticon/emoji polarities from Vector of emoticons and Vector of emojis where:
	 * - The first element is the number of positive emoticons/emojis in the tweet
	 * - The second element is the number of negative emoticons/emojis in the tweet
	 * Emoticon polarities are looked up in our own lists.
	 * Emoji polarities are looked up in Emoji Sentiment Ranking.
	 * @param emoticons Vector of emoticons in tweet
	 * @param emojis Vector of emojis in tweet
	 * @return Vector of emoticon/emoji polarities
	 */
	public static Vector<Integer> getEmoticonsInfo(Vector<String> emoticons, Vector<String> emojis) {
		Vector<Integer> out = new Vector<Integer>();
		int positives = 0;
		int negatives = 0;
		for (String e : emoticons) {
			Polarity pol = EmoticonExtractor.polarity(e);
			if (pol == Polarity.Positive) {
				++positives;
			}
			else if (pol == Polarity.Negative) {
				++negatives;
			}
		}
		
		for (String e : emojis) {
			if (LexiconMapping.emojisLexicon.containsKey(e)) {
				Polarity pol = getPolarity(LexiconMapping.emojisLexicon.get(e));
				if (pol == Polarity.Positive) {
					++positives;
				}
				else if (pol == Polarity.Negative) {
					++negatives;
				}
			}
		}
		out.addElement(positives);
		out.addElement(negatives);
		
		return out;
	}
	
	/**
	 * Given a vector of scores (like those of ArSenL and Emoji Sentiment Ranking), returns the polarity.
	 * The polarity is positive if it is more positive than negative.
	 * The polarity is negative if it is more negative than positive.
	 * @param scores Polarity vector
	 * @return Polarity that corresponds to the vector (null if word is as likely to be positive as it is to be negative)
	 */
	private static Polarity getPolarity(Vector<Double> scores) {
		double pos = scores.get(0);
		double neg = scores.get(1);
		if (pos > neg) { // if a word has a + score that is twice as much as its - score
			return Polarity.Positive;
		}
		else if (neg > pos) {
			return Polarity.Negative;
		}
		else {
			return null;
		}
	}
}
