package news.classifier;
import java.io.File;
import java.util.Random;
import javax.swing.text.AttributeSet;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.converters.DatabaseLoader;
import weka.core.Instances;
import weka.filters.unsupervised.attribute.NominalToString;
import weka.filters.unsupervised.attribute.StringToWordVector;
import weka.filters.SimpleFilter;
import weka.classifiers.bayes.NaiveBayesMultinomial;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Utils;

/**
 *
 * @author timothy.pratama
 */
public class Weka {
    
    //Class attributes
    private Instances dataSet; //Data latih
    private Instances testSet; //Data test
    private Classifier cls; //Model yang digunakan
    private static final String jdbcdriver = "com.mysql.jdbc.Driver"; //driver jdbc
    
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
            stringToWordVector.setIDFTransform(false);
            stringToWordVector.setTFTransform(false);
            stringToWordVector.setAttributeIndices("1-2");
            stringToWordVector.setLowerCaseTokens(true);
            stringToWordVector.setStopwords(new File("stopwords.txt"));    
            stringToWordVector.setUseStoplist(true);
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
    
    //evaluates model using cross-validation
    public void crossValidation(int fold)
    {
        try {
            Evaluation eval = new Evaluation (dataSet);
            eval.crossValidateModel(cls, dataSet, fold, new Random(1));
            System.out.println(eval.toSummaryString());
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }
    public void readInput(String title, String content)
    {
        //Create instances attribute
        Attribute judul = new Attribute("judul", (FastVector)null);
        Attribute konten = new Attribute("full_text", (FastVector)null);
        Attribute label = dataSet.attribute(2);
        FastVector attributes = new FastVector();
        attributes.addElement(judul);
        attributes.addElement(konten);
        attributes.addElement(label);
        
        //Create new instances
        testSet = new Instances("testSet", attributes, 0);
        testSet.attribute(0).addStringValue(title);
        testSet.attribute(1).addStringValue(content);
        
        //Adding data
        double[] values = new double[testSet.numAttributes()];
        values[0] = testSet.attribute(0).addStringValue(title);
        values[1] = testSet.attribute(1).addStringValue(content);
        Instance instance = new Instance(1.0, values);
        testSet.add(instance);
        System.out.println(testSet);
    }
    
    //main class
    public static void main(String[] args) {
        Weka weka = new Weka();
        weka.openDB("jdbc:mysql://localhost/news_aggregator", "root", "");
        //weka.filter();
        //weka.buildModel();
        //weka.crossValidation(10);
        weka.readInput("judul", "konten");
    }
}
