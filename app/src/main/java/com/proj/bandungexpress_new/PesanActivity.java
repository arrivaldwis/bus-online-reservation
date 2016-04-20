package com.proj.bandungexpress_new;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

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

public class PesanActivity extends AppCompatActivity implements
        TimePickerDialog.OnTimeSetListener,
        DatePickerDialog.OnDateSetListener {

    ActionBar mActionBar;
    private ProgressDialog pDialog;

    JSONParser jsonParser = new JSONParser();
    TextView inputTotalHarga, inputHargaKursi, inputTanggal;
    Spinner inputRute, inputJadwalKeberangkatan, inputJamKeberangkatan, inputKendaraan, inputTipeKelas;
    EditText inputNamaPenumpang, inputJumlahPenumpang, inputNoKursi, inputNoHp, inputNamaPemesanan;
    Button btnPesan;
    Calendar now;
    int count = 0;

    ArrayList<HashMap<String, String>> ruteList;
    ArrayList<HashMap<String, String>> jadwalList;
    ArrayList<HashMap<String, String>> kendaraanList;
    ArrayList<HashMap<String, String>> tipeBusList;
    ArrayList<HashMap<String, String>> sisaKursiList;
    List<String> noKursiDipilih = new ArrayList<String>();
    List<String> noKursiSudahDipilih = new ArrayList<String>();

    String tanggal, totalHarga, namaPemesanan, noHp, jadwalBerangkat, jamBerangkat, namaPenumpang, noKursi, jumlahPenumpang;

    private static String url_pesan_tiket = "http://sistempemesanan.com/server-side/insert_pemesanan.php";
    private static String url_get_rute = "http://sistempemesanan.com/server-side/get_rute.php";
    private static String url_get_bus = "http://sistempemesanan.com/server-side/get_bus.php";
    private static String url_get_tipe_bus = "http://sistempemesanan.com/server-side/get_tipe_bus.php";
    private static String url_get_jadwal = "http://sistempemesanan.com/server-side/get_jadwal_keberangkatan.php";
    private static String url_get_sisa_kursi = "http://sistempemesanan.com/server-side/get_sisa_kursi.php";

    private static final String TAG_SUCCESS = "success";

    //SISAKURSI
    private static final String TAG_SISA_KURSI = "pemesanan";
    private static final String TAG_ID_SISA_KURSI = "id_pemesanan";
    private static final String TAG_NO_KURSI = "no_kursi";

    //RUTE
    private static final String TAG_RUTE = "rute";
    private static final String TAG_ID_RUTE = "id_rute";
    private static final String TAG_KOTA_AWAL = "kota_awal";
    private static final String TAG_KOTA_AKHIR = "kota_akhir";

    //JADWAL && JAM
    private static final String TAG_JADWAL = "jadwal";
    private static final String TAG_ID_JADWAL = "id_jadwal";
    private static final String TAG_JAM_BERANGKAT = "jam";
    private static final String TAG_TGL_BERANGKAT = "tgl_berangkat";
    private static final String TAG_TARIF_RUTE = "tarif";

    //BUS
    private static final String TAG_BUS = "bus";
    private static final String TAG_ID_BUS = "id_bus";
    private static final String TAG_BUS_NAME = "nm_bus";

    //TIPE BUS
    private static final String TAG_TIPE_BUS = "tipe_bus";
    private static final String TAG_ID_TIPE_BUS = "id_tipe_bus";
    private static final String TAG_BUS_TIPE_NAME = "nm_tipe";
    private static final String TAG_BUS_HARGA = "harga";
    private static final String TAG_BUS_KURSI = "jumlah_kursi";
    private static final String TAG_AKTIVASI = "no_aktivasi";

    JSONArray rute = null;
    JSONArray jadwal = null;
    JSONArray kendaraan = null;
    JSONArray tipeBus = null;
    JSONArray sisaKursi = null;
    JSONArray kodeAktivasi = null;
    String kodeAk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pesan);

        ruteList = new ArrayList<HashMap<String, String>>();
        jadwalList = new ArrayList<HashMap<String, String>>();
        kendaraanList = new ArrayList<HashMap<String, String>>();
        tipeBusList = new ArrayList<HashMap<String, String>>();
        sisaKursiList = new ArrayList<HashMap<String, String>>();

        now = Calendar.getInstance();
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setTitle("Pesan Tiket");

        inputTanggal = (TextView) findViewById(R.id.tvTanggal);
        inputTotalHarga = (TextView) findViewById(R.id.tvTotalHarga);
        inputHargaKursi = (TextView) findViewById(R.id.tvHargaKursi);
        inputNoKursi = (EditText) findViewById(R.id.etNoKursi);
        inputNamaPemesanan = (EditText) findViewById(R.id.etNamaPemesan);
        inputNoHp = (EditText) findViewById(R.id.etNoTelp);
        inputNamaPenumpang = (EditText) findViewById(R.id.etNamaPenumpang);
        inputRute = (Spinner) findViewById(R.id.spRute);
        inputJadwalKeberangkatan = (Spinner) findViewById(R.id.spJadwalKeberangkatan);
        inputJumlahPenumpang = (EditText) findViewById(R.id.etJumlahPenumpang);
        inputJamKeberangkatan = (Spinner) findViewById(R.id.spJamKeberangkatan);
        inputKendaraan = (Spinner) findViewById(R.id.spKendaraan);
        inputTipeKelas = (Spinner) findViewById(R.id.spTipeKelas);
        btnPesan = (Button) findViewById(R.id.btnPesan);

        inputJumlahPenumpang.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(inputJumlahPenumpang.getText().toString().matches(".*[a-zA-Z]+.*")) {
                    inputJumlahPenumpang.setText("1");
                }

                if(inputJumlahPenumpang.getText().toString().equals("")) {
                    inputJumlahPenumpang.setText("1");
                }

                inputTotalHarga.setText("Rp. "+((Integer.parseInt(jadwalList.get(0).get(TAG_TARIF_RUTE))+(Integer.parseInt(tipeBusList.get(0).get(TAG_BUS_HARGA))))*Integer.parseInt(inputJumlahPenumpang.getText().toString())));

                //Toast.makeText(getApplicationContext(), (Integer.parseInt(inputHargaKursi.getText().toString().substring(4))*Integer.parseInt(inputJumlahPenumpang.getText().toString()))+"", Toast.LENGTH_LONG).show();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        inputNoKursi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(inputJumlahPenumpang.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Harap masukkan jumlah penumpang terlebih dahulu!", Toast.LENGTH_LONG).show();
                } else {
                    showKursiTersedia(Integer.parseInt(tipeBusList.get(0).get(TAG_BUS_KURSI)), Integer.parseInt(inputJumlahPenumpang.getText().toString()));
                }
            }
        });

        inputRute.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(jadwal!=null) {
                    jadwal = null;
                    jadwalList.clear();
                    kendaraanList.clear();
                    tipeBusList.clear();
                }

                HashMap<String, String> map = ruteList.get(i);
                loadJadwal(map.get(TAG_ID_RUTE));

                new loadBus().execute();
                new loadTipeBus().execute();
                loadSisa(i);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        new loadRuteData().execute();

        btnPesan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(inputJumlahPenumpang.getText().toString().equals("") || inputNamaPemesanan.getText().toString().equals("") || inputNamaPenumpang.getText().toString().equals("") || inputNoHp.getText().toString().equals("") || inputNoKursi.getText().toString().equals("Pilih Kursi..")) {
                    Toast.makeText(getApplicationContext(), "Harap Lengkapi Data!", Toast.LENGTH_SHORT).show();
                } else {
                    tanggal = inputTanggal.getText().toString();
                    totalHarga = inputTotalHarga.getText().toString();
                    namaPemesanan = inputNamaPemesanan.getText().toString();
                    noHp = inputNoHp.getText().toString();
                    namaPenumpang = inputNamaPenumpang.getText().toString();
                    jumlahPenumpang = inputJumlahPenumpang.getText().toString();
                    noKursi = inputNoKursi.getText().toString();

                    //Toast.makeText(getApplicationContext(), namaPemesanan+" "+noHp+" "+ruteList.get(0).get(TAG_ID_RUTE)+" "+jadwalBerangkat+" "+jamBerangkat+" "+kendaraanList.get(0).get(TAG_ID_BUS)+" "+tipeBusList.get(0).get(TAG_ID_TIPE_BUS)+" "+noKursi+" "+namaPenumpang+" "+totalHarga, Toast.LENGTH_LONG).show();
                    new InsertPesanTiket().execute();
                }
            }
        });

        setTime();
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
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(PesanActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        now.set(Calendar.YEAR,year);
        now.set(Calendar.MONTH,monthOfYear);
        now.set(Calendar.DATE,dayOfMonth);
        setTime();
    }

    public String getHari(int i){
        String[] hari = {"","Minggu","Senin","Selasa","Rabu","Kamis","Jumat","Sabtu"};
        return hari[i];
    }

    public void setTime() {
        inputTanggal.setText(getHari(now.get(Calendar.DAY_OF_WEEK))+", "+now.get(Calendar.DATE)+" "+getBulan(now.get(Calendar.MONTH))+" "+now.get(Calendar.YEAR));
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {

    }

    private void loadSisa(final int indexArray) {
        class loadSisaKursi extends AsyncTask<String, String, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            protected String doInBackground(String... args) {
                String id_tipe_bus = tipeBusList.get(0).get(TAG_ID_TIPE_BUS);
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("id_tipe_bus", id_tipe_bus));
                JSONObject json = jsonParser.makeHttpRequest(url_get_sisa_kursi, "GET", params);

                Log.d("Semua Sisa Kursi: ", json.toString());

                try {
                    int success = json.getInt(TAG_SUCCESS);

                    if (success == 1) {
                        sisaKursi = json.getJSONArray(TAG_SISA_KURSI);

                        for (int i = 0; i < sisaKursi.length(); i++) {
                            JSONObject c = sisaKursi.getJSONObject(i);

                            String id = c.getString(TAG_ID_SISA_KURSI);
                            String no_kursi = c.getString(TAG_NO_KURSI);

                            HashMap<String, String> map = new HashMap<String, String>();

                            map.put(TAG_ID_SISA_KURSI, id);
                            map.put(TAG_NO_KURSI, no_kursi);

                            sisaKursiList.add(map);
                        }
                    } else {
                        // jika tidak ada data
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return null;
            }

            /**
             * jika pekerjaan di belakang layar selesai maka hentikan progress dialog
             **/
            protected void onPostExecute(String file_url) {
                // hentikan progress ketika semua data didapat
                pDialog.dismiss();
                // perbarui screen
                runOnUiThread(new Runnable() {
                    public void run() {
                        jadwalBerangkat = jadwalList.get(0).get(TAG_TGL_BERANGKAT);
                        jamBerangkat = jadwalList.get(0).get(TAG_JAM_BERANGKAT);
                    }
                });

            }
        }

        loadSisaKursi s = new loadSisaKursi();
        s.execute();
    }

    public void showKursiTersedia(final int n, final int penumpang) {

        GridView gridView;
        Button btnPilihKursi;
        final ArrayList<Item> gridArray = new ArrayList<Item>();
        final CustomGridViewAdapter customGridAdapter;
        final Bitmap seatIcon;
        final Bitmap seatSelect;
        final Bitmap seatBooked;

        final Dialog d = new Dialog(PesanActivity.this);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setContentView(R.layout.dialog_pilih_kursi);

        seatIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.seat_layout_screen_nor_avl);
        seatSelect = BitmapFactory.decodeResource(this.getResources(), R.drawable.seat_layout_screen_nor_std);
        seatBooked = BitmapFactory.decodeResource(this.getResources(), R.drawable.seat_layout_screen_nor_bkd);

        noKursiSudahDipilih.clear();
        inputNoKursi.setText("");
        noKursiDipilih.clear();
        count = 0;

        for(int i=0; i<sisaKursiList.size(); i++) {
            noKursiSudahDipilih.add(sisaKursiList.get(i).get(TAG_NO_KURSI));
            Log.d("sisaKursiList: ", sisaKursiList.get(i).get(TAG_NO_KURSI));
        }

        for(int i=0; i<noKursiSudahDipilih.size(); i++) {
            if (noKursiSudahDipilih.get(i).contains(",")) {
                String[] kursiSplit = noKursiSudahDipilih.get(i).split(",");
                for (int a = 0; a < kursiSplit.length; a++) {
                    noKursiSudahDipilih.add(kursiSplit[a]);
                }
                noKursiSudahDipilih.remove(i);
            }
            Log.d("noKursiSudahDipilih: ", noKursiSudahDipilih.get(i));
        }

        for (int i = 1; i <= n; ++i)
        {
            if(noKursiSudahDipilih.contains(String.valueOf(i))) {
                gridArray.add(new Item(seatBooked, "" + i));
            } else {
                gridArray.add(new Item(seatIcon, "" + i));
            }
        }

        btnPilihKursi = (Button) d.findViewById(R.id.btnPilihKursi);
        gridView = (GridView) d.findViewById(R.id.gridView1);
        customGridAdapter = new CustomGridViewAdapter(this, R.layout.seatrow_grid, gridArray);
        gridView.setAdapter(customGridAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Item item = gridArray.get(i);
                Bitmap seatcompare = item.getImage();
                if (seatcompare == seatIcon) {
                    if(count<=penumpang-1) {
                        gridArray.remove(i);
                        gridArray.add(i, new Item(seatSelect, ""+(i+1)));
                        customGridAdapter.notifyDataSetChanged();
                        noKursiDipilih.add((i+1)+"");
                        count++;
                    } else {
                        Toast.makeText(getApplicationContext(), "Maaf, Batas Pemilihan Kursi sesuai jumlah penumpang", Toast.LENGTH_LONG).show();
                    }
                } else {
                    if(seatcompare == seatBooked) {
                        Toast.makeText(getApplicationContext(), "Maaf, Kursi telah di booking", Toast.LENGTH_LONG).show();
                    } else {
                        gridArray.remove(i);
                        int ia = i + 1;
                        gridArray.add(i, new Item(seatIcon, "" + ia));
                        customGridAdapter.notifyDataSetChanged();
                        noKursiDipilih.remove(ia + "");
                        count--;
                    }
                }
            }
        });

        btnPilihKursi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String noKursi = "";
                for(int i=0; i<noKursiDipilih.size(); i++) {
                    if(i!=noKursiDipilih.size()-1) {
                        noKursi += noKursiDipilih.get(i).toString()+",";
                    } else {
                        noKursi += noKursiDipilih.get(i).toString();
                    }
                }
                inputNoKursi.setText(noKursi);
                d.dismiss();
            }
        });

        d.show();
    }

    public void showDialogKonfirmasi(String kodeAktivasi) {

        Button btnOK;
        EditText etKodeAktivasi;

        final Dialog d = new Dialog(PesanActivity.this);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setContentView(R.layout.dialog_konfirmasi);

        btnOK = (Button) d.findViewById(R.id.btnOK);
        etKodeAktivasi = (EditText) d.findViewById(R.id.etKodeAktivasi);

        etKodeAktivasi.setText(kodeAktivasi);

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

    class loadRuteData extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(PesanActivity.this);
            pDialog.setMessage("Mohon tunggu, Loading Data...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... args) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            JSONObject json = jsonParser.makeHttpRequest(url_get_rute, "GET", params);

            Log.d("Semua Rute: ", json.toString());

            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    rute = json.getJSONArray(TAG_RUTE);

                    for (int i = 0; i < rute.length(); i++) {
                        JSONObject c = rute.getJSONObject(i);

                        String id = c.getString(TAG_ID_RUTE);
                        String rute = c.getString(TAG_KOTA_AWAL) + " - " + c.getString(TAG_KOTA_AKHIR);

                        HashMap<String, String> map = new HashMap<String, String>();

                        map.put(TAG_ID_RUTE, id);
                        map.put("RUTE", rute);

                        ruteList.add(map);
                    }
                } else {
                    // jika tidak ada data
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
                    SpinnerAdapter adapter = new SimpleAdapter(
                            PesanActivity.this, ruteList,
                            R.layout.list_pemesanan, new String[] { TAG_ID_RUTE,
                            "RUTE"},
                            new int[] { R.id.pid, R.id.name });
                    // perbarui list pendaftaran
                    inputRute.setAdapter(adapter);
                }
            });

        }

    }

    class loadBus extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {
            String tgl = jadwalList.get(0).get(TAG_TGL_BERANGKAT);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("tanggal", tgl));
            JSONObject json = jsonParser.makeHttpRequest(url_get_bus, "GET", params);
            Log.d("TGL: ",tgl);
            Log.d("Semua Kendaraan: ", json.toString());

            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    kendaraan = json.getJSONArray(TAG_BUS);

                    for (int i = 0; i < kendaraan.length(); i++) {
                        JSONObject c = kendaraan.getJSONObject(i);

                        String id = c.getString(TAG_ID_BUS);
                        String nama = c.getString(TAG_BUS_NAME);

                        HashMap<String, String> map = new HashMap<String, String>();

                        map.put(TAG_ID_BUS, id);
                        map.put(TAG_BUS_NAME, nama);

                        kendaraanList.add(map);
                    }
                } else {
                    // jika tidak ada data
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
                    SpinnerAdapter adapter = new SimpleAdapter(
                            PesanActivity.this, kendaraanList,
                            R.layout.list_pemesanan, new String[] { TAG_ID_BUS,
                            TAG_BUS_NAME},
                            new int[] { R.id.pid, R.id.name });
                    // perbarui list pendaftaran
                    inputKendaraan.setAdapter(adapter);
                }
            });

        }

    }

    class loadTipeBus extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(PesanActivity.this);
            pDialog.setMessage("Mohon tunggu, Loading Data...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... args) {
            String id_bus = kendaraanList.get(0).get(TAG_ID_BUS);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("id_bus", id_bus));
            JSONObject json = jsonParser.makeHttpRequest(url_get_tipe_bus, "GET", params);

            Log.d("Semua Tipe Bus: ", json.toString());

            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    tipeBus = json.getJSONArray(TAG_TIPE_BUS);

                    for (int i = 0; i < tipeBus.length(); i++) {
                        JSONObject c = tipeBus.getJSONObject(i);

                        String id = c.getString(TAG_ID_TIPE_BUS);
                        String nama = c.getString(TAG_BUS_TIPE_NAME);
                        String harga = c.getString(TAG_BUS_HARGA);
                        String kursi = c.getString(TAG_BUS_KURSI);

                        HashMap<String, String> map = new HashMap<String, String>();

                        map.put(TAG_ID_TIPE_BUS, id);
                        map.put(TAG_BUS_TIPE_NAME, nama);
                        map.put(TAG_BUS_HARGA, harga);
                        map.put(TAG_BUS_KURSI, kursi);

                        tipeBusList.add(map);
                    }
                } else {
                    // jika tidak ada data
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
                    SpinnerAdapter adapter = new SimpleAdapter(
                            PesanActivity.this, tipeBusList,
                            R.layout.list_pemesanan, new String[] { TAG_ID_TIPE_BUS,
                            TAG_BUS_TIPE_NAME},
                            new int[] { R.id.pid, R.id.name });
                    // perbarui list pendaftaran
                    inputTipeKelas.setAdapter(adapter);
                    inputHargaKursi.setText("Rp. "+jadwalList.get(0).get(TAG_TARIF_RUTE)+" + "+tipeBusList.get(0).get(TAG_BUS_HARGA));

                }
            });

        }

    }

    private void loadJadwal(String id_rute) {
        class loadJadwalKeberangkatan extends AsyncTask<String, String, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            protected String doInBackground(String... args) {
                String id_rute = args[0];
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("id_rute", id_rute));
                JSONObject json = jsonParser.makeHttpRequest(url_get_jadwal, "GET", params);

                Log.d("Semua Jadwal: ", json.toString());

                try {
                    int success = json.getInt(TAG_SUCCESS);

                    if (success == 1) {
                        jadwal = json.getJSONArray(TAG_JADWAL);

                        for (int i = 0; i < jadwal.length(); i++) {
                            JSONObject c = jadwal.getJSONObject(i);

                            String id = c.getString(TAG_ID_JADWAL);
                            String tgl = c.getString(TAG_TGL_BERANGKAT);
                            String jam = c.getString(TAG_JAM_BERANGKAT);
                            String tarif = c.getString(TAG_TARIF_RUTE);

                            HashMap<String, String> map = new HashMap<String, String>();

                            map.put(TAG_ID_RUTE, id);
                            map.put(TAG_TGL_BERANGKAT, tgl);
                            map.put(TAG_JAM_BERANGKAT, jam);
                            map.put(TAG_TARIF_RUTE, tarif);

                            jadwalList.add(map);
                        }
                    } else {
                        // jika tidak ada data
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return null;
            }

            /**
             * jika pekerjaan di belakang layar selesai maka hentikan progress dialog
             **/
            protected void onPostExecute(String file_url) {
                // hentikan progress ketika semua data didapat
                pDialog.dismiss();
                // perbarui screen
                runOnUiThread(new Runnable() {
                    public void run() {
                        /**
                         * perbarui json ke arraylist
                         * */
                        SpinnerAdapter adapter = new SimpleAdapter(
                                PesanActivity.this, jadwalList,
                                R.layout.list_pemesanan, new String[]{TAG_ID_JADWAL,
                                TAG_TGL_BERANGKAT},
                                new int[]{R.id.pid, R.id.name});


                        SpinnerAdapter adapter2 = new SimpleAdapter(
                                PesanActivity.this, jadwalList,
                                R.layout.list_pemesanan, new String[]{TAG_ID_JADWAL,
                                TAG_JAM_BERANGKAT},
                                new int[]{R.id.pid, R.id.name});
                        // perbarui list pendaftaran
                        inputJadwalKeberangkatan.setAdapter(adapter);
                        inputJamKeberangkatan.setAdapter(adapter2);
                    }
                });

            }
        }

        loadJadwalKeberangkatan la = new loadJadwalKeberangkatan();
        la.execute(id_rute);
    }

    class InsertPesanTiket extends AsyncTask<String, String, String> {

        /**
         * tampilkan progress dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(PesanActivity.this);
            pDialog.setMessage("Sedang memproses pemesanan...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * buat bukutamu baru
         * */
        protected String doInBackground(String... args) {
            String nm_pemesan = namaPemesanan;
            String no_hp = noHp;
            String rute = ruteList.get(0).get(TAG_ID_RUTE);
            String jadwal_berangkat = jadwalBerangkat;
            String jam_berangkat = jamBerangkat;
            String bus = kendaraanList.get(0).get(TAG_ID_BUS);
            String tipe_bus = tipeBusList.get(0).get(TAG_ID_TIPE_BUS);
            String no_kursi = noKursi;
            String nm_penumpang = namaPenumpang;
            String total_harga = totalHarga;
            String jum_penumpang = jumlahPenumpang;

            // parameter
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("nm_pemesan", nm_pemesan));
            params.add(new BasicNameValuePair("no_hp", no_hp));
            params.add(new BasicNameValuePair("id_rute", rute));
            params.add(new BasicNameValuePair("jadwal_berangkat", jadwal_berangkat));
            params.add(new BasicNameValuePair("jam_berangkat", jam_berangkat));
            params.add(new BasicNameValuePair("id_bus", bus));
            params.add(new BasicNameValuePair("id_tipe_bus", tipe_bus));
            params.add(new BasicNameValuePair("no_kursi", no_kursi));
            params.add(new BasicNameValuePair("nm_penumpang", nm_penumpang));
            params.add(new BasicNameValuePair("total_harga", total_harga));
            params.add(new BasicNameValuePair("jum_penumpang", jum_penumpang));

            // json object
            JSONObject json = jsonParser.makeHttpRequest(url_pesan_tiket,
                    "POST", params);

            // cek respon di logcat
            Log.d("Create Response", json.toString());

            // cek tag success
            try {
                int success = json.getInt(TAG_SUCCESS);
                kodeAk = json.getString(TAG_AKTIVASI);

                if (success == 1) {
                } else {
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
            showDialogKonfirmasi(kodeAk);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent i = new Intent(PesanActivity.this, MainActivity.class);
                startActivity(i);
                finish();
                break;
        }
        return true;
    }
}
