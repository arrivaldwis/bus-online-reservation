<?php

/*
 * kode untuk tampilak semua produk, pada halaman home
 */

$response = array();


// include db connect class
include '../admin/config/koneksi.php';

$result = mysql_query("SELECT * FROM rute ORDER BY id_rute desc") or die(mysql_error());

// cek
if (mysql_num_rows($result) > 0) {
    $response["rute"] = array();
    
    while ($row = mysql_fetch_array($result)) {
        $rute = array();
        $rute["id_rute"] = $row["id_rute"];
        $rute["nm_pool"] = $row["nm_pool"];
        $rute["lokasi_berangkat"] = $row["lokasi_berangkat"];
        $rute["kota_awal"] = $row["kota_awal"];
        $rute["kota_akhir"] = $row["kota_akhir"];
        $rute["tarif"] = $row["tarif"];
        $rute["jam_berangkat"] = $row["jam_berangkat"];
        array_push($response["rute"], $rute);
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
