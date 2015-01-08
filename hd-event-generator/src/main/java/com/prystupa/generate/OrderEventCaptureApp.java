package com.prystupa.generate;

import com.prystupa.generate.mapreduce.OrderEventCaptureMapper;
import com.prystupa.generate.mapreduce.SequenceFileAsOmsEventsInputFormat;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Counters;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.TaskCounter;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.NullOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class OrderEventCaptureApp extends Configured implements Tool {

    public static void main(String[] args) throws Exception {
        int exitCode = ToolRunner.run(new OrderEventCaptureApp(), args);
        System.exit(exitCode);
    }

    @Override
    public int run(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.printf("Usage: %s: [generic options] <input>\n", getClass().getSimpleName());
            ToolRunner.printGenericCommandUsage(System.err);
            return -1;
        }

        Job job = Job.getInstance(getConf());
        job.setJarByClass(getClass());

        String input = args[0];
        FileInputFormat.addInputPath(job, new Path(input));
        job.setInputFormatClass(SequenceFileAsOmsEventsInputFormat.class);
        job.setMapperClass(OrderEventCaptureMapper.class);

        job.setOutputFormatClass(NullOutputFormat.class);
        job.setNumReduceTasks(0);

        boolean succeeded = job.waitForCompletion(getConf().getBoolean("verbose", false));
        if (succeeded) {
            Counters counters = job.getCounters();
            System.out.printf("Captured %,d events\n", counters.findCounter(TaskCounter.MAP_INPUT_RECORDS).getValue());
        } else {
            System.err.println("The job failed");
        }

        return succeeded ? 0 : 1;
    }
}
