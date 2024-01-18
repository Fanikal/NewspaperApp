package es.upm.etsiinf.pui.pui_newsmanager;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;

import com.google.android.material.chip.Chip;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Hashtable;

import es.upm.etsiinf.pui.pui_newsmanager.exceptions.ServerCommunicationError;
import es.upm.etsiinf.pui.pui_newsmanager.model.Article;
import es.upm.etsiinf.pui.pui_newsmanager.model.ArticleAdapter;
import es.upm.etsiinf.pui.pui_newsmanager.model.Image;
import es.upm.etsiinf.pui.pui_newsmanager.model.ModelEntity;
import es.upm.etsiinf.pui.pui_newsmanager.util.Utils;

public class ArticleDetailsActivity extends AppCompatActivity {
    private String argReceived;
    public static final int REQUEST_CODE_OPEN_IMAGE = 1;
    ImageView imageOfArticle;
    private int articleId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article_details);

        Intent i = getIntent();

        //HERE YOU HAVE THE ID OF THE ARTICLE TO RETRIEVE THE INFO FROM THE SERVER
        argReceived = i.getStringExtra(ArticleAdapter.ARG_TO_DETAIL_ACT_ID_ARTICLE);
        Log.i("ArticleDetailsActivity", argReceived);

       Button btnChangeImage = findViewById(R.id.btn_details_edit_img);
       Button btnDeleteArticle = findViewById(R.id.btn_details_delete_article);
       if(MainActivity.modelManager.getIdUser() != null) {
           //To change the image
           btnChangeImage.setOnClickListener(view -> {
               Intent i1 = new Intent();
               i1.setAction(Intent.ACTION_GET_CONTENT);
               i1.addCategory(Intent.CATEGORY_OPENABLE);
               i1.setType("image/*");

               //We call the activity and we assign the result
               //We launch this and we will start the onActivityResult function
               startActivityForResult(i1, REQUEST_CODE_OPEN_IMAGE);
           });
           //To delete the article
          btnDeleteArticle.setOnClickListener(view -> {
              AlertDialog.Builder builder = new AlertDialog.Builder(this);
              Context that = this;
              builder.setCancelable(true);
              builder.setTitle(R.string.delete_article_title);
              builder.setMessage(R.string.delete_article_description);
              builder.setPositiveButton("Confirm",
                      new DialogInterface.OnClickListener() {
                          @Override
                          public void onClick(DialogInterface dialog, int which) {
                              //If the user confirms, delete the article and go back to the list of articles
                              try {
                                  MainActivity.modelManager.deleteArticle(articleId);
                                  finish();
                              } catch (ServerCommunicationError e) {
                                  e.printStackTrace();
                              }
                          }
                      });
              builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                  }
              });

              AlertDialog dialog = builder.create();
              dialog.show();

           });

       } else {
           btnChangeImage.setVisibility(View.INVISIBLE);
           btnDeleteArticle.setVisibility(View.INVISIBLE);
       }
       imageOfArticle = findViewById(R.id.img_details_article);

       ShowArticleDetailsThread showArticleDetailsThread = new ShowArticleDetailsThread(this, argReceived);
       (new Thread(showArticleDetailsThread)).start();
    }

    //Result code to know if the user has finished or cancelled
    //If the activity works well, we receive an Intent
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent iData) {
        super.onActivityResult(requestCode, resultCode, iData);
        switch (requestCode){
            case REQUEST_CODE_OPEN_IMAGE:
                if(resultCode == Activity.RESULT_OK){
                    //Manage the data received
                    try {
                        InputStream is = getContentResolver().openInputStream(iData.getData());
                        Bitmap image = BitmapFactory.decodeStream(is);
                        Log.i("Main Activity", "file opened!");
                        MainActivity.modelManager.saveImage((new Image(MainActivity.modelManager,  0, "", this.articleId, Utils.imgToBase64String(image))));
                        imageOfArticle.setImageBitmap(image);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (ServerCommunicationError e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(this, "User has cancelled image selection", Toast.LENGTH_SHORT);
                }
                break;
            default:
        }
    }

//    //GET CURRENT ARTICLE BASED ON ID
//    private void getCurrentArticle() throws AuthenticationError, ServerCommunicationError {
//        currentArticle = MainActivity.modelManager.getArticle(Integer.parseInt(argReceived));
//        loadArticle(currentArticle);
//    }

    //LOAD ARTICLE DETAILS ON SCREEN
    public void loadArticle(Article currArticle) {
        this.articleId = currArticle.getId();
        try {
            TextView titleText = findViewById(R.id.txt_details_title);
            TextView subtitleText = findViewById(R.id.txt_details_subtitle);
            Chip categoryText = findViewById(R.id.txt_details_category);
            TextView abstractText = findViewById(R.id.txt_details_abstract);
            TextView bodyText = findViewById(R.id.txt_details_body);
            TextView userDateUpdated = findViewById(R.id.txt_details_date_user);

            //Paint attributes' text in UI
            //Set title
            titleText.setText(currArticle.getTitleText());
            //Set subtitle
            subtitleText.setText(currArticle.getFooterText());
            //Set category
            categoryText.setText(currArticle.getCategory());
            //Set abstract
            abstractText.setText(Html.fromHtml(currArticle.getAbstractText(), HtmlCompat.FROM_HTML_MODE_LEGACY));
            //Set body
            if(currArticle.getBodyText() != null)
                bodyText.setText(Html.fromHtml(currArticle.getBodyText(), HtmlCompat.FROM_HTML_MODE_LEGACY));
            //Set image
            Bitmap bitmapImage = currArticle.getBitmapImage();

            //We check if the Bitmap is still null
            if(bitmapImage != null)
                imageOfArticle.setImageBitmap(bitmapImage);

            //Set user, lastUpdated
            String dateAndUser = currArticle.getPublicationDate() + ", " + currArticle.getUsername();
            userDateUpdated.setText(dateAndUser);
        } catch (Exception e) {
            Log.i("DetailsScreen", e.getMessage());
        }
    }



}