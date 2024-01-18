package es.upm.etsiinf.pui.pui_newsmanager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import es.upm.etsiinf.pui.pui_newsmanager.exceptions.ServerCommunicationError;
import es.upm.etsiinf.pui.pui_newsmanager.model.Article;

public class ShowArticlesThread implements Runnable {

    private final MainActivity ma;
    private final String[] params;

    public ShowArticlesThread(MainActivity ma, String... params){
        this.ma = ma;
        this.params = params;
    }

    @Override
    public void run() {

        //EQUIVALENT TO ONPREEXECUTE
        //starting the task we block the UI
        ma.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //We call the function of the main activity
                ma.prepareUIForServer();
            }
        });

        //EQUIVALENT TO DOINBACKGROUND
        //The same as doInBackground of the other class
        List<Article> res = null;
        try {
            //By default ordered by data
            res = MainActivity.modelManager.getArticles();
        } catch (ServerCommunicationError e) {
            e.printStackTrace();
        }

        final List<Article> listArticlesResponse = res;

        //EQUIVALENT TO ONPOSTEXECUTE
        //finishing the task we show the results
        ma.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(listArticlesResponse != null)
                    ma.printResultFromServer(listArticlesResponse);
            }
        });
    }
}
