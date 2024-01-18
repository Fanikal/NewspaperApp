package es.upm.etsiinf.pui.pui_newsmanager.model;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;

import java.util.LinkedList;
import java.util.List;

import es.upm.etsiinf.pui.pui_newsmanager.ArticleDetailsActivity;
import es.upm.etsiinf.pui.pui_newsmanager.R;
import es.upm.etsiinf.pui.pui_newsmanager.exceptions.ServerCommunicationError;

public class ArticleAdapter extends BaseAdapter {

    //Arguments passed from one activity to another
    public static String ARG_TO_DETAIL_ACT_ID_ARTICLE = "attribute_id";

    //Other arguments
    public static List<Article> articles = new LinkedList<>();
    Context context = null;

    public ArticleAdapter(Context ma){
        this.context = ma;
    }

    public void setData(List<Article> data){
        this.articles.clear();
        this.articles.addAll(data);
        this.notifyDataSetChanged();
    }

    public List<Article> getData(){
        return articles;
    }

    @Override
    public int getCount() {
        return articles.size();
    }

    //It returns the article
    @Override
    public Object getItem(int i) {
        return articles.get(i);
    }

    //It returns the id of the article in the i position
    @Override
    public long getItemId(int i) {
        return articles.get(i).getId();
    }

    //i = position, view = each row (the whole layout), viewGroup = listView
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        //If the row is null, and we scroll, we need to generate it
        if(view == null){
            //Inflating, we paint the object
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.article_row, null);
        }

        //We get the article that is going to be painted in the screen
        Article article = this.articles.get(i);

        //We set the elements of the layout to complete the information
        TextView txtTitle = view.findViewById(R.id.txt_main_title_article);
        txtTitle.setText(article.getTitleText());

        TextView txtAbstract = view.findViewById(R.id.txt_main_abstract_article);
        txtAbstract.setText(Html.fromHtml(article.getAbstractText(), HtmlCompat.FROM_HTML_MODE_LEGACY));

        TextView txtCategory = view.findViewById(R.id.txt_main_category_article);
        txtCategory.setText(article.getCategory());

        //Get the thumbail
        ImageView iv = view.findViewById(R.id.img_main_article);
        Bitmap bitmapImage = article.getBitmapImage();

        //We check if the Bitmap is still null
        if(bitmapImage != null)
            iv.setImageBitmap(bitmapImage);
        else {
            Drawable draw = ContextCompat.getDrawable(context, android.R.drawable.ic_menu_report_image);
            iv.setImageDrawable(draw);
        }

        //Once the article is complete, we set an action that should be done when it is selected
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent detailIntent = new Intent(context, ArticleDetailsActivity.class);
                detailIntent.putExtra(ARG_TO_DETAIL_ACT_ID_ARTICLE, String.valueOf(article.getId()));
                context.startActivity(detailIntent);
            }
        });
        return view;
    }


}
