package es.upm.etsiinf.pui.pui_newsmanager;

import es.upm.etsiinf.pui.pui_newsmanager.exceptions.ServerCommunicationError;
import es.upm.etsiinf.pui.pui_newsmanager.model.Article;

public class ShowArticleDetailsThread implements Runnable {

    private final ArticleDetailsActivity a;
    private final int idArticle;

    public ShowArticleDetailsThread(ArticleDetailsActivity a, String... params){
        this.a = a;
        this.idArticle = Integer.parseInt(params[0]);
    }

    @Override
    public void run() {

        //EQUIVALENT TO DOINBACKGROUND
        //The same as doInBackground of the other class
        Article res = null;
        try {
            res = MainActivity.modelManager.getArticle(idArticle);
        } catch (ServerCommunicationError e) {
            e.printStackTrace();
        }

        final Article articleResponse = res;

        //EQUIVALENT TO ONPOSTEXECUTE
        //finishing the task we show the results
        a.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(articleResponse != null)
                    a.loadArticle(articleResponse);
            }
        });
    }
}
