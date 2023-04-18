package wrks;

import java.io.*;
import java.sql.*;

public class Booklist {

    Connection con;

    public Booklist() {
        String Driver = "";
        String url = "jdbc:mysql://localhost:3306/madang?&serverTimezone=Asia/Seoul";
        String userid = "madang";
        String pwd = "madang";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Driver loaded");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            System.out.println("Waiting for DB connection...");
            con = DriverManager.getConnection(url, userid, pwd);
            System.out.println("DB Connected");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void run_sql(String selects, String tb, String options) {
        String query =
                "SELECT " + selects + " " +
                "FROM " + tb + " " +
                options;

        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            System.out.println(" publisher\t cnt_books\t mean_price");
            while (rs.next()) {
                System.out.print("\t\t" + rs.getString(1));
                System.out.print("\t" + rs.getInt(2));
                System.out.println("\t" + rs.getDouble(3));
                /*System.out.println("\t" + rs.getInt(4));*/
            }
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Booklist so = new Booklist();

        String selects = "publisher, COUNT(*) cnt_books, AVG(price) mean_price";
        String tb = "book";
        String options = "GROUP BY publisher";

        so.run_sql(selects, tb, options);
    }
}

// SQL 관련 클래스는 java.sql .*에 포함되어 있다.
// 접속변수를 초기화한다. url은 자바 드라이버 이름, 호스트명(localhost), 포트번호를 입력한다
// userid는 관리자(madang), pwd는 사용자의 비밀번호(madang)를 입력한다.
// Class.forName()으로 드라이버를 로딩한다. 드라이버 이름을 Class.forName에 입력한다.
/* 데이터베이스를 연결하는 과정 *//*
// 접속 객체 con을 DriverManager.getConnection 함수로 생성한다.
// 접속이 성공하면 "데이터베이스 연결 성공"을 출력하도록 한다.
// 문자열 query에 수행할 SQL 문을 입력한다.
/* SQL 문 *//*
/* 데이터베이스에 질의 결과를 가져오는 과정 *//*
*/
