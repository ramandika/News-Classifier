/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package news.classifier;


import org.apache.lucene.analysis.id.IndonesianStemmer;

import weka.core.stemmers.Stemmer;
/**
 *
 * @author Winson
 */
public class MyIndonesiaStemmer implements Stemmer {
    /**
	 * 
	 */
	private static final long serialVersionUID = -4438017343300336280L;
	String stemmedWord;
    
    public MyIndonesiaStemmer(){
        stemmedWord = "";
    }

    @Override
    public String stem(String word) {
        IndonesianStemmer stemmer = new IndonesianStemmer();
        char unstemmedWord[] = word.toCharArray();
        int stemmedWordLength = stemmer.stem(unstemmedWord,unstemmedWord.length, true);
        stemmedWord = String.copyValueOf(unstemmedWord,0,stemmedWordLength);
        return stemmedWord;
    }

    @Override
    public String getRevision() {
        return stemmedWord;
    }
    
}
