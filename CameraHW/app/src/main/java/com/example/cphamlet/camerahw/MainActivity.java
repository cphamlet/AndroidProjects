package com.example.cphamlet.camerahw;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.database.sqlite.SQLiteDatabase;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private String mCurrentPhotoPath;
    private SQLiteDatabase db;
    private File photoFile = null;
    private boolean saved = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = this.openOrCreateDatabase ("cw4_db", MODE_PRIVATE, null);
        //db.execSQL("DROP TABLE IF EXISTS photos");
        db.execSQL("CREATE TABLE IF NOT EXISTS photos(path TEXT, tags TEXT, size INTEGER);");

        setContentView(R.layout.activity_main);
    }

    public void load(View v) {
        EditText tag_box = (EditText) findViewById(R.id.tag_edit_box);
        EditText size_box = (EditText) findViewById(R.id.size_edit_box);
        ImageView thumbnail_view = (ImageView) findViewById(R.id.thumbnail);

        String query_tag = "";
        Cursor cursor = null;
        if (tag_box.length() == 0 && size_box.length() == 0) {
            Toast.makeText(getApplicationContext(), "Enter a tag or size first",
                    Toast.LENGTH_SHORT).show();
            return;
        } else if (tag_box.length() > 0 && size_box.length() == 0) {


            //Extracts tags from user input
            String[] tokens = tag_box.getText().toString().split(";");
            String query_ext = "(";
            //(tags LIKE "%right%" OR tags LIKE "%care%")
            for (int i = 0; i < tokens.length; i++) {
                query_ext += "tags LIKE \"%" + tokens[i] + "%\" ";
                if (i < tokens.length - 1) {
                    query_ext += "OR ";
                }
            }
            query_ext += ")";
            System.out.println("Here is the tokens: " + query_ext);

            query_tag = "SELECT path FROM photos WHERE " + query_ext;


        }else if(tag_box.length() == 0 && size_box.length() > 0){
            query_tag = "SELECT path FROM photos WHERE size ";
            long search_size = Long.parseLong(size_box.getText().toString());
            double upper_bound = search_size*1.25;
            double lower_bound = search_size*0.75;
            query_tag+= "BETWEEN "+Math.floor(lower_bound)+" AND "+Math.floor(upper_bound)+";";




        }else if(tag_box.length() > 0 && size_box.length() > 0){
            //Inserts an AND in between for both queries.
            query_tag = "SELECT path FROM photos WHERE (size ";
            long search_size = Long.parseLong(size_box.getText().toString());
            double upper_bound = search_size*1.25;
            double lower_bound = search_size*0.75;
            query_tag+= "BETWEEN "+Math.floor(lower_bound)+" AND "+Math.floor(upper_bound)+") AND ";



            //For the tags:
            //Extracts tags from user input
            String[] tokens = tag_box.getText().toString().split(";");
            String query_ext = "(";
            //(tags LIKE "%right%" OR tags LIKE "%care%")
            for (int i = 0; i < tokens.length; i++) {
                query_ext += "tags LIKE \"%" + tokens[i] + "%\" ";
                if (i < tokens.length - 1) {
                    query_ext += "OR ";
                }
            }
            query_ext += ")";
            //Combines the two
            query_tag+=query_ext;

        }else{
            Toast.makeText(getApplicationContext(), "Unable to make query",
                    Toast.LENGTH_SHORT).show();
            return;
        }


        System.out.println("Executing: " + query_tag);
        cursor = db.rawQuery(query_tag, new String[]{});
        String result = "";
        if (cursor.moveToFirst()) {
            System.out.println("inside");
            //   do {
            String data = cursor.getString(cursor.getColumnIndex("path"));

            result += data;


            //sets the image view based on result
            if (result.length() > 0) {
                Bitmap resized = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile
                        (result), 200, 200);
                thumbnail_view.setImageBitmap(resized);
            }
            //   result += '\n';
            // do what ever you want here
            //} while (cursor.moveToNext());
            //  }
            System.out.println("Result: " + result);
        }
    }

    public void save(View v){
        if(photoFile != null && saved == false){
            EditText tag_box = (EditText)findViewById(R.id.tag_edit_box);

            if(tag_box.length()==0) {
                Toast.makeText(getApplicationContext(), "Enter a tag first.",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            //Inserts item into database
            db.execSQL("INSERT INTO photos(path, tags, size) VALUES(\""+  photoFile.getAbsolutePath()
                    + "\", \""+  tag_box.getText()  +"\", "+
                    photoFile.length() + " );");

            System.out.println("Exec: " + "INSERT INTO photos(path, tags, size) VALUES("+
                    photoFile.getAbsolutePath()
                    + ", "+  tag_box.getText()  +", "+
                    photoFile.length() + " );");
            tag_box.setText("");

            //Prevents double database insertions
            saved = true;
        }else{
            Toast.makeText(getApplicationContext(), "Already saved",
                    Toast.LENGTH_SHORT).show();
        }




    }

    public void capture(View v){
        int REQUEST_TAKE_PHOTO = 1;
        //Empties tag box

        EditText tag_box = (EditText)findViewById(R.id.tag_edit_box);
        tag_box.setText("");



        //Sets up save capture
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);

            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        int REQUEST_TAKE_PHOTO = 1;
        EditText size_box = (EditText)findViewById(R.id.size_edit_box);
        EditText tag_box = (EditText)findViewById(R.id.tag_edit_box);
        ImageView mImageView = (ImageView)findViewById(R.id.thumbnail);
        if(requestCode == REQUEST_TAKE_PHOTO){
            System.out.println("Exit photo request");
            //-1 is a success
            if(resultCode == RESULT_OK){
                System.out.println("Photo Success");
                saved = false;
                size_box.setText(Long.toString(photoFile.length()));
                tag_box.requestFocus();
                if(data == null){
                    System.out.println("this is null and this is bad");
                }
//                System.out.println(data.getExtras());
//                Bundle extras = data.getExtras();
//                Bitmap imageBitmap = (Bitmap) extras.get("data");
//                mImageView.setImageBitmap(imageBitmap);
                Bitmap resized = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile
                        (photoFile.getPath()), 200, 200);
                mImageView.setImageBitmap(resized);
            }
        }

    }
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        System.out.println(storageDir.getAbsolutePath());
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        System.out.println("FILE PATH");
        System.out.println(mCurrentPhotoPath);
        return image;
    }

    public void show(View v){
        String query_tag = "SELECT * FROM photos";

        Cursor cursor = db.rawQuery(query_tag, new String[] {});
        String result = "";
            if (cursor.moveToFirst()) {
                do {
                    String tag_temp = cursor.getString(cursor.getColumnIndex("tags"));
                    String size_temp = cursor.getString(cursor.getColumnIndex("size"));
                    String file_temp = cursor.getString(cursor.getColumnIndex("path"));
                    result += file_temp + " \n "+ size_temp + " "+tag_temp;
                    result += '\n';
                    // do what ever you want here
                } while (cursor.moveToNext());
            }
            System.out.println("Result: "+result);
    }

}
