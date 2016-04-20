<?php

/*
 * kode untuk tampilak semua produk, pada halaman home
 */

$response = array();


// include db connect class
include '../admin/config/koneksi.php';
$tanggal = $_GET['tanggal'];
$jadwal = mysql_query("SELECT * FROM jadwal WHERE tgl_berangkat='$tanggal' GROUP BY id_bus");

while($data_jadwal = mysql_fetch_array($jadwal)) {
    $datas = $data_jadwal['id_bus'];
}
    
    $result = mysql_query("SELECT * FROM bus,tipe_bus WHERE bus.id_tipe_bus=tipe_bus.id_tipe_bus AND id_bus='$datas'") or die(mysql_error());

    // cek
    if (mysql_num_rows($result) > 0) {
        $response["bus"] = array();
        
        while ($row = mysql_fetch_array($result)) {
            $bus = array();
            $bus["id_bus"] = $row["id_bus"];
            $bus["nm_bus"] = $row["nm_bus"];
            $bus["nm_supir"] = $row["nm_supir"];
            $bus["no_polisi"] = $row["no_polisi"];
            $bus["nm_tipe"] = $row["nm_tipe"];
            $bus["tgl_bus"] = $row["tgl_bus"];
            array_push($response["bus"], $bus);
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
