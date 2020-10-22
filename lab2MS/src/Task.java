import java.util.ArrayList;
import java.util.List;

public class Task {

    public static void main(String[] args) {
        verification();
        //simulation();

    }

    private static void simulation() {
        Model model = new Model(2,1,5);
        model.simulate(1000);
    }

    private static void verification() {
        List<Model> models = new ArrayList<>();
        Model model = new Model(2,1,5, true);
        models.add(model);
        model = new Model(1,1,5,  true);
        models.add(model);
        model = new Model(2,2,5, true);
        models.add(model);
        model = new Model(2,0.5,5, true);
        models.add(model);
        model = new Model(2,1,0, true);
        models.add(model);
        model = new Model(2,1,5, true);
        models.add(model);
        model = new Model(3,1,5, true);
        models.add(model);
        model = new Model(2,1.5,5, true);
        models.add(model);
        model = new Model(2,1,10, true);
        models.add(model);
        model = new Model(1,1,100, true);
        models.add(model);
        System.out.println(String.format("| %61s ||| %69s |",
                "INPUTS","OUTPUTS"));
        System.out.println(String.format("| %s | %s | %s   |||   %s | %s | %s | %s | %s |",
                "Mean creation delay","Mean processing time", "Max queue size", "Mean loading",
                "Mean time in queue", "Created", "Failed", "% of failure"));
        for (Model mod: models) {
            mod.simulate(1000);
        }
    }
}


