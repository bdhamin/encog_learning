/**
 * Created by Bashar on 12/22/2015.
 */
public enum  FileNames {

    IRIS_SHUFFELD_INPUT_DATA("Iris_Shuffeld.csv"),
    IRIS_INPUT_DATA("IrisData.csv"),
    TRAINING_FILE("TrainingData.csv"),
    EVALUATION_FILE("EvaluationData.csv"),
    NORMALIZED_TRAINING_FILE("IrisTrainNorm.csv"),
    NORMALIZED_EVALUATION_FILE("IrisEvalNorm.csv"),
    IRIS_ANALYST("IrisAnalyst.ega"),
    TRAINED_NETWORK_FILE("IrisTrain.eg");


    private final String name;

    FileNames(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }






}
