package org.hibernate.tutorial.domain;

import java.util.HashSet;
import java.util.Set;

public class Person {

	private Long	id;
	private int		age;
	private String	firstname;
	private String	lastname;

	public Person() {}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	private Set<String>	emailAddresses	= new HashSet<String>();

	public Set<String> getEmailAddresses() {
		return emailAddresses;
	}

	public void setEmailAddresses(Set<String> emailAddresses) {
		this.emailAddresses = emailAddresses;
	}

	private Set<Event>	events	= new HashSet<Event>();

	// Defensive, convenience methods
	protected Set<Event> getEvents() {
		return events;
	}

	protected void setEvents(Set<Event> events) {
		this.events = events;
	}

	public void addToEvent(Event event) {
		getEvents().add(event);
		event.getParticipants().add(this);
	}

	public void removeFromEvent(Event event) {
		getEvents().remove(event);
		event.getParticipants().remove(this);
	}

}
