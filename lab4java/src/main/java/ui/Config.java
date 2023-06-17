package ui;

public final class Config {

    private String trainFileName;

    private String testFileName;

    private String nnArchitecture;

    private int popSize;

    private int elitism;

    private double chromosomeChangeProbability;

    private double gaussStdDev;

    private int iterations;

    private int threadPoolSize;

    public String getTrainFileName() {
        return trainFileName;
    }

    public void setTrainFileName(String trainFileName) {
        this.trainFileName = trainFileName;
    }

    public String getTestFileName() {
        return testFileName;
    }

    public void setTestFileName(String testFileName) {
        this.testFileName = testFileName;
    }

    public String getNnArchitecture() {
        return nnArchitecture;
    }

    public void setNnArchitecture(String nnArchitecture) {
        this.nnArchitecture = nnArchitecture;
    }

    public int getPopSize() {
        return popSize;
    }

    public void setPopSize(int popSize) {
        this.popSize = popSize;
    }

    public int getElitism() {
        return elitism;
    }

    public void setElitism(int elitism) {
        this.elitism = elitism;
    }

    public double getChromosomeChangeProbability() {
        return chromosomeChangeProbability;
    }

    public void setChromosomeChangeProbability(double chromosomeChangeProbability) {
        this.chromosomeChangeProbability = chromosomeChangeProbability;
    }

    public double getGaussStdDev() {
        return gaussStdDev;
    }

    public void setGaussStdDev(double gaussStdDev) {
        this.gaussStdDev = gaussStdDev;
    }

    public int getIterations() {
        return iterations;
    }

    public void setIterations(int iterations) {
        this.iterations = iterations;
    }

    public int getThreadPoolSize() {
        return threadPoolSize;
    }

    public void setThreadPoolSize(int threadPoolSize) {
        this.threadPoolSize = threadPoolSize;
    }

    @Override
    public String toString() {
        return "Config{" +
               "trainFileName='" + trainFileName + '\'' +
               ", testFileName='" + testFileName + '\'' +
               ", nnArchitecture='" + nnArchitecture + '\'' +
               ", popSize=" + popSize +
               ", elitism=" + elitism +
               ", chromosomeChangeProbability=" + chromosomeChangeProbability +
               ", gaussStdDev=" + gaussStdDev +
               ", iterations=" + iterations +
               '}';
    }
}
