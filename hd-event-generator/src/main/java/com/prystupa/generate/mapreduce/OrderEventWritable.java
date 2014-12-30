package com.prystupa.generate.mapreduce;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class OrderEventWritable implements Writable {

    private final Text id = new Text();
    private final Text parentId = new Text();
    private final Text primeId = new Text();
    private final LongWritable timestamp = new LongWritable();

    public OrderEventWritable() {
    }

    public OrderEventWritable(String id, String parentId, String primeId, long timestamp) {
        this();
        this.id.set(id);
        this.parentId.set(parentId);
        this.primeId.set(primeId);
        this.timestamp.set(timestamp);
    }

    @Override
    public void write(DataOutput out) throws IOException {
        id.write(out);
        parentId.write(out);
        primeId.write(out);
        timestamp.write(out);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        id.readFields(in);
        parentId.readFields(in);
        primeId.readFields(in);
        timestamp.readFields(in);
    }

    @Override
    public String toString() {
        return "OrderEventWritable{" +
                "id=" + id.toString().substring(0, 8) +
                ", parent=" + parentId.toString().substring(0, 8) +
                ", prime=" + primeId +
                ", ts=" + timestamp +
                '}';
    }
}
