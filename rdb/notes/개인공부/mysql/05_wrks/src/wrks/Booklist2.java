package wrks;

import java.sql.*;
import java.util.Scanner;

public class Booklist2 {


    Connection con;
    Scanner inputer;

    public Booklist2() {
        String Driver = "";
        String url = "jdbc:mysql://localhost:3306/madang?&serverTimezone=Asia/Seoul";
        String userid = null, pwd = null;
        inputer = new Scanner(System.in);

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Driver loaded");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        do {
            try {
                System.out.print("ID >>> ");
                userid = inputer.nextLine();
                System.out.print("PW >>> ");
                pwd = inputer.nextLine();

                System.out.println("Waiting for DB connection...");
                con = DriverManager.getConnection(url, userid, pwd);
                System.out.println("DB Connected");
            } catch (SQLException e) {
                System.out.println("INVALID ID or PW");
            }

            if (con != null) break;

        } while (!userid.equals("quit"));
    }

    private void run_sql() {
        String query = inputer.nextLine();

        try {
            /*DatabaseMetaData metadata = con.getMetaData();
            ResultSet rs = metadata.getColumns(
                    "kodejava",
                    null,
                    "books",
                    null
            );

            while (rs.next()) {
                String name = rs.getString("COLUMN_NAME");
                String type = rs.getString("TYPE_NAME");
                int size = rs.getInt("COLUMN_SIZE");

                System.out.println("Column name: [" + name + "]; " +
                        "type: [" + type + "]; size: [" + size + "]");
            }*/
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            ResultSetMetaData mtd = rs.getMetaData();

            for (int i = 1; i < mtd.getColumnCount(); i++) {
                System.out.format("\t%s", mtd.getColumnName(i));
            }
            System.out.println();

            while (rs.next()) {
                for (int i = 1; i < mtd.getColumnCount(); i++) {
                    switch (mtd.getColumnType(i)) {
                        case 4 -> System.out.print("\t" + rs.getInt(i));
                        case 12 -> System.out.print("\t" + rs.getString(i));
                        default -> System.out.print("\t-");
                    }
                }
                System.out.println();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

        public static void main(String[] args) {
        Booklist2 so = new Booklist2();

        so.run_sql();
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
