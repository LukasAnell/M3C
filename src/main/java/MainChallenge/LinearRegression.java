package MainChallenge;

public class LinearRegression {
    private String title;
    private double[][] independentVariables;
    private double[] dependentVariable;

    public LinearRegression(String t, double[][] iV, double[] dV) {
        title = t;
        independentVariables = iV;
        dependentVariable = dV;
    }

    public void setTitle(String t) {
        title = t;
    }

    public void setIndependentVariables(double[][] iV) {
        independentVariables = iV;
    }

    public void setDependentVariable(double[] dV) {
        dependentVariable = dV;
    }

    public double[] getDependentVariable() {
        return dependentVariable;
    }

    public double[][] getIndependentVariables() {
        return independentVariables;
    }

    public String getTitle() {
        return title;
    }

    public String toString() {
        return title;
    }
}
