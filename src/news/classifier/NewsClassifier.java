package news.classifier;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayesMultinomial;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.CSVLoader;
import weka.core.converters.DatabaseLoader;
import weka.core.tokenizers.AlphabeticTokenizer;
import weka.filters.Filter;
import weka.filters.MultiFilter;
import weka.filters.unsupervised.attribute.ClassAssigner;
import weka.filters.unsupervised.attribute.NominalToString;
import weka.filters.unsupervised.attribute.StringToWordVector;
import weka.classifiers.evaluation.output.prediction.CSV;

public class NewsClassifier {
    private static final String jdbcdriver = "com.mysql.jdbc.Driver"; 
    private WekaLearner wekaEngine;

    public NewsClassifier() {
        wekaEngine = new WekaLearner();
    }
    
    public void openDB(String jdbc_url, String the_user, String the_password) {
        try {
            Class.forName(jdbcdriver);
            DatabaseLoader loader = new DatabaseLoader();
            loader.setSource(jdbc_url, the_user, the_password);
            loader.setQuery("SELECT artikel.judul, artikel.full_text, "
                    + "kategori.label FROM artikel NATURAL JOIN artikel_kategori_verified NATURAL JOIN kategori");
            Instances initialDataSet = loader.getDataSet();
            initialDataSet.setClassIndex(2);
            
            //Filter Nominal To String
            NominalToString nominalToString = new NominalToString();
            nominalToString.setAttributeIndexes("1-2");
            nominalToString.setInputFormat(initialDataSet);
            initialDataSet = Filter.useFilter(initialDataSet, nominalToString);
            
            wekaEngine.setTrainingData(initialDataSet);
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }
    
    public void setClassifier(){       
        //Filter String To Word Vector
        StringToWordVector stringToWordVector = new StringToWordVector();
        stringToWordVector.setIDFTransform(true);
        stringToWordVector.setTFTransform(true);
        stringToWordVector.setAttributeIndices("1-2");
        stringToWordVector.setLowerCaseTokens(true);  
        stringToWordVector.setMinTermFreq(3);
        stringToWordVector.setOutputWordCounts(true);
        stringToWordVector.setStopwords(new File("stopwords2.txt"));
        stringToWordVector.setUseStoplist(true);
        stringToWordVector.setStemmer(new MyIndonesiaStemmer());
        stringToWordVector.setTokenizer(new AlphabeticTokenizer());
        stringToWordVector.setWordsToKeep(1000);
             
        //Set NaiveBayesMultinomial
        FilteredClassifier filteredClassifier = new FilteredClassifier();
        filteredClassifier.setClassifier(new NaiveBayesMultinomial());
        filteredClassifier.setFilter(stringToWordVector);
        
        Classifier classifier = filteredClassifier;
                        
        wekaEngine.setClassifier(classifier);
    }
    
    public void crossValidation(){
        try {
            System.out.println(wekaEngine.crossValidationEvaluation(10));
        } catch (Exception ex) {
            System.out.println("Fail to do cross-validation");
            Logger.getLogger(NewsClassifier.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void readInput(String title, String content)
    {
        double[] input = new double[wekaEngine.getInstances().numAttributes()];
        input[0] = wekaEngine.getInstances().attribute(0).addStringValue(title);
        input[1] = wekaEngine.getInstances().attribute(1).addStringValue(content);
        
        try {
            double result = wekaEngine.classifyInstance(input);
            System.out.println("Prediksi '"+wekaEngine.getInstances().classAttribute().name()+"' adalah: " 
                    + wekaEngine.getInstances().classAttribute().value((int) result));
            
        } catch (Exception ex) {
            System.out.println("Gagal klasifikasi");
            Logger.getLogger(Weka.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void buildClassifier(){
        try {
            wekaEngine.buildClassifier();
        } catch (Exception ex) {
            Logger.getLogger(NewsClassifier.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void testCSV(String filePathIn, String filePathOut){
        try {
            CSVLoader loader = new CSVLoader();
            loader.setSource(new File(filePathIn));
            Instances data = loader.getDataSet();
            CSV prediction = new CSV();
            wekaEngine.classifySuppliedTestSet(data, prediction);
            System.out.println(prediction.toString());
        } catch (IOException ex) {
            System.out.println("Klasifikasi dengan CSV gagal");
            System.out.println(ex);
            Logger.getLogger(NewsClassifier.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            System.out.println("Klasifikasi dengan CSV gagal");
            System.out.println(ex);
            Logger.getLogger(NewsClassifier.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void main(String args[]){
        NewsClassifier newsClassifier = new NewsClassifier();
        
        System.out.println("Connecting to database...");
        newsClassifier.openDB("jdbc:mysql://localhost/news_aggregator", "root", "");
        
        System.out.println("Set classifier...");
        newsClassifier.setClassifier();
        
        System.out.println("Validating with cross validation...");
        //newsClassifier.crossValidation();
        
        System.out.println("Klasifikasi 1 instance...");
        newsClassifier.readInput("Persipura kalahkan Pelita Bandung Raya 2-0", 
                "Bandung (ANTARA News) - Tim \\\"Mutiara Hitam\\\" Persipura Jayapura mengalahkan tuan rumah Pelita Bandung Raya (PBR) 2-0 pada lanjutan Liga Super Indonesia (LSI) 2013 di Stadion Si Jalak Harupat Soreang Kabupaten Bandung, Minggu.\\nGol kemenangan tim Jayapura itu diborong kapten tim Boas Salosa masing-masing melalui penalti menit ke-9 dan tembakan kaki kiri pada menit ke-58.\\nDengan kemenangan itu, Persipura kembali menempati puncak klasemen sementara Liga Super Indonesia 2013 menggeser Mitra Kukar. Persipura mengantongi total nilai 24, hasil 14 kali main dengan 10 kali menang dan empat seri  tanpa kalah.\\nPersipura juga menjadi tim paling produktif mencetak 31 gol dan hanya kemasukan empat gol. Selain itu, dua gol Boaz Salosa menjadikannya kokoh menjadi top scorer Liga Super Indonesia 2013 dengan 12 gol, meninggalkan beberapa pesaingnya di deretan pencetak gol terbanyak.\\nSebaliknya, bagi Pelita Bandung Raya, kekalahan itu membuatnya tetap berkutat di peringkat ke-16 klasemen dengan skor 11 hasil 14 kali berlaga, dua kali menang, lima seri dan tujuh kali kalah.\\nKekalahan itu sekaligus juga merupakan kekalahan kandang kedua karena pada laga kandang sebelumnya pada Maret lalu, tim Bandung itu kalah dalam laga derby lawan Persib Bandung.");
        
        System.out.println("Klasifikasi file CSV");
        
        
    }
    
}
