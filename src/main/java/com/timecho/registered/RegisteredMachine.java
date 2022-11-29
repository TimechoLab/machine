package com.timecho.registered;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.apache.tomcat.util.codec.binary.Base64;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.regex.Pattern;

public class RegisteredMachine {

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setSize(900, 600);
        //这里创建JLabel 放置用户名密码
        JLabel companyLabel = new JLabel("企业名称:");
        JPanel companyPanel = new JPanel(new FlowLayout());
        JTextField companyText = new JTextField(15);
        companyPanel.add(companyLabel);
        companyPanel.add(companyText);


        JLabel contactLabel = new JLabel("联系人:");
        JPanel contactPanel = new JPanel(new FlowLayout());
        JTextField contactText = new JTextField(15);
        contactPanel.add(contactLabel);
        contactPanel.add(contactText);

        JLabel phoneLabel = new JLabel("联系方式:");
        JPanel phonePanel = new JPanel(new FlowLayout());
        JTextField phoneText = new JTextField(15);
        phonePanel.add(phoneLabel);
        phonePanel.add(phoneText);


        JLabel emailLabel = new JLabel("邮箱:");
        JPanel emailPanel = new JPanel(new FlowLayout());
        JTextField emailText = new JTextField(15);
        emailPanel.add(emailLabel);
        emailPanel.add(emailText);

        JLabel createrLabel = new JLabel("创建人:");
        JPanel createrPanel = new JPanel(new FlowLayout());
        JTextField createrText = new JTextField(15);
        createrPanel.add(createrLabel);
        createrPanel.add(createrText);

        JLabel createrPhoneLabel = new JLabel("创建人联系方式:");
        JPanel createrPhonePanel = new JPanel(new FlowLayout());
        JTextField createrPhoneText = new JTextField(15);
        createrPhonePanel.add(createrPhoneLabel);
        createrPhonePanel.add(createrPhoneText);

        JLabel startTimeLabel = new JLabel("开始时间: ");
        String startDate = DateUtil.format(new Date(), "yyyy-MM-dd");
        JLabel startTime = new JLabel("     " + startDate + "                     ");
        JPanel startTimePanel = new JPanel(new FlowLayout());
        startTimePanel.add(startTimeLabel);
        startTimePanel.add(startTime);


        JLabel endTimeLabel = new JLabel("结束时间:");
        JPanel endTimePanel = new JPanel(new FlowLayout());

        JComboBox endYear = new JComboBox();
        endYear.addItem("年");
        for (int i = 2022; i < 2049; i++) {
            endYear.addItem(i + "");
        }
        JComboBox endMonth = new JComboBox();
        endMonth.addItem("月");
        for (int i = 1; i <= 12; i++) {
            endMonth.addItem(String.format("%02d", i));
        }

        JComboBox endDay = new JComboBox();
        endDay.addItem("日");
        for (int i = 1; i <= 31; i++) {
            endDay.addItem(String.format("%02d", i));
        }
        endTimePanel.add(endTimeLabel);
        endTimePanel.add(endYear);
        endTimePanel.add(endMonth);
        endTimePanel.add(endDay);


        JLabel maxSeriesLabel = new JLabel("最大序列数数量:");
        JPanel maxSeriesPanel = new JPanel(new FlowLayout());
        JTextField maxSeriesText = new JTextField(15);
        maxSeriesPanel.add(maxSeriesLabel);
        maxSeriesPanel.add(maxSeriesText);

        JLabel maxFrequenceLabel = new JLabel("最高写入频率:");
        JPanel maxFrequencePanel = new JPanel(new FlowLayout());
        JTextField maxFrequenceText = new JTextField(15);
        maxFrequencePanel.add(maxFrequenceLabel);
        maxFrequencePanel.add(maxFrequenceText);


        JLabel machineCodeLabel = new JLabel("机器码:");
        JPanel machineCodePanel = new JPanel(new FlowLayout());
        machineCodePanel.add(machineCodeLabel);
        JTextArea jTextArea1 = new JTextArea(5, 20);
        jTextArea1.setLineWrap(true);
        JScrollPane jScrollPane = new JScrollPane(jTextArea1);
        jScrollPane.setBorder(BorderFactory.createLoweredBevelBorder());
        machineCodePanel.add(jScrollPane);


        JLabel licenseCodeLabel = new JLabel("license:");
        JPanel licenseCodePanel = new JPanel(new FlowLayout());
        licenseCodePanel.add(licenseCodeLabel);
        JTextArea jTextArea2 = new JTextArea(5, 20);
        jTextArea2.setLineWrap(true);
        jTextArea2.setWrapStyleWord(true);
        JScrollPane jScrollPane1 = new JScrollPane(jTextArea2);
        jScrollPane.setBorder(BorderFactory.createLoweredBevelBorder());
        licenseCodePanel.add(jScrollPane1);

        JPanel submitPanel = new JPanel(new FlowLayout());//面板p3流式布局
        JButton submitButton = new JButton("生成license");
        submitPanel.add(submitButton);


        submitButton.addActionListener(e -> {
            String companyTextText = companyText.getText();
            String contactTextText = contactText.getText();
            String phoneTextText = phoneText.getText();
            String emailTextText = emailText.getText();
            String createrTextText = createrText.getText();
            String createrPhoneTextText = createrPhoneText.getText();
            String endYearSelect = (String) endYear.getSelectedItem();
            String endMonthSelect = (String) endMonth.getSelectedItem();
            String endDaySelect = (String) endDay.getSelectedItem();
            String maxSeriesTextText = maxSeriesText.getText();
            String maxFrequenceTextText = maxFrequenceText.getText();
            String machineText = jTextArea1.getText();

            if ("".equals(companyTextText) || "".equals(contactTextText) || "".equals(phoneTextText) || "".equals(emailTextText) || "".equals(createrTextText) || "".equals(createrPhoneTextText) || "".equals(maxSeriesTextText) || "".equals(maxFrequenceTextText) || "".equals(machineText)) {
                JOptionPane.showMessageDialog(frame, "有未输入的值，请继续输入");
                return;
            }
            if ("年".equals(endYearSelect) || "月".equals(endMonthSelect) || "日".equals(endDaySelect)) {
                JOptionPane.showMessageDialog(frame, "开始时间或结束时间不完整，请重新选择");
                return;
            }

            String endDate = endYearSelect + "-" + endMonthSelect + "-" + endDaySelect;
            if (endDate.compareTo(startDate) < 0) {
                JOptionPane.showMessageDialog(frame, "结束时间必须大于开始时间，请重新选择");
                return;
            }
            String phonePattern = "^1[3,5,7,8]\\d{9}$";
            if (!Pattern.matches(phonePattern, phoneTextText) || !Pattern.matches(phonePattern, createrPhoneTextText)) {
                JOptionPane.showMessageDialog(frame, "手机号格式不正确，请重新输入");
                return;
            }

            try {
                String privateDecrypt = EncRSA.privateDecrypt(machineText);
                String systemInfoStr = new String(Base64.decodeBase64(privateDecrypt.getBytes()));
                JSONObject systemInfo = JSON.parseObject(systemInfoStr);
                systemInfo.put("expireDate", endYearSelect + "-" + endMonthSelect + "-" + endDaySelect);
                systemInfo.put("maxAllowedTimeSeriesNumber", Integer.parseInt(maxSeriesTextText));
                systemInfo.put("maxInputFrequence", Integer.parseInt(maxFrequenceTextText));
                String s1 = EncRSA.privateEncrypt(systemInfo.toJSONString());
                insertIntoDatabase(frame, companyTextText, contactTextText, phoneTextText, emailTextText, createrTextText, createrPhoneTextText, endDate, systemInfo);
                jTextArea2.setText(s1);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "生成license异常，请联系研发人员");
            }


        });
        frame.setLayout(new GridLayout(7, 2));
        frame.add(companyPanel);
        frame.add(contactPanel);
        frame.add(phonePanel);
        frame.add(emailPanel);
        frame.add(createrPanel);
        frame.add(createrPhonePanel);
        frame.add(startTimePanel);
        frame.add(endTimePanel);
        frame.add(maxSeriesPanel);
        frame.add(maxFrequencePanel);

        frame.add(machineCodePanel);
        frame.add(licenseCodePanel);
        frame.add(submitPanel);
        frame.setTitle("注册器");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private static void insertIntoDatabase(Frame frame, String companyTextText, String contactTextText, String phoneTextText, String emailTextText, String createrTextText, String createrPhoneTextText, String endDate, JSONObject systemInfo) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        try (Connection con = DriverManager.getConnection("jdbc:mysql://172.20.31.56:3306/timecho", "root", "Dwf@12345")) {
            String startDate = DateUtil.format(new Date(), "yyyy-MM-dd");
            StringBuilder baseSql = new StringBuilder("INSERT INTO `timecho`.`timecho_registered` (`id`,`company`,`contact`,`phone`,`email`,`creater`,`creater_phone`,`start_time`,`expire_time`,`mac`,`cpu`,`main_board`,`status`,`remark`) VALUES (NULL,");
            baseSql.append("'").append(companyTextText).append("','").append(contactTextText).append("','").append(phoneTextText).append("','").append(emailTextText).append("','").append(createrTextText).append("','").append(createrPhoneTextText).append("','").append(startDate).append("','").append(endDate).append("','").append(systemInfo.getJSONArray("macs").toString()).append("','").append(systemInfo.getString("cpu")).append("','").append(systemInfo.getString("mainBoard")).append("','").append("1").append("','").append("无").append("');");
            Statement stat = con.createStatement();
            stat.executeUpdate(baseSql.toString());
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "生成license异常，请联系研发人员");
        }
    }


}



