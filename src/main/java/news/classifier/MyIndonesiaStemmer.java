/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package news.classifier;


import org.apache.lucene.analysis.id.IndonesianStemmer;
/**
 *
 * @author Winson
 */
public class MyIndonesiaStemmer implements weka.core.stemmers.Stemmer{
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
