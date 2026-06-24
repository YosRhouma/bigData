package tn.tekup.bigdata.sales;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SalesRevenueDriver extends Configured implements Tool {
    private static final Set<String> SUPPORTED_GROUPS = new HashSet<String>(Arrays.asList("city", "category", "product", "payment"));

    @Override
    public int run(String[] args) throws Exception {
        if (args.length != 3 || !SUPPORTED_GROUPS.contains(args[2])) {
            System.err.println("Usage: hadoop jar sales-batch-mapreduce.jar "
                    + SalesRevenueDriver.class.getName()
                    + " <input_hdfs> <output_hdfs> <city|category|product|payment>");
            return 2;
        }

        getConf().set(SalesRevenueMapper.GROUP_BY_CONF, args[2]);

        Job job = Job.getInstance(getConf(), "sales-revenue-by-" + args[2]);
        job.setJarByClass(SalesRevenueDriver.class);

        job.setMapperClass(SalesRevenueMapper.class);
        job.setCombinerClass(RevenueStatsReducer.class);
        job.setReducerClass(RevenueStatsReducer.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(RevenueStatsWritable.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(RevenueStatsWritable.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        return job.waitForCompletion(true) ? 0 : 1;
    }

    public static void main(String[] args) throws Exception {
        int exitCode = ToolRunner.run(new SalesRevenueDriver(), args);
        System.exit(exitCode);
    }
}
