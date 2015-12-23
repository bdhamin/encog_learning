import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;

/**
 * Created by Bashar on 12/19/2015.
 */
public class EncogXOR {


    public static void main(String[] args){
        new EncogXOR();
    }



    public EncogXOR(){

        double[][] XOR_INPUT = new double[][]{
                {0.0, 0.0},
                {1.0, 0.0},
                {0.0, 1.0},
                {1.0, 1.0}
        };

        double[][] XOR_IDEAL = new double[][]{
                {0.0},
                {1.0},
                {1.0},
                {0.0}
        };

        BasicMLDataSet trainingSet = new BasicMLDataSet(XOR_INPUT, XOR_IDEAL);
        BasicNetwork network = createNetwork();
        ResilientPropagation train = new ResilientPropagation(network, trainingSet);

        int epoch = 1;
        do {
            train.iteration();
            epoch++;
            System.out.println("Iteration No : " + epoch + ", Error: " + train.getError());
        }while (train.getError() > 0.001);


        for(MLDataPair i : trainingSet){
            MLData compute = network.compute(i.getInput());
            System.out.println("Input : " + i.getInputArray()[0] + ", " + i.getInputArray()[1] + " Ideal: " + i.getIdealArray()[0] + " Actual: " + (compute.getData(0)));
        }

    }


    private static BasicNetwork createNetwork(){
        BasicNetwork network = new BasicNetwork();
        network.addLayer(new BasicLayer(null, true, 2));
        network.addLayer(new BasicLayer(new ActivationSigmoid(), true, 2));
        network.addLayer(new BasicLayer(new ActivationSigmoid(), false, 1));
        network.getStructure().finalizeStructure();
        network.reset();
        return network;
    }


}
