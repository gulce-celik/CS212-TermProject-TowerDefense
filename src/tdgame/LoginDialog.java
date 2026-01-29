package tdgame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginDialog extends JDialog {

    private JTextField nameField;
    private JPasswordField passwordField;

    private JButton loginButton;
    private JButton registerButton;
    private JButton cancelButton;

    public LoginDialog(JFrame parent) {
        super(parent, "Login", true); // modal
        initUI();
        pack();
        setLocationRelativeTo(parent);
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10)); //Ekranı Ortaya (Form) ve Alta (Butonlar) böl

        // Form alanları Ortadaki formu satır-sütun şeklinde hizala
        JPanel formPanel = new JPanel(new GridLayout(2, 2, 5, 5)); //iç kutu

        JLabel nameLabel = new JLabel("Name:");
        JLabel passwordLabel = new JLabel("Password:");

        nameField = new JTextField(15);
        passwordField = new JPasswordField(15);

        formPanel.add(nameLabel);
        formPanel.add(nameField);
        formPanel.add(passwordLabel);
        formPanel.add(passwordField);

        add(formPanel, BorderLayout.CENTER);

        // Butonlar
        loginButton = new JButton("Login");
        registerButton = new JButton("Register");
        cancelButton = new JButton("Cancel");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // Buton davranışları
        // Login button
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = nameField.getText().trim();
                String password = new String(passwordField.getPassword());

                User user = UserManager.login(username, password);
                if (user != null) {
                    JOptionPane.showMessageDialog(
                            LoginDialog.this,
                            "Login successful. Welcome, " + username + "!",
                            "Login",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    dispose(); // close dialog
                } else {
                    JOptionPane.showMessageDialog(
                            LoginDialog.this,
                            "Invalid username or password.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });

        // Register button
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = nameField.getText().trim();
                String password = new String(passwordField.getPassword());

                boolean ok = UserManager.register(username, password);
                if (ok) {
                    JOptionPane.showMessageDialog(
                            LoginDialog.this,
                            "Registration successful for user: " + username,
                            "Register",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                } else {
                    JOptionPane.showMessageDialog(
                            LoginDialog.this,
                            "Registration failed. (Empty fields or user already exists.)",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });

        // Cancel button
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // just close dialog
            }
        });

    }
}
