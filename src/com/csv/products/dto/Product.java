package com.csv.products.dto;

/**
 * @author dilsh0d
 */
public class Product {
    private Integer id;
    private String name;
    private String condition;
    private String state;
    private Float price;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public static class Builder {

        private String id;
        private String name;
        private String condition;
        private String state;
        private String price;


        public Builder(String id) {
            this.id =id;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder condition(String condition) {
            this.condition = condition;
            return this;
        }

        public Builder state(String state) {
            this.state = state;
            return this;
        }

        public Builder price(String price) {
            this.price = price;
            return this;
        }

        public Product build(){
            Product product = new Product();
            product.setName(name);
            product.setState(state);
            product.setCondition(condition);
            try {
                if (id != null) {
                    product.setId(Integer.parseInt(id));
                }
                if (price != null) {
                    product.setPrice(Float.parseFloat(price));
                }
            } catch (NumberFormatException e){
                // e.printStackTrace();
            }
            return product;
        }
    }
}
