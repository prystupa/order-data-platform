package com.prystupa.generate.mapreduce;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class OrderEventCaptureMapper extends Mapper<Text, OrderEventWritable, NullWritable, NullWritable> {
}
