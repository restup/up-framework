package com.stack;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import java.time.ZonedDateTime;

abstract class AbstractRelease {

    @DynamoDBAttribute(attributeName = "Version")
    private String version;
    @DynamoDBAttribute(attributeName = "CostCenter")
    private String costCenter;
    @DynamoDBAttribute(attributeName = "Owner")
    private String owner;
    @DynamoDBAttribute(attributeName = "Product")
    private String product;
    @DynamoDBAttribute(attributeName = "CreateDateTime")
    private ZonedDateTime created;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getCostCenter() {
        return costCenter;
    }

    public void setCostCenter(String costCenter) {
        this.costCenter = costCenter;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public ZonedDateTime getCreated() {
        return created;
    }

    public void setCreated(ZonedDateTime created) {
        this.created = created;
    }
}
