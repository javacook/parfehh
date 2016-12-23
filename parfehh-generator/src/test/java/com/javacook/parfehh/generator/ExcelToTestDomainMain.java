package com.javacook.parfehh.generator;

import com.javacook.easyexcelaccess.ExcelCoordinateAccessor;

import java.io.IOException;

/**
 * Created by vollmer on 23.12.16.
 */
public class ExcelToTestDomainMain {

    /*********************************************************\
     * main                                                  *
    \*********************************************************/

    public static void main(String[] args) throws IOException {
        ExcelCoordinateAccessor excelAccessor = new ExcelCoordinateAccessor("MyTests.xls");
        ExcelToTestDomain factory = new ExcelToTestDomain(excelAccessor);
        factory.createTestSeries();
        System.out.println(factory.testSeries);
    }

}