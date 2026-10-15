// package com.feemanager.login;
package com.feemanager;

import com.feemanager.login.LoginFrame;
import javax.swing.*;
import javax.swing.SwingUtilities;

/**
 * Main class
 *
 * Ye application ka starting point hai.
 * Program run hote hi Login Screen open hogi.
 */
public class Main {

    public static void main(String[] args) {

        /*
         * Swing UI ko Event Dispatch Thread (EDT)
         * par run karna Java Swing ki best practice hai.
         */
        SwingUtilities.invokeLater(() -> {

            // Login screen ka object create kar rahe hain
            LoginFrame loginFrame = new LoginFrame();

            // Login window ko screen par show kar rahe hain
            loginFrame.setVisible(true);
        });
    }
}