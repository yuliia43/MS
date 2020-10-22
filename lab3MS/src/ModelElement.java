public class ModelElement extends Element {

    private static int nextId = 0;
    private int id;
    private String name;

    public ModelElement() {
        id = nextId;
        nextId++;
        name = "element" + id;
    }

    public ModelElement(double delay) {
        super(delay);
        name = "anonymus";
        id = nextId;
        nextId++;
        name = "element" + id;
    }

    public ModelElement(String nameOfElement, double delay) {
        super(nameOfElement, delay);
        name = nameOfElement;
        id = nextId;
        nextId++;
        name = "element" + id;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void printResult() {
        System.out.println(getName() + "  quantity = " + super.getQuantity());
    }

    public void printInfo() {
        System.out.println(getName() + " state= " + super.getState() +
                " quantity = " + super.getQuantity() +
                " tnext= " + super.getTnext());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
