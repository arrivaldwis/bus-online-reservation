<?php

/*
 * kode untuk tampilak semua produk, pada halaman home
 */

$response = array();


// include db connect class
include '../admin/config/koneksi.php';

if (isset($_GET['id_rute'])) {
    $id_rute = $_GET['id_rute'];
}

$result = mysql_query("SELECT * FROM jadwal, bus, rute WHERE jadwal.id_bus=bus.id_bus AND jadwal.id_rute=rute.id_rute AND jadwal.id_rute='$id_rute' ORDER BY jadwal.id_jadwal desc") or die(mysql_error());

// cek
if (mysql_num_rows($result) > 0) {
    $response["jadwal"] = array();
    
    while ($row = mysql_fetch_array($result)) {
        $jadwal = array();
        $jadwal["id_jadwal"] = $row["id_bus"];
        $jadwal["tgl_berangkat"] = $row["tgl_berangkat"];
        $jadwal["jam"] = $row["jam"];
        $jadwal["id_rute"] = $row["id_rute"];
        $jadwal["id_bus"] = $row["id_bus"];
        $jadwal["tgl_jadwal"] = $row["tgl_jadwal"];
        $jadwal["tarif"] = $row["tarif"];
        array_push($response["jadwal"], $jadwal);
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
