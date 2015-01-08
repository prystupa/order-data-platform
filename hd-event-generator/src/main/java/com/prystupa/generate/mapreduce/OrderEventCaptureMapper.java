package com.prystupa.generate.mapreduce;

import com.google.common.collect.ImmutableMap;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableUtils;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.IOException;
import java.util.Map;

public class OrderEventCaptureMapper extends Mapper<Text, OrderEventWritable, NullWritable, NullWritable> {

    private KafkaProducer producer;

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        super.setup(context);

        Map<String, Object> configs = ImmutableMap.<String, Object>of("bootstrap.servers", context.getConfiguration().get("bootstrap.servers"));
        producer = new KafkaProducer(configs);
    }

    @Override
    protected void map(Text oms, OrderEventWritable event, Context context) throws IOException, InterruptedException {

        ProducerRecord record = new ProducerRecord("oms.events", event.getId().copyBytes(), WritableUtils.toByteArray(event));
        producer.send(record);
    }
}
