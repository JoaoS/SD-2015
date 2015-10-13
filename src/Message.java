import java.io.Serializable;

/**
 * Created by joaosubtil on 12/10/15.
 */
public class Message implements Serializable {

    private String username;
    private String password;

    private int menuOption;
    private String mensagem;

    /*  option's menu
    *  login->1
    *
    *
    * */


    public int getMenuOption() {
        return menuOption;
    }

    public void setMenuOption(int menuOption) {
        this.menuOption = menuOption;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String toString(){
        return "msg="+mensagem;
    }
}
