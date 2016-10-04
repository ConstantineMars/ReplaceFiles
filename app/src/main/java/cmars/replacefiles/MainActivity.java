package cmars.replacefiles;

import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableField;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.common.io.Files;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import cmars.replacefiles.databinding.ActivityMainBinding;

import static android.Manifest.*;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getName();
    public static final int PERMISSIONS_REQUEST = 1;

    public ObservableField<String> fileName1 = new ObservableField<>("fileName1"),
            fileName2 = new ObservableField<>("fileName2");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setActivity(this);

        Log.d(TAG, fileName1.get());

        if (ContextCompat.checkSelfPermission(this, permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

            createDemoFiles();
            return;
        }

        ActivityCompat.requestPermissions(this,
                new String[]{permission.WRITE_EXTERNAL_STORAGE},
                PERMISSIONS_REQUEST);
    }

    public void replace(View view) {

        boolean result = file1.renameTo(file2);

        readFile(this.fileName1, file1);
        readFile(this.fileName2, file2);
    }

    File file1, file2;

    private void createDemoFiles() {
        file1 = new File(getFilesDir(), this.fileName1.get());
        writeFile(file1, "line1");

        file2 = new File(getFilesDir(), this.fileName2.get());
        writeFile(file2, "line2");

        readFile(this.fileName1, file1);
        readFile(this.fileName2, file2);

        Log.d(TAG, this.fileName2.get());
    }

    private void readFile(ObservableField<String> variable, File file) {
        try {
            FileInputStream inputStream = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String lines = "";
            String line;
            while ((line = reader.readLine()) != null) {
                lines += line + "\n";
            }

            variable.set(lines);
            Log.d(TAG, lines);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeFile(File file, String content) {
        Log.d(TAG, file.getAbsolutePath());
        try {
            FileWriter writer = new FileWriter(file.getAbsolutePath());
            writer.append(content);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    createDemoFiles();
                }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
