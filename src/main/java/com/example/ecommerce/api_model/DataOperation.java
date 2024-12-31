//package com.example.ecommerce.api_model;
//
///**
// * This class contains model of data operations.
// * @param <T> The data type to operate with.
// */
//public class DataOperation<T> {
//
//    /** The data to operates. */
//    private T data;
//
//    /** The data operate type. */
//    private DataOperatesType dataOperates;
//
//    /** The types of data operations. */
//    public enum DataOperatesType {
//        UPDATE,
//        INSERT,
//        DELETE
//    }
//
//    /**
//     * DataOperation Constructor.
//     * @param data The data to operates.
//     * @param dataOperates The data operation type.
//     */
//    public DataOperation(T data, DataOperatesType dataOperates) {
//        this.data = data;
//        this.dataOperates = dataOperates;
//    }
//
//    /**
//     * Gets the data that being operated.
//     * @return Returns the data that being operated.
//     */
//    public T getData() {
//        return data;
//    }
//
//    /**
//     * Sets the data that being operated.
//     * @param data The data that being operated to set.
//     */
//    public void setData(T data) {
//        this.data = data;
//    }
//
//    /**
//     * Gets the data operates type.
//     * @return Returns the data operates type.
//     */
//    public DataOperatesType getDataOperates() {
//        return dataOperates;
//    }
//
//}
