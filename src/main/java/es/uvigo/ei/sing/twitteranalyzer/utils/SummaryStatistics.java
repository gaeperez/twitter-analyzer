package es.uvigo.ei.sing.twitteranalyzer.utils;

import lombok.Getter;
import lombok.Setter;

import java.util.DoubleSummaryStatistics;

@Getter
@Setter
public class SummaryStatistics {
    private long count;
    private double sum;
    private double average;
    private double min;
    private double max;

    public SummaryStatistics(DoubleSummaryStatistics summary) {
        this.count = summary.getCount();
        this.sum = summary.getSum();
        this.average = summary.getAverage();
        this.min = summary.getMin();
        this.max = summary.getMax();
    }
}
