package com.example.demo.controller;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.example.demo.controllers.PatientRecordController;
import com.example.demo.exceptions.InvalidRequestException;
import com.example.demo.exceptions.NotFoundException;
import com.example.demo.models.PatientRecord;
import com.example.demo.repositories.PatientRecordRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(PatientRecordController.class)
class PatientRecordControllerTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper mapper;

	@MockBean
	PatientRecordRepository patientRecordRepository;

	PatientRecord RECORD_1 = new PatientRecord(1l, "Rayven Yor", 23, "Cebu Philippines");
	PatientRecord RECORD_2 = new PatientRecord(2l, "David Landup", 27, "New York USA");
	PatientRecord RECORD_3 = new PatientRecord(3l, "Jane Doe", 31, "New York USA");

	@Test
	public void getAllRecords_success() throws Exception {
		List<PatientRecord> records = new ArrayList<>(Arrays.asList(RECORD_1, RECORD_2, RECORD_3));

		Mockito.when(patientRecordRepository.findAll()).thenReturn(records);

		mockMvc.perform(MockMvcRequestBuilders.get("/patient").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(3)))
				.andExpect(jsonPath("$[2].name", is("Jane Doe")));
	}

	@Test
	public void getPatientById_success() throws Exception {
		Mockito.when(patientRecordRepository.findById(RECORD_1.getPatientId()))
				.thenReturn(java.util.Optional.of(RECORD_1));

		mockMvc.perform(MockMvcRequestBuilders.get("/patient/1").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$", notNullValue()))
				.andExpect(jsonPath("$.name", is("Rayven Yor")));
	}

	@Test
	public void createRecord_success() throws Exception {
		PatientRecord record = new PatientRecord(1L, "John Doe", 47, "New York USA");

		Mockito.when(patientRecordRepository.save(record)).thenReturn(record);

		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/patient")
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
				.content(this.mapper.writeValueAsString(record));

		mockMvc.perform(mockRequest).andExpect(status().isOk()).andExpect(jsonPath("$", notNullValue()))
				.andExpect(jsonPath("$.name", is("John Doe")));
	}

	@Test
	public void updatePatientRecord_success() throws Exception {
		PatientRecord updatedRecord = new PatientRecord(1L, "John Doe", 47, "New York USA");

		Mockito.when(patientRecordRepository.findById(RECORD_1.getPatientId())).thenReturn(Optional.of(RECORD_1));
		Mockito.when(patientRecordRepository.save(updatedRecord)).thenReturn(updatedRecord);

		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/patient")
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
				.content(this.mapper.writeValueAsString(updatedRecord));

		mockMvc.perform(mockRequest).andExpect(status().isOk()).andExpect(jsonPath("$", notNullValue()))
				.andExpect(jsonPath("$.name", is("John Doe")));
	}

	@Test
	public void updatePatientRecord_nullId() throws Exception {
		PatientRecord updatedRecord = new PatientRecord("John Doe", 47, "New York USA");

		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/patient")
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
				.content(this.mapper.writeValueAsString(updatedRecord));

		mockMvc.perform(mockRequest).andExpect(status().isBadRequest())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof InvalidRequestException))
				.andExpect(result -> assertEquals("PatientRecord or ID must not be null!",
						result.getResolvedException().getMessage()));
	}

	@Test
	public void updatePatientRecord_recordNotFound() throws Exception {
		PatientRecord updatedRecord = new PatientRecord(1L, "John Doe", 47, "New York USA");

		Mockito.when(patientRecordRepository.findById(updatedRecord.getPatientId())).thenReturn(Optional.empty());

		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/patient")
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
				.content(this.mapper.writeValueAsString(updatedRecord));

		mockMvc.perform(mockRequest).andExpect(status().isBadRequest())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof NotFoundException))
				.andExpect(result -> assertEquals("Patient with ID 1 does not exist.",
						result.getResolvedException().getMessage()));
	}
	
	@Test
	public void deletePatientById_success() throws Exception {
	    Mockito.when(patientRecordRepository.findById(RECORD_2.getPatientId())).thenReturn(Optional.of(RECORD_2));

	    mockMvc.perform(MockMvcRequestBuilders
	            .delete("/patient/2")
	            .contentType(MediaType.APPLICATION_JSON))
	            .andExpect(status().isOk());
	}

	@Test
	public void deletePatientById_notFound() throws Exception {
	    Mockito.when(patientRecordRepository.findById(5l)).thenReturn(Optional.empty());

	    mockMvc.perform(MockMvcRequestBuilders
	            .delete("/patient/5")
	            .contentType(MediaType.APPLICATION_JSON))
	    .andExpect(status().isBadRequest())
	            .andExpect(result ->
	                    assertTrue(result.getResolvedException() instanceof NotFoundException))
	    .andExpect(result ->
	            assertEquals("Patient with ID 5 does not exist.", result.getResolvedException().getMessage()));
	}
}
