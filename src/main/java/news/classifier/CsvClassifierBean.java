/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package news.classifier;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

/**
 *
 * @author ramandika
 */
@ManagedBean
@ViewScoped
public class CsvClassifierBean implements Serializable {

    private Part file;
    private NewsClassifier classifier;
    
    @PostConstruct
    public void init() {
        classifier = ClassifierBean.getClassifier();
    }
    
    public Part getFile() {
        return file;
    }
    
    public void setFile(Part file) {
        this.file = file;
    }
    
    public String processCsv() {
        try {
            File temp = uploadFile();
            
            File output = processInput(temp);
            
            sendProcessedCsv(output);
            
            temp.delete();
            output.delete();
        } catch (IOException e) {
            FacesMessage message = new FacesMessage("Caught exception: " + e.getMessage());
            
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
        
        return null;
    }
    
    private File uploadFile() throws IOException {
         HttpSession session = 
                (HttpSession) FacesContext.getCurrentInstance()
                                        .getExternalContext().getSession(false);
        
        File temp = null;
        if(session != null)
            temp = File.createTempFile(session.getId(), "csv");
        else
            temp = File.createTempFile("classifyTemp" + (int) Math.random(), "csv");
        
        InputStream input = null;
        FileOutputStream output = null;
        
        input = file.getInputStream();
        output = new FileOutputStream(temp);

        int read = 0;
        final byte[] bytes = new byte[1024];

        while((read = input.read(bytes)) != -1) {
            output.write(bytes, 0, read);
        }
        
        input.close();
            
        output.close();
        
        return temp;
    }
    
    private File processInput(File input) throws IOException {
        String filename = input.getName().replaceFirst("[.][^.]+$", "") + "output";
        
        File temp = File.createTempFile(filename, "csv");
        classifier.testCSV(input.getAbsolutePath(), temp.getAbsolutePath());
        
        return temp;
    }
    
    private void sendProcessedCsv(File file) throws IOException {
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        
        ec.responseReset();
        ec.setResponseContentType("text/csv");
        ec.setResponseContentLength((int) file.length());
        ec.setResponseHeader("Content-Disposition", 
                "attachment; filename=output.csv");
        
        OutputStream output = ec.getResponseOutputStream();
        FileInputStream in = new FileInputStream(file);
        final byte[] buffer = new byte[1024];
        
        int sent = 0;
        while((sent = in.read(buffer)) > 0) {
            output.write(buffer, 0, sent);
        }
        
        output.flush();
        in.close();
        output.close();
        
        fc.responseComplete();
    }
}
