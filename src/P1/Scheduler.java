package P1;

import java.util.List;

public interface Scheduler {
    double run(List<Job> jobs);
    String getName();
}
