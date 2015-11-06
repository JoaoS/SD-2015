import java.io.Serializable;

/**
 * Created by joaogoncalves on 17/10/15.
 */
public class Alternative implements Serializable {

    private String description;
    private double divisor;

    public Alternative(String description, float divisor) {
        this.description = description;
        this.divisor = divisor;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getDivisor() {
        return divisor;
    }

    public void setDivisor(double divisor) {
        this.divisor = divisor;
    }
}
