package com.example.suno;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.security.Permission;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listview);

        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
//                        Toast.makeText(MainActivity.this,"Runtime Permission Given",Toast.LENGTH_SHORT).show();
                        ArrayList<File> mysong = fetchSong(Environment.getExternalStorageDirectory());
                       String [] items = new String[mysong.size()];
                       for (int i = 0; i < mysong.size();i++ ){
                           items[i] = mysong.get(i).getName().replace("mp3","");

                       }
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>( MainActivity.this, android.R.layout.simple_list_item_1,items);
                       listView.setAdapter(adapter);
                       listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                           @Override
                           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                               Intent intent = new Intent(MainActivity.this,PlaySong.class);
                               String currentsong = listView.getItemAtPosition(position).toString();
                               intent.putExtra("songList",mysong);
                               intent.putExtra("currentSong",currentsong);
                               intent.putExtra("position",position);
                               startActivity(intent);
                           }
                       });
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                       permissionToken.continuePermissionRequest();
                    }
                })
                .check();
    }
    public ArrayList<File> fetchSong(File file){
        ArrayList arrayList = new ArrayList();
        File[] song = file.listFiles();
        if (song != null){
            for (File myFile:song){
                if (!myFile.isHidden()&& myFile.isDirectory()){
                    arrayList.addAll(fetchSong(myFile));
                }
                else {
                    if (myFile.getName().endsWith(".mp3")&& !myFile.getName().startsWith(".")){
                        arrayList.add(myFile);
                    }
                }
            }
        }
        return arrayList;
    }
}
