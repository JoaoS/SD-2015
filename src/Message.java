import java.io.Serializable;

/**
 * Created by joaosubtil on 12/10/15.
 */public class Message implements Serializable {

    private String username;
    private String password;
    private String operation;
    private String message;
    private int age;
    private String bi;
    private String email;
    private String projectName;
    private String projectDescription;
    private String projectLimitDate;
    private long projectTargetValue;
    private String projectEnterprise;


    public Message() {
        super();
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

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectDescription() {
        return projectDescription;
    }

    public void setProjectDescription(String projectDescription) {
        this.projectDescription = projectDescription;
    }

    public String getProjectLimitDate() {
        return projectLimitDate;
    }

    public void setProjectLimitDate(String projectLimitDate) {
        this.projectLimitDate = projectLimitDate;
    }

    public long getProjectTargetValue() {
        return projectTargetValue;
    }

    public void setProjectTargetValue(long projectTargetValue) {
        this.projectTargetValue = projectTargetValue;
    }

    public String getProjectEnterprise() {
        return projectEnterprise;
    }

    public void setProjectEnterprise(String projectEnterprise) {
        this.projectEnterprise = projectEnterprise;
    }
}
