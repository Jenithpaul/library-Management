package library_management;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author jenit
 */
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class DatabaseInteractionGUI extends JFrame {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/library";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "YourPassword"; // Change this to your actual password

    private Connection conn;

    private JTextField studentUSNField, bookIdField;
    private JButton enterRecordButton, displayBooksButton, displayBorrowingRecordsButton, returnBookButton, exitButton, getStudentDataButton;
    private JLabel connectionStatusLabel;
    private JTable dataTable;
    private DefaultTableModel model;

    public DatabaseInteractionGUI() {
        createUI();
        establishConnection();
    }

    private void createUI() {
        setTitle("Library Management System");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(Color.BLACK);
        mainPanel.setFont(new Font("Arial", Font.PLAIN, 16));

        connectionStatusLabel = new JLabel("Status: Connecting to database...", JLabel.CENTER);
        connectionStatusLabel.setForeground(Color.decode("#39ff14"));
        mainPanel.add(connectionStatusLabel, BorderLayout.NORTH);

        dataTable = new JTable();
        dataTable.setBackground(Color.BLACK);
        dataTable.setForeground(Color.decode("#39ff14"));
        dataTable.setFont(new Font("Arial", Font.PLAIN, 16));
        model = new DefaultTableModel();
        dataTable.setModel(model);
        JScrollPane scrollPane = new JScrollPane(dataTable);
        scrollPane.setBackground(Color.BLACK);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setBackground(Color.BLACK);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        displayBooksButton = new JButton("Display Books");
        displayBooksButton.addActionListener(e -> displayBooks());
        displayBooksButton.setForeground(Color.BLACK);
        displayBooksButton.setBackground(Color.BLACK);
        displayBooksButton.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        buttonPanel.add(displayBooksButton, gbc);

        displayBorrowingRecordsButton = new JButton("Display Borrowing Records");
        displayBorrowingRecordsButton.addActionListener(e -> displayBorrowingRecords());
        displayBorrowingRecordsButton.setForeground(Color.BLACK);
        displayBorrowingRecordsButton.setBackground(Color.BLACK);
        displayBorrowingRecordsButton.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 1;
        gbc.gridy = 0;
        buttonPanel.add(displayBorrowingRecordsButton, gbc);

        enterRecordButton = new JButton("Enter Record");
        enterRecordButton.addActionListener(e -> enterRecord());
        enterRecordButton.setForeground(Color.BLACK);
        enterRecordButton.setBackground(Color.BLACK);
        enterRecordButton.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 1;
        buttonPanel.add(enterRecordButton, gbc);

        returnBookButton = new JButton("Return Book");
        returnBookButton.addActionListener(e -> returnBook());
        returnBookButton.setForeground(Color.BLACK);
        returnBookButton.setBackground(Color.BLACK);
        returnBookButton.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 1;
        gbc.gridy = 1;
        buttonPanel.add(returnBookButton, gbc);

        exitButton = new JButton("Exit");
        exitButton.addActionListener(e -> System.exit(0));
        exitButton.setForeground(Color.BLACK);
        exitButton.setBackground(Color.BLACK);
        exitButton.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 2;
        buttonPanel.add(exitButton, gbc);

        getStudentDataButton = new JButton("Get Student Data");
        getStudentDataButton.addActionListener(e -> getStudentData());
        getStudentDataButton.setForeground(Color.BLACK);
        getStudentDataButton.setBackground(Color.BLACK);
        getStudentDataButton.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 1;
        gbc.gridy = 2;
        buttonPanel.add(getStudentDataButton, gbc);

        JLabel studentUSNLabel = new JLabel("Student USN:");
        studentUSNLabel.setForeground(Color.decode("#39ff14"));
        studentUSNLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 3;
        buttonPanel.add(studentUSNLabel, gbc);

        studentUSNField = new JTextField(10);
        studentUSNField.setBackground(Color.BLACK);
        studentUSNField.setForeground(Color.decode("#39ff14"));
        gbc.gridx = 1;
        gbc.gridy = 3;
        buttonPanel.add(studentUSNField, gbc);

        JLabel bookIdLabel = new JLabel("Book ID:");
        bookIdLabel.setForeground(Color.decode("#39ff14"));
        bookIdLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 4;
        buttonPanel.add(bookIdLabel, gbc);

        bookIdField = new JTextField(10);
        bookIdField.setBackground(Color.BLACK);
        bookIdField.setForeground(Color.decode("#39ff14"));
        gbc.gridx = 1;
        gbc.gridy = 4;
        buttonPanel.add(bookIdField, gbc);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.setForeground(Color.decode("#39ff14"));
        mainPanel.setBackground(Color.BLACK);

        add(mainPanel);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                updateConnectionStatus();
            }
        });
    }

    private void establishConnection() {
        try {
            conn = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
            System.out.println("Connected to the database");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error connecting to database", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    private void updateConnectionStatus() {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            if (connection.isValid(5)) {
                connectionStatusLabel.setText("Status: Connected to database");
            } else {
                connectionStatusLabel.setText("Status: Disconnected from database");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            connectionStatusLabel.setText("Status: Error connecting to database");
            throw new RuntimeException("Error establishing database connection", e);
        }
    }

    private void displayBooks() {
        try {
            System.out.println("Displaying books...");
            String selectQuery = "SELECT Book_id, Author_Name, Title, Available FROM books";
            try (Statement statement = conn.createStatement();
                 ResultSet resultSet = statement.executeQuery(selectQuery)) {
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();

                String[] columnNames = new String[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    columnNames[i - 1] = metaData.getColumnName(i);
                }
                model.setColumnIdentifiers(columnNames);

                model.setRowCount(0);

                while (resultSet.next()) {
                    Object[] row = new Object[columnCount];
                    for (int i = 1; i <= columnCount; i++) {
                        if (metaData.getColumnName(i).equals("Available")) {
                            row[i - 1] = resultSet.getBoolean(i) ? "Available" : "Not Available";
                        } else {
                            row[i - 1] = resultSet.getObject(i);
                        }
                    }
                    model.addRow(row);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error retrieving book data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void enterRecord() {
        String studentUSN = studentUSNField.getText();
        String bookId = bookIdField.getText();

        if (studentUSN.isEmpty() || bookId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!isValidStudent(studentUSN)) {
            JOptionPane.showMessageDialog(this, "No such student found in the database.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String sql = "INSERT INTO borrowing_records (Borrowed_by, Book_id, Borrowed_Date) VALUES (?, ?, NOW())";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentUSN);
            pstmt.setInt(2, Integer.parseInt(bookId));
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Record entered successfully", "Success", JOptionPane.INFORMATION_MESSAGE);

            displayBooks();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error entering record: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displayBorrowingRecords() {
        try {
            System.out.println("Displaying borrowing records...");
            String selectQuery = "SELECT @rownum:=@rownum+1 as Sl_No, Borrowed_by, Book_id, Borrowed_Date, Returned_Date FROM borrowing_records, (SELECT @rownum:=0) r";
            try (Statement statement = conn.createStatement();
                 ResultSet resultSet = statement.executeQuery(selectQuery)) {
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();

                String[] columnNames = new String[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    columnNames[i - 1] = metaData.getColumnName(i);
                }
                model.setColumnIdentifiers(columnNames);

                model.setRowCount(0);

                while (resultSet.next()) {
                    Object[] row = new Object[columnCount];
                    for (int i = 1; i <= columnCount; i++) {
                        row[i - 1] = resultSet.getObject(i);
                    }
                    model.addRow(row);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error retrieving borrowing records: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void returnBook() {
        String studentUSN = studentUSNField.getText();
        String bookId = bookIdField.getText();

        if (studentUSN.isEmpty() || bookId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!isValidStudent(studentUSN)) {
            JOptionPane.showMessageDialog(this, "No such student found in the database.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String updateQuery = "UPDATE borrowing_records SET Returned = TRUE, Returned_Date = NOW() WHERE Borrowed_by = ? AND Book_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {
                pstmt.setString(1, studentUSN);
                pstmt.setInt(2, Integer.parseInt(bookId));
                int updatedRows = pstmt.executeUpdate();
                if (updatedRows == 0) {
                    JOptionPane.showMessageDialog(this, "No borrowing record found for this student and book", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            String updateAvailabilityQuery = "UPDATE books SET Available = TRUE WHERE Book_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateAvailabilityQuery)) {
                pstmt.setInt(1, Integer.parseInt(bookId));
                pstmt.executeUpdate();
            }

            JOptionPane.showMessageDialog(this, "Book returned successfully", "Success", JOptionPane.INFORMATION_MESSAGE);

            model.setRowCount(0);

            displayBooks();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error returning book: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void getStudentData() {
        String usn = studentUSNField.getText();
        String query = "SELECT br.Borrowed_by, s.Student_name, br.Book_id, b.Title, br.Borrowed_Date, br.Returned_Date " +
                "FROM borrowing_records br " +
                "LEFT JOIN student s ON br.Borrowed_by = s.USN " +
                "LEFT JOIN books b ON br.Book_id = b.Book_id " +
                "WHERE br.Borrowed_by = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, usn);
            try (ResultSet resultSet = pstmt.executeQuery()) {
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();

                String[] columnNames = new String[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    columnNames[i - 1] = metaData.getColumnName(i);
                }
                model.setColumnIdentifiers(columnNames);

                model.setRowCount(0);

                while (resultSet.next()) {
                    Object[] row = new Object[columnCount];
                    for (int i = 1; i <= columnCount; i++) {
                        row[i - 1] = resultSet.getObject(i) != null ? resultSet.getObject(i) : "null";
                    }
                    model.addRow(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error retrieving student data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean isValidStudent(String usn) {
        // You can implement your logic to check if the student is valid
        return true;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Set the UI look and feel to the system's default
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
                e.printStackTrace();
            }

            DatabaseInteractionGUI gui = new DatabaseInteractionGUI();
            gui.setVisible(true);
        });
    }
}
