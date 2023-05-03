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
                String[] tokens = query.split("[(),\\s;']+");

                if (tokens[0].equals("call")) {
                    String pname = tokens[1];
                    StringBuilder params = new StringBuilder();
                    params.append('(');
                    params.append("?, ".repeat(tokens.length - 3));
                    params.append("?)");

                    cstmt = con.prepareCall("{call " + pname + params + "}");

                    /*DatabaseMetaData db_mtd = con.getMetaData();
                    ResultSet p_rs = db_mtd.getProcedures(null, null, pname);
*/

                    DatabaseMetaData dbMetaData = con.getMetaData();
                    ResultSet rs2 = dbMetaData.getProcedureColumns(con.getCatalog(),
                            null,
                            pname,
                            null);

                    /*while(rs2.next()) {
                        // get stored procedure metadata
                        String procedureCatalog     = rs2.getString(1);
                        String procedureSchema      = rs2.getString(2);
                        String procedureName        = rs2.getString(3);
                        String columnName           = rs2.getString(4);
                        short  columnReturn         = rs2.getShort(5);
                        int    columnDataType       = rs2.getInt(6);
                        String columnReturnTypeName = rs2.getString(7);
                        int    columnPrecision      = rs2.getInt(8);
                        int    columnByteLength     = rs2.getInt(9);
                        short  columnScale          = rs2.getShort(10);
                        short  columnRadix          = rs2.getShort(11);
                        short  columnNullable       = rs2.getShort(12);
                        String columnRemarks        = rs2.getString(13);

                        System.out.println("stored Procedure name="+procedureName);
                        System.out.println("procedureCatalog=" + procedureCatalog);
                        System.out.println("procedureSchema=" + procedureSchema);
                        System.out.println("procedureName=" + procedureName);
                        System.out.println("columnName=" + columnName);
                        System.out.println("columnReturn=" + columnReturn);
                        System.out.println("columnDataType=" + columnDataType);
                        System.out.println("columnReturnTypeName=" + columnReturnTypeName);
                        System.out.println("columnPrecision=" + columnPrecision);
                        System.out.println("columnByteLength=" + columnByteLength);
                        System.out.println("columnScale=" + columnScale);
                        System.out.println("columnRadix=" + columnRadix);
                        System.out.println("columnNullable=" + columnNullable);
                        System.out.println("columnRemarks=" + columnRemarks);
                        System.out.println();
                    }*/

                    //ResultSetMetaData p_rs_mtd = p_rs.getMetaData();
                    int i = 1;
                    while (rs2.next()) {
                        switch (rs2.getInt(6)) {
                            case 3 -> cstmt.setDouble(i, Double.parseDouble(tokens[i+1]));
                            case 4 -> cstmt.setInt(i, Integer.parseInt(tokens[i+1]));
                            case 12 -> cstmt.setString(i, tokens[i+1]);
                        }
                        i++;
                    }
                    cstmt.executeUpdate();
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
                                break;
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
