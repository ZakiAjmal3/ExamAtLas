package com.examatlas.models.Books;

public class DeliveryAddressItemModel {
    String billingId,addressType,firstName,lastName,houseNoOrApartmentNo,streetAddress,townCity,state,pinCode,countryName,phone,emailAddress,selected;

    public DeliveryAddressItemModel(String billingId, String addressType, String firstName, String lastName, String houseNoOrApartmentNo, String streetAddress, String townCity, String state, String pinCode, String countryName, String phone, String emailAddress, String selected) {
        this.billingId = billingId;
        this.addressType = addressType;
        this.firstName = firstName;
        this.lastName = lastName;
        this.houseNoOrApartmentNo = houseNoOrApartmentNo;
        this.streetAddress = streetAddress;
        this.townCity = townCity;
        this.state = state;
        this.pinCode = pinCode;
        this.countryName = countryName;
        this.phone = phone;
        this.emailAddress = emailAddress;
        this.selected = selected;
    }

    public String getSelected() {
        return selected;
    }

    public void setSelected(String selected) {
        this.selected = selected;
    }

    public String getBillingId() {
        return billingId;
    }

    public String getAddressType() {
        return addressType;
    }

    public void setAddressType(String addressType) {
        this.addressType = addressType;
    }

    public void setBillingId(String billingId) {
        this.billingId = billingId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getHouseNoOrApartmentNo() {
        return houseNoOrApartmentNo;
    }

    public void setHouseNoOrApartmentNo(String houseNoOrApartmentNo) {
        this.houseNoOrApartmentNo = houseNoOrApartmentNo;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getTownCity() {
        return townCity;
    }

    public void setTownCity(String townCity) {
        this.townCity = townCity;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPinCode() {
        return pinCode;
    }

    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
}
