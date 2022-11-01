package com.example.demo.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.lang.NonNull;

@Entity
@Table(name = "patient_record")
public class PatientRecord {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long patientId;

	@NonNull
	private String name;

	@NonNull
	private Integer age;

	@NonNull
	private String address;

	public PatientRecord() {
	}

	public PatientRecord(Long patientId, String name, Integer age, String address) {
		super();
		this.patientId = patientId;
		this.name = name;
		this.age = age;
		this.address = address;
	}

	public PatientRecord(String name, Integer age, String address) {
		super();
		this.name = name;
		this.age = age;
		this.address = address;
	}

	public Long getPatientId() {
		return patientId;
	}

	public void setPatientId(Long patientId) {
		this.patientId = patientId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Override
	public boolean equals(Object obj) {
		return this.getPatientId() == ((PatientRecord) obj).getPatientId();
	}
}
