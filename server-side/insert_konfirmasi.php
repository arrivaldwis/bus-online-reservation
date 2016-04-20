<?php

/*
 * Buat pendaftaran baru
 */

$response = array();

// cek form
if (isset($_POST['id_pemesanan']) && isset($_POST['nm_pengirim']) && isset($_POST['no_identitas']) && isset($_POST['no_rekening']) && isset($_POST['rekening_tujuan']) && isset($_POST['total_bayar']) && isset($_POST['bukti_transfer'])) {
    
    include '../admin/config/koneksi.php';
    $id_pemesanan = $_POST['id_pemesanan'];
    $nm_pengirim = $_POST['nm_pengirim'];
    $no_identitas = $_POST['no_identitas'];
    $no_rekening = $_POST['no_rekening'];
    $rekening_tujuan = $_POST['rekening_tujuan'];
    $total_bayar = $_POST['total_bayar'];
    //$total_harga = $_POST['total_harga'];
    $bukti_transfer = "../img/bukti_transfer/".$_POST['bukti_transfer'];

    $select_total = mysql_num_rows(mysql_query("SELECT * FROM pemesanan"));
    $random = substr(str_shuffle(str_repeat('ABCDEFGHIJKLMNOPQRSTUVWXYZ',5)),0,10);

    $select_pemesan = mysql_fetch_array(mysql_query("SELECT * FROM pemesanan WHERE id_pemesanan='$id_pemesanan'"));
    $tgl_input = strtotime($select_pemesan['tgl_input']);
    $tgl_sekarang = strtotime(date("d-m-Y H:i:s"));
    $selisih = round(abs($tgl_sekarang - $tgl_input)/60,2);

    $no_tiket = date("dmYHis").$random.$select_total;
    $tgl_pengiriman = date("d-m-Y H:i:s");

    // cek data udah masuk belum
    if($selisih >= 5) {
            $response["success"] = 0;
            $response["message"] = "No Verifikasi anda hangus karena batas waktu maksimal konfirmasi adalah 5 menit setelah pemesanan";
            $response["no_tiket"] = "";
            
            echo json_encode($response);
    } else {

        // insert ke db
        $result = mysql_query("INSERT INTO konfirmasi(id_pemesanan,nm_pengirim,no_identitas,no_rekening,rekening_tujuan, bukti_transfer,total_bayar,tgl_pengiriman) values('$id_pemesanan','$nm_pengirim','$no_identitas','$no_rekening','$rekening_tujuan','$bukti_transfer','$total_bayar','$tgl_pengiriman')");

        if ($result) {

        	$result2 = mysql_query("UPDATE pemesanan SET no_tiket='$no_tiket', status_pemesan='2' WHERE id_pemesanan='$id_pemesanan'");

        	if($result2) {
    	        // kalo sukses
    	        $response["success"] = 1;
    	        $response["message"] = "Pendaftaran anda berhasil";
    	        $response["no_tiket"] = $no_tiket;
        	}

            // echoing JSON response
            echo json_encode($response);
        } else {
            // fkalo gagal
            $response["success"] = 0;
            $response["message"] = "Sistem mendeteksi kesalahan, silahkan coba lagi";
            $response["no_tiket"] = "";
            
            // echoing JSON response
            echo json_encode($response);
        }
    }
} else {
    $response["success"] = 0;
    $response["message"] = "Silahkan lengkapi aksi sebelum memulai permintaan anda";
    $response["no_tiket"] = "";

    // echoing JSON response
    echo json_encode($response);
}
?>

