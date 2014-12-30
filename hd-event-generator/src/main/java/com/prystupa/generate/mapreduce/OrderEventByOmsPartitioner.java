package com.prystupa.generate.mapreduce;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;

public class OrderEventByOmsPartitioner extends Partitioner<Text, OrderEventWritable> {
    @Override
    public int getPartition(Text text, OrderEventWritable orderEventWritable, int numPartitions) {

        final String oms = text.toString();
        final int omsId = Integer.parseInt(oms.substring(oms.indexOf('-') + 1));
        return (omsId - 1) % numPartitions;
    }
}
