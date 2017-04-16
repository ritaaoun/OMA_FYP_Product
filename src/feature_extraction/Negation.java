package feature_extraction;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Negation {
	/**
	 * Given a sentence in the form of a Vector of words, it returns a Vector that specifies whether each word is negated.
	 * Every word between a negation term and the next punctuation is negated.
	 * @param words Sentence in the form of a Vector of words
	 * @return Vector of Boolean values that specify whether each word is negated
	 */
	public static Vector<Boolean> isNegated(Vector<String> words){
		Vector<Boolean> negate = new Vector<Boolean>();
		Vector<String> list = LexiconMapping.negationWords;
		
		int index = 0;
		while(index < words.size()){
			//if negation term 
			if(list.contains(words.elementAt(index))){
				negate.addElement(false);
				index++;
				
				while(index < words.size()){
					Pattern pattern = Pattern.compile("[;:.,?!¿(]+");
					String word = words.elementAt(index);
					Matcher matcher = pattern.matcher(word);
					index++;
					if(matcher.find()){
						//if not only punctuation
						Pattern pattern2 = Pattern.compile("[\u0621-\u064A\u0660-\u0669 0-9a-zA-Z]");
						Matcher matcher2 = pattern2.matcher(word);
						if(matcher2.find())
						{
							negate.addElement(true);
						}
						break;
					}
					else {
						negate.addElement(true);
					}
				}
			}else{
				negate.addElement(false);
				index++;
			}
		}		
		return negate;
	}
}
