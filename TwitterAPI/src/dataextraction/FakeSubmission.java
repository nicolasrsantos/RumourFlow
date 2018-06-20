package dataextraction;

import java.util.List;

public class FakeSubmission {
	private Double created;
	private List<String> users;
	
	public FakeSubmission() {
		
	}
	
	public Double getCreated() {
		return created;
	}
	public void setCreated(Double created) {
		this.created = created;
	}
	public List<String> getUsers() {
		return users;
	}
	public void setUsers(List<String> users) {
		this.users = users;
	}
}
