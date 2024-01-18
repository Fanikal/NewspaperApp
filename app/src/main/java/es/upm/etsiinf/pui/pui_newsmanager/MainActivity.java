package es.upm.etsiinf.pui.pui_newsmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import es.upm.etsiinf.pui.pui_newsmanager.exceptions.AuthenticationError;
import es.upm.etsiinf.pui.pui_newsmanager.model.Article;
import es.upm.etsiinf.pui.pui_newsmanager.model.ArticleAdapter;
import es.upm.etsiinf.pui.pui_newsmanager.model.ModelManager;

public class MainActivity extends AppCompatActivity {

    public static ModelManager modelManager;
    public static ArticleAdapter adapter;
    private static List<Article> allArticles;

    public static final String PREFERENCES_KEY = "preferences";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize model manager
        initModelManager();

        //Search the articles
        ShowArticlesThread showArticlesThread = new ShowArticlesThread(this);
        (new Thread(showArticlesThread)).start();

        //Reference
        ListView lv = findViewById(R.id.article_lst_main);
        adapter = new ArticleAdapter(this);
        lv.setAdapter(adapter);

        //Define elements of the spinner
        Spinner spinnerCategories = findViewById(R.id.spinner_main_categories);
        spinnerCategories.setVisibility(View.INVISIBLE);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.categories, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinnerCategories.setAdapter(spinnerAdapter);

        //Login button
        FloatingActionButton btnLoginPage = findViewById(R.id.btnLoginPage);
        btnLoginPage.setOnClickListener(this::onClickLogin);
    }

    /**
     * FUNCTION THAT IS EXECUTED BEFORE GETTING THE ARTICLES FROM THE SERVER
     */
    public void prepareUIForServer(){
        //WE ONLY BLOCK THE PART THAT IS CHARGING
        //We show the progress bar until the content is loaded
        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
    }

    /**
     * FUNCTION THAT PRINTS EACH ELEMENT OF THE LIST
     * @param articleList: list with the articles that should be included in the listView
     */
    public void printResultFromServer(List<Article> articleList){
        //We hide the progress bar once the content is loaded
        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        //Adding the data and how to show it
        ListView lv = findViewById(R.id.article_lst_main);
        ((ArticleAdapter)lv.getAdapter()).setData(articleList);

        //Save articleList with all the articles
        allArticles = articleList;

        //Show dropdown
        Spinner spinnerCategories = findViewById(R.id.spinner_main_categories);
        spinnerCategories.setVisibility(View.VISIBLE);

        //Action while clicking on the spinner
        spinnerCategories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i >= 0 && i < spinnerCategories.getCount())
                    getSelectedCategoryData(((TextView) view).getText().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    /**
     * FUNCTION TO INITIALIZE THE MODEL MANAGER
     */
    private void initModelManager(){
        Properties properties = new Properties();
        properties.setProperty(ModelManager.ATTR_SERVICE_URL, "https://sanger.dia.fi.upm.es/pmd-task/");
        SharedPreferences preferences = getSharedPreferences(PREFERENCES_KEY, MODE_PRIVATE);
        if (preferences != null) {
            String username = preferences.getString(ModelManager.ATTR_LOGIN_USER, "");
            String password = preferences.getString(ModelManager.ATTR_LOGIN_PASS, "");
            if (!username.isEmpty() && !password.isEmpty()) {
                properties.setProperty(ModelManager.ATTR_LOGIN_USER, username);
                properties.setProperty(ModelManager.ATTR_LOGIN_PASS, password);
            }
        }

        try {
            modelManager = new ModelManager(properties);
            checkAuthentication();
        }catch (AuthenticationError authError){
            authError.printStackTrace();
        }
    }

    /**
     * FUNCTION TO FILTER DEPENDING ON THE CATEGORY RECEIVED
     * @param category: string with the category for filtering
     */
    private void getSelectedCategoryData(String category){
        List<Article> filteredList = new LinkedList<>();
        if(category.equals("All")){
            adapter.setData(allArticles);
        } else {
            for(Article article: allArticles){
                if(article.getCategory().equals(category))
                    filteredList.add(article);
            }
            adapter.setData(filteredList);
        }
    }

    private void onClickLogin(View view) {
        Intent detailIntent = new Intent(view.getContext(), LoginActivity.class);
        view.getContext().startActivity(detailIntent);
    }

    private void onClickLogout(View view) {
//        Remove data
        modelManager.logout();
        SharedPreferences preferences = getSharedPreferences(PREFERENCES_KEY, MODE_PRIVATE);
        if (preferences != null) {
            preferences.edit().clear().apply();
        }

        showToast(view.getContext(), R.string.logged_out_message);

//        Graphical changes
        TextView txtLoginPage = findViewById(R.id.txtLoginPage);
        txtLoginPage.setText(R.string.login);
        FloatingActionButton btnLoginPage = findViewById(R.id.btnLoginPage);
        btnLoginPage.setOnClickListener(this::onClickLogin);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkAuthentication();
        ShowArticlesThread showArticlesThread = new ShowArticlesThread(this);
        (new Thread(showArticlesThread)).start();

        //Set the dropdown as ALL
        Spinner spinnerCategories = findViewById(R.id.spinner_main_categories);
        spinnerCategories.setSelection(0);
    }

    private void checkAuthentication() {
        if (modelManager.getIdUser() != null) {
            TextView txtLoginPage = findViewById(R.id.txtLoginPage);
            txtLoginPage.setText(R.string.logout);
            FloatingActionButton btnLoginPage = findViewById(R.id.btnLoginPage);
            btnLoginPage.setOnClickListener(this::onClickLogout);
        }
    }

    private void showToast(Context context, int text) {
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}