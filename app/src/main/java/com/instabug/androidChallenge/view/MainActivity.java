package com.instabug.androidChallenge.view;

import static com.instabug.androidChallenge.utils.NetworkUtil.isNetworkAvaliable;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;
import com.google.android.material.snackbar.Snackbar;
import com.instabug.androidChallenge.R;
import com.instabug.androidChallenge.adapter.WordsAdapter;
import com.instabug.androidChallenge.helper.SQLiteHelper;
import com.instabug.androidChallenge.model.Words;
import com.instabug.androidChallenge.utils.NetworkUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    Button btnTryAgain;
    EditText etSearch;
    TextView tvStatusDownload;
    ImageView ivStatus,ivSort,ivSearch;
    LinearLayout llProgressStatus,llmainActivity;
    RecyclerView rvList;
    WordsAdapter wordsAdapter;
    SQLiteHelper db;
    List<Words> words;
    String SORT_TABLE ="DESC";
    Boolean reload=true;
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("RELOAD", false);
    }
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);
        reload = savedInstanceState.getBoolean("RELOAD");
     }
     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            ProviderInstaller.installIfNeeded(getApplicationContext());
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
        btnTryAgain = findViewById(R.id.btnTryAgain);
        tvStatusDownload = findViewById(R.id.tvStatusDownload);
        ivStatus = findViewById(R.id.ivStatus);
        llProgressStatus = findViewById(R.id.llProgressStatus);
        rvList= findViewById(R.id.rvList);
        ivSort= findViewById(R.id.ivSort);
        ivSearch= findViewById(R.id.ivSearch);
        etSearch= findViewById(R.id.etSearch);
        llmainActivity= findViewById(R.id.llmainActivity);
        db = new SQLiteHelper(this);


        btnTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getData();
            }
        });

        ivSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(SORT_TABLE.equals("DESC")){
                    SORT_TABLE="ASC";
                    ivSort.setImageResource(R.drawable.sort_a);
                }else{
                    SORT_TABLE="DESC";
                    ivSort.setImageResource(R.drawable.sort_d);
                }

                setRecyclerView();
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                setRecyclerView();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                setRecyclerView();
            }
        });

        if(reload){
            getData();
        }else{
            reload=true;
           // setRecyclerView();
        }

    }
    void getData(){
        if(isNetworkAvaliable(getApplicationContext())){
            AsyncTaskGetPageHtmlContent asyncTask=new AsyncTaskGetPageHtmlContent();
            asyncTask.execute("https://instabug.com");
        }else{
            errorHandling();
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    void setRecyclerView(){
        // Reading  words
        llProgressStatus.setVisibility(View.GONE);
        rvList.setVisibility(View.VISIBLE);
        words = db.getAllWords(SORT_TABLE,etSearch.getText().toString());
        wordsAdapter = new WordsAdapter(getApplication(), words);
        RecyclerView.LayoutManager mLayoutHomeManager = new LinearLayoutManager(getApplication());
        rvList.setLayoutManager(mLayoutHomeManager);
        ((LinearLayoutManager) mLayoutHomeManager).setOrientation(RecyclerView.VERTICAL);
        rvList.setItemAnimator(new DefaultItemAnimator());
        rvList.setAdapter(wordsAdapter);
    }

    void errorHandling(){
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if(db.getAllWords(SORT_TABLE,"").size()>0){
                    showSnackBar(getString(R.string.internetConnectionError),llmainActivity);
                    setRecyclerView();
                }else{
                    llProgressStatus.setVisibility(View.VISIBLE);
                    btnTryAgain.setVisibility(View.VISIBLE);
                    tvStatusDownload.setText(getString(R.string.internetConnectionError));
                }

            }
        });

    }
    @SuppressLint("StaticFieldLeak")
    private class AsyncTaskGetPageHtmlContent extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            btnTryAgain.setVisibility(View.GONE);
            llProgressStatus.setVisibility(View.VISIBLE);
            rvList.setVisibility(View.GONE);
            tvStatusDownload.setText(getString(R.string.please_wait_while_the_download_is_in_progress));

        }


        @Override
        protected void onCancelled(String s) {
            super.onCancelled(s);
            errorHandling();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);
                BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                String input;
                StringBuffer stringBuffer = new StringBuffer();
                while ((input = in.readLine()) != null)
                {
                    stringBuffer.append(input);
                }
                in.close();
                return stringBuffer.toString();
            } catch (IOException e) {
                e.printStackTrace();
                errorHandling();

            }

            return "";
        }
        @Override
        protected void onPostExecute(String result) {
            try {
                Log.d("Result: ", result);
                llProgressStatus.setVisibility(View.GONE);
                rvList.setVisibility(View.VISIBLE);
                //clear database
                db.clearWordsTable();

                String wor[] = result.split(" ");
                int startIndex=0;
                int enIndex=wor.length;
                if(result.indexOf(0) !=' '){
                    startIndex=1;
                }
                if(result.lastIndexOf(result) !=' '){
                    enIndex--;
                }
                for(int i=startIndex;i<enIndex;i++){
                    String word=wor[i].toLowerCase();
                    Pattern p = Pattern.compile("[a-zA-Z0-9]");
                    if(p.matcher(word).find()){
                        Words wordSelect = db.getWord(word);
                        if(wordSelect !=null){
                            db.updateWord(new Words(wordSelect.getId(),word,wordSelect.getCount()+1));
                        }else {
                            db.addWord(new Words(word,1));
                        }
                    }

                }
                setRecyclerView();
            }catch (Exception e){
                errorHandling();
            }

        }
    }

    public void showSnackBar(String string, LinearLayout linearLayout )
    {
        Snackbar.make(linearLayout, string, Snackbar.LENGTH_INDEFINITE ).
                setAction("Retry", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!isNetworkAvaliable(getApplication())){
                            showSnackBar("No Internet Connection",(LinearLayout) findViewById(R.id.llmainActivity));
                        }
                        else getData();
                    }
                }).show();
    }
}