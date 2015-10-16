import java.io.Serializable;

/**
 * Created by joaosubtil on 12/10/15.
 */
public class Message implements Serializable {

    private String username;
    private String password;
    private String operation;
    private int menuOption;
    private String message;
	private int age;
	private String bi;
	private String email;
    /*  option's menu
    *  login->1
    *
    *
    * */

    public Message() {
		super();
	}

    
    public int getMenuOption() {
        return menuOption;
    }

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}


	public String getOperation() {
		return operation;
	}


	public void setOperation(String operation) {
		this.operation = operation;
	}


	public void setMenuOption(int menuOption) {
        this.menuOption = menuOption;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getBi() {
		return bi;
	}

	public void setBi(String bi) {
		this.bi = bi;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}


}
