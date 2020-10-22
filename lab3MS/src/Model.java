import java.util.ArrayList;

public class Model {

    private ArrayList<ModelElement> list = new ArrayList<>();
    double tnext, tcurr;
    int event;
    private static boolean verification = false;

    public Model(ArrayList<ModelElement> elements) {
        list = elements;
        tnext = 0.0;
        event = 0;
        tcurr = tnext;
    }


    public void simulate(double time) {
        while (tcurr < time) {
            tnext = Double.MAX_VALUE;
            for (ModelElement e : list) {
                if (e.getTnext() < tnext) {
                    tnext = e.getTnext();
                    event = e.getId();

                }
            }
            if (!verification)
                System.out.println("\nIt's time for event in " +
                        list.get(event).getName() +
                        ", time =   " + tnext);
            for (ModelElement e : list) {
                e.doStatistics(tnext - tcurr);
            }
            tcurr = tnext;
            for (ModelElement e : list) {
                e.setTcurr(tcurr);
            }
            list.stream().filter(e->e.getId() == event).findFirst().get().outAct();
            for (ModelElement e : list) {
                if (e.getTnext() == tcurr) {
                    e.outAct();
                }
            }
            if (!verification)
                printInfo();
        }
        if (!verification)
            printResult();
        else
            outputVerificationDetails();
    }

    public void printInfo() {
        for (ModelElement e : list) {
            e.printInfo();
        }
    }

    public void outputVerificationDetails(){
        String row = "";
        for (ModelElement element: list) {
            if (element instanceof Create)
                row += String.format("|| %10.3f ", element.getDelayMean());
            else if (element instanceof Process){
                Process process = (Process) element;
                row += String.format("|| %10.3f | %9d | %8d ", element.getDelayMean(), process.getMaxqueue(), process.getChannelsQuantity());
            }
        }
        row+= "|| ";
        for (ModelElement element: list) {
            if (element instanceof Process){
                Process process = (Process) element;
                row += String.format("|| %9.2f%s | %10.2fms | %10.2f | %8d ",
                        100*process.getAvgLoading()/process.getAvgTime(), "%", process.getMaxLoading(),
                        process.getAvgQueue()/tcurr, process.getFailure());
            }
        }
        row+= "|| ";
        System.out.println(row);

    }

    public void outputVerificationHeader(){
        String header = "";
        String subHeader = "";
        String headerMenu = "";
        int inputLength = 0;
        int outputLength = 0;
        for (ModelElement element: list) {
            if (element instanceof Create){
                subHeader  += String.format("|| %10s ", element.getName());
                headerMenu += String.format("|| %10s ", "Mean delay");
                inputLength+=14;
            }
            else if (element instanceof Process){
                subHeader  += String.format("|| %33s ", element.getName());
                headerMenu += String.format("|| %10s | %9s | %8s ", "Mean delay", "Max queue", "Channels");
                inputLength+=37;
            }
        }
        subHeader += "|| ";
        headerMenu+= "|| ";
        for (ModelElement element: list) {
            if (element instanceof Process){
                subHeader  += String.format("|| %49s ", element.getName());
                headerMenu += String.format("|| %10s | %12s | %10s | %8s ",
                        "Loading, %", "Max loading",
                        "Mean queue", "Failures");
                outputLength+=53;
            }
        }
        header = String.format("|| %" + (inputLength-4)+ "s || || %" + (outputLength-4) + "s ||", "INPUTS", "OUTPUTS");
        subHeader += "|| ";
        headerMenu+= "|| ";
        System.out.println(header);
        System.out.println(subHeader );
        System.out.println(headerMenu);
    }

    public void printResult() {
        System.out.println("\n-------------RESULTS-------------");
        for (ModelElement e : list) {
            e.printResult();
            if (e instanceof Process) {
                Process p = (Process) e;
                System.out.println("mean length of queue = " +
                        p.getAvgQueue() / tcurr
                        + "\nmax length of queue  = " + p.getMaxReachedQueue()
                        + "\nfailure probability  = " +
                        p.getFailure() / (double) p.getQuantity()
                        + "\nmean loading of Process element = " +
                        100*p.getAvgLoading()/p.getAvgTime() + "%"
                        + "\nmax loading of Process element = " +
                        p.getMaxLoading() + "ms");
            }
        }
        System.out.println("Fully processed "
                + Process.getElementsProcessed()
                + " elements");
    }

    public static void verificationMode() {
        verification = true;
    }

    public static void normalMode() {
        verification = false;
    }
}
