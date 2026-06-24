package tn.tekup.bigdata.sales;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.util.Optional;

public class SalesRevenueMapper extends Mapper<LongWritable, Text, Text, RevenueStatsWritable> {
    public static final String GROUP_BY_CONF = "sales.group.by";

    private final Text outputKey = new Text();
    private String groupBy;

    enum Counters {
        IGNORED_LINES
    }

    @Override
    protected void setup(Context context) {
        groupBy = context.getConfiguration().get(GROUP_BY_CONF, "city");
    }

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        Optional<SaleRecord> sale = SaleRecord.parse(value.toString());
        if (!sale.isPresent()) {
            context.getCounter(Counters.IGNORED_LINES).increment(1);
            return;
        }

        SaleRecord record = sale.get();
        outputKey.set(record.groupValue(groupBy));
        context.write(outputKey, new RevenueStatsWritable(record.amount(), 1));
    }
}
