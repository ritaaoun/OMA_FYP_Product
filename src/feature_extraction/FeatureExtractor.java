package feature_extraction;
import java.io.PrintWriter;
import java.util.HashMap;
//import java.util.LinkedHashMap;
import java.util.List;
import java.util.Vector;


public class FeatureExtractor {
	/**
	 * Extracts features from input data and generates a feature file for LibSVM classification.
	 * @param outputFilename Location of feature file
	 * @param data LinkedHashMap from tweets to their label. If the label is unknown, any value can be used.
	 * @param onlyNgrams Whether only n-gram features are desired, or all features
	 */
	@SuppressWarnings("unchecked")
	public static void generateFeatureFile(String outputFilename, Vector<String> data, boolean onlyNgrams) {
		try {
			Vector<HashMap<String, Object>> preprocessed = new Vector<HashMap<String, Object>>();
			Vector<Integer> labels = new Vector<Integer>();
			
			int nbOfTweets = data.size();
			
			for (String tweet : data)
			{
				preprocessed.add(Preprocessor.preprocess(tweet));
				labels.add(0);
			}
				
			String input = "Intermediate\\in.xml";
			String output = "Intermediate\\out.xml";
				
			XMLParser.inputXML(preprocessed,input,!onlyNgrams);
			Madamira.runMadamira(input, output);
			HashMap<String, Object> outputXML = XMLParser.outputXML(output, preprocessed,!onlyNgrams);
				
			Vector<Vector<String>> lemmatizedTweets = (Vector<Vector<String>>)(outputXML.get("lemmas"));
			Vector<HashMap<String, Integer>> posTags = (Vector<HashMap<String, Integer>>)(outputXML.get("pos"));
			Vector<Vector<Vector<String>>> hashtags = ((Vector<Vector<Vector<String>>>)(outputXML.get("hashtags")));
				
			Vector<Vector<Integer>> features = new Vector<Vector<Integer>>();
			for(int i = 0; i < nbOfTweets; i++){
				if (onlyNgrams) {
					String tweet = "";
					for (String word : lemmatizedTweets.elementAt(i)) {
						tweet += word + " ";
					}
					Vector<Integer> wordAndCharNgrams = FeatureExtractor.outputNgramFeatures(tweet);
					wordAndCharNgrams.addAll(FeatureExtractor.outputCharNgramFeatures(tweet));
					features.addElement(wordAndCharNgrams);
				}
				else {
					features.addElement(FeatureExtractor.outputFeatures(preprocessed.get(i), 
						posTags.get(i), lemmatizedTweets.get(i), hashtags.get(i)));
				}
			}
				
			PrintWriter writer = new PrintWriter(outputFilename, "UTF-8");
			
			int nbOfFeatures = features.elementAt(0).size();

			for(int i=0; i<nbOfTweets; ++i){
				Vector<Integer> tweetFeatures = features.elementAt(i);
				writer.print("0 ");
				
				for(int j=0; j<nbOfFeatures; ++j){
					Integer f = tweetFeatures.elementAt(j);
					if (f>0) {
						writer.print((j+1)+":"+f+" ");
					}
				}
				writer.println();
			}
			writer.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Given the result of preprocessing and Madamira, finds and outputs all features of a given tweet in a Vector of Integers.
	 * @param preprocessed Output of preprocessing
	 * @param posTags POS tags as parsed from Madamira output
	 * @param lemmatizedTweet Lemmatized tweet as parsed from Madamira output
	 * @param hashtags Lemmatized hashtags as parsed from Madamira output
	 * @return Vector of features
	 */
	public static Vector<Integer> outputFeatures(HashMap<String, Object> preprocessed, 
		HashMap<String,Integer> posTags, Vector<String> lemmatizedTweet, Vector<Vector<String>> hashtags){
		
		Vector<Integer> features = new Vector<Integer>();
		
		//positive and negative words
		@SuppressWarnings("unchecked")
		Vector<Integer> wordPol = PolarityInfo.getWordsInfo(lemmatizedTweet,
				(Vector<String>)(preprocessed.get("normalized")), (Vector<Boolean>)(preprocessed.get("negated"))); 
				
		features.add(wordPol.elementAt(0));
		features.add(wordPol.elementAt(1));

		//positive and negative hashtags
		int posHashtags = 0;
		int negHashtags = 0;
		for(int r= 0; r < hashtags.size(); r++){
			Vector<Boolean> hneg = new Vector<Boolean>();
			for(int u = 0; u < hashtags.get(r).size(); u++){
				hneg.addElement(false);
			}
			@SuppressWarnings("unchecked")
			Vector<Integer> hpol = PolarityInfo.getWordsInfo(hashtags.get(r), ((Vector<Vector<String>>)(preprocessed.get("hashtags"))).get(r), hneg);
 			if(hpol.elementAt(1) > hpol.elementAt(0)){
 				//more negative words 
 				negHashtags++;
 			}else{
 				posHashtags++;
 			}
		}
		features.add(posHashtags);
		features.add(negHashtags);
		
		// number of !, ?, ?!, elongated
		features.add((Integer)(preprocessed.get("!")));
		features.add((Integer)(preprocessed.get("?")));
		features.add((Integer)(preprocessed.get("?!")));
		features.add((Integer)(preprocessed.get("elongated")));
		
		//number of negated contexts
		@SuppressWarnings("unchecked")
		Vector<Boolean> isNegated = (Vector<Boolean>)(preprocessed.get("negated"));
		int switches = 0; 
		for(int i = 1; i < isNegated.size(); i++){
			if(isNegated.elementAt(i) && !isNegated.elementAt(i-1)){
				switches++;
			}
		}
		features.add(switches);
		
		//number of positive and negative emoticons
		@SuppressWarnings("unchecked")
		Vector<Integer> emoPolarity = PolarityInfo.getEmoticonsInfo((Vector<String>)(preprocessed.get("emoticons")), ((Vector<String>)preprocessed.get("emojis")));
		features.add(emoPolarity.elementAt(0));
		features.add(emoPolarity.elementAt(1));
		
		//presence of emoticons
		@SuppressWarnings("unchecked")
		int emoCount = ((Vector<String>)(preprocessed.get("emoticons"))).size()+((Vector<String>)(preprocessed.get("emojis"))).size();
		if(emoCount == 0){
			features.add(0);
		}else{
			features.add(1);
		}
		
		//mentions
		boolean hasMentions = (boolean)(preprocessed.get("mentions"));
		if(hasMentions){
			features.add(1);
		}else{
			features.add(0);
		}
		
		//urls
		boolean hasURL = (boolean)(preprocessed.get("urls"));
		if(hasURL){
			features.add(1);
		}else{
			features.add(0);
		}
		
		//POS list in MADAMIRA manual page 31
		Integer nouns = 0;
		Integer numberwords = 0;
		Integer propernouns = 0;
		Integer adjectives = 0;
		Integer adverbs = 0;
		Integer pronouns = 0;
		Integer verbs = 0;
		Integer particles = 0;
		Integer prepositions = 0;
		Integer abbreviations = 0;
		Integer punctuation = 0;
		Integer conjunctions = 0;
		Integer interjections = 0;
		Integer digitalnumbers = 0;
		Integer latin = 0;
		
		if(posTags.containsKey("noun")){
			nouns = (Integer)(posTags.get("noun"));
		}
		
		if(posTags.containsKey("noun_num")){
			numberwords += (Integer)(posTags.get("noun_num"));
		}
		
		if(posTags.containsKey("noun_quant")){
			numberwords += (Integer)(posTags.get("noun_quant"));
		}
		if(posTags.containsKey("noun_prop")){
			propernouns += (Integer)(posTags.get("noun_prop"));
		}
		if(posTags.containsKey("adj")){
			adjectives += (Integer)(posTags.get("adj"));
		}
		if(posTags.containsKey("adj_comp")){
			adjectives += (Integer)(posTags.get("adj_comp"));
		}
		if(posTags.containsKey("adj_num")){
			adjectives += (Integer)(posTags.get("adj_num"));
		}
		if(posTags.containsKey("adv")){
			adverbs += (Integer)(posTags.get("adv"));
		}
		if(posTags.containsKey("adv_interrog")){
			adverbs += (Integer)(posTags.get("adv_interrog"));
		}
		if(posTags.containsKey("adv_rel")){
			adverbs += (Integer)(posTags.get("adv_rel"));
		}
		if(posTags.containsKey("pron")){
			pronouns += (Integer)(posTags.get("pron"));
		}
		if(posTags.containsKey("pron_dem")){
			pronouns += (Integer)(posTags.get("pron_dem"));
		}
		if(posTags.containsKey("pron_exclam")){
			pronouns += (Integer)(posTags.get("pron_exclam"));
		}
		if(posTags.containsKey("pron_interrog")){
			pronouns += (Integer)(posTags.get("pron_interrog"));
		}
		if(posTags.containsKey("pron_rel")){
			pronouns += (Integer)(posTags.get("pron_rel"));
		}
		if(posTags.containsKey("verb")){
			verbs += (Integer)(posTags.get("verb"));
		}
		if(posTags.containsKey("verb_pseudo")){
			verbs += (Integer)(posTags.get("verb_pseudo"));
		}
		if(posTags.containsKey("part")){
			particles += (Integer)(posTags.get("part"));
		}
		if(posTags.containsKey("part_dem")){
			particles += (Integer)(posTags.get("part_dem"));
		}
		if(posTags.containsKey("part_det")){
			particles += (Integer)(posTags.get("part_det"));
		}
		if(posTags.containsKey("part_focus")){
			particles += (Integer)(posTags.get("part_focus"));
		}
		if(posTags.containsKey("part_fut")){
			particles += (Integer)(posTags.get("part_fut"));
		}
		if(posTags.containsKey("part_interrog")){
			particles += (Integer)(posTags.get("part_interrog"));
		}
		if(posTags.containsKey("part_neg")){
			particles += (Integer)(posTags.get("part_neg"));
		}
		if(posTags.containsKey("part_restrict")){
			particles += (Integer)(posTags.get("part_restrict"));
		}
		if(posTags.containsKey("part_verb")){
			particles += (Integer)(posTags.get("part_verb"));
		}
		if(posTags.containsKey("part_voc")){
			particles += (Integer)(posTags.get("part_voc"));
		}
		if(posTags.containsKey("prep")){
			prepositions += (Integer)(posTags.get("prep"));
		}
		if(posTags.containsKey("abbrev")){
			abbreviations += (Integer)(posTags.get("abbrev"));
		}
		if(posTags.containsKey("punc")){
			punctuation += (Integer)(posTags.get("punc"));
		}
		if(posTags.containsKey("conj")){
			conjunctions += (Integer)(posTags.get("conj"));
		}
		if(posTags.containsKey("conj_sub")){
			conjunctions += (Integer)(posTags.get("conj_sub"));
		}
		if(posTags.containsKey("interj")){
			interjections = (Integer)(posTags.get("interj"));
		}
		if(posTags.containsKey("digit")){
			digitalnumbers = (Integer)(posTags.get("digit"));
		}
		if(posTags.containsKey("latin")){
			latin = (Integer)(posTags.get("latin"));
		}
		
		
		features.add(nouns);
		features.add(numberwords);
		features.add(propernouns);
		features.add(adjectives);
		features.add(adverbs);
		features.add(pronouns);
		features.add(verbs);
		features.add(particles);
		features.add(prepositions);
		features.add(abbreviations);
		features.add(punctuation);
		features.add(conjunctions);
		features.add(interjections);
		features.add(digitalnumbers);
		features.add(latin);
		
		String tweet = "";
		for (String word : lemmatizedTweet) {
			tweet += word + " ";
		}
		
		features.addAll(outputNgramFeatures(tweet));
		features.addAll(outputCharNgramFeatures(tweet));
		
		return features;
	}
	
	/**
	 * Given a tweet, outputs its n-gram features: 1-grams, 2-grams, 3-grams then 4-grams
	 * @param tweet Input tweet
	 * @return Vector of features
	 */
	public static Vector<Integer> outputNgramFeatures(String tweet) {
		Vector<Integer> features = new Vector<Integer>();
		
		List<String> unigrams = NGramExtractor.ngrams(1, tweet);
		for (String ngram : NGramExtractor.unigrams) {
			if (unigrams.contains(ngram)) {
				features.add(1);
			}
			else {
				features.add(0);
			}
		}
		
		List<String> bigrams = NGramExtractor.ngrams(2, tweet);
		for (String ngram : NGramExtractor.bigrams) {
			if (bigrams.contains(ngram)) {
				features.add(1);
			}
			else {
				features.add(0);
			}
		}
		
		List<String> trigrams = NGramExtractor.ngrams(3, tweet);
		for (String ngram : NGramExtractor.trigrams) {
			if (trigrams.contains(ngram)) {
				features.add(1);
			}
			else {
				features.add(0);
			}
		}
		
		List<String> fourgrams = NGramExtractor.ngrams(4, tweet);
		for (String ngram : NGramExtractor.fourgrams) {
			if (fourgrams.contains(ngram)) {
				features.add(1);
			}
			else {
				features.add(0);
			}
		}
		return features;
	}
	
	/**
	 * Given a tweet, outputs its character n-gram features: 3-grams, 4-grams, then 5-grams
	 * @param tweet Input tweet
	 * @return Vector of features
	 */
	public static Vector<Integer> outputCharNgramFeatures(String tweet) {
		Vector<Integer> features = new Vector<Integer>();
		
		List<String> trigrams = NGramExtractor.charNgrams(3, tweet);
		for (String ngram : NGramExtractor.charTrigrams) {
			if (trigrams.contains(ngram)) {
				features.add(1);
			}
			else {
				features.add(0);
			}
		}
		
		List<String> fourgrams = NGramExtractor.charNgrams(4, tweet);
		for (String ngram : NGramExtractor.charFourgrams) {
			if (fourgrams.contains(ngram)) {
				features.add(1);
			}
			else {
				features.add(0);
			}
		}
		
		List<String> fivegrams = NGramExtractor.charNgrams(5, tweet);
		for (String ngram : NGramExtractor.charFivegrams) {
			if (fivegrams.contains(ngram)) {
				features.add(1);
			}
			else {
				features.add(0);
			}
		}
		return features;
	}
}
