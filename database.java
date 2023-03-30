import java.sql.*;
import java.util.Scanner;
import java.io.*;
import java.text.SimpleDateFormat;

public class database {
    static final Scanner scan = new Scanner(System.in);

    public static void main(String[] args) throws Exception {
        int i;
        do {
            System.out.println("\n------------------Main menu-----------------\n" +
                    "What kinds of operation would you like to perform\n" +
                    "1. Operations for administrator\n" +
                    "2. Operations for salesperson\n" +
                    "3. Operations for manager\n" +
                    "4. Exit this program");
            System.out.print("Enter your choice: ");
            i = scan.nextInt();
            
            switch (i) {
                case 1:
                    ope();
                    break;
                case 2:

                    sales_operation();
                    break;

                case 3:
                    manager_opration();
                    break;

            }
        } while (i != 4);
    }

    public static Connection getConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        String dbAddress = "jdbc:mysql://projgw.cse.cuhk.edu.hk:2633/db24";
        String dbUsername = "Group24";
        String dbPassword = "CSCI3170";
        Connection con = DriverManager.getConnection(dbAddress, dbUsername, dbPassword);
        // System.out.println("Database connected!");
        return con;
    }

    // --------------------------------------Admin Operation-----------------------
    public static void ope() throws Exception {
        int i;
        do {
            System.out.println("\n-----Operations for administrator menu-----\n" +
                    "What kinds of operation would you like to perform?\n" +
                    "1. Creat all tables\n" +
                    "2. Delete all tables\n" +
                    "3. Load from datafile\n" +
                    "4. Show content of a table\n" +
                    "5. Return to the main menu");
            System.out.print("Enter your choice: ");
            i = scan.nextInt();
            switch (i) {
                case 1:
                    createAllTables();
                    break;
                case 2:
                    dropAll();
                    break;
                case 3:
                    init();
                    break;
                case 4:
                    showContent();
                    break;
            }
        } while (i != 5);
    }

    public static void createAllTables() throws Exception {

        String createC = "CREATE TABLE `category`  (\n" +
                "  `cID` int(1) NOT NULL,\n" +
                "  `cName` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,\n" +
                "  PRIMARY KEY (`cId`) USING BTREE\n" +
                ") ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;";

        String createM = "CREATE TABLE `manufacturer`  (\n" +
                "  `mId` int(2) NOT NULL,\n" +
                "  `mName` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,\n" +
                "  `mAddress` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,\n" +
                "  `mPhoneNumber` int(8) NOT NULL,\n" +
                "  PRIMARY KEY (`mId`) USING BTREE\n" +
                ") ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;";

        String createP = "CREATE TABLE `part`  (\n" +
                "  `pId` int(3) NOT NULL,\n" +
                "  `pName` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,\n" +
                "  `pPrice` int(5) NOT NULL,\n" +
                "  `pManufacturerId` int(2) NOT NULL,\n" +
                "  `pCategoryId` int(1) NOT NULL,\n" +
                "  `pWarranty` int(2) NOT NULL,\n" +
                "  `pAvailableQuantity` int(2) NOT NULL,\n" +
                "  PRIMARY KEY (`pId`) USING BTREE\n" +
                ") ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;";

        String createS = "CREATE TABLE `salesperson`  (\n" +
                "  `sId` int(2) NOT NULL,\n" +
                "  `sName` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,\n" +
                "  `sAddress` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,\n" +
                "  `sPhoneNumber` int(8) NOT NULL,\n" +
                "  `sExperience` int(1) NOT NULL,\n" +
                "  PRIMARY KEY (`sId`) USING BTREE\n" +
                ") ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;";

        String createT = "CREATE TABLE `transaction_records`  (\n" +
                "  `tId` int(4) NOT NULL,\n" +
                "  `tPartId` int(3) NOT NULL,\n" +
                "  `tSalespersonId` int(2) NOT NULL,\n" +
                "  `tTransactionDate` datetime(0) NOT NULL,\n" +
                "  PRIMARY KEY (`tId`) USING BTREE\n" +
                ") ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;";

        Connection connection = getConnection();
        Statement statement = connection.createStatement();
        // dropAll(connection);
        statement.addBatch(createC);
        statement.addBatch(createM);
        statement.addBatch(createP);
        statement.addBatch(createS);
        statement.addBatch(createT);
        statement.executeBatch();
        statement.clearBatch();
        statement.close();
        connection.close();
        System.out.println("Processing...Done! Database is initialized!");
    }

    public static void dropAll() throws Exception {
        String dropc = "DROP TABLE IF EXISTS `category`;";
        String dropM = "DROP TABLE IF EXISTS `manufacturer`;";
        String dropP = "DROP TABLE IF EXISTS `part`;";
        String dropS = "DROP TABLE IF EXISTS `salesperson`;";
        String dropT = "DROP TABLE IF EXISTS `transaction_records`;";
        Connection connection = getConnection();
        Statement statement = connection.createStatement();
        statement.addBatch(dropc);
        statement.addBatch(dropM);
        statement.addBatch(dropP);
        statement.addBatch(dropS);
        statement.addBatch(dropT);
        statement.executeBatch();
        statement.clearBatch();
        statement.close();
        connection.close();
        System.out.println("Processing...Done! Database is removed!");
    }

    // ***Need to add path */
    public static void init() throws Exception {
        System.out.print("Type in the Source Data Folder Path: ");
        String path = "";
        if (scan.hasNext())
            path = scan.nextLine();
        // String path = "sample_data";
        initCategory(path);
        initTransaction_records(path);
        initSalePerson(path);
        initPart(path);
        initManufacturer(path);

        System.out.println("Processing...Done! Data are loaded successfully!\n");

    }

    public static void initCategory(String path) throws Exception {
        path = path + "/" + "category.txt";
        // System.out.println(path);
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path)), "UTF-8"));
            String lineTxt = null;

            String sql = "insert into category(cId,cName) values(?,?);";
            Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            while ((lineTxt = br.readLine()) != null) {
                String[] strings = lineTxt.split("\t");
                statement.setInt(1, Integer.parseInt(strings[0]));
                statement.setString(2, strings[1]);
                statement.addBatch();
            }
            br.close();
            statement.executeBatch();
            statement.clearBatch();
            statement.close();
            connection.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void initManufacturer(String path) throws Exception {
        path = path + "/" + "manufacturer.txt";
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path)), "UTF-8"));
        String lineTxt = null;
        String sql = "insert into manufacturer(mId,mName,mAddress,mPhoneNumber) values(?,?,?,?);";
        Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement(sql);
        while ((lineTxt = br.readLine()) != null) {
            String[] strings = lineTxt.split("\t");
            statement.setInt(1, Integer.parseInt(strings[0]));
            statement.setString(2, strings[1]);
            statement.setString(3, strings[2]);
            statement.setInt(4, Integer.parseInt(strings[3]));
            statement.addBatch();
        }
        br.close();
        statement.executeBatch();
        statement.clearBatch();
        statement.close();
        connection.close();
    }

    public static void initPart(String path) throws Exception {
        path = path + "/" + "part.txt";
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path)), "UTF-8"));
        String lineTxt = null;
        String sql = "insert into part(pId,pName,pPrice,pManufacturerId,pCategoryId,pWarranty,pAvailableQuantity) values(?,?,?,?,?,?,?);";
        Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement(sql);
        while ((lineTxt = br.readLine()) != null) {
            String[] strings = lineTxt.split("\t");
            statement.setInt(1, Integer.parseInt(strings[0]));
            statement.setString(2, strings[1]);
            statement.setInt(3, Integer.parseInt(strings[2]));
            statement.setInt(4, Integer.parseInt(strings[3]));
            statement.setInt(5, Integer.parseInt(strings[4]));
            statement.setInt(6, Integer.parseInt(strings[5]));
            statement.setInt(7, Integer.parseInt(strings[6]));
            statement.addBatch();
        }
        br.close();
        statement.executeBatch();
        statement.clearBatch();
        statement.close();
        connection.close();

    }

    public static void initSalePerson(String path) throws Exception {
        path = path + "/" + "salesperson.txt";
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path)), "UTF-8"));
        String lineTxt = null;
        String sql = "insert into salesperson(sId,sName,sAddress,sPhoneNumber,sExperience) values(?,?,?,?,?);";
        Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement(sql);
        while ((lineTxt = br.readLine()) != null) {
            String[] strings = lineTxt.split("\t");
            statement.setInt(1, Integer.parseInt(strings[0]));
            statement.setString(2, strings[1]);
            statement.setString(3, strings[2]);
            statement.setInt(4, Integer.parseInt(strings[3]));
            statement.setInt(5, Integer.parseInt(strings[4]));
            statement.addBatch();
        }
        br.close();
        statement.executeBatch();
        statement.clearBatch();
        statement.close();
        connection.close();
    }

    public static void initTransaction_records(String path) throws Exception {
        path = path + "/" + "transaction.txt";
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path)), "UTF-8"));
        String lineTxt = null;
        String sql = "insert into transaction_records(tId,tPartId,tSalespersonId,tTransactionDate) values(?,?,?,?);";
        Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement(sql);
        while ((lineTxt = br.readLine()) != null) {
            String[] strings = lineTxt.split("\\s+");
            statement.setInt(1, Integer.parseInt(strings[0]));
            statement.setInt(2, Integer.parseInt(strings[1]));
            statement.setInt(3, Integer.parseInt(strings[2]));
            statement.setDate(4, new Date(new SimpleDateFormat("dd/MM/yyyy").parse(strings[3]).getTime()));
            statement.addBatch();
        }
        br.close();
        statement.executeBatch();
        statement.clearBatch();
        statement.close();
        connection.close();
    }

    public static void showContent() throws Exception {

        System.out.print("Which table would you like to show:");
        String str = scan.next();
        switch (str) {
            case "category":
                showCategory();
                break;
            case "manufacturer":
                showManufacturer();
                break;
            case "part":
                showPart();
                break;
            case "salesperson":
                showSalesPerson();
                break;
            case "transaction":
                showTransaction();
                break;
            default:
                System.out.println("no this table");
        }
    }

    public static void showCategory() throws Exception {
        String sql = "select * from category";
        Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet resultSet = statement.executeQuery();
        System.out.println("| cId | cName |");
        while (resultSet.next()) {
            System.out.print("| " + resultSet.getInt("cId") + " | ");
            System.out.println(resultSet.getString("cName") + " |");
        }
        statement.close();
        connection.close();
    }

    public static void showManufacturer() throws Exception {
        String sql = "select * from manufacturer";
        Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet resultSet = statement.executeQuery();
        System.out.println("| mId | mName | mAddress | mPhoneNumber");
        while (resultSet.next()) {
            System.out.print("| " + resultSet.getInt("mId") + " | ");
            System.out.print(resultSet.getString("mName") + " | ");
            System.out.print(resultSet.getString("mAddress") + " | ");
            System.out.println(resultSet.getString("mPhoneNumber") + " | ");
        }
        statement.close();
        connection.close();
    }

    public static void showPart() throws Exception {
        String sql = "select * from part";
        Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet resultSet = statement.executeQuery();
        System.out.println("| pId | pName | pPrice | pManufacturerId | pCategoryId | pWarranty | pAvailableQuantity");
        while (resultSet.next()) {
            System.out.print("| " + resultSet.getInt("pId") + "| ");
            System.out.print(resultSet.getString("pName") + " | ");
            System.out.print(resultSet.getString("pPrice") + " | ");
            System.out.print(resultSet.getString("pManufacturerId") + " | ");
            System.out.print(resultSet.getString("pCategoryId") + " | ");
            System.out.print(resultSet.getString("pWarranty") + " | ");
            System.out.println(resultSet.getString("pAvailableQuantity") + " | ");
        }
        statement.close();
        connection.close();
    }

    public static void showSalesPerson() throws Exception {
        String sql = "select * from salesperson";
        Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet resultSet = statement.executeQuery();
        System.out.println("| sId | sName | sAddress | sPhoneNumber | sExperience |");
        while (resultSet.next()) {
            System.out.print("|" + resultSet.getInt("sId") + " | ");
            System.out.print(resultSet.getString("sName") + " | ");
            System.out.print(resultSet.getString("sAddress") + " | ");
            System.out.print(resultSet.getString("sPhoneNumber") + " | ");
            System.out.println(resultSet.getString("sExperience") + " | ");
        }
        statement.close();
        connection.close();
    }

    public static void showTransaction() throws Exception {
        String sql = "select * from transaction_records";
        Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet resultSet = statement.executeQuery();
        System.out.println("|tId|tPartId|tSalesPersonId|tTransactionDate|");
        while (resultSet.next()) {
            System.out.print("|" + resultSet.getInt("tId") + "|");
            System.out.print(resultSet.getString("tPartId") + "|");
            System.out.print(resultSet.getString("tSalesPersonId") + "|");
            System.out.println(resultSet.getString("tTransactionDate") + "|");
        }
        System.out.println("End of Query");
        statement.close();
        connection.close();
    }

    // ------------------ Sales Operation -------------------------------
    public static void sales_operation() throws Exception {
        int input;
       
        do {
            System.out.println("\n-----Operations for salesperson menu-----");
            System.out.println("What kinds of operations would you like to perform?");
            System.out.println("1. Search for parts");
            System.out.println("2. Sell a part");
            System.out.println("3. Return to the main menu");
            System.out.print("Enter Your Choice: ");
         input = scan.nextInt();
           
        } while (input < 1 || input > 3);
        if (input == 1)
        { 
            System.out.print("Choose the Search criterion: \n1. Part Name\n2. Manufacturer Name\nChoose the search criterion: ");
            int input1=scan.nextInt();
            if(input1==1)
            {
                SearchByParts();
            }
            else if(input1==2)

            SearchByManufacturerName();



        }
            

        else if (input == 2)
            SellPart();

      

        
    }

    public static void SearchByParts() throws Exception{
        try {

            
            Scanner scan = new Scanner(System.in);
            Connection conn = getConnection();
            
            
            System.out.print("Type in the Search Keyword: ");
            String inputString = scan.nextLine();
            
            System.out.print(
                    "Choose ordering:\n1. By price, ascending order\n2. By price, descending order\nChoose the search criterion: ");

            String stmt_str="";
            int intinput = scan.nextInt();
            if (intinput == 1) {
                 stmt_str = "SELECT p.pId, p.pName, m.mName, c.cName,p.pAvailableQuantity, p.pWarranty, p.pPrice FROM part p, manufacturer m, category c WHERE pName=? and p.pManufacturerId=m.mId and p.pCategoryId=c.cId ORDER BY p.pPrice ASC";;
                
            } 
            else if (intinput == 2) {

                
                stmt_str = "SELECT p.pId, p.pName, m.mName, c.cName,p.pAvailableQuantity, p.pWarranty, p.pPrice FROM part p, manufacturer m, category c WHERE pName=? and p.pManufacturerId=m.mId and p.pCategoryId=c.cId ORDER BY p.pPrice DESC";
                
            }
            PreparedStatement pstmt = conn.prepareStatement(stmt_str);
            pstmt.setString(1, inputString);

            String query_result = "";
            ResultSet resultSet = pstmt.executeQuery();
            if (!resultSet.isBeforeFirst()) {
                System.out.println("No Records.");
            } else {
                System.out.println("| ID | Name | Manufacturer | Category | Quantity | Warranty | Price |");
                while (resultSet.next()) {
                    // System.out.println(resultSet.getString(1));
                    Integer PartID = resultSet.getInt(1);
                    String PartName = resultSet.getString(2);
                    String ManufacturerName = resultSet.getString(3);
                    String CategoryName = resultSet.getString(4);
                    Integer PartQuantity = resultSet.getInt(5);
                    Integer PartWarranty = resultSet.getInt(6);
                    Integer PartPrice = resultSet.getInt(7);
                    String PartIDs = PartID.toString();
                    String PartQuantitys = PartQuantity.toString();
                    String PartWarrantys = PartWarranty.toString();
                    String PartPrices = PartPrice.toString();
                    String result = "| " + PartIDs + " | " + PartName + " | " + ManufacturerName + " | " + CategoryName
                            + " | " + PartQuantitys + " | " + PartWarrantys + " | " + PartPrices + " |\n";
                    query_result = query_result + result;
                }
                System.out.print(query_result);
            }
            pstmt.close();
            conn.close();
        } catch (SQLException e) {
            System.out.println(e);
            System.exit(0);
        } finally {
            System.out.print("End of query\n");

        }
    }

    public static void SellPart() throws Exception {
        try {
            Integer PID;
            Connection conn= getConnection();
            System.out.print("Enter the Part ID: ");
             PID = scan.nextInt();
            Integer PIDsave=PID;
            System.out.print("Enter the Salesperson ID: ");
            
            Integer SalespersonID = scan.nextInt();
            
            String check_str="Select pAvailableQuantity from part WHERE pId=?;";
            //check_str=check_str+PID.toString();
            System.out.println(check_str);
            PreparedStatement pcheck_str=conn.prepareStatement(check_str);
            pcheck_str.setInt(1,PID);
            ResultSet checking=pcheck_str.executeQuery();
            
            int check=0;
          
            if (!checking.isBeforeFirst()) {
                System.out.println("No Records.");
            } else 
            {
                while(checking.next())
            {check=checking.getInt(1);}
        }
        System.out.println(check);
            if(check==0)
            {System.out.println("Sold out!");
        
        }
    



        PreparedStatement pstmt2 = conn.prepareStatement("Select COUNT(*) FROM transaction_records");
        ResultSet number1=pstmt2.executeQuery();
        int num2=0;
        if (!number1.isBeforeFirst()) {
            System.out.println("No Records.");
        } else 
        {
            while(number1.next())
        {num2=number1.getInt(1)+1;}
    }

    String stmt_str3 = "INSERT INTO transaction_records (tId,tPartId, tSalespersonId, tTransactionDate) VALUES(?, ?, ?, now())";
    PreparedStatement pstmt3 = conn.prepareStatement(stmt_str3);
    pstmt3.setInt(1, num2);
    pstmt3.setInt(2, PID);
    pstmt3.setInt(3, SalespersonID);
    pstmt3.addBatch();
    pstmt3.executeBatch();
    pstmt3.clearBatch();
  


    //System.out.print("NUM:");
    //System.out.println(num2);
   // System.out.print("PIDSAVE:");
       // System.out.println(PIDsave);
        //System.out.print("SalespersonID:");
        //System.out.println(SalespersonID);
        
        String stmt_str4 = "UPDATE part SET pAvailableQuantity=? WHERE pId=?";
    PreparedStatement pstmt4 = conn.prepareStatement(stmt_str4);
    pstmt4.setInt(1, check-1);
    pstmt4.setInt(2, PID);
    pstmt4.executeBatch();

      

       String updated="";
        String stmt_str2 = "Select p.pName, p.pId, p.pAvailableQuantity from part p Where p.pId=?";
        
        PreparedStatement pstmt_str2=conn.prepareStatement(stmt_str2);
        System.out.print("PID:");
        System.out.println(PID);
        pstmt_str2.setInt(1,PID);
        ResultSet checking2=pstmt_str2.executeQuery();
       
        
Integer check2=check-1;
        if (!checking2.isBeforeFirst()) {
            System.out.println("No Records.");
        } else 
        {
            while(checking2.next())
        {
            
            String Pname=checking2.getString(1);
        Integer ID=checking2.getInt(2);
        Integer rm=checking2.getInt(3);
        updated="Product: "+ Pname +"(id: "+ID.toString()+") Remaining Qulity: "+ check2.toString();
        System.out.println(updated);
        
        }
    }
        








            
    
        }
        catch (SQLException e) {
            System.out.println(e);
            System.exit(0);
        } finally {
            System.out.println("End of query\n");
        }
    }

    public static void SearchByManufacturerName() throws Exception {
        try {
            
            Connection conn = getConnection();
            String stmt_str="";
            System.out.print("Type in the Search Keyword: ");
          
            String inputString = scan.next();
            
            System.out.print(
                    "Choose ordering:\n1. By price, ascending order\n2. By price, descending order\nChoose the search criterion: ");

            int intinput = scan.nextInt();
            if (intinput == 1) {

                stmt_str = "SELECT p.pId, p.pName, m.mName, c.cName,p.pAvailableQuantity, p.pWarranty, p.pPrice FROM part p, manufacturer m, category c WHERE m.mName=? and p.pManufacturerId=m.mId and p.pCategoryId=c.cId ORDER BY p.pPrice ASC;";
           

            } else if (intinput == 2) {

                stmt_str = "SELECT p.pId, p.pName, m.mName, c.cName,p.pAvailableQuantity, p.pWarranty, p.pPrice FROM part p, manufacturer m, category c WHERE m.mName=? and p.pManufacturerId=m.mId and p.pCategoryId=c.cId ORDER BY p.pPrice DESC;";
            }
            PreparedStatement pstmt = conn.prepareStatement(stmt_str);
            pstmt.setString(1, inputString);
            String query_result = "";
            ResultSet resultSet = pstmt.executeQuery();
            if (!resultSet.isBeforeFirst()) {
                System.out.println("No Records.");
            } else 
            {
                System.out.println("| ID | Name | Manufacturer | Category | Quantity | Warranty | Price |");
                while (resultSet.next()) {
                    // System.out.println(resultSet.getString(1));
                    Integer PartID = resultSet.getInt(1);
                    String PartName = resultSet.getString(2);
                    String ManufacturerName = resultSet.getString(3);
                    String CategoryName = resultSet.getString(4);
                    Integer PartQuantity = resultSet.getInt(5);
                    Integer PartWarranty = resultSet.getInt(6);
                    Integer PartPrice = resultSet.getInt(7);
                    String PartIDs = PartID.toString();
                    String PartQuantitys = PartQuantity.toString();
                    String PartWarrantys = PartWarranty.toString();
                    String PartPrices = PartPrice.toString();
                    String result = "| " + PartIDs + " | " + PartName + " | " + ManufacturerName + " | " + CategoryName
                            + " | " + PartQuantitys + " | " + PartWarrantys + " | " + PartPrices + " |\n";

                    query_result = query_result + result;
                }
                System.out.println(query_result);
            }
            conn.close();
        } catch (SQLException e) {
            System.out.println(e);
            System.exit(0);
        } finally {
            System.out.print("End of query\n");
            // stmt.close();
        }
    }



    

    // ------------------ Manager Operation -------------------------------
    public static void manager_opration() throws Exception {
        int input;
        System.out.println("\n-----Operations for manager menu-----");
        System.out.println("What kinds of operations would you like to perform?");
        System.out.println("1. List all salespersons");
        System.out.println(
                "2. Count the no. of transaction records of each salesperson within a given range on years of experience");
        System.out.println("3. Show the total sales value of each manufacturer");
        System.out.println("4. Show the N most popular part");
        System.out.println("5. Return to the main menu");
        do {
            System.out.print("Enter Your Choice: ");
            input = scan.nextInt();
        } while (input < 1 || input > 5);
        if (input == 1)
            listSalespersons();
        else if (input == 2)
            countTransaction();
        else if (input == 3)
            showTotalSales();
        else if (input == 4)
            showPopular();
    }

    public static void listSalespersons() {
        int input = 0;
        System.out.println("Choose ordering:");
        System.out.println("1. By ascending order");
        System.out.println("2. By descending order");
        do {
            System.out.print("Choose the list ordering: ");
            input = scan.nextInt();
        } while (input < 1 || input > 2);

        String sqlStatement = "";
        if (input == 1)
            sqlStatement = "SELECT sId, sName, sPhoneNumber, sExperience FROM salesperson ORDER BY sExperience ASC;";
        else if (input == 2)
            sqlStatement = "SELECT sId, sName, sPhoneNumber, sExperience FROM salesperson ORDER BY sExperience DESC;";

        try {
            Connection conn = getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sqlStatement);
            ResultSet rs = pstmt.executeQuery();
            System.out.println("| ID | Name | Mobile Phone | Years of Experience | ");
            while (rs.next()) {
                int id = rs.getInt(1);
                String name = rs.getString(2);
                int phone = rs.getInt(3);
                int experience = rs.getInt(4);
                System.out.println("| " + id + " | " + name + " | " + phone + " | " + experience + " | ");
            }
            System.out.println("End of Query");
            rs.close();
            pstmt.close();
            conn.close();
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    public static void countTransaction() {
        int lowerBound, upperBound;
        do {
            System.out.print("Type in the lower bound for years of experience: ");
            lowerBound = scan.nextInt();
            System.out.print("Type in the upper bound for years of experience: ");
            upperBound = scan.nextInt();
            System.out.println();
        } while (lowerBound < 0 || upperBound < 0 || (upperBound - lowerBound < 0));

        try {
            String sqlStatement = "SELECT s.sId, s.sName, s.sExperience, COUNT(*) FROM salesperson s, transaction_records t WHERE s.sId=t.tSalespersonId AND s.sExperience >= "
                    + Integer.toString(lowerBound) + " AND s.sExperience<= " + Integer.toString(upperBound)
                    + " GROUP BY s.sId, s.sName, s.sExperience;";
            Connection conn = getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sqlStatement);
            ResultSet rs = pstmt.executeQuery();

            System.out.println("Transaction Record:");
            System.out.println("| ID | Name | Years of Experience | Number of Transaction | ");
            while (rs.next()) {
                int id = rs.getInt(1);
                String name = rs.getString(2);
                int experience = rs.getInt(3);
                int transaction = rs.getInt(4);
                System.out.println("| " + id + " | " + name + " | " + experience + " | " + transaction + " | ");
            }
            System.out.println("End of Query");
            rs.close();
            pstmt.close();
            conn.close();
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    public static void showTotalSales() {
        System.out.println("| Manufacturer ID | Manufacturer Name | Total Sales Value | ");
        try {
            String sqlStatement = "SELECT m.mId, m.mName, SUM(p.pPrice) FROM manufacturer m, part p, transaction_records t WHERE t.tPartId=p.pId AND p.pManufacturerId=m.mId GROUP BY m.mId, m.mName ORDER BY SUM(p.pPrice) DESC;";
            Connection conn = getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sqlStatement);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt(1);
                String name = rs.getString(2);
                int sales = rs.getInt(3);
                System.out.println("| " + id + " | " + name + " | " + sales + " | ");
            }
            conn.close();
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        System.out.println("End of Query");
    }

    public static void showPopular() {
        int input = 0;
        do {
            System.out.print("Enter Your Choice: ");
            input = scan.nextInt();
            if (input <= 0)
                System.out.println("Invalid input. N should be an integer larger than 0");
        } while (input <= 0);

        try {
            String sqlStatement;
            sqlStatement = "SELECT p.pId, p.pName, COUNT(*) FROM part AS p, transaction_records AS t WHERE t.tPartId=p.pID GROUP BY p.pID, p.pName ORDER BY COUNT(*) DESC LIMIT "
                    + Integer.toString(input) + ";";

            Connection conn = getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sqlStatement);
            ResultSet rs = pstmt.executeQuery();

            System.out.println("| Part ID | Part Name | Number of Transaction |");
            while (rs.next()) {
                int id = rs.getInt(1);
                String name = rs.getString(2);
                int transaction = rs.getInt(3);
                System.out.println("| " + id + " | " + name + " | " + transaction + " | ");
            }
            rs.close();
            pstmt.close();
            conn.close();
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        System.out.println("End of Query");
    }
}