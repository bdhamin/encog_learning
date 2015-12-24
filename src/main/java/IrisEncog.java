import org.encog.app.analyst.AnalystFileFormat;
import org.encog.app.analyst.EncogAnalyst;
import org.encog.app.analyst.csv.normalize.AnalystNormalizeCSV;
import org.encog.app.analyst.csv.segregate.SegregateCSV;
import org.encog.app.analyst.csv.segregate.SegregateTargetPercent;
import org.encog.app.analyst.csv.shuffle.ShuffleCSV;
import org.encog.app.analyst.wizard.AnalystWizard;
import org.encog.engine.network.activation.ActivationLinear;
import org.encog.engine.network.activation.ActivationTANH;
import org.encog.mathutil.EncogMath;
import org.encog.mathutil.Equilateral;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.persist.EncogDirectoryPersistence;
import org.encog.util.csv.CSVFormat;
import org.encog.util.simple.EncogUtility;

import java.io.File;
import java.util.concurrent.CompletableFuture;

/**
 * Created by Bashar on 12/21/2015.
 */
public class IrisEncog {

    private static final String PATH = "";

    public IrisEncog(){

//        CompletableFuture<File> shuffeldData = CompletableFuture.completedFuture(FileUtil.loadFile(FileNames.IRIS_INPUT_DATA.getName(), PATH));
//        CompletableFuture<File> segrateData= shuffeldData.whenComplete(segregate(FileUtil.loadFile(FileNames.IRIS_SHUFFELD_INPUT_DATA.getName(), PATH)));

//        shuffle(FileUtil.loadFile(FileNames.IRIS_INPUT_DATA.getName(), PATH));
//        segregate(FileUtil.loadFile(FileNames.IRIS_SHUFFELD_INPUT_DATA.getName(), PATH));
//        normalize();
//        createNetwork(FileUtil.saveFile(FileNames.TRAINED_NETWORK_FILE.getName(), PATH));
//        trainNetwork();
        evaluate();
    }

    private static void shuffle(File filename){

        if(filename.exists()){
            ShuffleCSV shuffleCSV = new ShuffleCSV();
            shuffleCSV.analyze(filename,true, CSVFormat.ENGLISH);
            shuffleCSV.setProduceOutputHeaders(true);
            shuffleCSV.process(FileUtil.saveFile(FileNames.IRIS_SHUFFELD_INPUT_DATA.getName(), PATH));
        }
    }

    private static void segregate(File shuffeldFile) {
        SegregateCSV segregateCSV = new SegregateCSV();
        segregateCSV.getTargets().add(new SegregateTargetPercent(FileUtil.saveFile(FileNames.TRAINING_FILE.getName(), ""), 75));
        segregateCSV.getTargets().add(new SegregateTargetPercent(FileUtil.saveFile(FileNames.EVALUATION_FILE.getName(), ""), 25));
        segregateCSV.setProduceOutputHeaders(true);
        segregateCSV.analyze(shuffeldFile, true, CSVFormat.ENGLISH);
        segregateCSV.process();
    }

    private static void normalize() {
        EncogAnalyst analyst = new EncogAnalyst();

        AnalystWizard wizard = new AnalystWizard(analyst);
        wizard.wizard(FileUtil.loadFile(FileNames.TRAINING_FILE.getName(), PATH), true, AnalystFileFormat.DECPNT_COMMA);

        AnalystNormalizeCSV normalizeCSV = new AnalystNormalizeCSV();
        normalizeCSV.analyze(FileUtil.loadFile(FileNames.TRAINING_FILE.getName(), PATH),true, CSVFormat.ENGLISH, analyst);
        normalizeCSV.setProduceOutputHeaders(true);
        normalizeCSV.normalize(FileUtil.saveFile(FileNames.NORMALIZED_TRAINING_FILE.getName(), PATH));

        normalizeCSV.analyze(FileUtil.loadFile(FileNames.EVALUATION_FILE.getName(), PATH), true, CSVFormat.ENGLISH, analyst);
        normalizeCSV.normalize(FileUtil.saveFile(FileNames.NORMALIZED_EVALUATION_FILE.getName(), PATH));

        analyst.save(FileUtil.saveFile(FileNames.IRIS_ANALYST.getName(), PATH));
    }

    private static void createNetwork(File networkFile) {
        BasicNetwork network = new BasicNetwork();
        network.addLayer(new BasicLayer(new ActivationLinear(), true, 4));
        network.addLayer(new BasicLayer(new ActivationTANH(), true, 6));
        network.addLayer(new BasicLayer(new ActivationTANH(), false, 2));
        network.getStructure().finalizeStructure();
        network.reset();
        EncogDirectoryPersistence.saveObject(networkFile, network);
    }

    private static void trainNetwork() {

        BasicNetwork network = (BasicNetwork) EncogDirectoryPersistence.loadObject(FileUtil.loadFile(FileNames.TRAINED_NETWORK_FILE.getName(), PATH));
        MLDataSet dataSet = EncogUtility.loadCSV2Memory(FileUtil.loadFile(FileNames.NORMALIZED_TRAINING_FILE.getName(), PATH).getAbsolutePath(), network.getInputCount(), network.getOutputCount(), true, CSVFormat.ENGLISH, false);

        ResilientPropagation train = new ResilientPropagation(network, dataSet);
        int epoch = 1;
        do {
            train.iteration();
//            System.out.printf("Epoch : %d Error : %f", epoch, train.getError());
            System.out.println("Epoch: " + epoch +  "ErrorT: " + train.getError());
            epoch++;
        }while (train.getError() > 0.01);

        EncogDirectoryPersistence.saveObject(FileUtil.loadFile(FileNames.TRAINED_NETWORK_FILE.getName(), PATH), network);
    }

    private static void evaluate() {

        BasicNetwork network = (BasicNetwork) EncogDirectoryPersistence.loadObject(FileUtil.loadFile(FileNames.TRAINED_NETWORK_FILE.getName(), PATH));
        EncogAnalyst analyst = new EncogAnalyst();
        analyst.load(FileUtil.loadFile(FileNames.IRIS_ANALYST.getName(), PATH).getAbsoluteFile());
        MLDataSet dataSet = EncogUtility.loadCSV2Memory(FileUtil.loadFile(FileNames.NORMALIZED_EVALUATION_FILE.getName(), PATH).toString(), network.getInputCount(), network.getOutputCount(), true, CSVFormat.ENGLISH, false);

        int count = 0;
        int correctCount = 0;
        for(MLDataPair i : dataSet){
            count++;
            MLData output = network.compute(i.getInput());
            double sepal_l = analyst.getScript().getNormalize().getNormalizedFields().get(0).deNormalize(i.getInputArray()[0]);
            double sepal_w = analyst.getScript().getNormalize().getNormalizedFields().get(1).deNormalize(i.getInputArray()[1]);
            double petal_l = analyst.getScript().getNormalize().getNormalizedFields().get(2).deNormalize(i.getInputArray()[2]);
            double petal_w = analyst.getScript().getNormalize().getNormalizedFields().get(3).deNormalize(i.getInputArray()[3]);

            int classCount = analyst.getScript().getNormalize().getNormalizedFields().get(4).getClasses().size();
            double normalizationHigh = analyst.getScript().getNormalize().getNormalizedFields().get(4).getNormalizedHigh();
            double normalizationLow = analyst.getScript().getNormalize().getNormalizedFields().get(4).getNormalizedLow();

            Equilateral equilateral = new Equilateral(classCount, normalizationHigh, normalizationLow);
            int predictedClassInt = equilateral.decode(output.getData());
            String predictedClass = analyst.getScript().getNormalize().getNormalizedFields().get(4).getClasses().get(predictedClassInt).getName();
            int idealClassInt = equilateral.decode(i.getIdealArray());
            String idealClass = analyst.getScript().getNormalize().getNormalizedFields().get(4).getClasses().get(idealClassInt).getName();

            if(predictedClassInt == idealClassInt){
                correctCount++;
            }
            System.out.printf("Count: %d Properties [%f, %f, %f, %f], Ideal %s Predicted %s", count, sepal_l, sepal_w, petal_l, petal_w, idealClass, predictedClass);
        }

        System.out.println("Total test count: " + count);
        System.out.println("Total correct prediction count: " + correctCount);
        System.out.println("Success " + ((correctCount * 100.0) / count));
    }

}
