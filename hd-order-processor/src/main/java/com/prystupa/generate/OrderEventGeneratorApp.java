package com.prystupa.generate;

import com.prystupa.generate.mapreduce.OrderEventByOmsPartitioner;
import com.prystupa.generate.mapreduce.OrderEventGeneratorMapper;
import com.prystupa.generate.mapreduce.OrderEventWritable;
import com.prystupa.generate.mapreduce.SimulatorParametersInputFormat;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class OrderEventGeneratorApp extends Configured implements Tool {

    private static final int OMS_TOTAL = 40;

    public static void main(String[] args) throws Exception {
        int exitCode = ToolRunner.run(new OrderEventGeneratorApp(), args);
        System.exit(exitCode);
    }

    @Override
    public int run(String[] args) throws Exception {

        if (args.length != 1) {
            System.err.printf("Usage: %s: [generic options] <output>\n", getClass().getSimpleName());
            ToolRunner.printGenericCommandUsage(System.err);
            return -1;
        }

        Job job = Job.getInstance(getConf());
        job.setJarByClass(getClass());
        job.setInputFormatClass(SimulatorParametersInputFormat.class);
        job.setMapperClass(OrderEventGeneratorMapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(OrderEventWritable.class);
        job.setPartitionerClass(OrderEventByOmsPartitioner.class);
        job.setNumReduceTasks(OMS_TOTAL);
        FileOutputFormat.setOutputPath(job, new Path(args[0]));

        return job.waitForCompletion(false) ? 0 : 1;
    }
}
