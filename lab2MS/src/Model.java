public class Model {
    private double tnext;
    private double tcurr;
    private double t0, t1;
    private double delayCreate, delayProcess;
    private int numCreate, numProcess, failure;
    private int state, maxqueue, queue;
    private int nextEvent;
    float restTime;
    float timeInQueue;
    boolean isVerification ;

    public Model(double delay0, double delay1) {
        delayCreate = delay0;
        delayProcess = delay1;
        tnext = 0.0;
        tcurr = tnext;
        t0 = tcurr;
        t1 = Double.MAX_VALUE;
        maxqueue = 0;
        restTime = 0;
        isVerification = false;
    }

    public Model(double delay0, double delay1, boolean isVerification) {
        delayCreate = delay0;
        delayProcess = delay1;
        tnext = 0.0;
        tcurr = tnext;
        t0 = tcurr;
        t1 = Double.MAX_VALUE;
        maxqueue = 0;
        restTime = 0;
        this.isVerification = isVerification;
    }

    public Model(double delay0, double delay1, int maxQ) {
        delayCreate = delay0;
        delayProcess = delay1;
        tnext = 0.0;
        tcurr = tnext;
        t0 = tcurr;
        t1 = Double.MAX_VALUE;
        maxqueue = maxQ;
        restTime = 0;
        isVerification = false;
    }

    public Model(double delay0, double delay1, int maxQ, boolean isVerification) {
        delayCreate = delay0;
        delayProcess = delay1;
        tnext = 0.0;
        tcurr = tnext;
        t0 = tcurr;
        t1 = Double.MAX_VALUE;
        maxqueue = maxQ;
        restTime = 0;
        this.isVerification = isVerification;
    }

    public void simulate(double timeModeling) {
        while (tcurr < timeModeling) {
            tnext = t0;

            nextEvent = 0;

            if (t1 < tnext) {
                tnext = t1;
                nextEvent = 1;
            }
            tcurr = tnext;
            switch (nextEvent) {
                case 0:
                    event0();
                    break;
                case 1:
                    event1();

            }
            if (!isVerification)
                printInfo();
            timeInQueue+= queue*(t0-tcurr);
        }
        double meanLoading = 100-100*(restTime/timeModeling);

        if (isVerification)
            System.out.println(String
                    .format("| %19.1f | %20.1f | %14d   |||   %12.2f | %18.2f | %7d | %6d | %12.2f |",
                            delayCreate,delayProcess, maxqueue, meanLoading,
                            timeInQueue/timeModeling, numCreate, failure, (100.0*failure/numCreate)));
        else{
            System.out.println("___________________________________");
            System.out.println("INPUT DATA\n___________________________________");
            System.out.println("Mean creation delay is " + delayCreate + " ms");
            System.out.println("Mean processing time is " + delayProcess + " ms");
            System.out.println("Maximum queue size is " + maxqueue);
            System.out.println("___________________________________");
            System.out.println("OUTPUT DATA\n___________________________________");
            System.out.println("Mean loading is " + meanLoading+ "%");
            System.out.println("Mean time in queue is " + (timeInQueue/timeModeling) + "ms");
            printStatistic();
        }



    }

    public void printStatistic() {
        System.out.println(" numCreate= " + numCreate + " numProcess = " + numProcess + " failure = " + failure);
    }

    public void printInfo() {
        System.out.println(" t= " + tcurr + " state = " + state + " queue = " + queue);
    }

    public void event0() {
        t0 = tcurr + getDelayOfCreate();
        numCreate++;
        if (state == 0) {
            restTime+=t0-tcurr;
            state = 1;
            t1 = tcurr + getDelayOfProcess();
        } else {
            if (queue < maxqueue)
                queue++;
            else
                failure++;
        }


    }

    public void event1() {
        t1 = Double.MAX_VALUE;
        state = 0;
        if (queue > 0) {
            queue--;
            state = 1;
            t1 = tcurr + getDelayOfProcess();
        }
        numProcess++;
    }


    private double getDelayOfCreate() {
        return FunRand.Exp(delayCreate);
    }

    private double getDelayOfProcess() {
        return FunRand.Exp(delayProcess);
    }
}
