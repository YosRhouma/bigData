package tn.tekup.bigdata.sales;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class RevenueStatsReducer extends Reducer<Text, RevenueStatsWritable, Text, RevenueStatsWritable> {
    @Override
    protected void reduce(Text key, Iterable<RevenueStatsWritable> values, Context context) throws IOException, InterruptedException {
        RevenueStatsWritable result = new RevenueStatsWritable();

        for (RevenueStatsWritable value : values) {
            result.add(value);
        }

        context.write(key, result);
    }
}
