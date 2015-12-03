package fundStarter.DataServer;

import java.io.Serializable;

/**
 * Created by joaogoncalves on 17/10/15.
 */
public class Reward implements Serializable {
    private String description;
    private double minValue;

    public Reward(String description, double minValue) {
        this.description = description;
        this.minValue = minValue;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getMinValue() {
        return minValue;
    }

    public void setMinValue(long minValue) {
        this.minValue = minValue;
    }
}
