import java.util.ArrayList;
import java.util.List;

public class Process extends ModelElement {
    public static int i = 1;

    private int queue, maxqueue, failure, maxReachedQueue;
    private double avgQueue;
    private double loading;
    private double maxLoading;
    private double avgLoading;
    private double avgTime;
    private static int elementsProcessed;
    private int iterationQuantity;
    private double theorQueue;
    private double theorLoading;
    private List<Double> priorities = new ArrayList<>();

    private List<Channel> channels = new ArrayList<>();
    private List<Process> nextProcesses = new ArrayList<>();

    public Process(double delay) {
        super(delay);
        queue = 0;
        maxqueue = Integer.MAX_VALUE;
        avgQueue = 0.0;
        maxReachedQueue = 0;
        channels.add(new Channel(delay, this));
        elementsProcessed = 0;
        iterationQuantity = 0;
    }

    public Process(double delay, int channelsQuantity){
        super(delay);
        queue = 0;
        maxqueue = Integer.MAX_VALUE;
        avgQueue = 0.0;
        maxReachedQueue = 0;
        if (channelsQuantity <= 0)
            channels.add(new Channel(delay, this));
        else{
            for (int i = 0; i < channelsQuantity; i++)
                channels.add(new Channel(delay, this));

        }
        elementsProcessed = 0;
        iterationQuantity = 0;
    }

    @Override
    public void inAct() {
        boolean processStarted = false;
        super.setState(1);
        for (Channel channel: channels) {
            if (channel.getState() == 0){
                channel.setTcurr(this.getTcurr());
                channel.inAct();
                double tNext = channel.getTnext();
                if (tNext < this.getTnext())
                {
                    this.setTnext(tNext);
                }
                processStarted = true;
                break;
            }
        }
        if (!processStarted){
            if (getQueue() < getMaxqueue()) {
                setQueue(getQueue() + 1);
            } else {
                failure++;
            }
        }


    }

    @Override
    public void outAct() {
        super.outAct();
        double tNext = Double.MAX_VALUE;
        for (Channel channel : channels) {
            if (channel.getTnext() == this.getTnext()) {
                channel.setTcurr(this.getTcurr());
                channel.outAct();
                if (!nextProcesses.isEmpty()){
                    Process nextProcess;
                    if (nextProcesses.size() == 1)
                        nextProcess = nextProcesses.get(0);
                    else
                        nextProcess = getNextProcess();
                    if (nextProcess != null){
                        nextProcess.setTcurr(this.getTnext()); //chngd
                        nextProcess.inAct();
                    }
                    else
                        elementsProcessed++;
                }
                else
                    elementsProcessed++;
                if (getQueue() > 0) {
                    setQueue(getQueue() - 1);
                    channel.setTcurr(this.getTcurr());
                    channel.inAct();
                }
            }
            double tNextCur = channel.getTnext();
            if (tNextCur < tNext)
                tNext = tNextCur;
        }
        super.setTnext(tNext);
        if (tNext == Double.MAX_VALUE)
            super.setState(0);


    }


    public int getFailure() {
        return failure;
    }

    public int getQueue() {
        return queue;
    }


    public void setQueue(int queue) {
        this.queue = queue;
    }


    public int getMaxqueue() {
        return maxqueue;
    }


    public void setMaxqueue(int maxqueue) {
        this.maxqueue = maxqueue;
    }

    @Override
    public void printInfo() {
        super.printInfo();
        System.out.println("failure = " + this.getFailure());
        if (channels.size() > 1)
            printChannelsInfo();
    }

    @Override
    public void doStatistics(double delta) {
        avgTime+=delta;
        if (super.getState() == 0){
            loading = 0.0;
        }
        else {
            loading += delta;
            avgLoading += delta;
            if (maxLoading < loading)
                maxLoading = loading;
        }

        avgQueue += queue * delta;
        iterationQuantity++;
        if (queue > maxReachedQueue)
            maxReachedQueue = queue;
    }

    public double getAvgQueue() {
        return avgQueue;
    }

    public double getMaxReachedQueue() { return maxReachedQueue; }

    public double getLoading() {
        return loading;
    }

    public double getMaxLoading() {
        return maxLoading;
    }

    public double getAvgLoading() {
        return avgLoading;
    }

    public double getAvgTime() {
        return avgTime;
    }

    public void printChannelsInfo(){
        int i =1;
        for (Channel channel: channels) {
            System.out.println("Channel " + i + "has state " + channel.getState());
            i++;
        }
    }

    @Override
    public void setDistribution(String distribution) {
        super.setDistribution(distribution);
        for (Channel channel: channels) {
            channel.setDistribution(distribution);
        }
    }

    public void addNextProcess(Process process, double priority){
        if (process != null)
            process.setTnext(Double.MAX_VALUE);
        nextProcesses.add(process);
        priorities.add(priority);
    }

    public Process getNextProcess(){
        double randNum = FunRand.Unif(0, 1);
        float cumulative_prev_priority = 0;
        for (int i = 0; i < nextProcesses.size(); i++) {
            if (randNum <= cumulative_prev_priority+priorities.get(i)
                    && randNum > cumulative_prev_priority)
                return nextProcesses.get(i);
            cumulative_prev_priority+=priorities.get(i);
        }
        return nextProcesses.get(0);
    }

    public static int getElementsProcessed() {
        return elementsProcessed;
    }

    public int getChannelsQuantity() {
        return channels.size();
    }

    public double getTheorQueue() {
        return theorQueue;
    }

    public void setTheorQueue(double theorQueue) {
        this.theorQueue = theorQueue;
    }

    public double getTheorLoading() {
        return theorLoading;
    }

    public void setTheorLoading(double theorLoading) {
        this.theorLoading = theorLoading;
    }

    public List<Double> getPriorities() {
        return priorities;
    }

    public List<Process> getNextProcesses() {
        return nextProcesses;
    }
}
