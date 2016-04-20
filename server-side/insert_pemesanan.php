<?php

/*
 * Buat pendaftaran baru
 */

$response = array();

// cek form
if (isset($_POST['nm_pemesan']) && isset($_POST['no_hp']) && isset($_POST['id_rute']) && isset($_POST['jadwal_berangkat']) && isset($_POST['jam_berangkat']) && isset($_POST['id_bus']) && isset($_POST['id_tipe_bus']) && isset($_POST['no_kursi']) && isset($_POST['nm_penumpang']) && isset($_POST['total_harga'])) {
    
    $nm_pemesan = $_POST['nm_pemesan'];
    $no_hp = $_POST['no_hp'];
    $id_rute = $_POST['id_rute'];
    $jadwal_berangkat = $_POST['jadwal_berangkat'];
    $jam_berangkat = $_POST['jam_berangkat'];
    $id_bus = $_POST['id_bus'];
    $id_tipe_bus = $_POST['id_tipe_bus'];
    $no_kursi = $_POST['no_kursi'];
    $nm_penumpang = $_POST['nm_penumpang'];
    $total_harga = $_POST['total_harga'];
    $jum_penumpang = $_POST['jum_penumpang'];

    // include db connect
    include '../admin/config/koneksi.php';

    if(substr($no_hp,0,1) == 0){
        $no_hp_baru = substr($no_hp,1);
    } else {
        $no_hp_baru = $no_hp;
    }

    $select_total = mysql_num_rows(mysql_query("SELECT * FROM konfirmasi"));
    $random = substr(str_shuffle(str_repeat('ABCDEFGHIJKLMNOPQRSTUVWXYZ',5)),0,10);

    $no_tiket = date("dmYHis").$random.$select_total;

    $data_tipe_bus = mysql_fetch_array(mysql_query("SELECT * FROM tipe_bus WHERE id_tipe_bus='$id_tipe_bus'"));
    $harga_tipe_bus = $data_tipe_bus['harga'];

    $data_rute = mysql_fetch_array(mysql_query("SELECT * FROM rute WHERE id_rute='$id_rute'"));
    $tarif_rute = $data_rute['tarif'];

    $total_harga = ($harga_tipe_bus+$tarif_rute)*$jum_penumpang;
    $tgl_input = date("Y-m-d H:i:s");

    // insert ke db
    $result = mysql_query("INSERT INTO pemesanan(nm_pemesan, no_hp_pemesan, id_rute, jadwal_berangkat, jam_berangkat, id_bus, id_tipe_bus, no_kursi, nm_penumpang, status_pemesan, no_verifikasi, no_tiket, total_harga, tgl_input) VALUES('$nm_pemesan', '$no_hp_baru', '$id_rute','$jadwal_berangkat','$jam_berangkat','$id_bus','$id_tipe_bus','$no_kursi','$nm_penumpang','1','$no_tiket','','$total_harga','$tgl_input')");
    // cek data udah masuk belum
    if ($result) {
        // kalo sukses
        $response["success"] = 1;
        $response["message"] = "Pendaftaran anda berhasil";
        $response["no_aktivasi"] = $no_tiket;

        // echoing JSON response
        echo json_encode($response);
    } else {
        // fkalo gagal
        $response["success"] = 0;
        $response["message"] = "Sistem mendeteksi kesalahan, silahkan coba lagi";
        
        // echoing JSON response
        echo json_encode($response);
    }
} else {
    $response["success"] = 0;
    $response["message"] = "Silahkan lengkapi aksi sebelum memulai permintaan anda";

    // echoing JSON response
    echo json_encode($response);
}
?>
