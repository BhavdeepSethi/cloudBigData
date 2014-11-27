package edu.columbia.cbd.models;

/**
 * Created by bhavdeepsethi on 11/26/14.
 */
public class Sentiment {


    private SentimentLabel sentimentLabel;
    private double score;


    public SentimentLabel getSentimentLabel() {
        return sentimentLabel;
    }

    public void setSentimentLabel(SentimentLabel sentimentLabel) {
        this.sentimentLabel = sentimentLabel;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public enum SentimentLabel {POSITIVE, NEGATIVE}

    @Override
    public String toString() {
        return "Sentiment{" +
                "sentimentLabel=" + sentimentLabel +
                ", score=" + score +
                '}';
    }
}
