package MainChallenge;

public class Guess {
    public double[] parameters;

    public Guess(double[] p) {
        parameters = p;
    }

    public double evaluateActual(double t) {
        return parameters[0] * Math.sin(parameters[1] * t + parameters[2]) + parameters[3];
    }

    public double evaluateDerivativeOne(double t) {
        return Math.sin(parameters[1] * t + parameters[2]);
    }
    public double evaluateDerivativeTwo(double t) {
        return parameters[0] * Math.cos(parameters[1] * t + parameters[2]) * t;
    }
    public double evaluateDerivativeThree(double t) {
        return parameters[0] * Math.cos(parameters[1] * t + parameters[2]);
    }
    public double evaluateDerivativeFour(double t) {
        return 1;
    }

    public String toString() {
        String out = "";
        for(int i = 0; i < parameters.length; i++) {
            out += parameters[i] + " ";
        }
        out += "\n";
        return out;
    }
}
