<?php

/*
 * kode untuk tampilak semua produk, pada halaman home
 */

$response = array();


// include db connect class
include '../admin/config/koneksi.php';

if (isset($_GET['no_tiket'])) {
    $no_tiket = $_GET['no_tiket'];
}

$result = mysql_query("SELECT * FROM pemesanan INNER JOIN bus ON pemesanan.id_bus = bus.id_bus INNER JOIN rute ON pemesanan.id_rute = rute.id_rute INNER JOIN tipe_bus ON pemesanan.id_tipe_bus = tipe_bus.id_tipe_bus WHERE no_tiket='$no_tiket'") or die(mysql_error());

// cek
if (mysql_num_rows($result) > 0) {
    $response["pemesanan"] = array();
    
    while ($row = mysql_fetch_array($result)) {
        $pemesanan = array();
        $pemesanan["id_pemesanan"] = $row["id_pemesanan"];
        $pemesanan["nm_pemesan"] = $row["nm_pemesan"];
        $pemesanan["no_hp_pemesan"] = $row["no_hp_pemesan"];
        $pemesanan["rute"] = $row["kota_awal"]." - ".$row["kota_akhir"];
        $pemesanan["jadwal_berangkat"] = $row["jadwal_berangkat"];
        $pemesanan["jam_berangkat"] = $row["jam_berangkat"];
        $pemesanan["nm_bus"] = $row["nm_bus"];
        $pemesanan["nm_tipe"] = $row["nm_tipe"];
        $pemesanan["no_kursi"] = $row["no_kursi"];
        $pemesanan["nm_penumpang"] = $row["nm_penumpang"];
        $pemesanan["no_tiket"] = $row["no_tiket"];
        array_push($response["pemesanan"], $pemesanan);
    }

    // sukses
    $response["success"] = 1;

    // echo JSON response
    echo json_encode($response);
} else {
    $response["success"] = 0;
    $response["message"] = "Tidak ada data yang ditemukan";

    echo json_encode($response);
}
?>
