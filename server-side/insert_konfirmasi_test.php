<?php

$target_path = "../img/bukti_transfer/";

$target_path = $target_path . basename( $_FILES['uploadedfile']['name']);

if(move_uploaded_file($_FILES['uploadedfile']['tmp_name'], $target_path)) {
echo "File ". basename( $_FILES['uploadedfile']['name']).
" berhasil diupload";
} else{
echo "Terjadi kesalahan!";
}

?>