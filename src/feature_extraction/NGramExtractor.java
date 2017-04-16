package feature_extraction;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class NGramExtractor {
	/**
	 * Unigram features.
	 */
	public static final HashSet<String> unigrams = getUnigrams();

	/**
	 * Bigram features.
	 */
	public static final HashSet<String> bigrams = getBigrams();

	/**
	 * Trigram features.
	 */
	public static final HashSet<String> trigrams = getTrigrams();

	/**
	 * Fourgram features.
	 */
	public static final HashSet<String> fourgrams = getFourgrams();

	/**
	 * Character trigram features.
	 */
	public static final HashSet<String> charTrigrams = getCharTrigrams();

	/**
	 * Character fourgram features.
	 */
	public static final HashSet<String> charFourgrams = getCharFourgrams();

	/**
	 * Character fivegram features.
	 */
	public static final HashSet<String> charFivegrams = getCharFivegrams();

	/**
	 * Extracts n-grams from String.
	 * Taken from: http://stackoverflow.com/a/3656824/4477341
	 * @param n Number of words per n-gram
	 * @param str Input string
	 * @return List of n-grams
	 */
	public static List<String> ngrams(int n, String str) {
        List<String> ngrams = new ArrayList<String>();
        String[] words = str.split(" ");
        for (int i = 0; i < words.length - n + 1; i++)
            ngrams.add(concat(words, i, i+n));
        return ngrams;
    }

	/**
	 * Forms n-gram from n words.
	 * Taken from: http://stackoverflow.com/a/3656824/4477341
	 * @param words Array of all words
	 * @param start Index of first word from n-gram
	 * @param end Index of last word from n-gram
	 * @return N-gram
	 */
    public static String concat(String[] words, int start, int end) {
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < end; i++)
            sb.append((i > start ? " " : "") + words[i]);
        return sb.toString();
    }

	/**
	 * Extracts character n-grams from String.
	 * Inspired by: http://stackoverflow.com/a/3656824/4477341
	 * @param n Number of characters per n-gram
	 * @param str Input string
	 * @return List of character n-grams
	 */
	public static List<String> charNgrams(int n, String str) {
        List<String> charNgrams = new ArrayList<String>();
        for (int i = 0; i <= str.length() - n; ++i) {
            charNgrams.add(str.substring(i, i+n));
        }
        return charNgrams;
    }
	
	/**
	 * Retrieves unigram features.
	 * @return HashSet of unigram features
	 */
    public static HashSet<String> getUnigrams() {
    	HashSet<String> unigrams = new HashSet<String>();
    	try {
	    	BufferedReader br = new BufferedReader(new FileReader("Resources\\N-Grams\\unigrams.txt"));
			String line;
			while ((line = br.readLine()) != null && line.length()!=0) {
				unigrams.add(line);
			}
			br.close();
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    	}
    	return unigrams;
    }
	
	/**
	 * Retrieves bigram features.
	 * @return HashSet of bigram features
	 */
    public static HashSet<String> getBigrams() {
    	HashSet<String> bigrams = new HashSet<String>();
    	try {
	    	BufferedReader br = new BufferedReader(new FileReader("Resources\\N-Grams\\bigrams.txt"));
			String line;
			while ((line = br.readLine()) != null && line.length()!=0) {
				bigrams.add(line);
			}
			br.close();
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    	}
    	return bigrams;
    }
	
	/**
	 * Retrieves trigram features.
	 * @return HashSet of trigram features
	 */
    public static HashSet<String> getTrigrams() {
    	HashSet<String> trigrams = new HashSet<String>();
    	try {
	    	BufferedReader br = new BufferedReader(new FileReader("Resources\\N-Grams\\trigrams.txt"));
			String line;
			while ((line = br.readLine()) != null && line.length()!=0) {
				trigrams.add(line);
			}
			br.close();
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    	}
    	return trigrams;
    }
	
	/**
	 * Retrieves fourgram features.
	 * @return HashSet of fourgram features
	 */
    public static HashSet<String> getFourgrams() {
    	HashSet<String> fourgrams = new HashSet<String>();
    	try {
	    	BufferedReader br = new BufferedReader(new FileReader("Resources\\N-Grams\\fourgrams.txt"));
			String line;
			while ((line = br.readLine()) != null && line.length()!=0) {
				fourgrams.add(line);
			}
			br.close();
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    	}
    	return fourgrams;
    }
	
	/**
	 * Retrieves character trigram features.
	 * @return HashSet of character trigram features
	 */
    public static HashSet<String> getCharTrigrams() {
    	HashSet<String> charTrigrams = new HashSet<String>();
    	try {
	    	BufferedReader br = new BufferedReader(new FileReader("Resources\\N-Grams\\char_trigrams.txt"));
			String line;
			while ((line = br.readLine()) != null && line.length()!=0) {
				charTrigrams.add(line);
			}
			br.close();
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    	}
    	return charTrigrams;
    }
	
	/**
	 * Retrieves character fourgram features.
	 * @return HashSet of character fourgram features
	 */
    public static HashSet<String> getCharFourgrams() {
    	HashSet<String> charFourgrams = new HashSet<String>();
    	try {
	    	BufferedReader br = new BufferedReader(new FileReader("Resources\\N-Grams\\char_fourgrams.txt"));
			String line;
			while ((line = br.readLine()) != null && line.length()!=0) {
				charFourgrams.add(line);
			}
			br.close();
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    	}
    	return charFourgrams;
    }
	
	/**
	 * Retrieves character fivegram features.
	 * @return HashSet of character fivegram features
	 */
    public static HashSet<String> getCharFivegrams() {
    	HashSet<String> getCharFivegrams = new HashSet<String>();
    	try {
	    	BufferedReader br = new BufferedReader(new FileReader("Resources\\N-Grams\\char_fivegrams.txt"));
			String line;
			while ((line = br.readLine()) != null && line.length()!=0) {
				getCharFivegrams.add(line);
			}
			br.close();
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    	}
    	return getCharFivegrams;
    }
}
