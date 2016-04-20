package com.proj.bandungexpress_new;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.nononsenseapps.filepicker.FilePickerActivity;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class KonfirmasiActivity extends AppCompatActivity implements
        TimePickerDialog.OnTimeSetListener,
        DatePickerDialog.OnDateSetListener {

    ActionBar mActionBar;
    EditText etKodeAktivasi, etNamaPengirim, etNoIdentitas, etNoRekening, etTotalHarga, etTotalBayar;
    TextView tvTglPengiriman;
    Spinner spRekeningTujuan;
    Button btnUnggah, btnKonfirmasi, btnCek;
    String fileName = "";
    Calendar now;
    String errorName = "";
    private ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();
    String kodeTiket, idPemesanan, nmPengirim, noIdentitas, noRekening, rekeningTujuan, totalBayar;

    boolean Verified = false;

    private static String url_konfirmasi = "http://sistempemesanan.com/server-side/insert_konfirmasi.php";
    private static String url_id_pemesanan = "http://sistempemesanan.com/server-side/get_id_pemesanan.php";

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_NO_TIKET = "no_tiket";
    private static final String TAG_PEMESANAN = "pemesanan";
    private static final String TAG_ID_PEMESANAN = "id_pemesanan";
    private static final String TAG_TOTAL_HARGA = "total_harga";

    String kodeVerifikasi;
    JSONArray kodeAktivasi = null;
    ArrayList<HashMap<String, String>> kodeAktivasiList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_konfirmasi);
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setTitle("Konfirmasi");

        kodeAktivasiList = new ArrayList<HashMap<String, String>>();

        now = Calendar.getInstance();

        etKodeAktivasi = (EditText) findViewById(R.id.etKodeAktivasi);
        etNamaPengirim = (EditText) findViewById(R.id.etNamaPengirim);
        etNoIdentitas = (EditText) findViewById(R.id.etNoIdentitas);
        etNoRekening = (EditText) findViewById(R.id.etNoRekening);
        etTotalBayar = (EditText) findViewById(R.id.etTotalBayar);
        etTotalHarga = (EditText) findViewById(R.id.etTotalHarga);
        tvTglPengiriman = (TextView) findViewById(R.id.tvTanggal);
        spRekeningTujuan = (Spinner) findViewById(R.id.spRekeningTujuan);

        btnUnggah = (Button) findViewById(R.id.btnUnggah);
        btnKonfirmasi = (Button) findViewById(R.id.btnKonfirmasi);
        btnCek = (Button) findViewById(R.id.btnCek);
        
        etTotalBayar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(etTotalBayar.getText().toString().matches(".*[a-zA-Z]+.*")) {
                    etTotalBayar.setText("");
                    Toast.makeText(KonfirmasiActivity.this, "Isi dengan angka!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        btnCek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!etKodeAktivasi.getText().toString().equals("")) {
                    kodeVerifikasi = etKodeAktivasi.getText().toString();
                    new loadIdPemesanan().execute();
                } else {
                    Toast.makeText(KonfirmasiActivity.this, "Harap Masukkan No Verifikasi!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnUnggah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), FilePickerActivity.class);
                i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
                i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
                i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE);
                i.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().getPath());

                startActivityForResult(i, 0);
            }
        });

        btnKonfirmasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etKodeAktivasi.getText().toString().equals("") || etNamaPengirim.getText().toString().equals("")
                        || etNoIdentitas.getText().toString().equals("") || fileName.equals("") || etNoRekening.getText().toString().equals("")
                        || etTotalBayar.getText().toString().equals("") || Verified == false) {
                    Toast.makeText(getApplicationContext(), "Harap lengkapi data!", Toast.LENGTH_SHORT).show();
                } else {
                    nmPengirim = etNamaPengirim.getText().toString();
                    noIdentitas = etNoIdentitas.getText().toString();
                    noRekening = etNoRekening.getText().toString();
                    rekeningTujuan = spRekeningTujuan.getSelectedItem().toString();
                    totalBayar = etTotalBayar.getText().toString();
                    new InsertKonfirmasi().execute();
                }
            }
        });

        ArrayAdapter<String> adapter;
        List<String> list;

        list = new ArrayList<String>();
        list.add("BCA");
        list.add("BNI");
        list.add("BRI");
        list.add("Mandiri");
        list.add("CIMB Niaga");
        list.add("OCBC NISP");
        list.add("BJB");
        list.add("Bukopin");
        adapter =
                new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, list)
                {

                    public View getView(int position, View convertView, ViewGroup parent) {
                        View v = super.getView(position, convertView, parent);

                        ((TextView) v).setTextSize(16);
                        ((TextView) v).setTextColor(
                                getResources().getColorStateList(R.color.spinner_text)
                        );

                        return v;
                    }

                    public View getDropDownView(int position, View convertView, ViewGroup parent) {
                        View v = super.getDropDownView(position, convertView, parent);
                        //v.setBackgroundResource(R.drawable.spinner_bg);

                        ((TextView) v).setTextColor(
                                getResources().getColorStateList(R.color.spinner_text)
                        );

                        //((TextView) v).setTypeface(fontStyle);
                        ((TextView) v).setGravity(Gravity.CENTER);

                        return v;
                    }
                };

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRekeningTujuan.setAdapter(adapter);

        setTime();
    }

    class loadIdPemesanan extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(KonfirmasiActivity.this);
            pDialog.setMessage("Cek No Verifikasi...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... args) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("no_verifikasi", kodeVerifikasi));
            JSONObject json = jsonParser.makeHttpRequest(url_id_pemesanan, "GET", params);

            Log.d("Semua No Verif: ", json.toString());

            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    kodeAktivasi = json.getJSONArray(TAG_PEMESANAN);

                    for (int i = 0; i < kodeAktivasi.length(); i++) {
                        JSONObject c = kodeAktivasi.getJSONObject(i);

                        String id = c.getString(TAG_ID_PEMESANAN);
                        String total_harga = c.getString(TAG_TOTAL_HARGA);

                        HashMap<String, String> map = new HashMap<String, String>();

                        map.put(TAG_ID_PEMESANAN, id);
                        map.put(TAG_TOTAL_HARGA, total_harga);

                        kodeAktivasiList.add(map);

                        Verified = true;
                    }
                } else {
                    // jika tidak ada data
                    Verified = false;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * jika pekerjaan di belakang layar selesai maka hentikan progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // hentikan progress ketika semua data didapat
            pDialog.dismiss();
            // perbarui screen
            runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * perbarui json ke arraylist
                     * */
                    if(Verified) {
                        idPemesanan = kodeAktivasiList.get(0).get(TAG_ID_PEMESANAN);
                        etTotalHarga.setText("Rp. " + kodeAktivasiList.get(0).get(TAG_TOTAL_HARGA));
                        Toast.makeText(getApplicationContext(), "No Verifikasi Benar", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "No Verifikasi Salah", Toast.LENGTH_SHORT).show();
                    }

                }
            });

        }

    }

    class InsertKonfirmasi extends AsyncTask<String, String, String> {

        /**
         * tampilkan progress dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(KonfirmasiActivity.this);
            pDialog.setMessage("Sedang memproses pemesanan...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * buat bukutamu baru
         * */
        protected String doInBackground(String... args) {
            String id_pemesanan = idPemesanan;
            String nm_pengirim = nmPengirim;
            String no_identitas = noIdentitas;
            String no_rekening = noRekening;
            String rekening_tujuan = rekeningTujuan;
            String total_bayar = totalBayar;
            String bukti_transfer = fileName;

            // parameter
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("id_pemesanan", id_pemesanan));
            params.add(new BasicNameValuePair("nm_pengirim", nm_pengirim));
            params.add(new BasicNameValuePair("no_identitas", no_identitas));
            params.add(new BasicNameValuePair("no_rekening", no_rekening));
            params.add(new BasicNameValuePair("rekening_tujuan", rekening_tujuan));
            params.add(new BasicNameValuePair("total_bayar", total_bayar));
            params.add(new BasicNameValuePair("bukti_transfer", bukti_transfer));

            // json object
            JSONObject json = jsonParser.makeHttpRequest(url_konfirmasi,
                    "POST", params);

            // cek respon di logcat
            Log.d("Create Response", json.toString());

            // cek tag success
            try {
                int success = json.getInt(TAG_SUCCESS);
                kodeTiket = json.getString(TAG_NO_TIKET);

                if (success == 1) {
                    errorName = "";
                } else {
                    errorName = "No Verifikasi anda hangus karena batas waktu maksimal konfirmasi adalah 5 menit setelah pemesanan";
                    // jika gagal
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * jika proses selesai maka hentikan progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            pDialog.dismiss();
            if(errorName.equals("")) {
                WebFileUploader upl = new WebFileUploader();
                upl.setUrlAndFile("http://sistempemesanan.com/server-side/insert_konfirmasi_test.php", fileName, (Button) (findViewById(R.id.btnKonfirmasi)));
                upl.execute();
                showDialogNoTiket(kodeTiket);
            } else {
                Toast.makeText(getApplicationContext(), errorName, Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void showDialogNoTiket(String kdTiket) {

        Button btnOK;
        EditText etKodeAktivasi;

        final Dialog d = new Dialog(KonfirmasiActivity.this);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setContentView(R.layout.dialog_notiket);

        btnOK = (Button) d.findViewById(R.id.btnOK);
        etKodeAktivasi = (EditText) d.findViewById(R.id.etKodeAktivasi);

        etKodeAktivasi.setText(kdTiket);

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                d.dismiss();

                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
                finish();
            }
        });

        d.show();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            if (data.getBooleanExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false)) {
                // For JellyBean and above
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    ClipData clip = data.getClipData();

                    if (clip != null) {
                        for (int i = 0; i < clip.getItemCount(); i++) {
                            Uri uri = clip.getItemAt(i).getUri();
                            fileName = uri.getPath();
                            btnUnggah.setText("FILE SUDAH DIPILIH");
                            // Do something with the URI
                        }
                    }
                    // For Ice Cream Sandwich
                } else {
                    ArrayList<String> paths = data.getStringArrayListExtra
                            (FilePickerActivity.EXTRA_PATHS);

                    if (paths != null) {
                        for (String path: paths) {
                            Uri uri = Uri.parse(path);
                            fileName = uri.getPath();
                            btnUnggah.setText("FILE SUDAH DIPILIH");
                            // Do something with the URI
                        }
                    }
                }

            } else {
                Uri uri = data.getData();
                fileName = uri.getPath();
                btnUnggah.setText("FILE SUDAH DIPILIH");
                // Do something with the URI
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(KonfirmasiActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent i = new Intent(KonfirmasiActivity.this, MainActivity.class);
                startActivity(i);
                finish();
                break;
        }
        return true;
    }

    public String getHari(int i){
        String[] hari = {"","Minggu","Senin","Selasa","Rabu","Kamis","Jumat","Sabtu"};
        return hari[i];
    }

    public  String bulan[] = {"January","February","March","April","May","June",
            "July","August","September","October","November","December"};
    public String getBulan(int i){
        return bulan[i];
    }

    public void setTanggal(View v){
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.show(getFragmentManager(), "Datepickerdialog");
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        now.set(Calendar.YEAR,year);
        now.set(Calendar.MONTH,monthOfYear);
        now.set(Calendar.DATE,dayOfMonth);
        setTime();
    }

    public void setTime() {
        tvTglPengiriman.setText(getHari(now.get(Calendar.DAY_OF_WEEK))+", "+now.get(Calendar.DATE)+" "+getBulan(now.get(Calendar.MONTH))+" "+now.get(Calendar.YEAR));
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {

    }
}
