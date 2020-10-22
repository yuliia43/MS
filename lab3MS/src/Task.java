import java.util.ArrayList;
import java.util.List;

public class Task {

    public static void main(String[] args) {
        //Model model = getSingleProcess();
        generateModel();
        getModelVerification();

    }

    private static void getModelVerification() {
        List<Model> models = new ArrayList<>();
        Model model = getModelFromScheme(2.0, 1.0, 1, 5,
                1.0, 2, 5,
                3.0, 3, 5,
                3.0, 1, 5);
        models.add(model);
        model = getModelFromScheme(1.0, 1.0, 1, 5,
                1.0, 2, 5,
                3.0, 3, 5,
                3.0, 1, 5);
        models.add(model);
        model = getModelFromScheme(2.0, 3.0, 1, 5,
                1.0, 1, 5,
                3.0, 3, 5,
                3.0, 1, 5);
        models.add(model);
        model = getModelFromScheme(2.0, 1.0, 1, 5,
                1.0, 2, 5,
                3.0, 1, 5,
                3.0, 1, 5);
        models.add(model);
        model = getModelFromScheme(2.0, 1.0, 2, 5,
                1.0, 2, 5,
                3.0, 1, 5,
                3.0, 1, 5);
        models.add(model);
        model = getModelFromScheme(2.0, 1.0, 1, 5,
                1.0, 2, 5,
                3.0, 3, 5,
                5.0, 1, 5);
        models.add(model);
        model = getModelFromScheme(2.0, 1.0, 1, 5,
                1.0, 2, 5,
                3.0, 3, 5,
                5.0, 1, 10);
        models.add(model);
        model = getModelFromScheme(2.0, 3.0, 1, 10,
                1.0, 2, 5,
                3.0, 3, 5,
                3.0, 1, 5);
        models.add(model);
        model = getModelFromScheme(2.0, 1.0, 1, 5,
                1.0, 1, 5,
                3.0, 1, 5,
                3.0, 1, 5);
        models.add(model);
        Model.verificationMode();
        model.outputVerificationHeader();
        for (Model m: models)
            m.simulate(1000.0);
    }

    private static void generateModel() {
        Model model = getModelFromScheme(2.0, 1.0, 1, 5,
                1.0, 2, 5,
                3.0, 3, 5,
                3.0, 1, 5);
        Model.normalMode();
        model.simulate(1000.0);
    }

    private static Model getSingleProcess() {
        Create c = new Create(2.0);
        Process p = new Process(1.0, 2);
        System.out.println("id0 = " + c.getId() + "   id1=" + p.getId());
        c.setNextElement(p);
        p.setMaxqueue(5);
        c.setName("CREATOR");
        p.setName("PROCESSOR");
        c.setDistribution("exp");
        p.setDistribution("exp");

        ArrayList<ModelElement> list = new ArrayList<>();
        list.add(c);
        list.add(p);
        return new Model(list);
    }

    public static Model getModelFromScheme(double creationDelay, double process1Delay,int process1ChannelsQuantity, int process1MaxQueue,
                                           double process2Delay,int process2ChannelsQuantity, int process2MaxQueue,
                                           double process3Delay,int process3ChannelsQuantity, int process3MaxQueue,
                                           double process4Delay,int process4ChannelsQuantity, int process4MaxQueue){
        Create c = new Create(creationDelay);
        Process process1 = new Process(process1Delay, process1ChannelsQuantity);
        Process process2 = new Process(process2Delay, process2ChannelsQuantity);
        Process process3 = new Process(process3Delay, process3ChannelsQuantity);
        Process process4 = new Process(process4Delay, process4ChannelsQuantity);
        process1.addNextProcess(process2);
        process1.addNextProcess(process3);
        process3.addNextProcess(process4);
        process4.addNextProcess(process1);
        process4.addNextProcess(null);
        //System.out.println("id0 = " + c.getId() + "   id1=" + p.getId());
        c.setNextElement(process1);
        process1.setMaxqueue(process1MaxQueue);
        process2.setMaxqueue(process2MaxQueue);
        process3.setMaxqueue(process3MaxQueue);
        process4.setMaxqueue(process4MaxQueue);
        c.setName("CREATOR");
        process1.setName("PROCESSOR1");
        process2.setName("PROCESSOR2");
        process3.setName("PROCESSOR3");
        process4.setName("PROCESSOR4");
        c.setDistribution("exp");
        process1.setDistribution("exp");
        process2.setDistribution("exp");
        process3.setDistribution("exp");
        process4.setDistribution("exp");

        ArrayList<ModelElement> list = new ArrayList<>();
        list.add(c);
        list.add(process1);
        list.add(process2);
        list.add(process3);
        list.add(process4);

        return new Model(list);
    }


}
