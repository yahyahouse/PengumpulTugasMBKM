package com.fasilkom.pengumpulmbkm.view;

import com.fasilkom.pengumpulmbkm.model.response.LaporanResponse;
import lombok.Getter;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class LaporanAdmin extends JDialog {
    private JTable tableLaporan;
    private final DefaultTableModel tableModel;
    private JButton closeButton;
    private JPanel laporanPanel;

//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(new Runnable() {
//            @Override
//            public void run() {
//                LaporanAdmin laporanAdmin = null;
//                try {
//                    laporanAdmin = new LaporanAdmin(null);
//                    laporanAdmin.setVisible(true);
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//
//            }
//        });
//    }

    public LaporanAdmin(JFrame parent) throws IOException {
        setTitle("Laporan");
        setContentPane(laporanPanel);
        setMinimumSize(new Dimension(450, 450));
        setMaximumSize(new Dimension(450, 450));
        setModal(true);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        tableModel = new DefaultTableModel();
        tableModel.addColumn("Laporan ID");
        tableModel.addColumn("User ID");
        tableModel.addColumn("Dosen ID");
        tableModel.addColumn("Laporan");
        tableModel.addColumn("Verifikasi");
        tableModel.addColumn("Catatan");
        tableModel.addColumn("Waktu Pengumpulan");
        tableModel.addColumn("Waktu Update");
        tableLaporan.setModel(tableModel);

        populateTable();
        pack();
        setLocationRelativeTo(null);
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    private void populateTable() throws IOException {
        // Panggil API untuk mendapatkan data dari sumber eksternal
        List<Laporan> data = fetchDataFromURL();

        // Hapus semua baris yang ada di model tabel sebelum menambahkan data baru
        tableModel.setRowCount(0);

        // Tambahkan baris-baris data ke dalam DefaultTableModel
        for (Laporan laporan : data) {
            Object[] rowData = {
                    laporan.getLaporanId(),
                    laporan.getUserId(),
                    laporan.getDosenId(),
                    laporan.getLaporan(),
                    laporan.getVerifikasi() != null ? laporan.getVerifikasi() : "",
                    laporan.getCatatan() != null ? laporan.getCatatan() : "",
                    laporan.getWaktuPengumpulan(),
                    laporan.getWaktuUpdate() != null ? laporan.getWaktuUpdate() : ""
            };
            tableModel.addRow(rowData);
        }

    }

    private List<Laporan> fetchDataFromURL() {
        List<Laporan> data = new ArrayList<>();
        // Implementasikan kode untuk mengambil data dari API dengan menggunakan authorization JWT
        try {

            URL url = new URL("http://localhost:8080/admin/all-laporan");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + UserSession.getToken());
            conn.setDoOutput(true);

            // Gunakan metode koneksi ke API yang sesuai dengan kebutuhan Anda
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder responseBuilder = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    responseBuilder.append(line);
                }
                reader.close();

                String jsonResponse = responseBuilder.toString();
                JSONArray jsonArray = new JSONArray(jsonResponse);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonLaporan = jsonArray.getJSONObject(i);
                    int laporanId = jsonLaporan.getInt("laporanId");
                    int userId = jsonLaporan.getInt("userId");
                    int dosenId = jsonLaporan.getInt("dosenId");
                    String laporan = jsonLaporan.getString("laporan");
                    String verifikasi = "";
                    if (!jsonLaporan.isNull("verifikasi")) {
                        verifikasi = jsonLaporan.getString("verifikasi");
                    }
                    String catatan = "";
                    if (!jsonLaporan.isNull("verifikasi")) {
                        catatan = jsonLaporan.getString("catatan");
                    }
                    String waktuPengumpulan = jsonLaporan.getString("waktuPengumpulan");
                    String waktuUpdate = "";
                    if (!jsonLaporan.isNull("verifikasi")) {
                        waktuUpdate = jsonLaporan.getString("waktuUpdate");
                    }
                    // Tambahkan data ke dalam List
                    data.add(new Laporan(laporanId, userId, dosenId, laporan, verifikasi, catatan, waktuPengumpulan, waktuUpdate));
                }
            } else {
                conn.getResponseCode();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    private static class Laporan {

        @Getter
        private final int laporanId;

        @Getter
        private final int userId;

        @Getter
        private final int dosenId;
        @Getter
        private final String laporan;
        @Getter
        private final String verifikasi;
        @Getter
        private final String catatan;
        @Getter
        private final String waktuPengumpulan;
        @Getter
        private final String waktuUpdate;

        public Laporan(int laporanId, int userId, int dosenId, String laporan, String verifikasi, String catatan, String waktuPengumpulan, String waktuUpdate) {
            this.laporanId = laporanId;
            this.userId = userId;
            this.dosenId = dosenId;
            this.laporan = laporan;
            this.verifikasi = verifikasi;
            this.catatan = catatan;
            this.waktuPengumpulan = waktuPengumpulan;
            this.waktuUpdate = waktuUpdate;
        }

    }
}
