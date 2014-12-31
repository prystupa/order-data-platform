package com.prystupa.generate;

import com.prystupa.generate.mapreduce.*;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.compress.GzipCodec;
import org.apache.hadoop.mapreduce.Counters;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.TaskCounter;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrderEventGeneratorApp extends Configured implements Tool {

    private static Logger logger = LoggerFactory.getLogger(OrderEventGeneratorApp.class);
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

        logger.info("Starting the job");
        Job job = Job.getInstance(getConf());
        job.setJarByClass(getClass());

        job.setInputFormatClass(SimulatorParametersInputFormat.class);
        job.setMapperClass(OrderEventGeneratorMapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(OrderEventWritable.class);

        job.setPartitionerClass(OrderEventByOmsPartitioner.class);
        job.setNumReduceTasks(OMS_TOTAL);

        job.setOutputFormatClass(SequenceFileOutputFormat.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(OrderEventWritable.class);

        FileOutputFormat.setCompressOutput(job, true);
        FileOutputFormat.setOutputCompressorClass(job, GzipCodec.class);
        SequenceFileOutputFormat.setOutputCompressionType(job, SequenceFile.CompressionType.BLOCK);

        FileOutputFormat.setOutputPath(job, new Path(args[0]));

        Configuration configuration = job.getConfiguration();
        boolean succeeded = job.waitForCompletion(configuration.getBoolean("verbose", false));
        logger.info("Completed the job");

        if (succeeded) {
            Counters counters = job.getCounters();
            System.out.printf("%-12s%,12d\n", "systems", counters.findCounter(TaskCounter.REDUCE_INPUT_GROUPS).getValue());
            System.out.printf("%-12s%,12d\n", "events", counters.findCounter(TaskCounter.REDUCE_OUTPUT_RECORDS).getValue());
            System.out.printf("%-12s%,12d\n", "chains", counters.findCounter(EventCounter.CHAINS).getValue());
        } else {
            System.err.println("The job failed");
        }

        return succeeded ? 0 : 1;
    }
}
