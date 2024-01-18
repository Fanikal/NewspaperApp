package es.upm.etsiinf.pui.pui_newsmanager;

import es.upm.etsiinf.pui.pui_newsmanager.exceptions.AuthenticationError;

public class LoginThread implements Runnable{

    private final LoginActivity la;
    private final String username;
    private final String password;


    public LoginThread (LoginActivity la, String ...params){
        this.la = la;
        this.username = params[0];
        this.password = params[1];
    }

    @Override
    public void run() {

        try {
            MainActivity.modelManager.login(username, password);
        } catch (AuthenticationError e) {
            e.printStackTrace();
        }

        //EQUIVALENT TO ONPOSTEXECUTE
        //finishing the task we show the results
        la.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(MainActivity.modelManager.getIdUser() != null){
                    la.userLogged(username, password);
                } else {
                    la.showIncorrectCredentialsToast();
                }
            }
        });
    }
}
