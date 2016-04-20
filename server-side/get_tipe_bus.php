<?php

/*
 * kode untuk tampilak semua produk, pada halaman home
 */

$response = array();


// include db connect class
include '../admin/config/koneksi.php';

$id_bus = $_GET['id_bus'];
$result = mysql_query("SELECT * FROM bus, tipe_bus WHERE bus.id_tipe_bus=tipe_bus.id_tipe_bus AND bus.id_bus='$id_bus'") or die(mysql_error());

// cek
if (mysql_num_rows($result) > 0) {
    $response["tipe_bus"] = array();
    
    while ($row = mysql_fetch_array($result)) {
        $tipe_bus = array();
        $tipe_bus["id_tipe_bus"] = $row["id_tipe_bus"];
        $tipe_bus["nm_tipe"] = $row["nm_tipe"];
        $tipe_bus["jumlah_kursi"] = $row["jumlah_kursi"];
        $tipe_bus["harga"] = $row["harga"];
        array_push($response["tipe_bus"], $tipe_bus);
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
