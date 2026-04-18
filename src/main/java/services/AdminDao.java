package services;

import models.User;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AdminDao {

    private static final String filePath = System.getProperty("user.home") + java.io.File.separator + "movie-rental-data" + java.io.File.separator + "admins.txt";

    // Add a new admin
    public static void addAdmin(String name, String username, String email, String password) {
        if (isAdminExist(email)) {
            throw new IllegalArgumentException("Admin with this email already exists.");
        }

        String adminId = "A" + System.currentTimeMillis();
        User admin = new User(adminId, name, username, email, password, "ADMIN");
        saveAdmin(admin);
    }

    private static void saveAdmin(User admin) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(admin.toFileFormat());
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Check if admin exists by email
    public static boolean isAdminExist(String email) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4 && parts[3].equalsIgnoreCase(email)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Authenticate admin by username and password
    public static User authenticateAdmin(String username, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 6) {
                    String storedUsername = parts[2];
                    String storedPassword = parts[4];
                    String role = parts[5];
                    if (storedUsername.equals(username) && storedPassword.equals(password) && "ADMIN".equals(role)) {
                        return new User(parts[0], parts[1], storedUsername, parts[3], storedPassword, role);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;  // Authentication failed
    }

    // Get all admins
    public static List<User> getAllAdmins() {
        List<User> admins = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                admins.add(User.fromFileFormat(line));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return admins;
    }

    // Update admin details
    public static boolean updateAdmin(String oldEmail, String newName, String newUsername, String newPassword) {
        List<String> admins = new ArrayList<>();
        boolean updated = false;
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 6) {
                    if (parts[3].equalsIgnoreCase(oldEmail)) {
                        admins.add(parts[0] + "," + newName + "," + newUsername + "," + oldEmail + "," + newPassword + "," + parts[5]);
                        updated = true;
                    } else {
                        admins.add(line);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        if (updated) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, false))) {
                for (String admin : admins) {
                    writer.write(admin);
                    writer.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return updated;
    }

    // Delete admin
    public static boolean deleteAdmin(String email) {
        List<String> admins = new ArrayList<>();
        boolean deleted = false;
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    if (parts[3].equalsIgnoreCase(email)) {
                        deleted = true;
                    } else {
                        admins.add(line);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        if (deleted) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, false))) {
                for (String admin : admins) {
                    writer.write(admin);
                    writer.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return deleted;
    }
}
