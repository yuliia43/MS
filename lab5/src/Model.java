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
        }
        printResult();
    }

    public void simulate(double time, int numOfEvents) {
        boolean finishedCreation = false;
        while (tcurr < time) {
            tnext = Double.MAX_VALUE;
            for (ModelElement e : list) {
                if (e.getTnext() < tnext) {
                    tnext = e.getTnext();
                    event = e.getId();
                }
            }
            if (tnext == Double.MAX_VALUE)
                break;
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
                    if (e instanceof Create && finishedCreation)
                        break;
                    e.outAct();
                }
                if (e instanceof Create && e.getQuantity() >= numOfEvents){
                    e.setTnext(Double.MAX_VALUE);
                    finishedCreation = true;
                }
            }
        }
        verification = true;
        if (!verification)
            printComplexityInfo();
        else
            outputVerificationDetails();
    }

    private void printComplexityInfo() {
        System.out.println("Time spent: " + tcurr + " ms");
        System.out.println("Fully processed "
                + Process.getElementsProcessed()
                + " elements");
    }

    public void printInfo() {
        for (ModelElement e : list) {
            e.printInfo();
        }
    }

    public double theoretical_complexity(){
        double intensity = 1/list.get(0).getDelayMean();
        int numOfProcesses = list.size()-1;
        int operations = numOfProcesses;
        // if we have return
        Process lastProcess = (Process) list.get(numOfProcesses);
        if (lastProcess.getNextProcesses().size() != 0) {
            operations += (int)(lastProcess.getPriorities().get(0)*numOfProcesses);
        }
        return intensity*tcurr*operations;
    }

    public void outputVerificationDetails(){
        String row = "";
        row += String.format("|| %15d ", (list.size()-1));
        row += String.format("|| %30d ", Process.getElementsProcessed());
        row += String.format("|| %15.3f ||", tcurr);
        row += String.format("|| %20.3f ||", theoretical_complexity());
        System.out.println(row);

    }

    public static void outputVerificationHeader(){
        String row = "";
            row += String.format("|| %15s ", "PROCESSES COUNT");
        row += String.format("|| %30s ", "QUANTITY OF PROCESSED ELEMENTS");
        row += String.format("|| %15s ||", "TIME PROCESSING");
        row += String.format("|| %20s ||", "COMPLEXITY ESTIMATION");
        System.out.println(row);
    }

    public void printResult() {
        System.out.println("\n-------------RESULTS-------------");
        for (ModelElement e : list) {
            e.printResult();
            if (e instanceof Process) {
                Process p = (Process)e;
                System.out.println("theoretical mean length of queue = " +
                        p.getTheorQueue()
                        + "; actual mean length of queue = " +
                        p.getAvgQueue() / tcurr
                        + "; mistake = " +
                        (Math.abs(100*(p.getAvgQueue() / tcurr - p.getTheorQueue())/p.getTheorQueue())) + "%"
                        + "\ntheoretical mean loading of Process element = " +
                        p.getAvgLoading()/p.getAvgTime()
                        + "; actual mean loading of Process element = " +
                        p.getTheorLoading()
                        + "; mistake = " +
                        (Math.abs(100*(p.getAvgLoading()/p.getAvgTime() -  p.getTheorLoading())/ p.getTheorLoading())) + "%");
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
