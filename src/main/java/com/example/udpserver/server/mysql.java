package com.example.udpserver.server;

import java.sql.*;

public class mysql {
    public static String SELECT(String THECPUID) {
        /**
         * 先查询用户的CPUID,如果不在就插入一下.
         */
        Connection connection = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(
                    "jdbc:mysql://47.113.189.105:3306/userid", // mysql服务器地址，端口，数据库名
                    "USERID", // 数据库的用户名
                    "sEZPRnJhcp4z7rFE" // 数据库密码
            );
            String selectid = "select COUNT(*) AS EXIST from alluser WHERE CPUID =?";
            PreparedStatement pstmt1 = connection.prepareStatement(selectid);
            pstmt1.setString(1, THECPUID);
            ResultSet getid = pstmt1.executeQuery();
            while(getid.next()){
                int count  = getid.getInt("EXIST");
                if(count==0)
                {
                    String insert = "insert into alluser values(?,0)";
                    PreparedStatement pstmt2 = connection.prepareStatement(insert);
                    pstmt2.setString(1, THECPUID);
                    pstmt2.executeUpdate();
                    return "OK";
                }
                else {
                    /**
                     * 需要UPDATE
                     */
                    String selectstate = "select STATE from alluser WHERE CPUID = ?";
                    PreparedStatement pstmt2 = connection.prepareStatement(selectstate);
                    pstmt2.setString(1, THECPUID);
                    ResultSet getstate = pstmt2.executeQuery();
                    while (getstate.next())
                    {
                        int state = getstate.getInt("STATE");
                        if(state==1)
                        {
                            return "NO";//黑名单
                        }
                        else {
                            return "OK";//白名单
                        }
                    }
                }
            }
        }catch (ClassNotFoundException | SQLException e){
            System.out.println("error");
        } finally {
            // 第五步：释放资源
            try {
                if (connection != null) {
                    connection.close();
                }
            }catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return "NO";
    }
}


