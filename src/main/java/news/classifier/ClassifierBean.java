/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package news.classifier;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.faces.application.FacesMessage;
import javax.faces.bean.*;

import javax.faces.context.FacesContext;


/**
 *
 * @author ramandika
 */
@ManagedBean
@ApplicationScoped
public class ClassifierBean implements Serializable {
    private String judul;
    private String sugesti;
    Connection con;

    public String getSugesti() {
        return sugesti;
    }

    public void setSugesti(String sugesti) {
        this.sugesti = sugesti;
    }
    private String konten;
    private String selecteditem;

    public String getSelecteditem() {
        return selecteditem;
    }

    public void setSelecteditem(String selecteditem) {
        this.selecteditem = selecteditem;
    }

    private static final NewsClassifier classifier;
    
    static {
        classifier = new NewsClassifier();
        
        classifier.loadData();
        classifier.setClassifier();
        classifier.buildClassifier();
    }
    
    public static NewsClassifier getClassifier() {
        return classifier;
    }

    public String getJudul() {
        return judul;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public String getKonten() {
        return konten;
    }

    public void setKonten(String konten) {
        this.konten = konten;
    }
    
    public void classifyNews() throws SQLException {
        sugesti = classifier.readInput(judul, konten);
        
           if(sugesti!=null){
            if(!"None".equals(selecteditem)){
                if(selecteditem.equals(sugesti)){
                    System.out.println("Tebakian tepat");
                }
                else{
                    System.out.println("Connect to DB");
                          try {  
                              Class.forName("com.mysql.jdbc.Driver");  
                              con = DriverManager.getConnection("jdbc:mysql://localhost/news_aggregator","root","");  
                          } catch (ClassNotFoundException e) {  
                              System.out.println("Class Not Found");  
                          } catch (SQLException e) {  
                              System.out.println("Unable to connect");  
                          }    
                            try {
                                  System.out.println("Ready to do Query");
                                  String query = "INSERT INTO classify (judul, full_text, label) VALUES  (?,?,?)";
                                             
                                    Object values[] = {
                                        judul,
                                        konten,
                                        selecteditem
                                    };
                                    PreparedStatement ps = con.prepareStatement(query);
                                    for(int i = 0; i < values.length; i++) 
                                        ps.setObject(i+1, values[i]);
                                  int affectedRow = ps.executeUpdate();  
                                  if(affectedRow == 0)
                                      throw new SQLException("Data insertion failed");

                              } catch (SQLException e) {
                                  throw e;
                         }
                }
            }
        }
    }
}
