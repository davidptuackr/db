package wrks;

import java.sql.*;
import java.util.Arrays;
import java.util.Locale;
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
                userid = "root";
                pwd = "0000";
                System.out.print("ID >>> " + userid + "\n");
                System.out.print("PW >>> " + pwd + "\n");

                System.out.println("Waiting for DB connection...");
                con = DriverManager.getConnection(url, userid, pwd);
                System.out.println("DB Connected");
            } catch (SQLException e) {
                System.out.println("INVALID ID or PW");
            }

            if (con != null) break;

        } while (true);
    }

    private void run_sql() {

        String query;
        do {
            System.out.println("INPUT QUERY");
            query = inputer.nextLine();
            if (query.equals("exit")) {
                if (con != null) try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                System.out.println("Bye!");
                return;
            }

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
                ResultSet rs = null;
                ResultSetMetaData mtd;
                CallableStatement cstmt;

                if (query.substring(0, 4).equals("call")) {
                    String procedure_name = query.split(" ")[1];
                    cstmt = con.prepareCall("{call" + procedure_name + " (?, ?, ?, ?)}");
                }
                else {
                    stmt.execute(query);
                }

                switch (query.substring(0, 6)) {
                    case "select" -> rs = stmt.executeQuery(query);
                    case "insert", "delete" -> rs = stmt.executeQuery("select * from ".concat(query.split(" ")[2]));
                    case "update" -> rs = stmt.executeQuery("select * from ".concat(query.split(" ")[1]));
                    default -> {
                        String q;
                        do {
                            System.out.printf("""
                                            %s IS NOT TREATED AS DML
                                            TO SEE THE RESULT, 'SELECT' STATEMENT IS REQUIRED
                                            IF YOU DON'T WANT, PRESS '[i]gnore (default)'
                                            """,
                                    query
                            );
                            q = inputer.nextLine();
                            if (!q.isEmpty() && !q.equals("i") && q.substring(0, 6).toLowerCase(Locale.ROOT).equals("select")) {
                                rs = stmt.executeQuery(q);
                            }
                        } while (!q.isEmpty() && !q.equals("i"));
                    }
                }

                if (rs != null) {
                    mtd = rs.getMetaData();
                    for (int i = 1; i <= mtd.getColumnCount(); i++) {
                        System.out.format("\t%s", mtd.getColumnName(i));
                    }
                    System.out.println();

                    while (rs.next()) {
                        for (int i = 1; i <= mtd.getColumnCount(); i++) {
                            switch (mtd.getColumnType(i)) {
                                case 3 -> System.out.print("\t" + rs.getDouble(i));
                                case 4 -> System.out.print("\t" + rs.getInt(i));
                                case 12 -> System.out.print("\t" + rs.getString(i));
                                default -> System.out.print("\t-");
                            }
                        }
                        System.out.println();
                    }
                }

            } catch (SQLException e) {
                System.out.println("INVALID SQL SYNTAX");
            }
        } while (true);


    }

    public static void main(String[] args) {
        Booklist2 so = new Booklist2();

        so.run_sql();

    }
}
