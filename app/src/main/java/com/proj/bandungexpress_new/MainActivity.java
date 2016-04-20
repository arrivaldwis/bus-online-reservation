package com.proj.bandungexpress_new;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hendrix.pdfmyxml.PdfDocument;
import com.hendrix.pdfmyxml.viewRenderer.AbstractViewRenderer;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    Button btnPesan, btnKonfirmasi, btnDownloadBukti, btnLokasi;
    private ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();
    String no_tiket, nm_pemesan, no_hp_pemesan, rute, jadwal_berangkat, jam_berangkat, nm_bus, nm_tipe, no_kursi, nm_penumpang;

    private static String url_id_pemesanan = "http://sistempemesanan.com/server-side/get_pemesanan_detail.php";

    private static String TAG_SUCCESS = "success";

    private static String TAG_PEMESANAN = "pemesanan";
    private static String TAG_ID_PEMESANAN = "id_pemesanan";
    private static String TAG_NAMA_PEMESAN = "nm_pemesan";
    private static String TAG_NOHP_PEMESAN = "no_hp_pemesan";
    private static String TAG_RUTE_PEMESAN = "rute";
    private static String TAG_JADWAL_BERANGKAT = "jadwal_berangkat";
    private static String TAG_JAM_BERANGKAT = "jam_berangkat";
    private static String TAG_NAMA_BUS = "nm_bus";
    private static String TAG_TIPE_BUS = "nm_tipe";
    private static String TAG_NO_KURSI = "no_kursi";
    private static String TAG_NAMA_PENUMPANG = "nm_penumpang";
    private static String TAG_NO_TIKET = "no_tiket";

    ArrayList<HashMap<String, String>> pemesananDetailList;

    JSONArray pemesananDetail = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pemesananDetailList = new ArrayList<HashMap<String, String>>();

        btnPesan = (Button) findViewById(R.id.btnPesan);
        btnKonfirmasi = (Button) findViewById(R.id.btnKonfirmasi);
        btnDownloadBukti = (Button) findViewById(R.id.btnDownloadBukti);
        btnLokasi = (Button) findViewById(R.id.btnLokasi);

        btnPesan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, PesanActivity.class);
                startActivity(i);
                finish();
            }
        });

        btnKonfirmasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, KonfirmasiActivity.class);
                startActivity(i);
                finish();
            }
        });

        btnDownloadBukti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDownloadBuktiDialog();
            }
        });

        btnLokasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    class loadPemesananDetail extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Cek No Verifikasi...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... args) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("no_tiket", no_tiket));
            JSONObject json = jsonParser.makeHttpRequest(url_id_pemesanan, "GET", params);

            Log.d("Semua Pemesanan: ", json.toString());

            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    pemesananDetail = json.getJSONArray(TAG_PEMESANAN);

                    for (int i = 0; i < pemesananDetail.length(); i++) {
                        JSONObject c = pemesananDetail.getJSONObject(i);

                        String id = c.getString(TAG_ID_PEMESANAN);
                        String nm_pemesan = c.getString(TAG_NAMA_PEMESAN);
                        String no_hp = c.getString(TAG_NOHP_PEMESAN);
                        String rute = c.getString(TAG_RUTE_PEMESAN);
                        String jadwal = c.getString(TAG_JADWAL_BERANGKAT);
                        String jam = c.getString(TAG_JAM_BERANGKAT);
                        String nm_bus = c.getString(TAG_NAMA_BUS);
                        String nm_tipe = c.getString(TAG_TIPE_BUS);
                        String no_kursi = c.getString(TAG_NO_KURSI);
                        String nm_penumpang = c.getString(TAG_NAMA_PENUMPANG);
                        String no_tiket = c.getString(TAG_NO_TIKET);

                        HashMap<String, String> map = new HashMap<String, String>();

                        map.put(TAG_ID_PEMESANAN, id);
                        map.put(TAG_NAMA_PEMESAN, nm_pemesan);
                        map.put(TAG_NOHP_PEMESAN, no_hp);
                        map.put(TAG_RUTE_PEMESAN, rute);
                        map.put(TAG_JADWAL_BERANGKAT, jadwal);
                        map.put(TAG_JAM_BERANGKAT, jam);
                        map.put(TAG_NAMA_BUS, nm_bus);
                        map.put(TAG_TIPE_BUS, nm_tipe);
                        map.put(TAG_NO_KURSI, no_kursi);
                        map.put(TAG_NAMA_PENUMPANG, nm_penumpang);
                        map.put(TAG_NO_TIKET, no_tiket);

                        pemesananDetailList.add(map);
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

                    nm_pemesan = pemesananDetailList.get(0).get(TAG_NAMA_PEMESAN);
                    no_hp_pemesan = pemesananDetailList.get(0).get(TAG_NOHP_PEMESAN);
                    rute = pemesananDetailList.get(0).get(TAG_RUTE_PEMESAN);
                    jadwal_berangkat = pemesananDetailList.get(0).get(TAG_JADWAL_BERANGKAT);
                    jam_berangkat = pemesananDetailList.get(0).get(TAG_JAM_BERANGKAT);
                    nm_bus = pemesananDetailList.get(0).get(TAG_NAMA_BUS);
                    nm_tipe = pemesananDetailList.get(0).get(TAG_TIPE_BUS);
                    no_kursi = pemesananDetailList.get(0).get(TAG_NO_KURSI);
                    nm_penumpang = pemesananDetailList.get(0).get(TAG_NAMA_PENUMPANG);
                    no_tiket = pemesananDetailList.get(0).get(TAG_NO_TIKET);

                    showBuktiPemesananDialog();
                }
            });

        }

    }

    public void showDownloadBuktiDialog() {
        final Dialog d = new Dialog(MainActivity.this);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setContentView(R.layout.dialog_downloadbukti);

        final EditText etKodePemesanan = (EditText) d.findViewById(R.id.etKodePemesanan);
        Button btnDownload = (Button) d.findViewById(R.id.btnDownload);

        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                no_tiket = etKodePemesanan.getText().toString();
                d.dismiss();
                new loadPemesananDetail().execute();
            }
        });
        d.show();
    }

    public void showBuktiPemesananDialog() {
        final Dialog d = new Dialog(MainActivity.this);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setContentView(R.layout.dialog_buktipemesanan);

        TextView tvNamaPemesanan = (TextView) d.findViewById(R.id.tvNamaPemesan);
        TextView tvNoHP = (TextView) d.findViewById(R.id.tvNoHP);
        TextView tvRute = (TextView) d.findViewById(R.id.tvRute);
        TextView tvJadwalBerangkat = (TextView) d.findViewById(R.id.tvJadwalBerangkat);
        TextView tvJamBerangkat = (TextView) d.findViewById(R.id.tvJamBerangkat);
        TextView tvBus = (TextView) d.findViewById(R.id.tvBus);
        TextView tvTipeBus = (TextView) d.findViewById(R.id.tvTipeBus);
        TextView tvNoKursi = (TextView) d.findViewById(R.id.tvNoKursi);
        TextView tvNamaPenumpang = (TextView) d.findViewById(R.id.tvNamaPenumpang);
        TextView tvNoTiket = (TextView) d.findViewById(R.id.tvNoTiket);
        Button btnSimpan = (Button) d.findViewById(R.id.btnSimpan);

        if(pemesananDetailList.get(0).get(TAG_NAMA_PEMESAN) != null || !pemesananDetailList.get(0).get(TAG_NAMA_PEMESAN).equals("")) {
            tvNamaPemesanan.setText(nm_pemesan);
            tvNoHP.setText(no_hp_pemesan);
            tvRute.setText(rute);
            tvJadwalBerangkat.setText(jadwal_berangkat);
            tvJamBerangkat.setText(jam_berangkat);
            tvBus.setText(nm_bus);
            tvTipeBus.setText(nm_tipe);
            tvNoKursi.setText(no_kursi);
            tvNamaPenumpang.setText(nm_penumpang);
            tvNoTiket.setText(no_tiket);
        }

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                d.dismiss();
                downloadBuktiPDF();
            }
        });
        d.show();
    }

    private void downloadBuktiPDF() {
        AbstractViewRenderer page = new AbstractViewRenderer(MainActivity.this, R.layout.pdf_buktipemesanan) {

            @Override
            protected void initView(View d) {
                TextView tvNamaPemesanan = (TextView) d.findViewById(R.id.tvNamaPemesan);
                TextView tvNoHP = (TextView) d.findViewById(R.id.tvNoHP);
                TextView tvRute = (TextView) d.findViewById(R.id.tvRute);
                TextView tvJadwalBerangkat = (TextView) d.findViewById(R.id.tvJadwalBerangkat);
                TextView tvJamBerangkat = (TextView) d.findViewById(R.id.tvJamBerangkat);
                TextView tvBus = (TextView) d.findViewById(R.id.tvBus);
                TextView tvTipeBus = (TextView) d.findViewById(R.id.tvTipeBus);
                TextView tvNoKursi = (TextView) d.findViewById(R.id.tvNoKursi);
                TextView tvNamaPenumpang = (TextView) d.findViewById(R.id.tvNamaPenumpang);
                TextView tvNoTiket = (TextView) d.findViewById(R.id.tvNoTiket);

                tvNamaPemesanan.setText(nm_pemesan);
                tvNoHP.setText(no_hp_pemesan);
                tvRute.setText(rute);
                tvJadwalBerangkat.setText(jadwal_berangkat);
                tvJamBerangkat.setText(jam_berangkat);
                tvBus.setText(nm_bus);
                tvTipeBus.setText(nm_tipe);
                tvNoKursi.setText(no_kursi);
                tvNamaPenumpang.setText(nm_penumpang);
                tvNoTiket.setText(no_tiket);
            }
        };

        page.setReuseBitmap(true);

        PdfDocument doc = new PdfDocument(MainActivity.this);

        doc.setContext(MainActivity.this);
        doc.addPage(page);

        doc.setRenderWidth(2115);
        doc.setRenderHeight(1500);
        doc.setOrientation(PdfDocument.A4_MODE.LANDSCAPE);
        doc.setProgressTitle(R.string.progress_title);
        doc.setProgressMessage(R.string.progress_message);
        doc.setFileName("Bukti Pemesanan");
        doc.setInflateOnMainThread(false);
        doc.setListener(new PdfDocument.Callback() {
            @Override
            public void onComplete(File file) {
                Log.i(PdfDocument.TAG_PDF_MY_XML, "Complete ");
                Toast.makeText(getApplicationContext(), "Bukti Pemesanan berhasil disimpan, disimpan di: "+file.getPath(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError() {
                Log.i(PdfDocument.TAG_PDF_MY_XML, "Error");
                Toast.makeText(getApplicationContext(), "Bukti Pemesanan gagal disimpan", Toast.LENGTH_SHORT).show();
            }
        });

        doc.createPdf(MainActivity.this);
    }
}
