package tn.tekup.bigdata.sales;

import java.util.Optional;

public class SaleRecord {
    private final String city;
    private final String category;
    private final String product;
    private final int quantity;
    private final double unitPrice;
    private final String payment;

    private SaleRecord(String city, String category, String product, int quantity, double unitPrice, String payment) {
        this.city = city;
        this.category = category;
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.payment = payment;
    }

    public static Optional<SaleRecord> parse(String line) {
        if (line == null) {
            return Optional.empty();
        }

        String trimmed = line.trim();
        if (trimmed.isEmpty() || trimmed.toLowerCase().startsWith("order_id,")) {
            return Optional.empty();
        }

        String[] parts = trimmed.split(",", -1);
        if (parts.length < 8) {
            return Optional.empty();
        }

        try {
            String city = parts[2].trim();
            String category = parts[3].trim();
            String product = parts[4].trim();
            int quantity = Integer.parseInt(parts[5].trim());
            double unitPrice = Double.parseDouble(parts[6].trim());
            String payment = parts[7].trim();

            if (city.isEmpty() || category.isEmpty() || product.isEmpty() || payment.isEmpty() || quantity <= 0 || unitPrice < 0) {
                return Optional.empty();
            }

            return Optional.of(new SaleRecord(city, category, product, quantity, unitPrice, payment));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }

    public String groupValue(String groupBy) {
        switch (groupBy) {
            case "city":
                return city;
            case "category":
                return category;
            case "product":
                return product;
            case "payment":
                return payment;
            default:
                throw new IllegalArgumentException("Unsupported group field: " + groupBy);
        }
    }

    public double amount() {
        return quantity * unitPrice;
    }
}
