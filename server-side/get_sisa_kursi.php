<?php

/*
 * kode untuk tampilak semua produk, pada halaman home
 */

$response = array();


// include db connect class
include '../admin/config/koneksi.php';

if (isset($_GET['id_tipe_bus'])) {
    $id_tipe_bus = $_GET['id_tipe_bus'];
}

$result = mysql_query("SELECT * FROM pemesanan, tipe_bus WHERE pemesanan.id_tipe_bus=tipe_bus.id_tipe_bus AND tipe_bus.id_tipe_bus='$id_tipe_bus'") or die(mysql_error());

// cek
if (mysql_num_rows($result) > 0) {
    $response["pemesanan"] = array();
    
    while ($row = mysql_fetch_array($result)) {
        $sisaKursi = array();
        $sisaKursi["id_pemesanan"] = $row["id_pemesanan"];
        $sisaKursi["no_kursi"] = $row["no_kursi"];
        array_push($response["pemesanan"], $sisaKursi);
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
