/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package news.classifier;

import java.util.Random;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ConverterUtils;

/**
 *
 * @author Winson
 */
public class WekaLearner {
    public static final String[] CLASSIFIER_AVAILABLE = {"J48", "NaiveBayes", "IBk", "MultilayerPerceptron"};
    private Classifier wClassifier;
    private Instances wTrainingSet;
    private int wClassIndex;
    private Evaluation wEvaluation;

    public void setClassifier(String name, String[] options) throws Exception {
        wClassifier = AbstractClassifier.forName(name, options);
    }
    
    public void setClassifier(Classifier classifier){
        wClassifier = classifier;
    }

    public void setTrainingData(String fileLocation) throws Exception {
        wTrainingSet = ConverterUtils.DataSource.read(fileLocation);
        wClassIndex = wTrainingSet.numAttributes() - 1;
        wTrainingSet.setClassIndex(wClassIndex);
        wEvaluation = new Evaluation(wTrainingSet);
    }
    
    public void setTrainingData(Instances trainingData){
        wTrainingSet = trainingData;
    }

    public void setClassIndex(int index) {
        wClassIndex = index;
        wTrainingSet.setClassIndex(wClassIndex);
    }
    
    public void buildClassifier() throws Exception{
        wClassifier.buildClassifier(wTrainingSet);
    }

    public double classifyInstance(double[] instance) throws Exception {
        wClassifier.buildClassifier(wTrainingSet);
        Instances ins = new Instances(wTrainingSet,0);
        Instance row = new DenseInstance(1.0, instance);
        ins.add(row);
        return wClassifier.classifyInstance(ins.lastInstance());
    }

    public String fullTrainingEvaluation() throws Exception {
        wClassifier.buildClassifier(wTrainingSet);
        
        wEvaluation = new Evaluation(wTrainingSet);
        wEvaluation.evaluateModel(wClassifier, wTrainingSet);
        
        return wClassifier.toString()+wEvaluation.toSummaryString("\nHasil evaluasi dengan full-trainning:\n", false);
    }

    public String crossValidationEvaluation(int fold) throws Exception {
        wEvaluation = new Evaluation(wTrainingSet);
        wEvaluation.crossValidateModel(wClassifier, wTrainingSet, fold, new Random(1));
        
        return wClassifier.toString()+wEvaluation.toSummaryString("\nHasil evaluasi dengan cross-validation " + Integer.toString(fold) + "-fold:\n", false);
    }

    public void loadModel(String fileLocation) throws Exception {
        wClassifier = (Classifier) SerializationHelper.read(fileLocation);
    }

    public void saveModel(String fileLocation) throws Exception {
        SerializationHelper.write(fileLocation, wClassifier);
    }
    
    public final Instances getInstances(){
        return wTrainingSet;
    }
    
}
