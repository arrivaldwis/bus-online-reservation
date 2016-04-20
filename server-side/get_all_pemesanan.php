<?php

/*
 * kode untuk tampilak semua produk, pada halaman home
 */

$response = array();


// include db connect class
include '../admin/config/koneksi.php';

$result = mysql_query("SELECT * FROM pemesanan") or die(mysql_error());

// cek
if (mysql_num_rows($result) > 0) {
    $response["pemesanan"] = array();
    
    while ($row = mysql_fetch_array($result)) {
        $pemesanan = array();
        $pemesanan["id_pemesanan"] = $row["id_pemesanan"];
        $pemesanan["nm_pemesan"] = $row["nm_pemesan"];
        $pemesanan["no_hp_pemesan"] = $row["no_hp_pemesan"];
        $pemesanan["id_rute"] = $row["id_rute"];
        $pemesanan["jadwal_berangkat"] = $row["jadwal_berangkat"];
        $pemesanan["jam_berangkat"] = $row["jam_berangkat"];
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
