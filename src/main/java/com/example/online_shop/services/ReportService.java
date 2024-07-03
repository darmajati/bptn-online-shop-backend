package com.example.online_shop.services;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;

@Service
public class ReportService {

    @Autowired
    private DataSource dataSource;

    private Connection getConnection(){
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
           e.printStackTrace();
           return null;
        }
    }

    public JasperPrint generateJasperPrint() throws Exception{
        InputStream fileReport = new ClassPathResource("reports/OrderReport.jasper").getInputStream();
        JasperReport jasperReport = (JasperReport) JRLoader.loadObject(fileReport);
        JasperPrint jasperPrint =  JasperFillManager.fillReport(jasperReport, null, getConnection());
        return jasperPrint;
    }
    
}
