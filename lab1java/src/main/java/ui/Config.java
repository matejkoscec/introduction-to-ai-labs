package ui;

public class Config {

    private String algorithm;

    private String statesFilePath;

    private String heuristicsFilePath;

    private boolean checkOptimistic;

    private boolean checkConsistent;

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public String getStatesFilePath() {
        return statesFilePath;
    }

    public void setStatesFilePath(String statesFilePath) {
        this.statesFilePath = statesFilePath;
    }

    public String getHeuristicsFilePath() {
        return heuristicsFilePath;
    }

    public void setHeuristicsFilePath(String heuristicsFilePath) {
        this.heuristicsFilePath = heuristicsFilePath;
    }

    public boolean isCheckOptimistic() {
        return checkOptimistic;
    }

    public void setCheckOptimistic(boolean checkOptimistic) {
        this.checkOptimistic = checkOptimistic;
    }

    public boolean isCheckConsistent() {
        return checkConsistent;
    }

    public void setCheckConsistent(boolean checkConsistent) {
        this.checkConsistent = checkConsistent;
    }
}
