package feature_extraction;
import java.util.List;
import java.util.Vector;

import com.vdurmont.emoji.EmojiParser;

public class EmojiExtractor extends EmojiParser {
	/**
	 * Extracts emojis from input and returns them in a vector.
	 * @param input Input tweet
	 * @return Vector of emojis in the input tweet
	 */
	public static Vector<String> getEmojis(String input) {
		List<UnicodeCandidate> em = EmojiParser.getUnicodeCandidates(input);
		Vector<String> emojis = new Vector<String>();
		for (UnicodeCandidate u : em) {
			emojis.addElement(u.getEmoji().getUnicode());
		}
		return emojis;
	}
	
	/**
	 * Replaces all emojis in the input String by an Arabic token.
	 * @param str Input tweet
	 * @return Normalized tweet
	 */
	public static String normalizeAllEmojis(String str) {
	    EmojiTransformer emojiTransformer = new EmojiTransformer() {
	      public String transform(UnicodeCandidate unicodeCandidate) {
	        return " ايموتيكونتوكن ";
	      }
	    };
	    return parseFromUnicode(str, emojiTransformer);
	}

}
