package com.prystupa.generate.mapreduce;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SimulatorParametersInputFormat extends InputFormat<LongWritable, Text> {

    @Override
    public List<InputSplit> getSplits(JobContext jobContext) throws IOException {
        final int eventCount = Integer.parseInt(System.getProperty("event-count"));
        if (eventCount < 0) {
            throw new IOException("Invalid order events count: " + eventCount);
        }

        List<InputSplit> result = new ArrayList<>();

        DataOutputBuffer outputBuffer = new DataOutputBuffer();
        new Text("" + eventCount).write(outputBuffer);
        DataInputBuffer buffer = new DataInputBuffer();
        buffer.reset(outputBuffer.getData(), outputBuffer.getLength());

        ConfigurationSplit split = new ConfigurationSplit();
        split.readFields(buffer);
        result.add(split);
        return result;
    }

    @Override
    public RecordReader<LongWritable, Text> createRecordReader(InputSplit split, TaskAttemptContext taskContext) throws IOException, InterruptedException {
        final int count = 1;

        return new RecordReader<LongWritable, Text>() {
            private int records = 0;
            private final LongWritable key = new LongWritable(0L);
            private final Text value = new Text(String.join(":", split.getLocations()));

            @Override
            public void initialize(InputSplit split, TaskAttemptContext context) {
            }

            @Override
            public boolean nextKeyValue() throws IOException {
                return records++ < count;
            }

            @Override
            public LongWritable getCurrentKey() {
                return key;
            }

            @Override
            public Text getCurrentValue() {
                return value;
            }

            @Override
            public void close() throws IOException {
            }

            @Override
            public float getProgress() throws IOException {
                return records / ((float) count);
            }
        };
    }

    public static class ConfigurationSplit extends InputSplit implements Writable {
        private Text configuration = new Text();

        @Override
        public void write(DataOutput out) throws IOException {
            configuration.write(out);
        }

        @Override
        public void readFields(DataInput in) throws IOException {
            configuration.readFields(in);
        }

        @Override
        public long getLength() {
            return 0L;
        }

        @Override
        public String[] getLocations() {
            return new String[]{configuration.toString()};
        }
    }
}