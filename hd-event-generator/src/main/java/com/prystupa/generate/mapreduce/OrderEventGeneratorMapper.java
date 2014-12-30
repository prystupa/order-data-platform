package com.prystupa.generate.mapreduce;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.util.Random;
import java.util.Stack;
import java.util.UUID;

public class OrderEventGeneratorMapper extends Mapper<LongWritable, Text, Text, OrderEventWritable> {

    private static final int PRIME_TOTAL = 5000;
    private static final int CHAIN_MIN = 10;
    private static final int CHAIN_MAX = 10000;
    private static final int CHILDREN_MAX = 20;
    private final Random random = new Random(0L);

    @Override
    protected void map(LongWritable ignored, Text value, Context context) throws IOException, InterruptedException {

        String[] config = value.toString().split(":");
        int eventCount = Integer.parseInt(config[0]);
        int events = 0;

        boolean done = events >= eventCount;
        while (!done) {
            String primeId = "PRIME-" + nextInt(1, PRIME_TOTAL);

            int chainSize = nextSkewedBoundedDouble(CHAIN_MIN, CHAIN_MAX, 1, -6);
            int chainLast = events + chainSize;
            Stack<String> pending = new Stack<>();
            pending.push("");
            for (String parent = ""; !done && events < chainLast && parent != null; parent = pending.pop()) {
                buildEvent(parent, primeId, context, pending);
                events++;
                done = events >= eventCount;
            }

            context.getCounter(EventCounter.CHAINS).increment(1);
        }
    }

    private int nextInt(int from, int to) {
        return from + random.nextInt(to + 1 - from);
    }

    private int nextSkewedBoundedDouble(int min, int max, double skew, double bias) {
        double range = max - min;
        double mid = min + range / 2.0;
        double unitGaussian = random.nextGaussian();
        double biasFactor = Math.exp(bias);
        double result = mid + (range * (biasFactor / (biasFactor + Math.exp(-unitGaussian / skew)) - 0.5));
        return (int) Math.round(result);
    }

    private void buildEvent(String parentId, String primeId, Context context, Stack<String> stack) throws IOException, InterruptedException {
        int omsTotal = context.getNumReduceTasks();
        String id = UUID.randomUUID().toString();
        long timestamp = nextInt(1000, 9999);
        context.write(new Text(String.format("OMS-%02d", nextInt(1, omsTotal))), new OrderEventWritable(id, parentId.length() != 0 ? parentId : id, primeId, timestamp));

        // add children
        int childrenCount = nextInt(1, CHILDREN_MAX);
        for (int child = 0; child < childrenCount; child++) {
            stack.push(id);
        }
    }
}
