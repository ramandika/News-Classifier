package news.classifier;
import weka.core.converters.DatabaseLoader;
import weka.core.Instances;

/**
 *
 * @author timothy.pratama
 */
public class Weka {
    //methods
    public void openDB(String jdbc_url, String the_user, String the_password)
    {
        try {
            Class.forName(jdbcdriver);
            DatabaseLoader loader = new DatabaseLoader();
            loader.setSource(jdbc_url, the_user, the_password);
            loader.setQuery("select artikel.judul, artikel.full_text, kategori.label from artikel natural join artikel_kategori_verified natural join kategori");
            dataSet = loader.getDataSet();
            System.out.println(dataSet.toString());
            
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }
    
    //attributes
    private Instances dataSet;
    private static final String jdbcdriver = "com.mysql.jdbc.Driver";
    
    //main class
    public static void main(String[] args) {
        Weka weka = new Weka();
        weka.openDB("jdbc:mysql://localhost/news_aggregator", "root", "");
    }
}
