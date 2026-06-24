package tn.tekup.bigdata.sales;

import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Locale;

public class RevenueStatsWritable implements Writable {
    private long orders;
    private double revenue;

    public RevenueStatsWritable() {
    }

    public RevenueStatsWritable(double revenue, long orders) {
        this.revenue = revenue;
        this.orders = orders;
    }

    public long getOrders() {
        return orders;
    }

    public double getRevenue() {
        return revenue;
    }

    public void add(RevenueStatsWritable other) {
        this.orders += other.orders;
        this.revenue += other.revenue;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeLong(orders);
        out.writeDouble(revenue);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        orders = in.readLong();
        revenue = in.readDouble();
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "orders=%d\trevenue=%.2f", orders, revenue);
    }
}
