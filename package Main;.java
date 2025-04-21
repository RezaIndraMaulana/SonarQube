package Main;

import java.sql.*;
import java.util.Scanner;

interface StudentExchangeEligible {
    void studentExchange();
}

// Parent Class Mahasiswa
class Mahasiswa {
    protected String nim;
    protected String nama;
    protected String prodi;
    protected String angkatan;

    public Mahasiswa(String nim, String nama, String prodi, String angkatan) {
        this.nim = nim;
        this.nama = nama;
        this.prodi = prodi;
        this.angkatan = angkatan;
    }

    public String getNim() { return nim; }
    public String getNama() { return nama; }
    public String getProdi() { return prodi; }
    public String getAngkatan() { return angkatan; }
}

// Child Class Mahasiswa Reguler
class MahasiswaReguler extends Mahasiswa {
    public MahasiswaReguler(String nim, String nama, String prodi, String angkatan) {
        super(nim, nama, prodi, angkatan);
    }
}

// Child Class Mahasiswa Internasional
class MahasiswaInternasional extends Mahasiswa implements StudentExchangeEligible {
    public MahasiswaInternasional(String nim, String nama, String prodi, String angkatan) {
        super(nim, nama, prodi, angkatan);
    }

    @Override
    public void studentExchange() {
        System.out.println("Berhak mengikuti Program Student Exchange ke luar negeri");
    }
}

class connectDatabase {
    private final String url = "jdbc:mysql://localhost:3306/tpmod8";
    private final String user = "root";
    private final String pass = "";
    private Connection conn;

    public void connect() {
        try {
            conn = DriverManager.getConnection(url, user, pass);
        } catch (SQLException e) {
            System.out.println("Koneksi Gagal: " + e.getMessage());
        }
    }

    public void save(Mahasiswa mhs) {
        String sql = "INSERT INTO mahasiswa(nim, nama, prodi, angkatan) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, mhs.getNim());
            stmt.setString(2, mhs.getNama());
            stmt.setString(3, mhs.getProdi());
            stmt.setString(4, mhs.getAngkatan());
            stmt.executeUpdate();
            System.out.println("Data Mahasiswa Berhasil Ditambahkan");
        } catch (SQLException e) {
            System.out.println("Kesalahan Insert: " + e.getMessage());
        }
    }

    public void disconnect() {
        try {
            if (conn != null) conn.close();
        } catch (SQLException e) {
            System.out.println("Kesalahan Menutup Koneksi: " + e.getMessage());
        }
    }
}

// Main Class
public class soal {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        System.out.println("=== Program Mahasiswa (Versi SOLID) ===");

        System.out.print("Masukkan NIM: ");
        String nim = input.nextLine();

        System.out.print("Masukkan Nama: ");
        String nama = input.nextLine();

        System.out.print("Masukkan Prodi: ");
        String prodi = input.nextLine();

        System.out.print("Masukkan Angkatan: ");
        String angkatan = input.nextLine();

        System.out.print("Status Mahasiswa (1. Reguler | 2. Internasional): ");
        int status = input.nextInt();

        Mahasiswa mhs;
        if (status == 1) {
            mhs = new MahasiswaReguler(nim, nama, prodi, angkatan);
        } else {
            mhs = new MahasiswaInternasional(nim, nama, prodi, angkatan);
        }

        // Simpan ke DB
        connectDatabase repo = new connectDatabase();
        repo.connect();
        repo.save(mhs);
        repo.disconnect();

        // Jalankan student exchange hanya jika bisa (ISP)
        if (mhs instanceof StudentExchangeEligible) {
            ((StudentExchangeEligible) mhs).studentExchange();
        }

        System.out.println("Program Selesai.");
    }
}
