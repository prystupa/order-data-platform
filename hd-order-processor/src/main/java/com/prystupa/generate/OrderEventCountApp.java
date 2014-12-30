package com.prystupa.generate;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Counters;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.TaskCounter;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileAsBinaryInputFormat;
import org.apache.hadoop.mapreduce.lib.output.NullOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrderEventCountApp extends Configured implements Tool {

    private static Logger logger = LoggerFactory.getLogger(OrderEventCountApp.class);

    public static void main(String[] args) throws Exception {
        int exitCode = ToolRunner.run(new OrderEventCountApp(), args);
        System.exit(exitCode);
    }

    @Override
    public int run(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.printf("Usage: %s: [generic options] <input>\n", getClass().getSimpleName());
            ToolRunner.printGenericCommandUsage(System.err);
            return -1;
        }

        logger.info("Starting the job");
        Job job = Job.getInstance(getConf());
        job.setJarByClass(getClass());
        FileInputFormat.addInputPath(job, new Path(args[0]));
        job.setInputFormatClass(SequenceFileAsBinaryInputFormat.class);
        job.setOutputFormatClass(NullOutputFormat.class);
        job.setNumReduceTasks(0);

        boolean succeeded = job.waitForCompletion(getConf().getBoolean("verbose", false));
        if (succeeded) {
            Counters counters = job.getCounters();
            System.out.printf("%-12s%,12d\n", "events", counters.findCounter(TaskCounter.MAP_INPUT_RECORDS).getValue());
        } else {
            System.err.println("The job failed");
        }

        return succeeded ? 0 : 1;
    }
}
