package news.classifier;
import java.io.File;
import java.util.logging.Filter;
import java.util.logging.Level;
import java.util.logging.Logger;
import weka.classifiers.Classifier;
import weka.core.converters.DatabaseLoader;
import weka.core.Instances;
import weka.filters.unsupervised.attribute.NominalToString;
import weka.filters.unsupervised.attribute.StringToWordVector;
import weka.filters.SimpleFilter;
import weka.classifiers.bayes.NaiveBayesMultinomial;

/**
 *
 * @author timothy.pratama
 */
public class Weka {
    //Class attributes
    private Instances dataSet; //Data Latih
    private static final String jdbcdriver = "com.mysql.jdbc.Driver"; //driver jdbc
    private Classifier cls; //Model yang digunakan
    
    //Setter and Getter
    public void setDataSet(Instances dataSet)
    {
        this.dataSet = dataSet;
    }

    public void setCls(Classifier cls) {
        this.cls = cls;
    }

    public Instances getDataSet() {
        return dataSet;
    }

    public Classifier getClassifier() {
        return cls;
    }

    //Methods
    //fetch dataSet from database
    public void openDB(String jdbc_url, String the_user, String the_password) {
        try {
            Class.forName(jdbcdriver);
            DatabaseLoader loader = new DatabaseLoader();
            loader.setSource(jdbc_url, the_user, the_password);
            loader.setQuery("select artikel.judul, artikel.full_text, kategori.label from artikel natural join artikel_kategori_verified natural join kategori");
            dataSet = loader.getDataSet();
            dataSet.setClassIndex(2);
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }
    
    //apply filter to data
    public void filter()
    {
        try {
            //Filter Nominal To String
            NominalToString nominalToString = new NominalToString();
            nominalToString.setAttributeIndexes("1-2");
            nominalToString.setInputFormat(dataSet);
            dataSet = SimpleFilter.useFilter(dataSet,nominalToString);
            
            //Filter String To Word Vector
            StringToWordVector stringToWordVector = new StringToWordVector();
            stringToWordVector.setAttributeIndices("1-2");
            stringToWordVector.setStopwords(new File("stopwords.txt"));     
            stringToWordVector.setLowerCaseTokens(true);
            stringToWordVector.setInputFormat(dataSet);
            dataSet = SimpleFilter.useFilter(dataSet,stringToWordVector);
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }
    
    //build model based on dataSet
    public void buildModel()
    {
        try {
            cls = new NaiveBayesMultinomial();
            cls.buildClassifier(dataSet);
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }
        
    //main class
    public static void main(String[] args) {
        Weka weka = new Weka();
        weka.openDB("jdbc:mysql://localhost/news_aggregator", "root", "");
        weka.filter();
        weka.buildModel();
        System.out.println(weka.getClassifier().toString());
    }
}
