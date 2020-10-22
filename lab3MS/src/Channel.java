public class Channel extends Element {
    private Process parent;

    public Channel(double delay, Process parent) {
        super(delay);
        this.parent = parent;
        super.setTnext(Double.MAX_VALUE);
    }

    @Override
    public void inAct() {
        super.setState(1);
        double tNext = super.getTcurr() +super.getDelay();
        super.setTnext(tNext);
        if (parent.getTnext() > tNext)
            parent.setTnext(tNext);
    }

    @Override
    public void outAct() {
        super.outAct();
        super.setTnext(Double.MAX_VALUE);
        super.setState(0);
    }
}
