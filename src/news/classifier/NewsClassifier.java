package news.classifier;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayesMultinomial;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.DatabaseLoader;
import weka.core.tokenizers.AlphabeticTokenizer;
import weka.filters.Filter;
import weka.filters.MultiFilter;
import weka.filters.unsupervised.attribute.ClassAssigner;
import weka.filters.unsupervised.attribute.NominalToString;
import weka.filters.unsupervised.attribute.StringToWordVector;

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
            
            wekaEngine.setTrainingData(initialDataSet);
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }
    
    public void setClassifier(){       
        //Set Filter
        MultiFilter setOfFilter = new MultiFilter();
        Filter filters[] = new Filter[3];
        
        //Filter Nominal To String
        NominalToString nominalToString = new NominalToString();
        nominalToString.setAttributeIndexes("1-2");
        filters[0] = nominalToString;
        
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
        filters[1] = stringToWordVector;
              
        //ClassAssigner
        ClassAssigner classAssigner = new ClassAssigner();
        classAssigner.setClassIndex("first");
        filters[2] = classAssigner;
        
        setOfFilter.setFilters(filters);
        
        //Set NaiveBayesMultinomial
        FilteredClassifier filteredClassifier = new FilteredClassifier();
        filteredClassifier.setClassifier(new NaiveBayesMultinomial());
        filteredClassifier.setFilter(setOfFilter);
        
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
    
    public static void main(String args[]){
        NewsClassifier newsClassifier = new NewsClassifier();
        
        System.out.println("Connecting to database...");
        newsClassifier.openDB("jdbc:mysql://localhost/news_aggregator", "root", "");
        
        System.out.println("Set classifier...");
        newsClassifier.setClassifier();
        
        System.out.println("Validating with cross validation...");
        //newsClassifier.crossValidation();
        
        System.out.println("Klasifikasi 1 instance...");
        //newsClassifier.readInput("Perang di mana-mana", "Perang korban korban perang");
        newsClassifier.buildClassifier();
    }
    
}
