package com.prystupa.generate.mapreduce;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;

public class SequenceFileAsOmsEventsInputFormat extends SequenceFileInputFormat<Text, OrderEventWritable> {
}
