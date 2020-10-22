import java.util.ArrayList;
import java.util.List;

public class Task {

    public static void main(String[] args) {
        generateModel();
        System.out.println("\n\nTYPE1\n");
        verificateAlorithmComplexity("type1");
        System.out.println("\n\nTYPE2\n");
        verificateAlorithmComplexity("type2");
    }


    private static void generateModel() {
        Model.normalMode();
        Model model = getModelFromScheme(2.0, 0.6, 1,
                0.3, 1,
                0.4, 1,
                0.1, 2);
        Model.normalMode();
        model.simulate(10000.0);
    }

    public static Model getModelFromScheme(double creationDelay, double process1Delay,int process1ChannelsQuantity,
                                           double process2Delay,int process2ChannelsQuantity,
                                           double process3Delay,int process3ChannelsQuantity,
                                           double process4Delay,int process4ChannelsQuantity){
        Create c = new Create(creationDelay);
        Process process1 = new Process(process1Delay, process1ChannelsQuantity);
        Process process2 = new Process(process2Delay, process2ChannelsQuantity);
        Process process3 = new Process(process3Delay, process3ChannelsQuantity);
        Process process4 = new Process(process4Delay, process4ChannelsQuantity);
        process1.addNextProcess(process2, 0.15);
        process1.addNextProcess(process3, 0.13);
        process1.addNextProcess(process4, 0.3);
        process1.addNextProcess(null, 0.58);
        process2.addNextProcess(process1, 1);
        process3.addNextProcess(process1, 1);
        process4.addNextProcess(process1, 1);
        c.setNextElement(process1);
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
        process1.setTheorQueue(1.786);
        process2.setTheorQueue(0.003);
        process3.setTheorQueue(0.004);
        process4.setTheorQueue(0.00001);
        process1.setTheorLoading(0.714);
        process2.setTheorLoading(0.054);
        process3.setTheorLoading(0.062);
        process4.setTheorLoading(0.036);

        ArrayList<ModelElement> list = new ArrayList<>();
        list.add(c);
        list.add(process1);
        list.add(process2);
        list.add(process3);
        list.add(process4);

        return new Model(list);
    }


    public static void simulateModelWithNProcesses(double creationDelay, double processDelay, int channelsQuantity, int n){
        ArrayList<ModelElement> modelElements = new ArrayList<>();

        Create c = new Create(creationDelay);
        c.setName("CREATOR");
        c.setDistribution("exp");
        modelElements.add(c);
        Process prevProcess = new Process(processDelay, channelsQuantity);
        prevProcess.setName("PROCESS1");
        prevProcess.setDistribution("exp");
        modelElements.add(prevProcess);
        c.setNextElement(prevProcess);
        for(int i = 0; i < n-1; i++){
            Process process = new Process(processDelay, channelsQuantity);
            process.setName("PROCESS"+(i+2));
            process.setDistribution("exp");
            modelElements.add(process);
            prevProcess.addNextProcess(process, 1);
            prevProcess = process;
        }
        Model model = new Model(modelElements);
        model.simulate(Double.MAX_VALUE, n+1);
    }

    public static void simulateModelWithNProcessesAndReturn(double creationDelay, double processDelay, int channelsQuantity, int n){
        ArrayList<ModelElement> modelElements = new ArrayList<>();

        Create c = new Create(creationDelay);
        c.setName("CREATOR");
        c.setDistribution("exp");
        modelElements.add(c);
        Process prevProcess = new Process(processDelay, channelsQuantity);
        prevProcess.setName("PROCESS1");
        prevProcess.setDistribution("exp");
        modelElements.add(prevProcess);
        c.setNextElement(prevProcess);
        for(int i = 0; i < n-1; i++){
            Process process = new Process(processDelay, channelsQuantity);
            process.setName("PROCESS"+(i+2));
            process.setDistribution("exp");
            modelElements.add(process);
            prevProcess.addNextProcess(process, 1);
            prevProcess = process;
        }
        prevProcess.addNextProcess((Process) modelElements.get(1), 0.3);
        prevProcess.addNextProcess(null, 0.7);
        Model model = new Model(modelElements);
        model.simulate(Double.MAX_VALUE, n+1);
    }

    public static void verificateAlorithmComplexity(String type){
        Model.verificationMode();
        Model.outputVerificationHeader();
            for (int i = 1; i < 100; i+=5){
                if (type == "type1")
                    simulateModelWithNProcesses(2.0, 1.0, 1, i);
                else if (type == "type2")
                    simulateModelWithNProcessesAndReturn(2.0, 1.0, 1, i);
            }
    }



}
