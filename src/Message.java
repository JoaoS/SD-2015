import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by joaosubtil on 12/10/15.
 */
public class Message implements Serializable {

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
    private ArrayList <Reward> rewards = new ArrayList<Reward>();
    private ArrayList <Alternative> alternatives = new ArrayList<Alternative>();
    private long idProject;
    private float pledgeValue;
    private long alternativeChoosen;
    private String comment;
    private long idReward;
    private String reply;
    private long idMessage;


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

    public ArrayList<Reward> getRewards() {
        return rewards;
    }

    public void setRewards(ArrayList<Reward> rewards) {
        this.rewards = rewards;
    }

    public ArrayList<Alternative> getAlternatives() {
        return alternatives;
    }

    public void setAlternatives(ArrayList<Alternative> alternatives) {
        this.alternatives = alternatives;
    }

    public long getIdProject() {
        return idProject;
    }

    public void setIdProject(long idProject) {
        this.idProject = idProject;
    }

    public float getPledgeValue() {
        return pledgeValue;
    }

    public void setPledgeValue(float pledgeValue) {
        this.pledgeValue = pledgeValue;
    }

    public long getAlternativeChoosen() {
        return alternativeChoosen;
    }

    public void setAlternativeChoosen(long alternativeChoosen) {
        this.alternativeChoosen = alternativeChoosen;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public long getIdReward() {
        return idReward;
    }

    public void setIdReward(long idReward) {
        this.idReward = idReward;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public long getIdMessage() {
        return idMessage;
    }

    public void setIdMessage(long idMessage) {
        this.idMessage = idMessage;
    }
}
