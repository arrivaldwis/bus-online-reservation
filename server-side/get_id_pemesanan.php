<?php

/*
 * kode untuk tampilak semua produk, pada halaman home
 */

$response = array();


// include db connect class
include '../admin/config/koneksi.php';

if (isset($_GET['no_verifikasi'])) {
    $no_verifikasi = $_GET['no_verifikasi'];
}

$result = mysql_query("SELECT * FROM pemesanan WHERE no_verifikasi='$no_verifikasi' AND status_pemesan='1'") or die(mysql_error());

// cek
if (mysql_num_rows($result) > 0) {
    $response["pemesanan"] = array();
    
    while ($row = mysql_fetch_array($result)) {
        $pemesanan = array();
        $pemesanan["id_pemesanan"] = $row["id_pemesanan"];
        $pemesanan["total_harga"] = $row["total_harga"];
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
