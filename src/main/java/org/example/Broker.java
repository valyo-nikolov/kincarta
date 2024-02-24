package org.example;

public class Broker {

    private String name;
    private String address;
    private String landlinePhone;
    private String mobilePhone;
    private String numProperties;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLandlinePhone() {
        return landlinePhone;
    }

    public void setLandlinePhone(String landlinePhone) {
        this.landlinePhone = landlinePhone;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getNumProperties() {
        return numProperties;
    }

    public void setNumProperties(String numProperties) {
        this.numProperties = numProperties;
    }

    @Override
    public String toString() {
        return "Broker{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", landlinePhone='" + landlinePhone + '\'' +
                ", mobilePhone='" + mobilePhone + '\'' +
                ", numProperties='" + numProperties + '\'' +
                '}';
    }
}
